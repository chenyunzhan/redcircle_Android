package cloud.com.redcircle;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import cloud.com.redcircle.ui.fragment.BookFragment;
import cloud.com.redcircle.ui.fragment.MeFragment;
import cloud.com.redcircle.ui.fragment.MessageFragment;
import io.rong.imkit.RongContext;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.widget.adapter.ConversationListAdapter;
import io.rong.imlib.model.Conversation;

/**
 * Created by Jay on 2015/8/31 0031.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private final int PAGER_COUNT = 3;
    private MessageFragment myFragment1 = null;
    private BookFragment myFragment2 = null;
    private MeFragment myFragment3 = null;

    private Context mContext;



    public MyFragmentPagerAdapter(Activity activity, FragmentManager fm) {
        super(fm);
        mContext = activity;

//        myFragment1 = new MessageFragment();
//        myFragment2 = new BookFragment();
//        myFragment3 = new MeFragment();

    }


    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println("position Destory" + position);
        super.destroyItem(container, position, object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case MainActivity.PAGE_ONE:
                if (myFragment1 == null) {
//                    ConversationListFragment listFragment = ConversationListFragment.getInstance();
//                    listFragment.setAdapter(new ConversationListAdapter(RongContext.getInstance()));
//                    Uri uri = Uri.parse("rong://" + mContext.getApplicationInfo().packageName).buildUpon()
//                            .appendPath("conversationlist")
//                            .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
//                            .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//群组
//                            .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//讨论组
//                            .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
//                            .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//公共服务号
//                            .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//系统
//                            .build();
//                    listFragment.setUri(uri);
//                    fragment = listFragment;

                    fragment = new MessageFragment();
                } else {
                    fragment = myFragment1;
                }
                break;
            case MainActivity.PAGE_TWO:
                if (myFragment2 == null) {
                    fragment = new BookFragment();
                } else {
                    fragment = myFragment2;
                }
                break;
            case MainActivity.PAGE_THREE:
                if (myFragment3 == null) {
                    fragment = new MeFragment();
                } else {
                    fragment = myFragment3;
                }
                break;
        }
        return fragment;
    }


}

