package cloud.com.redcircle;


import android.util.Log;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by zhan on 16/4/21.
 */
public class Application extends android.app.Application {

    private static final String APPKEY = "11f391ad7b6fd";
    private static final String APPSECRET = "2d111f4bb7f9cd61815ca519bb63a2fc";
    protected static final String ACTIVITY_TAG="MyAndroid";


    @Override
    public void onCreate() {
        super.onCreate();

        SMSSDK.initSDK(this,APPKEY,APPSECRET);
        EventHandler eh=new EventHandler(){

            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        Log.i(Application.ACTIVITY_TAG, "first activity--------onResume()");

                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功




                    }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        //返回支持发送验证码的国家列表
                    }
                }else{
                    ((Throwable)data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调

    }



}
