package smart.app;

import java.io.Serializable;
import java.util.ArrayList;

public class Gsonparsebean implements Serializable {
    public int status;
    public String time;
    public ArrayList<datainfo> data;

    public class datainfo implements Serializable {
        public String Id;
        public String Name;
        public String Type;
        public String Location;
        public float Longitude;
        public float Latitude;
        public String Address;
        public String Summary;

    }
}
