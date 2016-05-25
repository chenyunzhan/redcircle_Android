package cloud.com.redcircle.api;

import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cloud.com.redcircle.Application;
import cloud.com.redcircle.utils.AccountUtils;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.entity.StringEntity;
import io.rong.imlib.model.UserInfo;

/**
 * Created by zhan on 16/4/22.
 */
public class RedCircleManager {

    private static Application mApp = Application.getInstance();

    private static AsyncHttpClient sClient = null;
//    public static final String HTTP_BASE_URL = "http://redcircle.tiger.mopaasapp.com";
    public static final String HTTP_BASE_URL = "http://192.168.1.101:8080";

//    public static final String HTTP_BASE_URL = "http://10.0.2.2:8080";

    protected static final String ACTIVITY_TAG = "MyAndroid";


    public static final String LOGIN_IN_URL = HTTP_BASE_URL + "/login";

    public static final String UPLOAD_PHOTO_URL = HTTP_BASE_URL + "/uploadPhoto";

    public static final String MODIFY_USER_URL = HTTP_BASE_URL + "/modify";

    public static final String ADD_FRIEND_URL = HTTP_BASE_URL + "/addFriend";

    public static final String REGISTER_URL = HTTP_BASE_URL + "/register";

    public static final String FRIENDS_URL = HTTP_BASE_URL + "/getFriends";

    public static final String RONG_CLOUD_KEY_URL = HTTP_BASE_URL + "/getRongCloudToken";

    private static UserInfo userInfo;


    /**
     * 使用用户名密码登录
     *
     * @param cxt
     * @param phone            用户电话
     * @param verificationCode 验证码
     * @param handler          返回结果处理
     */
    public static void loginWithUsername(final Context cxt, final String phone, final String verificationCode,
                                         final HttpRequestHandler<JSONObject> handler) {

        AsyncHttpClient client = new AsyncHttpClient();
        StringEntity entity = new StringEntity("{\"mePhone\":\"" + phone + "\"}", "UTF-8");

        client.post(cxt, LOGIN_IN_URL, entity, "application/json", new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e(RedCircleManager.ACTIVITY_TAG, "1");
                SafeHandler.onSuccess(handler, response);


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(RedCircleManager.ACTIVITY_TAG, "5");

            }
        });


    }


    public static void registerAccount(Context cxt, JSONObject friendArrayMap, final HttpRequestHandler<JSONObject> handler) {
        AsyncHttpClient client = new AsyncHttpClient();
//        StringEntity entity = new StringEntity("{\"friendArrayMap\":[{\"phone_text\":\"\"}],\"meInfo\":{\"me_phone\":\"11\"}}","UTF-8");
        StringEntity entity = new StringEntity(friendArrayMap.toString(), "UTF-8");

        client.post(cxt, REGISTER_URL, entity, "application/json", new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e(RedCircleManager.ACTIVITY_TAG, "1");
                SafeHandler.onSuccess(handler, response);


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(RedCircleManager.ACTIVITY_TAG, "5");

            }
        });
    }


    public static void addFriend(final Context cxt, final String mePhone, final String friendPhone, final HttpRequestHandler<JSONObject> handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        StringEntity entity = new StringEntity("{\"mePhone\":\"" + mePhone + "\", \"friendPhone\":\"" + friendPhone + "\"}", "UTF-8");

        client.post(cxt, ADD_FRIEND_URL, entity, "application/json", new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e(RedCircleManager.ACTIVITY_TAG, "1");
                SafeHandler.onSuccess(handler, response);


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(RedCircleManager.ACTIVITY_TAG, "5");

            }
        });
    }


    public static void modifyUser(final Context cxt, final String name, final String value, final HttpRequestHandler<JSONObject> handler) {
        AsyncHttpClient client = new AsyncHttpClient();

        StringEntity entity = null;

        JSONObject mUser = AccountUtils.readLoginMember(cxt);
        String mePhone = null;
        try {
            mePhone = mUser.getString("mePhone");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if ("姓名".equals(name)) {
            entity = new StringEntity("{\"mePhone\":\"" + mePhone + "\", \"name\":\"" + value + "\"}", "UTF-8");
        } else if ("性别".equals(name)) {
            entity = new StringEntity("{\"mePhone\":\"" + mePhone + "\", \"sex\":\"" + value + "\"}", "UTF-8");
        }

        client.post(cxt, MODIFY_USER_URL, entity, "application/json", new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e(RedCircleManager.ACTIVITY_TAG, "1");


                JSONObject mUser = AccountUtils.readLoginMember(cxt);
                String mePhone = null;
                try {
                    mePhone = mUser.getString("mePhone");
                } catch (JSONException e) {
                    e.printStackTrace();

                }
                RedCircleManager.loginWithUsername(cxt, mePhone, "", handler);


//                SafeHandler.onSuccess(handler,response);


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(RedCircleManager.ACTIVITY_TAG, "5");

            }
        });
    }


    /**
     * @param userId
     * @return
     */
    public static UserInfo getUserInfoById(Context ctx, String userId) {
//
//        StringEntity entity = new StringEntity("{\"mePhone\":\"" + userId +"\"}","UTF-8");
//
//        SyncHttpClient client = new SyncHttpClient();
//
//
//        client.post(ctx,LOGIN_IN_URL,entity,"application/json", new JsonHttpResponseHandler(){
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);
//                try {
//                    RedCircleManager.userInfo = new UserInfo(response.getString("mePhone"),response.getString("name"), Uri.parse(response.getString("")));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                super.onFailure(statusCode, headers, throwable, errorResponse);
//            }
//        });
//
//
//
//        return RedCircleManager.userInfo;


        String result = HttpUtils.sendPostMessage(null, "{\"mePhone\":\"" + userId + "\"}", "UTF-8");

        try {
            JSONObject jsonObject = new JSONObject(result);
            String name = jsonObject.getString("name");
            String showName;
            if (name.length() > 0) {
                showName = name;
            } else {
                showName = jsonObject.getString("mePhone");
            }
            UserInfo userInfo = new UserInfo(jsonObject.getString("mePhone"), showName, Uri.parse(""));

            return userInfo;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;


    }


    //获取所有朋友
    public static void getAllFriends(Context ctx, String mePhone,
                                     final HttpRequestHandler<JSONArray> handler) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("mePhone", mePhone);

        client.get(ctx, FRIENDS_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                SafeHandler.onSuccess(handler, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

    }


    //获取融云token
    public static void getRongCloudToken(Context ctx, String mePhone, String name,
                                         final HttpRequestHandler<JSONObject> handler) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("mePhone", mePhone);
        params.put("name", name);

        client.get(ctx, RONG_CLOUD_KEY_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    int code = response.getInt("code");
                    JSONObject result = response.getJSONObject("result");
                    if (code == 200) {
                        SafeHandler.onSuccess(handler, result);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

    }


    public static String getBaseUrl() {
        return HTTP_BASE_URL;
    }

    private static void requestOnceWithURLString(final Context cxt, String url,
                                                 final HttpRequestHandler<String> handler) {
        AsyncHttpClient client = getClient(cxt);
        client.addHeader("Referer", getBaseUrl());
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = new String(responseBody);
                String once = getOnceStringFromHtmlResponseObject(content);
                if (once != null)
                    SafeHandler.onSuccess(handler, once);
                else
                    SafeHandler.onFailure(handler, RedCircleErrorType.errorMessage(cxt, RedCircleErrorType.ErrorNoOnceAndNext));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                SafeHandler.onFailure(handler, RedCircleErrorType.errorMessage(cxt, RedCircleErrorType.ErrorNoOnceAndNext));
            }
        });
//        client.get(url, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, PreferenceActivity.Header[] headers, byte[] responseBody) {
//                String content = new String(responseBody);
//                String once = getOnceStringFromHtmlResponseObject(content);
//                if (once != null)
//                    SafeHandler.onSuccess(handler, once);
//                else
//                    SafeHandler.onFailure(handler, RedCircleErrorType.errorMessage(cxt, RedCircleErrorType.ErrorNoOnceAndNext));
//            }
//
//            @Override
//            public void onFailure(int statusCode, PreferenceActivity.Header[] headers, byte[] responseBody, Throwable error) {
//                SafeHandler.onFailure(handler, RedCircleErrorType.errorMessage(cxt, RedCircleErrorType.ErrorNoOnceAndNext));
//            }
//        });
    }


    private static AsyncHttpClient getClient(Context context) {
        return getClient(context, true);
    }

    private static String getOnceStringFromHtmlResponseObject(String content) {
        Pattern pattern = Pattern.compile("<input type=\"hidden\" value=\"([0-9]+)\" name=\"once\" />");
        final Matcher matcher = pattern.matcher(content);
        if (matcher.find())
            return matcher.group(1);
        return null;
    }


    private static AsyncHttpClient getClient(Context context, boolean mobile) {
        if (context == null)
            context = mApp.getBaseContext();

        if (sClient == null) {
            sClient = new AsyncHttpClient();
            sClient.setEnableRedirects(false);
            sClient.setCookieStore(new PersistentCookieStore(context));
            sClient.addHeader("Accept", "application/json; charset=utf-8");
            sClient.addHeader("Content-type", "application/json; charset=utf-8");

//            sClient.addHeader("Cache-Control", "max-age=0");
//            sClient.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//            sClient.addHeader("Accept-Charset", "utf-8, iso-8859-1, utf-16, *;q=0.7");
//            sClient.addHeader("Accept-Language", "zh-CN, en-US");
//            sClient.addHeader("Host", "www.v2ex.com");
        }

        if (mobile) {
//            sClient.addHeader("X-Requested-With", "com.android.browser");
//            sClient.setUserAgent("Mozilla/5.0 (Linux; U; Android 4.2.1; en-us; M040 Build/JOP40D) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
        } else {
//            sClient.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
        }

        return sClient;
    }


    /**
     * 退出登录
     *
     * @param context
     */
    public static void logout(Context context) {
        PersistentCookieStore cookieStore = new PersistentCookieStore(context);
        cookieStore.clear();
    }


    /**
     * @param path 要上传的文件路径
     * @param name  服务端接收URL
     * @throws Exception
     */
    public static void uploadFile(final Context mContext, String path, String name) throws Exception {
        String url = UPLOAD_PHOTO_URL;
        File file = new File(path);
        if (file.exists() && file.length() > 0) {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("name", name);
            params.put("file", file);

            // 上传文件
            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers,
                                      byte[] responseBody) {
                    // 上传成功后要做的工作
                    Toast.makeText(mContext, "上传成功", Toast.LENGTH_LONG).show();
//                    progress.setProgress(0);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers,
                                      byte[] responseBody, Throwable error) {
                    // 上传失败后要做到工作
                    Toast.makeText(mContext, "上传失败", Toast.LENGTH_LONG).show();
                }


                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    super.onProgress(bytesWritten, totalSize);
                    int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
                    // 上传进度显示
//                    progress.setProgress(count);
                    Log.e("上传 Progress>>>>>", bytesWritten + " / " + totalSize);
                }


                @Override
                public void onRetry(int retryNo) {
                    // TODO Auto-generated method stub
                    super.onRetry(retryNo);
                    // 返回重试次数
                }

            });
        } else {
            Toast.makeText(mContext, "文件不存在", Toast.LENGTH_LONG).show();
        }

    }
}