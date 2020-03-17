package smart.app.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import smart.app.R;
import smart.app.bean.Sensorbean;

public class DeviceAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<Sensorbean.sensorinfos.sensorinfo>  status_List;
    private Context context;
//    private ArrayList<String> SensorId;

    public DeviceAdapter(Context context, ArrayList<Sensorbean.sensorinfos.sensorinfo>  status_List) {
        super();
        this.status_List = status_List;
        this.context = context;
//        this.SensorId = SensorId;
    }

    @Override
    public int getCount() {
        return status_List.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Sensorbean.sensorinfos.sensorinfo bean=status_List.get(position);
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = View.inflate(context, R.layout.device_adapter_menu,
                    null);
            vh.id_name = convertView
                    .findViewById(R.id.id_name);
            vh.id_value = convertView
                    .findViewById(R.id.id_value);
            vh.id_unit = convertView.findViewById(R.id.id_unit);

//            vh.id_image = convertView.findViewById(R.id.imageView);
//            vh.id_image.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, LineChartActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("sensorid", SensorId.get(position));
//                    intent.putExtras(bundle);
//                    context.startActivity(intent);
//                }
//            });
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();

        }
        vh.id_name.setText(bean.Name);
        vh.id_value.setText(bean.Value);
        vh.id_unit.setText(bean.Unit);

        if(vh.id_value.getText().toString().equals("停止")){
            vh.id_value.setTextColor(Color.RED);
        }else if(vh.id_value.getText().toString().equals("运行")){
            vh.id_value.setTextColor(Color.GREEN);
        }
        return convertView;
    }

    private class ViewHolder {
        TextView id_name;
        TextView id_value;
        TextView id_unit;
//        ImageView id_image;
    }
}