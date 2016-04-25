package cloud.com.redcircle.api;

import android.content.Context;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cloud.com.redcircle.Application;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by zhan on 16/4/22.
 */
public class RedCircleManager {

    private static Application mApp = Application.getInstance();

    private static AsyncHttpClient sClient = null;
    public static final String HTTP_BASE_URL = "http://redcircle.tiger.mopaasapp.com";
//    public static final String HTTP_BASE_URL = "http://192.168.1.101:8080";

    protected static final String ACTIVITY_TAG="MyAndroid";



    public static final String LOGIN_IN_URL = HTTP_BASE_URL + "/login";


    /**
     * 使用用户名密码登录
     *
     * @param cxt
     * @param phone 用户电话
     * @param verificationCode 验证码
     * @param handler  返回结果处理
     */
    public static void loginWithUsername(final Context cxt, final String phone, final String verificationCode,
                                         final HttpRequestHandler<JSONObject> handler) {

        AsyncHttpClient client = new AsyncHttpClient();

        StringEntity entity = new StringEntity("{\"mePhone\":\"" + phone +"\"}","UTF-8");

        Map paramMap = new HashMap();
        paramMap.put("mePhone",phone);

        client.post(cxt,LOGIN_IN_URL,entity,"application/json",new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e(RedCircleManager.ACTIVITY_TAG,"1");
                SafeHandler.onSuccess(handler,response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(RedCircleManager.ACTIVITY_TAG,"5");

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
            sClient.addHeader("Accept","application/json; charset=utf-8");
            sClient.addHeader("Content-type","application/json; charset=utf-8");

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

}
