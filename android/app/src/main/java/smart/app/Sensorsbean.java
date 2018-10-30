package smart.app;

import java.io.Serializable;
import java.util.ArrayList;

public class Sensorsbean implements Serializable {
    Devicesparsebean devicesbean;
    public ArrayList<groupsinfo> Groups;

    public Sensorsbean(Devicesparsebean devicesparsebean) {
        this.devicesbean = devicesparsebean;
        Groups = getGroups(devicesparsebean);
    }

    public static class groupsinfo implements Serializable {
        public String Name;

        public void setName(String name) {
            Name = name;
        }

        ArrayList<sensorsinfo> Sensors = new ArrayList<>();

        public static class sensorsinfo implements Serializable {
            public String Id;
            public String Name;
            public boolean Editable;
            public String Time;
            public String Value;
            public String Unit;

            public void setId(String id) {
                Id = id;
            }

            public void setName(String name) {
                Name = name;
            }

            public void setEditable(boolean editable) {
                Editable = editable;
            }

            public void setTime(String time) {
                Time = time;
            }

            public void setValue(String value) {
                Value = value;
            }

            public void setUnit(String unit) {
                Unit = unit;
            }

        }
    }

    private ArrayList<groupsinfo> getGroups(Devicesparsebean devices) {
        ArrayList<groupsinfo> Groups = new ArrayList<>();
        for (int i = 0; i < devices.data.Groups.size(); i++) {
            groupsinfo group = new groupsinfo();
            group.setName(devices.data.Groups.get(i).Name);
            ArrayList<groupsinfo.sensorsinfo> Sensors = new ArrayList<>();
            if (i == 0 && devices.data.Groups.get(i).Devices.size() > 0) {
                for (int m = 0; m < devices.data.Groups.get(i).Devices.get(0).Sensors.size(); m++) {
                    groupsinfo.sensorsinfo sensorsinfo = new groupsinfo.sensorsinfo();
                    sensorsinfo.setName(devices.data.Groups.get(i).Devices.get(0).Sensors.get(m).getName());
                    sensorsinfo.setEditable(devices.data.Groups.get(i).Devices.get(0).Sensors.get(m).isEditable());
                    sensorsinfo.setTime(devices.data.Groups.get(i).Devices.get(0).Sensors.get(m).getTime());
                    sensorsinfo.setUnit(devices.data.Groups.get(i).Devices.get(0).Sensors.get(m).getUnit());
                    sensorsinfo.setValue(devices.data.Groups.get(i).Devices.get(0).Sensors.get(m).getValue());
                    sensorsinfo.setId(devices.data.Groups.get(i).Devices.get(0).Sensors.get(m).getId());
                    Sensors.add(m, sensorsinfo);
                }
            }
            for (int j = 0; j < devices.data.Groups.get(i).Sensors.size(); j++) {
                groupsinfo.sensorsinfo sensorsinfo = new groupsinfo.sensorsinfo();
                sensorsinfo.setName(devices.data.Groups.get(i).Sensors.get(j).getName());
                sensorsinfo.setEditable(devices.data.Groups.get(i).Sensors.get(j).isEditable());
                sensorsinfo.setTime(devices.data.Groups.get(i).Sensors.get(j).getTime());
                sensorsinfo.setUnit(devices.data.Groups.get(i).Sensors.get(j).getUnit());
                sensorsinfo.setValue(devices.data.Groups.get(i).Sensors.get(j).getValue());
                sensorsinfo.setId(devices.data.Groups.get(i).Sensors.get(j).getId());
                Sensors.add(j, sensorsinfo);
            }
            group.Sensors = Sensors;
            Groups.add(i, group);
        }
        return Groups;
    }
}