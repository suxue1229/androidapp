package smart.app;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.io.Serializable;
import java.util.ArrayList;


public class Institutebean implements Serializable  {
    public int status;
    public String time;
    public ArrayList<datainfo> data;


    public static class datainfo implements Serializable,OnGetGeoCoderResultListener {
        public String Id;
        public String Name;
        public String Type;
        public String Location;
        public float Longitude;
        public float Latitude;
        public String Address;
        public String Summary;

        private String ProvinceName;


        /*逆地理编码（即坐标转地址）*/
        GeoCoder mCoder  = null;

        public String getProvinceName(){
            mCoder=GeoCoder.newInstance();
            mCoder.setOnGetGeoCodeResultListener(this);
            //获取经纬度
           LatLng desLatLng = transcoordinate(new LatLng(Latitude, Longitude));
            // 反Geo搜索
            if(mCoder.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(desLatLng))) {
                System.out.println("1111");
                return this.ProvinceName;
            }
            return null;
        }

        public LatLng transcoordinate(LatLng lating) {
            //将标准GPS坐标转为百度坐标
            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.GPS);
            // sourceLatLng待转换坐标
            converter.coord(lating);
            return converter.convert();
        }

        @Override
        public void onGetGeoCodeResult(GeoCodeResult result) {

        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有找到检索结果
                return;
            } else {
               this.ProvinceName=result.getAddressDetail().province;
               System.out.println("this.ProvinceName:"+this.ProvinceName);

            }

        }

    }

}
