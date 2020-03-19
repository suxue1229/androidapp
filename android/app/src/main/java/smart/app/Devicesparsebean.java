package smart.app;

import java.io.Serializable;
import java.util.ArrayList;

public class Devicesparsebean implements Serializable {
    public int status;
    public String time;
    public data data;

    public class data implements Serializable {
        public String Id;
        public String Name;
        public String Type;
        public ArrayList<groupsinfo> Groups;

        public class groupsinfo implements Serializable {
            public String Id;
            public String Name;
            public int Count;
            public ArrayList<devicesinfo> Devices;
            public ArrayList<sensorsinfo> Sensors = new ArrayList<>();

            public class devicesinfo implements Serializable {
                public String Id;
                public String Name;
                public boolean Editable;
                public String Status;
                public ArrayList<sensorsinfo> Sensors;

                public class sensorsinfo implements Serializable {
                    public String Id;
                    public String Name;
                    public boolean Editable;
                    public String Time;
                    public String Value;
                    public String Unit;

                    public String getId() {
                        return Id;
                    }

                    public String getName() {
                        return Name;
                    }

                    public boolean isEditable() {
                        return Editable;
                    }

                    public String getTime() {
                        return Time;
                    }

                    public String getValue() {
                        return Value;
                    }

                    public String getUnit() {
                        return Unit;
                    }
                }
            }

            public class sensorsinfo implements Serializable {
                public String Id;
                public String Name;
                public boolean Editable;
                public String Time;
                public String Value;
                public String Unit;

                public String getId() {
                    return Id;
                }

                public String getName() {
                    return Name;
                }

                public boolean isEditable() {
                    return Editable;
                }

                public String getTime() {
                    return Time;
                }

                public String getValue() {
                    return Value;
                }

                public String getUnit() {
                    return Unit;
                }

            }
        }

    }


}
