package cloud.com.redcircle;


import android.app.ActivityManager;
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
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imlib.ipc.RongExceptionHandler;

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



        initImageLoader(getApplicationContext());




        /**
         * 注意：
         *
         * IMKit SDK调用第一步 初始化
         *
         * context上下文
         *
         * 只有两个进程需要初始化，主进程和 push 进程
         */
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {

            RongIM.init(this);


            /**
             * 融云SDK事件监听处理
             *
             * 注册相关代码，只需要在主进程里做。
             */
            if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {

                RongCloudEvent.init(this);

                Thread.setDefaultUncaughtExceptionHandler(new RongExceptionHandler(this));

                try {
//                    RongIM.registerMessageType(AgreedFriendRequestMessage.class);
//
//                    RongIM.registerMessageTemplate(new ContactNotificationMessageProvider());
//                    RongIM.registerMessageTemplate(new RealTimeLocationMessageProvider());
//                    //@ 消息模板展示
//                    RongContext.getInstance().registerConversationTemplate(new NewDiscussionConversationProvider());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

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


    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

}
