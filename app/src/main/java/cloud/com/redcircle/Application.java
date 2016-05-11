package cloud.com.redcircle;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import io.rong.imkit.RongIM;

/**
 * Created by zhan on 16/4/21.
 */
public class Application extends android.app.Application {


    public static final String MOB_APPKEY = "11f391ad7b6fd";
    public static final String MOB_APPSECRET = "2d111f4bb7f9cd61815ca519bb63a2fc";

    private static Application mContext;
    private Boolean verifyResult;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        /**
         * 初始化融云
         */
        RongIM.init(this);

    }


    public Boolean getVerifyResult() {
        return verifyResult;
    }

    public void setVerifyResult(Boolean verifyResult) {
        this.verifyResult = verifyResult;
    }

    public static Application getInstance() {
        return mContext;
    }


}
