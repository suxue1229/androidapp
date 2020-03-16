package smart.app;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ProvincelistAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private ArrayList<String> mData;

    public ProvincelistAdapter(Context mContext, ArrayList<String> mData) {
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
            convertView = mLayoutInflater.inflate(R.layout.provincelist_adapter, parent, false);
            holder.relativeLayout = convertView.findViewById(R.id.relativelayout);
            holder.province = convertView.findViewById(R.id.province_textview);
            holder.next_button = convertView.findViewById(R.id.next);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
            holder.province.setText(mData.get(position));

//        holder.next_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void OnClickListener(){}
//
//        });

        return convertView;
    }

    class ViewHolder {
        private RelativeLayout relativeLayout;
        private TextView province;
        private ImageButton next_button;
    }


}