package cloud.com.redcircle;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

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
 * Created by zhan on 16/5/27.
 */
public class MyConversationBehaviorListener implements RongIM.ConversationBehaviorListener{

    private static final String TAG = MyConversationBehaviorListener.class.getSimpleName();


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

    /**
     * 当长按消息时执行。
     *
     * @param context 上下文。
     * @param view    触发点击的 View。
     * @param message 被长按的消息的实体信息。
     * @return 如果用户自己处理了长按后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onMessageLongClick(Context context, View view, Message message) {
        return false;
    }
    /**
     * 当点击链接消息时执行。
     *
     * @param context 上下文。
     * @param link    被点击的链接。
     * @return 如果用户自己处理了点击后的逻辑处理，则返回 true， 否则返回 false, false 走融云默认处理方式。
     */
    @Override
    public boolean onMessageLinkClick(Context context, String link) {
        return false;
    }
}