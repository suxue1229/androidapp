package smart.app;


import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpService {
    static long mLastActionTime; // 上一次操作时间
    static HashMap<String, Object> hashMap = new HashMap<>();
    static Accountbean accountbean=new Accountbean();
    static Institutebean institutebean=new Institutebean();


    static OkHttpClient client = new OkHttpClient();// 1.声明okhttp客户端
    static String https_path="https://www.oriwater.cn/api/";
    static String http_path="http://www.oriwater.cn/api/";
    static StringBuilder url =null;
    static Gson gson=null;


    public static Boolean authorize(String username, String password) throws Exception {
        url=new StringBuilder();
        url.append(https_path);
        url.append("user/authorize");
        RequestBody formBody = new FormBody.Builder().add("grant_type", "password").add("username", username)
                .add("password", password).build();
        // 2.构造request,声明构造器 ,设置请求方式,设置请求参数
        Request request = new Request.Builder().url(url.toString()).post(formBody).build();
        // 3.通过客户端执行请求,获得response
        Response response = client.newCall(request).execute();
        String res = response.body().string();
        if (response.isSuccessful()) {
            hashMap.put("access_token", new JSONObject(res).getString("access_token"));
            hashMap.put("token_type", new JSONObject(res).getString("token_type"));
            hashMap.put("expires_in", new JSONObject(res).getInt("expires_in"));
            hashMap.put("refresh_token", new JSONObject(res).getString("refresh_token"));
            return true;
        }
        return false;
    }

    public static void refreshtoken() throws Exception {
        url=new StringBuilder();
        url.append(https_path);
        url.append("user/authorize");
        RequestBody formBody1 = new FormBody.Builder().add("grant_type", "refresh_token")
                .add("refresh_token", (String) hashMap.get("refresh_token")).build();
        Request request1 = new Request.Builder().url(url.toString()).post(formBody1).build();
        Response response1 = client.newCall(request1).execute();
        String str1 = response1.body().string();
        if (response1.isSuccessful()) {
            hashMap.put("access_token", new JSONObject(str1).getString("access_token"));
            hashMap.put("token_type", new JSONObject(str1).getString("token_type"));
            hashMap.put("expires_in", new JSONObject(str1).getInt("expires_in"));
            hashMap.put("refresh_token", new JSONObject(str1).getString("refresh_token"));
        }
    }

    /* 获取当前账户信息 */
    public static Accountbean accountinfo() throws Exception {
        if (System.currentTimeMillis() - mLastActionTime > (int) hashMap.get("expires_in") * 1000) {
            refreshtoken();
            mLastActionTime = System.currentTimeMillis();
        }
        url=new StringBuilder();
        url.append(https_path);
        url.append("user/account");
        Request request = new Request.Builder().addHeader("Authorization", hashMap.get("token_type") + " " + hashMap.get("access_token"))
                .url(url.toString()).build();
        // 3.通过客户端执行请求,获得response
        Response response = client.newCall(request).execute();
        String str = response.body().string();
        gson = new Gson();
        if (response.isSuccessful()) {
            accountbean =gson.fromJson(str,Accountbean.class);
            if(accountbean.status == 0){
                return accountbean;
            }
        }
        return null;
    }

    /* 修改当前账户信息 */
    public static Boolean authorizeaccess(Accountbean accountbean1) throws Exception {
        if (System.currentTimeMillis() - mLastActionTime > (int) hashMap.get("expires_in") * 1000) {
            refreshtoken();
            mLastActionTime = System.currentTimeMillis();
        }
        url=new StringBuilder();
        url.append(https_path);
        url.append("user/account");
        RequestBody formBody = new FormBody.Builder().add("LastName", accountbean1.data.getLastName()).add("FirstName", accountbean1.data.getFirstName()).add("NickName", accountbean1.data.getNickName())
                .add("Company", accountbean1.data.getCompany()).add("Department", accountbean1.data.getDepartment()).build();
        Request accountrequest = new Request.Builder().addHeader("Authorization", hashMap.get("token_type") + " " + hashMap.get("access_token"))
                .url(url.toString()).post(formBody).build();
        // 3.通过客户端执行请求,获得response
        Response response = client.newCall(accountrequest).execute();
        if (response.isSuccessful()) {
            return true;
        }
        return false;
    }

    public static Institutebean institutelists() throws Exception {
        if (System.currentTimeMillis() - mLastActionTime > (int) hashMap.get("expires_in") * 1000) {
            refreshtoken();
            mLastActionTime = System.currentTimeMillis();
        }
        url=new StringBuilder();
        url.append(http_path);
        url.append("institute");
        Request request = new Request.Builder().addHeader("Authorization", hashMap.get("token_type") + " " + hashMap.get("access_token"))
                .url(url.toString()).build();
        // 3.通过客户端执行请求,获得response
        Response response = client.newCall(request).execute();
        String str = response.body().string();
        gson=new Gson();
        if (response.isSuccessful()) {
            institutebean=gson.fromJson(str,Institutebean.class);
            if(institutebean.status == 0) {
                return institutebean;
            }
        }
        return null;
    }

    public static Institutebean instituteInfo() throws Exception {
        if (System.currentTimeMillis() - mLastActionTime > (int) hashMap.get("expires_in") * 1000) {
            refreshtoken();
            mLastActionTime = System.currentTimeMillis();
        }
        url=new StringBuilder();
        url.append(https_path);
        url.append("institute");
        Request request = new Request.Builder().addHeader("Authorization", hashMap.get("token_type") + " " + hashMap.get("access_token"))
                .url(url.toString()).build();
        // 3.通过客户端执行请求,获得response
        Response response = client.newCall(request).execute();
        String str = response.body().string();
        gson = new Gson();
        if (response.isSuccessful()) {
             institutebean =gson.fromJson(str,Institutebean.class);
            if(institutebean.status == 0){
                return institutebean;
            }
        }
        return null;
    }


//    public static HashMap<String, Object> devicesdata(String id, String type) throws Exception {
//        if (System.currentTimeMillis() - mLastActionTime > ((int) hashMap.get("expires_in") * 1000)) {
//            refreshtoken();
//            mLastActionTime = System.currentTimeMillis();
//        }
//        url = new StringBuilder();
//        url.append(http_path+"data/");
//        url.append(id);
//        url.append("?type=" + type);
//        Request request = new Request.Builder().addHeader("Authorization", hashMap.get("token_type") + " " + hashMap.get("access_token"))
//                .url(url.toString()).build();
//        // 3.通过客户端执行请求,获得response
//        Response response = client.newCall(request).execute();
//        String str = response.body().string();
//        if (response.isSuccessful()) {
//            hashMap.put("str", str);
//        }
//        return hashMap;
//    }


    public static Devicebean deviceinfo(String id) throws Exception {
        if (System.currentTimeMillis() - mLastActionTime > ((int) hashMap.get("expires_in") * 1000)) {
            refreshtoken();
            mLastActionTime = System.currentTimeMillis();
        }
        url = new StringBuilder();
        url.append(http_path+"data/"+id);
        Request request = new Request.Builder().addHeader("Authorization", hashMap.get("token_type") + " " + hashMap.get("access_token"))
                .url(url.toString()).build();
        // 3.通过客户端执行请求,获得response
        Response response = client.newCall(request).execute();
        String str = response.body().string();
        Gson gson = new Gson();
        if (response.isSuccessful()) {
            Devicebean devicebean =gson.fromJson(str,Devicebean.class);
            if(devicebean.status == 0) {
                return devicebean;
            }
        }
        return null;
    }
}
