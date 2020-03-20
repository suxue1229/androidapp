package smart.app.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import smart.app.bean.Institutebean;
import smart.app.R;

public class ExtendableListViewAdapter extends BaseExpandableListAdapter {
    private Context mcontext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<String> groups;
    private HashMap<String,ArrayList<Institutebean.datainfo>>  stations;

    public ExtendableListViewAdapter(Context mContext, ArrayList<String> groups, HashMap<String,ArrayList<Institutebean.datainfo>> stations) {
        this.mcontext=mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.groups=groups;
        this.stations=stations;
    }
    public ArrayList<Institutebean.datainfo> getchilds(String str){
            Set<String> keys = stations.keySet();
            ArrayList<Institutebean.datainfo> station=new ArrayList<>();
            Iterator<String> iterator = keys.iterator();
            while(iterator.hasNext()) {
                String key = iterator.next();
                if(key.equals(str)){
                    station = stations.get(key);
                }
            }
            return station;
    }

    @Override
    // 获取分组的个数
    public int getGroupCount() {
        return groups.size();
    }

    //获取指定分组中的子选项的个数
    @Override
    public int getChildrenCount(int groupPosition) {
        return getchilds(groups.get(groupPosition)).size();
    }

    //        获取指定的分组数据
    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    //获取指定分组中的指定子选项数据
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return getchilds(groups.get(groupPosition)).get(childPosition);
    }

    //获取指定分组的ID, 这个ID必须是唯一的
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //获取子选项的ID, 这个ID必须是唯一的
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们
    @Override
    public boolean hasStableIds() {
        return true;
    }
    /**
     *
     * 获取显示指定组的视图对象
     *
     * @param groupPosition 组位置
     * @param isExpanded 该组是展开状态还是伸缩状态
     * @param convertView 重用已有的视图对象
     * @param parent 返回的视图对象始终依附于的视图组
     */
// 获取显示指定分组的视图
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.partent_item, parent, false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.province_textview = convertView.findViewById(R.id.province_textview);
            groupViewHolder.parent_image=convertView.findViewById(R.id.parent_image);
            convertView.setTag(groupViewHolder);
        }else {
            groupViewHolder = (GroupViewHolder)convertView.getTag();
        }
        groupViewHolder.province_textview.setText(groups.get(groupPosition));
        //如果是展开状态，
        if (isExpanded){
            groupViewHolder.parent_image.setImageDrawable(ContextCompat.getDrawable(mcontext,R.drawable.ic_keyboard_arrow_down_grey600_24dp));
        }else{
            groupViewHolder.parent_image.setImageDrawable(ContextCompat.getDrawable(mcontext,R.drawable.ic_keyboard_arrow_right_grey600_24dp));
        }
        return convertView;
    }
    /**
     *
     * 获取一个视图对象，显示指定组中的指定子元素数据。
     *
     * @param groupPosition 组位置
     * @param childPosition 子元素位置
     * @param isLastChild 子元素是否处于组中的最后一个
     * @param convertView 重用已有的视图(View)对象
     * @param parent 返回的视图(View)对象始终依附于的视图组
     * @return
     * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean, android.view.View,
     *      android.view.ViewGroup)
     */

   Handler handler = new Handler();


    //取得显示给定分组给定子位置的数据用的视图
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
         Institutebean.datainfo datainfo=getchilds(groups.get(groupPosition)).get(childPosition);

        if (convertView==null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_item,parent,false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.station_name = convertView.findViewById(R.id.station_name);
            childViewHolder.address = convertView.findViewById(R.id.address);
            childViewHolder.status=convertView.findViewById(R.id.status);

            convertView.setTag(childViewHolder);

        }else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        childViewHolder.station_name.setText(datainfo.Name);
        childViewHolder.address.setText(datainfo.Address);

        if(datainfo.getFlagName().equals("故障")){
            childViewHolder.status.setText("故障");
            childViewHolder.status.setBackgroundColor(Color.RED);
        }else if(datainfo.getFlagName().equals("停止")){
            childViewHolder.status.setText("停止");
            childViewHolder.status.setBackgroundColor(Color.GRAY);
        }else if(datainfo.getFlagName().equals("运行")){
            childViewHolder.status.setText("运行");
            childViewHolder.status.setBackgroundColor(Color.GREEN);
        }

        return convertView;
    }

    //指定位置上的子元素是否可选中
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class GroupViewHolder {
        TextView province_textview;
        ImageView parent_image;
    }

    static class ChildViewHolder {
        TextView station_name;
        TextView address;
        TextView status;

    }
}