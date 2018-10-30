package smart.app;


import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
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

    public static boolean authorize(String username, String password) throws Exception {
        String path = "https://www.oriwater.cn/api/user/authorize";
        // 1.声明okhttp客户端
        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(createSSLSocketFactory())
                .hostnameVerifier(new TrustAllHostnameVerifier())//设置忽略安全证书验证
                .build();
        RequestBody formBody = new FormBody.Builder().add("grant_type", "password").add("username", username)
                .add("password", password).build();
        // 2.构造request,声明构造器 ,设置请求方式,设置请求参数
        Request request = new Request.Builder().url(path).post(formBody).build();
        // 3.通过客户端执行请求,获得response
        Response response = client.newCall(request).execute();
        String res = response.body().string();
        if (response.isSuccessful()) {
            JSONObject jsonobject = new JSONObject(res);
            hashMap.put("access_token", jsonobject.getString("access_token"));
            hashMap.put("refresh_token", jsonobject.getString("refresh_token"));
            hashMap.put("token_type", jsonobject.getString("token_type"));
            hashMap.put("expires_in", jsonobject.getInt("expires_in"));
            mLastActionTime = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }

    public static void refreshtoken() throws Exception {
        String path = "https://www.oriwater.cn/api/user/authorize";
        OkHttpClient client1 = new OkHttpClient.Builder()
                .sslSocketFactory(createSSLSocketFactory())
                .hostnameVerifier(new TrustAllHostnameVerifier())//设置忽略安全证书验证
                .build();
        RequestBody formBody1 = new FormBody.Builder().add("grant_type", "refresh_token")
                .add("refresh_token", (String) hashMap.get("refresh_token")).build();
        Request request1 = new Request.Builder().url(path).post(formBody1).build();
        Response response1 = client1.newCall(request1).execute();
        String str1 = response1.body().string();
        if (response1.isSuccessful()) {
            hashMap.put("access_token", new JSONObject(str1).getString("access_token"));
            hashMap.put("token_type", new JSONObject(str1).getString("token_type"));
            hashMap.put("expires_in", new JSONObject(str1).getInt("expires_in"));
            hashMap.put("refresh_token", new JSONObject(str1).getString("refresh_token"));
        }
    }

    public static HashMap<String, Object> accountinfo() throws Exception {
        if (System.currentTimeMillis() - mLastActionTime > (int) hashMap.get("expires_in") * 1000) {
            refreshtoken();
            mLastActionTime = System.currentTimeMillis();
        }
        String account_url = "https://www.oriwater.cn/api/user/account";
        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(createSSLSocketFactory())
                .hostnameVerifier(new TrustAllHostnameVerifier())//设置忽略安全证书验证
                .build();
        Request request = new Request.Builder().addHeader("Authorization", hashMap.get("token_type") + " " + hashMap.get("access_token"))
                .url(account_url).build();
        // 3.通过客户端执行请求,获得response
        Response response = client.newCall(request).execute();
        String str = response.body().string();
        JSONObject jsonobject = new JSONObject(str);
        JSONObject jsonobject1 = new JSONObject(jsonobject.getString("data"));
        if (response.isSuccessful()) {
            hashMap.put("username", jsonobject1.getString("UserName"));
            hashMap.put("firstname", jsonobject1.getString("FirstName"));
            hashMap.put("lastname", jsonobject1.getString("LastName"));
            hashMap.put("name", jsonobject1.getString("LastName") + jsonobject1.getString("FirstName"));
            hashMap.put("nickname", jsonobject1.getString("NickName"));
            hashMap.put("companyname", jsonobject1.getString("Company"));
            hashMap.put("departmentname", jsonobject1.getString("Department"));
        }
        return hashMap;
    }

    public static HashMap<String, Object> authorizeaccess(String firstname, String lastname, String nickname, String companyname, String departmentname) throws Exception {
        if (System.currentTimeMillis() - mLastActionTime > (int) hashMap.get("expires_in") * 1000) {
            refreshtoken();
            mLastActionTime = System.currentTimeMillis();
        }
        String account_url = "https://www.oriwater.cn/api/user/account";
        OkHttpClient accountclient = new OkHttpClient.Builder()
                .sslSocketFactory(createSSLSocketFactory())
                .hostnameVerifier(new TrustAllHostnameVerifier())//设置忽略安全证书验证
                .build();
        RequestBody formBody = new FormBody.Builder().add("LastName", lastname).add("FirstName", firstname).add("NickName", nickname)
                .add("Company", companyname).add("Department", departmentname).build();
        Request accountrequest = new Request.Builder().addHeader("Authorization", hashMap.get("token_type") + " " + hashMap.get("access_token"))
                .url(account_url).post(formBody).build();
        // 3.通过客户端执行请求,获得response
        Response accountresponse = accountclient.newCall(accountrequest).execute();
        String accountstr = accountresponse.body().string();
        if (accountresponse.isSuccessful()) {
            hashMap.put("str", accountstr);
        }
        return hashMap;
    }

    public static HashMap<String, Object> institutelists() throws Exception {
        if (System.currentTimeMillis() - mLastActionTime > (int) hashMap.get("expires_in") * 1000) {
            refreshtoken();
            mLastActionTime = System.currentTimeMillis();
        }
        String account_url = "http://www.oriwater.cn/api/institute";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().addHeader("Authorization", hashMap.get("token_type") + " " + hashMap.get("access_token"))
                .url(account_url).build();
        // 3.通过客户端执行请求,获得response
        Response response = client.newCall(request).execute();
        String str = response.body().string();
        if (response.isSuccessful()) {
            hashMap.put("str", str);
        } else {
            throw new IOException("Unexpected code " + response);
        }
        return hashMap;
    }

    public static <T> T JsonToObject(String json, Class<T> type) {
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }


    public static HashMap<String, Object> devicesdata(String id, String type) throws Exception {
        if (System.currentTimeMillis() - mLastActionTime > ((int) hashMap.get("expires_in") * 1000)) {
            refreshtoken();
            mLastActionTime = System.currentTimeMillis();
        }
        String account_url = "http://www.oriwater.cn/api/data/";
        StringBuilder url = new StringBuilder();
        url.append(account_url);
        url.append(id);
        url.append("?type=" + type);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().addHeader("Authorization", hashMap.get("token_type") + " " + hashMap.get("access_token"))
                .url(url.toString()).build();
        // 3.通过客户端执行请求,获得response
        Response response = client.newCall(request).execute();
        String str = response.body().string();
        if (response.isSuccessful()) {
            hashMap.put("str", str);
        }
        return hashMap;
    }

    private static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ssfFactory;
    }
}
