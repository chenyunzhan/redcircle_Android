package cloud.com.redcircle;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import cloud.com.redcircle.api.HttpRequestHandler;
import cloud.com.redcircle.api.RedCircleManager;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by zhan on 16/6/2.
 */
public class RongCloudEvent implements RongIM.UserInfoProvider {

    private static RongCloudEvent mRongCloudInstance;
    private Context mContext;



    /**
     * 初始化 RongCloud.
     *
     * @param context 上下文。
     */
    public static void init(Context context) {

        if (mRongCloudInstance == null) {

            synchronized (RongCloudEvent.class) {

                if (mRongCloudInstance == null) {
                    mRongCloudInstance = new RongCloudEvent(context);
                }
            }
        }
    }

    /**
     * 构造方法。
     *
     * @param context 上下文。
     */
    private RongCloudEvent(Context context) {
        mContext = context;

    }

    /**
     * 获取RongCloud 实例。
     *
     * @return RongCloud。
     */
    public static RongCloudEvent getInstance() {
        return mRongCloudInstance;
    }


    /**
     * RongIM.init(this) 后直接可注册的Listener。
     */
    public void setUserInfoProviderListener() {
        RongIM.setUserInfoProvider(this,true);


    }

    @Override
    public UserInfo getUserInfo(String s) {

        final UserInfo[] userInfo = {null};

        RedCircleManager.getUserInfoById(mContext, s, new HttpRequestHandler<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String name = response.getString("name");
                    String mePhone = response.getString("mePhone");

                    String showName;
                    if (name.length() > 0) {
                        showName = name;
                    } else {
                        showName = response.getString("mePhone");
                    }

                    userInfo[0] = new UserInfo(mePhone, showName, Uri.parse(RedCircleManager.HTTP_BASE_URL + "/downPhotoByPhone?mePhone=" + mePhone + "&random=" + Math.random()));

//                    RedCircleManager.userInfo = new UserInfo(response.getString("mePhone"),response.getString("name"), Uri.parse(response.getString("")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(JSONObject data, int totalPages, int currentPage) {

            }

            @Override
            public void onFailure(String error) {

            }
        });
//        RongIM.getInstance().refreshUserInfoCache(userInfo);
        return userInfo[0];
    }
}
