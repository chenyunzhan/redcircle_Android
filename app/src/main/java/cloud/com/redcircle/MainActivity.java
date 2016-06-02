package cloud.com.redcircle;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cloud.com.redcircle.ui.ButtonAwesome;
import cloud.com.redcircle.ui.TextAwesome;
import cloud.com.redcircle.ui.fragment.BookFragment;
import cloud.com.redcircle.ui.fragment.MeFragment;
import cloud.com.redcircle.ui.fragment.MessageFragment;
import cloud.com.redcircle.utils.AccountUtils;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;




public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener,
        ViewPager.OnPageChangeListener {

    private FragmentTabHost mTabHost;
    private LayoutInflater mLayoutInflater;
    private List<LinearLayout> mTabIndicators = new ArrayList<LinearLayout>();


    protected boolean mIsLogin;
    private RadioGroup rg_tab_bar;
    private RadioButton rb_channel;
    private RadioButton rb_message;
    private RadioButton rb_better;
    private RadioButton rb_setting;
    private ViewPager vpager;

    private MyFragmentPagerAdapter mAdapter;

    //几个代表页面的常量
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    public static final int PAGE_THREE = 2;


    @android.support.annotation.IdRes int id1 = 100;
    @android.support.annotation.IdRes int id2 = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsLogin = true;
        if (mIsLogin) {
            setContentView(R.layout.activity_main);

            initRongCloud();
            mAdapter = new MyFragmentPagerAdapter(this, getSupportFragmentManager());
            bindViews();
            rb_channel.setChecked(true);

        }


    }

    private void bindViews() {
        rg_tab_bar = (RadioGroup) findViewById(R.id.rg_tab_bar);
        rb_channel = (RadioButton) findViewById(R.id.rb_channel);
        rb_message = (RadioButton) findViewById(R.id.rb_message);
        rb_better = (RadioButton) findViewById(R.id.rb_better);
        rg_tab_bar.setOnCheckedChangeListener(this);

        vpager = (ViewPager) findViewById(R.id.vpager);
        vpager.setAdapter(mAdapter);
        vpager.setCurrentItem(0);
        vpager.addOnPageChangeListener(this);
    }




    private void initRongCloud() {

        JSONObject rongCloudToken = AccountUtils.readRongCloudToken(this);


        String Token = null;
        try {
            if (rongCloudToken == null) {
                AccountUtils.removeAll(this);
                this.finish();
            } else  {
                Token = rongCloudToken.getString("token");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        /**
         * 设置会话界面操作的监听器。
         */
        RongIM.setConversationBehaviorListener(new MyConversationBehaviorListener());


        /**
         * IMKit SDK调用第二步
         *
         * 建立与服务器的连接
         *
         */
        RongIM.connect(Token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                //Connect Token 失效的状态处理，需要重新获取 Token
            }
            @Override
            public void onSuccess(String userId) {


                RongCloudEvent.getInstance().setUserInfoProviderListener();


                /**
                 *  设置接收消息的监听器。
                 */

                MyReceiveMessageListener myReceiveMessageListener = new MyReceiveMessageListener();
                myReceiveMessageListener.mContext = MainActivity.this;
                RongIM.setOnReceiveMessageListener(myReceiveMessageListener);


                vpager.getAdapter().notifyDataSetChanged();

//                if (isAppOnForeground()) {
//                    mTabHost.onTabChanged("朋友");
//                    mTabHost.onTabChanged("消息");
//                }

                Log.e("MainActivity", "——onSuccess—-" + userId);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.e("MainActivity", "——onError—-" + errorCode);
            }
        });
    }


    //在进程中去寻找当前APP的信息，判断是否在前台运行
    private boolean isAppOnForeground() {
        ActivityManager activityManager =(ActivityManager) getApplicationContext().getSystemService(
                Context.ACTIVITY_SERVICE);
        String packageName =getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo>appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_channel:
                vpager.setCurrentItem(PAGE_ONE,false);
                break;
            case R.id.rb_message:
                vpager.setCurrentItem(PAGE_TWO,false);
                break;
            case R.id.rb_better:
                vpager.setCurrentItem(PAGE_THREE,false);
                break;
        }
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //state的状态有三个，0表示什么都没做，1正在滑动，2滑动完毕
        if (state == 2) {
            switch (vpager.getCurrentItem()) {
                case PAGE_ONE:
                    rb_channel.setChecked(true);
                    break;
                case PAGE_TWO:
                    rb_message.setChecked(true);
                    break;
                case PAGE_THREE:
                    rb_better.setChecked(true);
                    break;
            }
        }
    }
}
