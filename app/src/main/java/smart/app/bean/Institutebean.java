package smart.app.bean;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Institutebean implements Serializable  {
    public int status;
    public String time;
    public ArrayList<datainfo> data;


    public static class datainfo implements Serializable{
        public String Id;
        public String Name;
        public String Type;
        public String Location;
        public float Longitude;
        public float Latitude;
        public String Address;
        public String Summary;

        //自治区+|上海|北京市|天津|重庆市|
        public String getProvince(String adr){
            String regex="([^省]+省|.+自治区+|上海市|北京市|天津市|重庆市)?([^市]+市|.+自治州)?([^县]+县|.+区|.+镇|.+局)?([^区]+区|.+镇)?(.*)";
            Matcher m= Pattern.compile(regex).matcher(adr);
            String province=null;
            if(m.find()){
                MatchResult result = m.toMatchResult();
                province=result.group(1);
            }
            return province;
            }
        public LatLng getLatLng(float lat,float lon){
            return transcoordinate(new LatLng(lat,lon));
        }
        public LatLng transcoordinate(LatLng lating) {
            //将标准GPS坐标转为百度坐标
            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.GPS);
            // sourceLatLng待转换坐标
            converter.coord(lating);
            return converter.convert();
        }
    }

}