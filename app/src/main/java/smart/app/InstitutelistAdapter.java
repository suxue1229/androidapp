package smart.app;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class InstitutelistAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private ArrayList<Institutebean.datainfo> mData;

    public InstitutelistAdapter(Context mContext, ArrayList<Institutebean.datainfo> mData) {
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.institutelist_adapter_menu, parent, false);
            holder.tv_title = convertView.findViewById(R.id.textView);
            holder.tv_content = convertView.findViewById(R.id.address);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_title.setText(mData.get(position).Name);
        holder.tv_content.setText(mData.get(position).Address);

        return convertView;
    }

    class ViewHolder {
        private TextView tv_title;
        private TextView tv_content;
    }


}