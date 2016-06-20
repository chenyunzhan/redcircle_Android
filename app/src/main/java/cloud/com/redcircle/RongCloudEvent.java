package cloud.com.redcircle;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import cloud.com.redcircle.api.HttpRequestHandler;
import cloud.com.redcircle.api.RedCircleManager;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.AlterDialogFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.location.RealTimeLocationConstant;
import io.rong.imlib.location.message.RealTimeLocationStartMessage;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.PublicServiceMultiRichContentMessage;
import io.rong.message.PublicServiceRichContentMessage;
import io.rong.message.RichContentMessage;

/**
 * Created by zhan on 16/6/2.
 */
public class RongCloudEvent implements RongIM.UserInfoProvider, RongIM.ConversationBehaviorListener {

    private static RongCloudEvent mRongCloudInstance;
    private Context mContext;


    private static final String TAG = RongCloudEvent.class.getSimpleName();



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
        initDefaultListener();
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

    /**
     * RongIM.init(this) 后直接可注册的Listener。
     */
    private void initDefaultListener() {

        RongIM.setUserInfoProvider(this, true);//设置用户信息提供者。
//        RongIM.setGroupInfoProvider(this, true);//设置群组信息提供者。
        RongIM.setConversationBehaviorListener(this);//设置会话界面操作的监听器。
//        RongIM.setLocationProvider(this);//设置地理位置提供者,不用位置的同学可以注掉此行代码
//        RongIM.setConversationListBehaviorListener(this);//会话列表界面操作的监听器
//        RongIM.getInstance().setSendMessageListener(this);//设置发出消息接收监听器.

//        RongIM.setGroupUserInfoProvider(this, true);
//        RongIM.setOnReceivePushMessageListener(this);//自定义 push 通知。
        //消息体内是否有 userinfo 这个属性
//        RongIM.getInstance().setMessageAttachedUserInfo(true);
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

                    userInfo[0] = new UserInfo(mePhone, showName, Uri.parse(RedCircleManager.HTTP_BASE_URL + "/downPhotoByPhone?mePhone=" + mePhone + "&type=thumbnail&random=" + Math.random()));

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


    /**
     * 当点击用户头像后执行。
     *
     * @param context           上下文。
     * @param conversationType  会话类型。
     * @param userInfo          被点击的用户的信息。
     * @return 如果用户自己处理了点击后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {

        if (userInfo != null) {

            if (conversationType.equals(Conversation.ConversationType.PUBLIC_SERVICE) || conversationType.equals(Conversation.ConversationType.APP_PUBLIC_SERVICE)) {
//                RongIM.getInstance().startPublicServiceProfile(mContext, conversationType, user.getUserId());
            } else {

                Intent intent = new Intent(context, PhotoActivity.class);

                Uri uri = Uri.parse((RedCircleManager.HTTP_BASE_URL + "/downPhotoByPhone?mePhone=" + userInfo.getUserId() + "&type=original&random=" + Math.random()));

                intent.putExtra("photo", uri);
//                if (imageMessage.getThumUri() != null)
//                    intent.putExtra("thumbnail", imageMessage.getThumUri());

                context.startActivity(intent);
            }
        }

        return false;
    }

    /**
     * 当长按用户头像后执行。
     *
     * @param context          上下文。
     * @param conversationType 会话类型。
     * @param userInfo         被点击的用户的信息。
     * @return 如果用户自己处理了点击后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {


        return false;
    }

    /**
     * 会话界面操作的监听器：ConversationBehaviorListener 的回调方法，当点击消息时执行。
     *
     * @param context 应用当前上下文。
     * @param message 被点击的消息的实体信息。
     * @return 返回True不执行后续SDK操作，返回False继续执行SDK操作。
     */
    @Override
    public boolean onMessageClick(final Context context, final View view, final Message message) {
        Log.e(TAG, "----onMessageClick");

        //real-time location message begin
        if (message.getContent() instanceof RealTimeLocationStartMessage) {
            RealTimeLocationConstant.RealTimeLocationStatus status = RongIMClient.getInstance().getRealTimeLocationCurrentState(message.getConversationType(), message.getTargetId());

//            if (status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_IDLE) {
//                startRealTimeLocation(context, message.getConversationType(), message.getTargetId());
//            } else
            if (status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_INCOMING) {


                final AlterDialogFragment alterDialogFragment = AlterDialogFragment.newInstance("", "加入位置共享", "取消", "加入");
                alterDialogFragment.setOnAlterDialogBtnListener(new AlterDialogFragment.AlterDialogBtnListener() {

                    @Override
                    public void onDialogPositiveClick(AlterDialogFragment dialog) {
                        RealTimeLocationConstant.RealTimeLocationStatus status = RongIMClient.getInstance().getRealTimeLocationCurrentState(message.getConversationType(), message.getTargetId());

//                        if (status == null || status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_IDLE) {
//                            startRealTimeLocation(context, message.getConversationType(), message.getTargetId());
//                        } else {
//                            joinRealTimeLocation(context, message.getConversationType(), message.getTargetId());
//                        }

                    }

                    @Override
                    public void onDialogNegativeClick(AlterDialogFragment dialog) {
                        alterDialogFragment.dismiss();
                    }
                });

                alterDialogFragment.show(((FragmentActivity) context).getSupportFragmentManager());
            } else {

                if (status != null && (status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_OUTGOING || status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_CONNECTED)) {

//                    Intent intent = new Intent(((FragmentActivity) context), RealTimeLocationActivity.class);
//                    intent.putExtra("conversationType", message.getConversationType().getValue());
//                    intent.putExtra("targetId", message.getTargetId());
//                    context.startActivity(intent);
                }
            }
            return true;
        }

        //real-time location message end
        /**
         * demo 代码  开发者需替换成自己的代码。
         */
        if (message.getContent() instanceof LocationMessage) {
//            Intent intent = new Intent(context, SOSOLocationActivity.class);
//            intent.putExtra("location", message.getContent());
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
        } else if (message.getContent() instanceof RichContentMessage) {
            RichContentMessage mRichContentMessage = (RichContentMessage) message.getContent();
            Log.d("Begavior", "extra:" + mRichContentMessage.getExtra());
            Log.e(TAG, "----RichContentMessage-------");

        } else if (message.getContent() instanceof ImageMessage) {
            ImageMessage imageMessage = (ImageMessage) message.getContent();
            Intent intent = new Intent(context, PhotoActivity.class);

            intent.putExtra("photo", imageMessage.getLocalUri() == null ? imageMessage.getRemoteUri() : imageMessage.getLocalUri());
            if (imageMessage.getThumUri() != null)
                intent.putExtra("thumbnail", imageMessage.getThumUri());

            context.startActivity(intent);
        } else if (message.getContent() instanceof PublicServiceMultiRichContentMessage) {
            Log.e(TAG, "----PublicServiceMultiRichContentMessage-------");

        } else if (message.getContent() instanceof PublicServiceRichContentMessage) {
            Log.e(TAG, "----PublicServiceRichContentMessage-------");

        }

        Log.d("Begavior", message.getObjectName() + ":" + message.getMessageId());

        return false;
    }



    @Override
    public boolean onMessageLinkClick(Context context, String s) {
        return false;
    }

    @Override
    public boolean onMessageLongClick(Context context, View view, Message message) {
        return false;
    }
}
