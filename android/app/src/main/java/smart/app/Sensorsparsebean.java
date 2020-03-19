package smart.app;

import java.io.Serializable;

public class Sensorsparsebean implements Serializable {
    public int status;
    public String time;
    public data data;

    public class data implements Serializable {
        public String Id;
        public String Name;
        public boolean Editable;
        public String Time;
        public String Value;
        public String Unit;
    }
}

