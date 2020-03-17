//package smart.app.bean;
//
//import com.baidu.mapapi.model.LatLng;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//
//import smart.app.bean.Institutebean;
//
//public class Province implements Serializable {
//
//    private String code;
//    private String name;
//    private String msg;
//    public String getCode() {
//        return code;
//    }
//
//    public String getName() {
//        return name;
//    }
//    public ArrayList<Institutebean.datainfo> institutes=new ArrayList<>();
//
//    public Province(String codenum){
//        this.code=codenum;
//        if (codenum.equals("0")){
//            this.name="";
//            this.msg="离线";
//        }
//        else {
//            this.msg="在线";
//            switch (codenum){
//                case "11":
//                    this.name="北京市";
//                    break;
//                case "12":
//                    this.name="天津市";
//                    break;
//                case "13":
//                    this.name="河北省";
//                    break;
//                case "14":
//                    this.name="山西省";
//                    break;
//                case "15":
//                    this.name="内蒙古自治区";
//                    break;
//                case "21":
//                    this.name="辽宁省";
//                    break;
//                case "22":
//                    this.name="吉林省";
//                    break;
//                case "23":
//                    this.name="黑龙江省";
//                    break;
//                case "31":
//                    this.name="上海市";
//                    break;
//                case "32":
//                    this.name="江苏省";
//                    break;
//                case "33":
//                    this.name="浙江省";
//                    break;
//                case "34":
//                    this.name="安徽省";
//                    break;
//                case "35":
//                    this.name="福建省";
//                    break;
//                case "36":
//                    this.name="江西省";
//                    break;
//                case "37":
//                    this.name="山东省";
//                    break;
//                case "41":
//                    this.name="河南省";
//                    break;
//                case "42":
//                    this.name="湖北省";
//                    break;
//                case "43":
//                    this.name="湖南省";
//                    break;
//                case "44":
//                    this.name="广东省";
//                    break;
//                case "45":
//                    this.name="广西壮族自治区";
//                    break;
//                case "46":
//                    this.name="海南省";
//                    break;
//                case "50":
//                    this.name="重庆市";
//                    break;
//                case "51":
//                    this.name="四川省";
//                    break;
//                case "52":
//                    this.name="贵州省";
//                    break;
//                case "53":
//                    this.name="云南省";
//                    break;
//                case "54":
//                    this.name="西藏自治区";
//                    break;
//                case "61":
//                    this.name="陕西省";
//                    break;
//                case "62":
//                    this.name="甘肃省";
//                    break;
//                case "63":
//                    this.name="青海省";
//                    break;
//                case "64":
//                    this.name="宁夏回族自治区";
//                    break;
//
//                case "65":
//                    this.name="新疆维吾尔自治区";
//                    break;
//
//                case "71":
//                    this.name="台湾";
//                    break;
//
//                case "81":
//                    this.name="香港特别行政区";
//                    break;
//
//                case "82":
//                    this.name="澳门特别行政区";
//                    break;
//
//                default:
//                    this.name="不是中国境内地区";
//
//            }
//        }
//    }
//}
