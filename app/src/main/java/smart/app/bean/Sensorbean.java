package smart.app.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import smart.app.bean.Devicebean;

public class Sensorbean implements Serializable {

    Devicebean devicebean;
    public ArrayList<sensorinfos> sensors=new ArrayList<>();

    public Sensorbean(Devicebean devicebean) {
        this.devicebean = devicebean;
        sensors = getsensors(devicebean);
    }

    public static class sensorinfos implements Serializable {

        public HashMap<String,ArrayList<sensorinfo>> sensor = new HashMap<>();

        public static class sensorinfo implements Serializable {
            public String Id;
            public String Name;
            public boolean Editable;
            public String Time;
            public String Value;
            public String Unit;

            public void setId(String id) {
                this.Id = id;
            }

            public void setName(String name) {
                this.Name = name;
            }

            public void setEditable(boolean editable) {
                this.Editable = editable;
            }

            public void setTime(String time) {
                this.Time = time;
            }

            public void setValue(String value) {
                this.Value = value;
            }

            public void setUnit(String unit) {
                this.Unit = unit;
            }
        }
    }

    private ArrayList<sensorinfos> getsensors(Devicebean devicebean) {
        ArrayList<sensorinfos> infos=new ArrayList<>();
        for (int i = 0; i < devicebean.data.Groups.size(); i++) {
            sensorinfos sensors=new sensorinfos();
            HashMap<String,ArrayList<sensorinfos.sensorinfo>> sensor=new HashMap<>();
            ArrayList<sensorinfos.sensorinfo> status_info=new ArrayList<>();
            ArrayList<sensorinfos.sensorinfo> value1_info=new ArrayList<>();
            ArrayList<sensorinfos.sensorinfo> value2_info=new ArrayList<>();

                for (int m = 0; m < devicebean.data.Groups.get(i).Devices.size(); m++) {
                    String val=devicebean.data.Groups.get(i).Devices.get(m).getStatus().substring(devicebean.data.Groups.get(i).Devices.get(m).getStatus().indexOf("}")+1);
                    sensorinfos.sensorinfo sensorinfo =new sensorinfos.sensorinfo();
                    sensorinfo.setName(devicebean.data.Groups.get(i).Devices.get(m).getName());
                    sensorinfo.setEditable(devicebean.data.Groups.get(i).Devices.get(m).isEditable());
                    sensorinfo.setValue(val);
                    sensorinfo.setId(devicebean.data.Groups.get(i).Devices.get(m).getId());
                    status_info.add(m,sensorinfo);
                    sensor.put("status",status_info);

                    for (int n = 0; n <devicebean.data.Groups.get(i).Devices.get(m).Sensors.size() ; n++) {
                        sensorinfos.sensorinfo sensorinfo1 = new sensorinfos.sensorinfo();
                        sensorinfo1.setName(devicebean.data.Groups.get(i).Devices.get(m).Sensors.get(n).getName());
                        sensorinfo1.setEditable(devicebean.data.Groups.get(i).Devices.get(m).Sensors.get(n).isEditable());
                        sensorinfo1.setTime(devicebean.data.Groups.get(i).Devices.get(m).Sensors.get(n).getTime());
                        sensorinfo1.setUnit(devicebean.data.Groups.get(i).Devices.get(m).Sensors.get(n).getUnit());
                        sensorinfo1.setValue(devicebean.data.Groups.get(i).Devices.get(m).Sensors.get(n).getValue());
                        sensorinfo1.setId(devicebean.data.Groups.get(i).Devices.get(m).Sensors.get(n).getId());
                        value1_info.add(n,sensorinfo1);
                        sensor.put("value1",value1_info);
                    }
                }
            for (int j = 0; j < devicebean.data.Groups.get(i).Sensors.size(); j++) {
                sensorinfos.sensorinfo sensorinfo = new sensorinfos.sensorinfo();
                sensorinfo.setName(devicebean.data.Groups.get(i).Sensors.get(j).getName());
                sensorinfo.setEditable(devicebean.data.Groups.get(i).Sensors.get(j).isEditable());
                sensorinfo.setTime(devicebean.data.Groups.get(i).Sensors.get(j).getTime());
                sensorinfo.setUnit(devicebean.data.Groups.get(i).Sensors.get(j).getUnit());
                sensorinfo.setValue(devicebean.data.Groups.get(i).Sensors.get(j).getValue());
                sensorinfo.setId(devicebean.data.Groups.get(i).Sensors.get(j).getId());
                value2_info.add(j,sensorinfo);
                sensor.put("value2",value2_info);
            }
            sensors.sensor=sensor;
            infos.add(i,sensors);
        }
        return infos;
    }
}

