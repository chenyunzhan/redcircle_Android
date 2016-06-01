package cloud.com.redcircle.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import cloud.com.redcircle.MyReceiveMessageListener;
import cloud.com.redcircle.R;
import cloud.com.redcircle.api.HttpRequestHandler;
import cloud.com.redcircle.api.RedCircleManager;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

/**
 * Created by zhan on 16/4/25.
 */
public class MessageFragment extends BaseFragment implements RongIM.UserInfoProvider{


    private View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RongIM.setUserInfoProvider(this,true);
        /**
         *  设置接收消息的监听器。
         */

        MyReceiveMessageListener myReceiveMessageListener = new MyReceiveMessageListener();
        myReceiveMessageListener.mContext = this.getActivity();
        RongIM.setOnReceiveMessageListener(myReceiveMessageListener);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(rootView == null && RongIM.getInstance().getRongIMClient().getCurrentConnectionStatus() == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {

            View view = inflater.inflate(R.layout.fragment_message, container, false);

            ConversationListFragment fragment = new ConversationListFragment();
            Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversationlist")
                    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//设置群组会话聚合显示
                    .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
                    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
                    .build();
            fragment.setUri(uri);




            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.rong_content, fragment);
            transaction.commit();
            rootView = view;

        }

        return rootView;


    }


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.conversationlist,container,false);
//
//        ConversationListFragment fragment = (ConversationListFragment) getChildFragmentManager().findFragmentById(R.id.conversationlist1);
//
//        Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
//                .appendPath("conversationlist")
//                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
//                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//设置群组会话聚合显示
//                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
//                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
//                .build();
//
//        fragment.setUri(uri);
//        return  view;
//    }


    @Override
    public UserInfo getUserInfo(String s) {

        final UserInfo[] userInfo = {null};

        RedCircleManager.getUserInfoById(this.getActivity(), s, new HttpRequestHandler<JSONObject>() {
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
