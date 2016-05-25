package cloud.com.redcircle;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

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
        initImageLoader(getApplicationContext());

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

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }


}
