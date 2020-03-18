package smart.app.bean;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import smart.app.Network.HttpService;


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
        private String FlagName="运行"; //站点状态分析 故障 停止

        public String getFlagName() {
            return FlagName;
        }

        public void setFlagName(Devicebean devicebean) {
            for (int i = 0; i < devicebean.data.Groups.size(); i++) {

                int count=0;int sum=0;
                for (int j = 0; j <devicebean.data.Groups.get(i).Devices.size() ; j++) {
                    String status=devicebean.data.Groups.get(i).Devices.get(j).getStatus().substring(devicebean.data.Groups.get(i).Devices.get(j).getStatus().indexOf("}")+1);
                    if(status.equals("故障")){
                        count++;
                    }else if(status.equals("停止")){
                        sum++;
                    }
                }
                if(count>0){
                    FlagName="故障";
                }else if(devicebean.data.Groups.get(i).Devices.size()==sum){
                    FlagName="停止";
                }
            }
        }

        //自治区+|上海|北京市|天津|重庆市|
        public String getProvince(String adr){
            String regex="([^省]+省|.+自治区+|上海市|北京市|天津市|重庆市|台湾|香港|澳门)?([^市]+市|.+自治州)?([^县]+县|.+区|.+镇|.+局)?([^区]+区|.+镇)?(.*)";
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
