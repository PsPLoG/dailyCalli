package com.psplog.dailycalli.http;

import android.util.Log;

import com.psplog.dailycalli.item.Cali_Item;
import com.psplog.dailycalli.item.CalliContent_Item;
import com.psplog.dailycalli.item.CalliLearn_Item;
import com.psplog.dailycalli.item.CalliLearn_SubItem;
import com.psplog.dailycalli.item.Comment_item;
import com.psplog.dailycalli.item.ProfileItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TlsVersion;

public class HttpClient {
    public static ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_2)
            .cipherSuites(
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
            .build();
    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectionSpecs(Collections.singletonList(spec))
            .build();
    public static String serverIp = "https://dailycalli.tk";
    public static JSONObject jArr = null;
    public static String email;
    public static String user_id;
    public static String user_nickname;
    public static String token;


    public OkHttpClient getInstance() {
        return client;
    }

    public static String signup(String email, String pw, String nickname) {
        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("pwd", pw)
                .add("nickname", nickname)
                .build();
        Request request = new Request.Builder()
                .url(serverIp + "/users/signup")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String ret = response.body().string();
            Log.d("Login response", ret);
            jArr = new JSONObject(ret);
            if (jArr.getString("msg").equals("fail"))
                return "fail";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

    public static String checkEmail(String email) {
        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .build();
        Request request = new Request.Builder()
                .url(serverIp + "/users/signup/email")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String ret = response.body().string();
            Log.d("checkEmail response", ret);
            jArr = new JSONObject(ret);
            return jArr.getString("msg");
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    public static String checkNickname(String nickname) {
        RequestBody formBody = new FormBody.Builder()
                .add("nickname", nickname)
                .build();
        Request request = new Request.Builder()
                .url(serverIp + "/users/signup/email")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String ret = response.body().string();
            Log.d("checkEmail response", ret);
            jArr = new JSONObject(ret);
            return jArr.getString("msg");
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    public String login(String email, String pw) {
        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("pwd", pw)
                .build();
        Request request = new Request.Builder()
                .url(serverIp + "/users/login")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            Log.d("Login response", res);
            jArr = new JSONObject(res);
            if (jArr.getString("msg").equals("fail"))
                return "fail";
            JSONObject tmp = jArr.getJSONObject("userInfo");
            this.email = tmp.getString("email");
            user_id = tmp.getString("user_id");
            user_nickname = tmp.getString("user_nickname");
            token = tmp.getString("token");
            return jArr.getString("msg");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "fail";
    }

    public String login(String token) {
        RequestBody formBody = new FormBody.Builder()
                .add("usertoken", token)
                .build();
        Request request = new Request.Builder()
                .url(serverIp + "/tokenCheck")
                .addHeader("usertoken", token)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            Log.d("token Login response", res);
            jArr = new JSONObject(res);
            if (jArr.getString("msg").equals("fail"))
                return "fail";
            JSONObject tmp = jArr.getJSONObject("userInfo");
            this.email = tmp.getString("email");
            user_id = tmp.getString("user_id");
            user_nickname = tmp.getString("user_nickname");
            this.token = tmp.getString("token");
            return jArr.getString("msg");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "fail";
    }

    static List<Cali_Item> getMainCalliJSON(String name) {
        List<Cali_Item> list = new ArrayList<Cali_Item>();
        try {
            JSONArray tmp = jArr.getJSONArray(name);

            Log.d("asd", "name :" + tmp.length() + "tmp");
            for (int j = 0; j < 4; j++) {
                if (tmp.length() > 4) {
                    JSONObject t = tmp.getJSONObject(j);
                    String img="";
                    if(!t.isNull("user_img"))
                        img=t.getString("user_img");

                    list.add(new Cali_Item(0,
                            t.getInt("calli_id"),
                            "",
                            t.getString("calli_img"),
                            "",img));
                } else if (j < tmp.length()) {
                    JSONObject t = tmp.getJSONObject(j);
                    String img="";
                    if(!t.isNull("user_img"))
                        img=t.getString("user_img");
                    list.add(new Cali_Item(0,
                            t.getInt("calli_id"),
                            "",
                            t.getString("calli_img"),
                            "",img));
                } else {
                    list.add(new Cali_Item(-1,
                            -1,
                            "",
                            "",
                            "",""));
                }
            }
            for (int j = 0; j < list.size(); j++) {
                Log.d("asd", name + "id :" + list.get(j).getCalli_id() + " link : " + list.get(j).getCalli_img());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return list;
        }
        return list;
    }

    public static List<Cali_Item> getCalliList() {
        List<Cali_Item> list = new ArrayList<Cali_Item>();
        Request request = new Request.Builder().addHeader("usertoken", token)
                .url(serverIp + "/calli/mainList")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            Log.d("getCalliList response", res);
            jArr = new JSONObject(res);
            if (jArr.getString("msg").equals("fail"))
                return list;
//            Log.d("asd",tmp.length()+"tmp");
//            for (int j = 0; j < tmp.length(); j++) {
//                JSONObject t = tmp.getJSONObject(j);
//                //(int user_id, int calli_id, String user_nickname, String calli_img, String calli_txt)
//                list.add(new Cali_Item(t.getInt("user_id"),
//                        t.getInt("calli_id"),
//                        "",
//                        t.getString("calli_img"),
//                        ""));
//            }

            list.addAll(getMainCalliJSON("drawTopList"));
            list.addAll(getMainCalliJSON("learnTopList"));
            list.addAll(getMainCalliJSON("newList"));
            list.addAll(getMainCalliJSON("recomList"));

        } catch (Exception e) {

            e.printStackTrace();

        }

        Log.d("asd", list.size() + "");
        return list;
    }

    public static List<Cali_Item> getCalliList(int getParam) {
        List<Cali_Item> list = new ArrayList<Cali_Item>();
        RequestBody formBody = new FormBody.Builder()
                .add("order", getParam + "")
                .build();

        Request request = new Request.Builder().addHeader("usertoken", token)
                .url(serverIp + "/calli/calliList")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            Log.d("getCalliList response", res);
            jArr = new JSONObject(res);
            if (jArr.getString("msg").equals("fail"))
                return list;
            JSONArray tmp = jArr.getJSONArray("calliList");
            Log.d("asd", tmp.length() + "tmp");
            for (int j = 0; j < tmp.length(); j++) {

                Log.d("asd", j + "j");
                JSONObject t = tmp.getJSONObject(j);
                //(int user_id, int calli_id, String user_nickname, String calli_img, String calli_txt)
                list.add(new Cali_Item(t.getInt("user_id"),
                        t.getInt("calli_id"),
                        t.getString("user_nickname"),
                        t.getString("calli_img"),
                        t.getString("calli_title"),t.getString("user_img")));
            }

        } catch (Exception e) {

            e.printStackTrace();

        }
        Log.d("asd", list.size() + "");
        return list;
    }

    public static CalliContent_Item getCalliContent(int calli_id) {
        CalliContent_Item list = null;

        Request request = new Request.Builder().addHeader("usertoken", token)
                .url(serverIp + "/calli/detail/" + calli_id)
                .get()
                .build();

        Log.d("getCalliConte response", serverIp + "/calli/detail/" + calli_id);
        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            Log.d("getCalliConte response", res);
            jArr = new JSONObject(res);
            if (jArr.getString("msg").equals("fail"))
                return list;
            JSONObject tmp = jArr.getJSONObject("calliResult");
            list = new CalliContent_Item(tmp.getInt("calli_id"),
                    tmp.getString("calli_img"),
                    tmp.getString("calli_title"),
                    tmp.getString("calli_txt"),
                    tmp.getString("calli_date"),
                    tmp.getString("calli_tag"),
                    tmp.getString("calliBool"),
                    tmp.getString("user_nickname"),
                    tmp.getString("user_img"),
                    tmp.getInt("commentCount"),
                    tmp.getInt("likeCount"),
                    tmp.getString("likeBool"),
                    tmp.getInt("user_id"));
        } catch (Exception e) {

            e.printStackTrace();
        }
        return list;
    }

    //{ "msg": "success", "comList": [
    // { "com_id": 1, "user_id": 2, "com_txt": "와 정말 멋진 작품이에요!", "com_date": "2018-07-22T00:36:15.000Z", "com_parent": 0, "com_seq": 1, "user_nickname": null, "user_img": null },
    //
    public static List<Comment_item> getCommentList(int calli_id) {
        List<Comment_item> list = new ArrayList<Comment_item>();
        RequestBody formBody = new FormBody.Builder()
                .add("calli_id", calli_id + "")
                .build();

        Request request = new Request.Builder().addHeader("usertoken", token)
                .url(serverIp + "/comment/list")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            Log.d("getCommentList response", res);
            jArr = new JSONObject(res);
            if (jArr.getString("msg").equals("fail"))
                return list;
            JSONArray tmp = jArr.getJSONArray("comList");
            for (int j = 0; j < tmp.length(); j++) {
                JSONObject t = tmp.getJSONObject(j);
                list.add(new Comment_item(t.getInt("com_id"),
                        t.getInt("user_id"),
                        t.getString("com_txt"),
                        t.getString("com_date"),
                        t.getInt("com_parent"),
                        t.getInt("com_seq"),
                        t.getString("user_nickname"),
                        t.getString("user_img")));
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
        Log.d("asd", list.size() + "");
        return list;
    }

    //    public CalliLearn_Item(ArrayList<CalliLearn_SubItem> subItem, String guide_title, int guide_id, String guide_img, ArrayList<String> guide_tag) {
//        this.subItem = subItem;
//        this.guide_title = guide_title;
//        this.guide_id = guide_id;
//        this.guide_img = guide_img;
//        this.guide_tag = guide_tag;
//    }
    public static List<CalliLearn_Item> getLearnList() {
        List<CalliLearn_Item> list = new ArrayList<CalliLearn_Item>();
        RequestBody formBody = new FormBody.Builder()
                .build();

        Request request = new Request.Builder().addHeader("usertoken", token)
                .url(serverIp + "/calli/besttrace")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            Log.d("getLearnList response", res);
            jArr = new JSONObject(res);
            if (jArr.getString("msg").equals("fail"))
                return list;

            JSONArray arrTmp = jArr.getJSONArray("list");
            Log.d("asd", arrTmp.length() + "leadsad");
            for (int j = 0; j < arrTmp.length(); j++) {
                ArrayList<String> tag = new ArrayList<String>();
                ArrayList<CalliLearn_SubItem> subitem = new ArrayList<CalliLearn_SubItem>();
                JSONArray arr = arrTmp.getJSONObject(j).getJSONArray("guide_tag");
                for (int i = 0; i < arr.length(); i++)
                    tag.add(arr.getString(i));

                arr = arrTmp.getJSONObject(j).getJSONArray("trace");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject tmp = arr.getJSONObject(i);
                    subitem.add(new CalliLearn_SubItem(tmp.getInt("calli_id"), tmp.getString("calli_img")));
                }
                list.add(new CalliLearn_Item(subitem, arrTmp.getJSONObject(j).getString("guide_title"),
                        arrTmp.getJSONObject(j).getInt("guide_id"),
                        arrTmp.getJSONObject(j).getString("guide_img"),
                        tag, null));
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
        Log.d("asd", list.size() + "");
        return list;
    }

    public static CalliLearn_Item getLearnContent(int calli_id) {
        CalliLearn_Item list = null;
        RequestBody formBody = new FormBody.Builder().add("guide_id", calli_id + "")
                .build();
        Request request = new Request.Builder().addHeader("usertoken", token)
                .url(serverIp + "/calli/traceDetail")
                .post(formBody)
                .build();

        Log.d("getCalliConte response", serverIp + "/calli/detail/" + calli_id);
        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            Log.d("getCalliConte response", res);
            jArr = new JSONObject(res);
            if (jArr.getString("msg").equals("fail"))
                return list;

            JSONObject arrTmp = jArr.getJSONObject("list");
            ArrayList<String> tag = new ArrayList<String>();
            ArrayList<CalliLearn_SubItem> subitem = new ArrayList<CalliLearn_SubItem>();
            JSONArray arr = arrTmp.getJSONArray("guide_tag");
            for (int i = 0; i < arr.length(); i++)
                tag.add(arr.getString(i));

            arr = arrTmp.getJSONArray("trace");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject tmp = arr.getJSONObject(i);
                subitem.add(new CalliLearn_SubItem(tmp.getInt("calli_id"), tmp.getString("calli_img")));
            }
            list = new CalliLearn_Item(subitem, arrTmp.getString("guide_title"),
                    arrTmp.getInt("guide_id"),
                    arrTmp.getString("guide_img"),
                    tag, arrTmp.getString("guide_file"));

        } catch (Exception e) {

            e.printStackTrace();
        }
        return list;
    }

    public

    static List<Cali_Item> getMyCallijList() {
        List<Cali_Item> list = new ArrayList<Cali_Item>();
        RequestBody formBody = new FormBody.Builder()
                .build();

        Request request = new Request.Builder().addHeader("usertoken", token)
                .url(serverIp + "/myPage/myPost")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            Log.d("getMyCalliList response", res);
            jArr = new JSONObject(res);
            if (jArr.getString("msg").equals("fail"))
                return list;
            JSONArray tmp = jArr.getJSONArray("myPostList");
            Log.d("asd", tmp.length() + "tmp");
            for (int j = 0; j < tmp.length(); j++) {
                Log.d("asd", j + "j");
                JSONObject t = tmp.getJSONObject(j);
                //(int user_id, int calli_id, String user_nickname, String calli_img, String calli_txt)
                list.add(new Cali_Item(0,
                        t.getInt("calli_id"),
                        t.getString("user_nickname"),
                        t.getString("calli_img"),
                        t.getString("calli_title"),
                        t.getString("user_img")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("asd", list.size() + "");
        return list;
    }
    public static ProfileItem getProfile() {
        ProfileItem list = new ProfileItem(-1, "", "", "", "", 0, 0, 0);
        Request request = new Request.Builder().addHeader("usertoken", token)
                .url(serverIp + "/myPage/myProfile")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            Log.d("getprofile response", res + "send token :" + token);
            jArr = new JSONObject(res);
            if (jArr.getString("msg").equals("fail"))
                return list;

            list = new ProfileItem(jArr.getInt("user_id"),
                    jArr.getString("user_img"),
                    jArr.getString("user_intro"),
                    jArr.getString("user_nickname"),
                    jArr.getString("user_email"),
                    jArr.getInt("postCount"),
                    jArr.getInt("follower"),
                    jArr.getInt("following"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public static List<Cali_Item> getOtherCallijList(int user_id) {
        List<Cali_Item> list = new ArrayList<Cali_Item>();
        RequestBody formBody = new FormBody.Builder()
                .add("user_id",user_id+"")
                .build();

        Request request = new Request.Builder().addHeader("usertoken", token)
                .url(serverIp + "/calli/userPost")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            Log.d("getMyCalliList response", res);
            jArr = new JSONObject(res);
            if (jArr.getString("msg").equals("fail"))
                return list;
            JSONArray tmp = jArr.getJSONArray("myPostList");
            Log.d("asd", tmp.length() + "tmp");
            for (int j = 0; j < tmp.length(); j++) {
                Log.d("asd", j + "j");
                JSONObject t = tmp.getJSONObject(j);
                //(int user_id, int calli_id, String user_nickname, String calli_img, String calli_txt)
                list.add(new Cali_Item(0,
                        t.getInt("calli_id"),
                        t.getString("user_nickname"),
                        t.getString("calli_img"),
                        t.getString("calli_title"),t.getString("user_img")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("asd", list.size() + "");
        return list;
    }
    public static ProfileItem getOtherProfile(int user_id) {
        ProfileItem list = new ProfileItem(-1, "", "", "", "", 0, 0, 0);
        RequestBody formBody = new FormBody.Builder()
                .add("user_id",user_id+"")
                .build();
        Request request = new Request.Builder().addHeader("usertoken", token)
                .url(serverIp + "/calli/userProfile")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            Log.d("getotheprofile response", res + "send token :" + token);
            jArr = new JSONObject(res);
            if (jArr.getString("msg").equals("fail"))
                return list;

            list = new ProfileItem(jArr.getInt("user_id"),
                    jArr.getString("user_img"),
                    jArr.getString("user_intro"),
                    jArr.getString("user_nickname"),
                    jArr.getString("user_email"),
                    jArr.getInt("postCount"),
                    jArr.getInt("follower"),
                    jArr.getInt("following"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //    static boolean uploadCalliData() {
//
//        RequestBody formBody = new MultipartBody.Builder().
//        Request request = new Request.Builder().addHeader("usertoken", token)
//                .url(serverIp + "/myPage/myProfile")
//                .get()
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            String res = response.body().string();
//            Log.d("getprofile response", res+"send token :"+token);
//            jArr = new JSONObject(res);
//            if (jArr.getString("msg").equals("fail"))
//                return list;
//
//            list = new ProfileItem(jArr.getInt("user_id"),
//                    jArr.getString("user_img"),
//                    jArr.getString("user_intro"),
//                    jArr.getString("user_nickname"),
//                    jArr.getString("user_email"),
//                    jArr.getInt("postCount"),
//                    jArr.getInt("follower"),
//                    jArr.getInt("following"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
    public static boolean setLike(int calli_id){
    RequestBody formBody = new FormBody.Builder()
            .add("calli_id", calli_id + "")
            .build();
    Request request = new Request.Builder().addHeader("usertoken", token)
            .url(serverIp + "/calli/calliLike")
            .put(formBody)
            .build();

        try (Response response = client.newCall(request).execute()) {
        String res = response.body().string();
        Log.d("sendLike response", res);
        jArr = new JSONObject(res);
        if (jArr.getString("msg").equals("fail") ||jArr.getString("msg").equals("successful unlike"))
            return false;
    } catch (Exception e) {

        e.printStackTrace();
    }
        return true;
}
    public static boolean sendComment(int calli_id, String com_txt, int com_parent) {
        RequestBody formBody = new FormBody.Builder()
                .add("calli_id", calli_id + "")
                .add("com_txt", com_txt + "")
                .add("com_parent", com_parent + "")
                .build();
        Request request = new Request.Builder().addHeader("usertoken", token)
                .url(serverIp + "/comment/write")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            Log.d("sendComment response", res);
            jArr = new JSONObject(res);
            if (jArr.getString("msg").equals("fail"))
                return false;
        } catch (Exception e) {

            e.printStackTrace();
        }
        return true;
    }
    public static List<Cali_Item> getSearchjList(String word, int order) {
        List<Cali_Item> list = new ArrayList<Cali_Item>();
        RequestBody formBody = new FormBody.Builder()
                .add("word",word)
                .add("order", order + "")
                .build();

        Request request = new Request.Builder().addHeader("usertoken", token)
                .url(serverIp + "/calli/mainSearch")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            Log.d("getCalliList response", res);
            jArr = new JSONObject(res);
            if (jArr.getString("msg").equals("fail"))
                return list;
            JSONArray tmp = jArr.getJSONArray("calliList");
            Log.d("asd", tmp.length() + "tmp");
            for (int j = 0; j < tmp.length(); j++) {

                Log.d("asd", j + "j");
                JSONObject t = tmp.getJSONObject(j);
                //(int user_id, int calli_id, String user_nickname, String calli_img, String calli_txt)
                list.add(new Cali_Item(t.getInt("user_id"),
                        t.getInt("calli_id"),
                        t.getString("user_nickname"),
                        t.getString("calli_img"),
                        t.getString("calli_title"),t.getString("user_img")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("asd", list.size() + "");
        return list;
    }
//
//    {
//        "calli_txt": "여름 맞이 캘리입니다.",
//            "calli_trace": 1,
//            "guide_id": 3,
//            "calli_title": "산들산들바람"
//        "calli_tag": ["산뜻함","따뜻함"],
//        "image": 이미지,
//            "drawData" : 파일1, 파일2, ...
//    }
public static boolean uploadCalli(String calli_txt, int calli_trace, int guide_id, String calli_title, String calli_tag, File image, File drawData, String filename) {
    RequestBody formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("calli_txt", calli_txt+"")
            .addFormDataPart("calli_trace", calli_trace+"")
            .addFormDataPart("guide_id", guide_id+"")
            .addFormDataPart("calli_title", calli_title+"")
            .addFormDataPart("calli_tag", "")
            .addFormDataPart("image","filename.jpg", RequestBody.create(MultipartBody.FORM, image))
            .addFormDataPart("drawData","filename.spd", RequestBody.create(MultipartBody.FORM, drawData))
            .build();

    Request request = new Request.Builder().addHeader("usertoken", token)
            .url(serverIp + "/calli/upload")
            .post(formBody)
            .build();

    try (Response response = client.newCall(request).execute()) {
        String res = response.body().string();
        Log.d("upload response", res);
        jArr = new JSONObject(res);
        if (jArr.getString("msg").equals("fail"))
            return false;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return true;
}
}
