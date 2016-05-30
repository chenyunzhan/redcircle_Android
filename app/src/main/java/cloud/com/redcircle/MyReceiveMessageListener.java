package cloud.com.redcircle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

import com.sea_monster.network.ApiCallback;

import cloud.com.redcircle.utils.VibratorUtil;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.DiscussionNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

/**
 * Created by zhan on 16/5/30.
 */
public class MyReceiveMessageListener implements RongIMClient.OnReceiveMessageListener {

    private static final String TAG = MyReceiveMessageListener.class.getSimpleName();

    public Activity mContext;

    /**
     * 收到消息的处理。
     *
     * @param message 收到的消息实体。
     * @param left    剩余未拉取消息数目。
     * @return 收到消息是否处理完成，true 表示走自已的处理方式，false 走融云默认处理方式。
     */
    @Override
    public boolean onReceived(Message message, int left) {

        VibratorUtil.Vibrate(this.mContext,500);

        MessageContent messageContent = message.getContent();
        if (messageContent instanceof TextMessage) {//文本消息
            TextMessage textMessage = (TextMessage) messageContent;
            Log.d(TAG, "onReceived-TextMessage:" + textMessage.getContent());
        } else if (messageContent instanceof ImageMessage) {//图片消息
            ImageMessage imageMessage = (ImageMessage) messageContent;
            Log.d(TAG, "onReceived-ImageMessage:" + imageMessage.getRemoteUri());
        } else if (messageContent instanceof VoiceMessage) {//语音消息
            VoiceMessage voiceMessage = (VoiceMessage) messageContent;
            Log.d(TAG, "onReceived-voiceMessage:" + voiceMessage.getUri().toString());
        } else if (messageContent instanceof RichContentMessage) {//图文消息
            RichContentMessage richContentMessage = (RichContentMessage) messageContent;
            Log.d(TAG, "onReceived-RichContentMessage:" + richContentMessage.getContent());
        } else if (messageContent instanceof InformationNotificationMessage) {//小灰条消息
//            InformationNotificationMessage informationNotificationMessage = (InformationNotificationMessage) messageContent;
//            Log.e(TAG, "onReceived-informationNotificationMessage:" + informationNotificationMessage.getMessage());
//            if (DemoContext.getInstance() != null)
//                getFriendByUserIdHttpRequest = DemoContext.getInstance().getDemoApi().getUserInfoByUserId(message.getSenderUserId(), (ApiCallback<User>) this);
//        } else if (messageContent instanceof AgreedFriendRequestMessage) {//好友添加成功消息
//            AgreedFriendRequestMessage agreedFriendRequestMessage = (AgreedFriendRequestMessage) messageContent;
//            Log.d(TAG, "onReceived-deAgreedFriendRequestMessage:" + agreedFriendRequestMessage.getMessage());
//            Intent in = new Intent();
//            in.setAction(MainActivity.ACTION_DMEO_AGREE_REQUEST);
//            in.putExtra("AGREE_REQUEST", true);
//            mContext.sendBroadcast(in);
//        } else if (messageContent instanceof ContactNotificationMessage) {//好友添加消息
//            ContactNotificationMessage contactContentMessage = (ContactNotificationMessage) messageContent;
//            Log.d(TAG, "onReceived-ContactNotificationMessage:getExtra;" + contactContentMessage.getExtra());
//            Log.d(TAG, "onReceived-ContactNotificationMessage:+getmessage:" + contactContentMessage.getMessage().toString());
//            Intent in = new Intent();
//            in.setAction(MainActivity.ACTION_DMEO_RECEIVE_MESSAGE);
//            in.putExtra("rongCloud", contactContentMessage);
//            in.putExtra("has_message", true);
//            mContext.sendBroadcast(in);
//        } else if (messageContent instanceof DiscussionNotificationMessage) {//讨论组通知消息
//            DiscussionNotificationMessage discussionNotificationMessage = (DiscussionNotificationMessage) messageContent;
//            Log.d(TAG, "onReceived-discussionNotificationMessage:getExtra;" + discussionNotificationMessage.getOperator());
//            setDiscussionName(message.getTargetId());
        } else {
            Log.d(TAG, "onReceived-其他消息，自己来判断处理");
        }


        return false;

    }





}