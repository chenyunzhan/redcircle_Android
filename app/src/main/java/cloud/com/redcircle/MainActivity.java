package cloud.com.redcircle;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
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

public class MainActivity extends BaseActivity implements TabHost.OnTabChangeListener {

    private FragmentTabHost mTabHost;
    private LayoutInflater mLayoutInflater;
    private List<LinearLayout> mTabIndicators = new ArrayList<LinearLayout>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mIsLogin) {
            setContentView(R.layout.activity_main);
            initRongCloud();
            initTabHost();
        }


    }

    private void initTabHost() {
        mLayoutInflater = LayoutInflater.from(this);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.tab_content);
        mTabHost.getTabWidget().setDividerDrawable(null);

        TabHost.TabSpec[] tabSpecs = new TabHost.TabSpec[3];
        String[] texts = new String[3];

        LinearLayout[] tabviews = new LinearLayout[3];



        ButtonAwesome btnMessage = new ButtonAwesome(this);
        btnMessage.setText(this.getResources().getString(R.string.fa_comment));
        btnMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
        btnMessage.setBackgroundColor(Color.TRANSPARENT);
        btnMessage.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
        btnMessage.setPadding(0,0,0,0);
        btnMessage.setClickable(false);


        ButtonAwesome btnBook = new ButtonAwesome(this);
        btnBook.setText(this.getResources().getString(R.string.fa_users));
        btnBook.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
        btnBook.setBackgroundColor(Color.TRANSPARENT);
        btnBook.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
        btnBook.setPadding(0,0,0,0);
        btnBook.setClickable(false);

        ButtonAwesome btnMe = new ButtonAwesome(this);
        btnMe.setText(this.getResources().getString(R.string.fa_user));
        btnMe.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
        btnMe.setBackgroundColor(Color.TRANSPARENT);
        btnMe.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
        btnMe.setPadding(0,0,0,0);
        btnMe.setClickable(false);

        TextView textViewMessage = new TextView(this);
        textViewMessage.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);
        textViewMessage.setText("消息");

        TextView textViewBook = new TextView(this);
        textViewBook.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);
        textViewBook.setText("朋友");

        TextView textViewMe = new TextView(this);
        textViewMe.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);
        textViewMe.setText("我的");


        LinearLayout linearLayoutMessage = new LinearLayout(this);
        linearLayoutMessage.setOrientation(LinearLayout.VERTICAL);

        LinearLayout linearLayoutBook = new LinearLayout(this);
        linearLayoutBook.setOrientation(LinearLayout.VERTICAL);

        LinearLayout linearLayoutMe = new LinearLayout(this);
        linearLayoutMe.setOrientation(LinearLayout.VERTICAL);


        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(LinearLayout.
                LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(LinearLayout.
                LayoutParams.MATCH_PARENT,60);




//        TextAwesome textAwesome = new TextAwesome(this);
//        textAwesome.setText(this.getResources().getString(R.string.fa_comment));
//        textAwesome.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
//        textAwesome.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);



        linearLayoutMessage.addView(btnMessage,param2);
        linearLayoutMessage.addView(textViewMessage,param1);

        linearLayoutBook.addView(btnBook,param2);
        linearLayoutBook.addView(textViewBook,param1);

        linearLayoutMe.addView(btnMe,param2);
        linearLayoutMe.addView(textViewMe,param1);





//        AwesomeButton b = new AwesomeButton(this,this.getResources().getString(R.string.fa_user));

        tabviews[0] = linearLayoutMessage;
        tabviews[1] = linearLayoutBook;
        tabviews[2] = linearLayoutMe;


//        Bundle bundle = new Bundle();
//        bundle.putInt("type", ViewPagerFragment.TypeViewPager_Aggregation);
        texts[0] = getString(R.string.tab_message);
        tabSpecs[0] = mTabHost.newTabSpec(texts[0]).setIndicator(tabviews[0]);
        mTabHost.addTab(tabSpecs[0], MessageFragment.class, null);
        mTabIndicators.add(tabviews[0]);

        texts[1] = getString(R.string.tab_book);
        tabSpecs[1] = mTabHost.newTabSpec(texts[1]).setIndicator(tabviews[1]);
        mTabHost.addTab(tabSpecs[1], BookFragment.class, null);
        mTabIndicators.add(tabviews[1]);

        texts[2] = getString(R.string.tab_me);
        tabSpecs[2] = mTabHost.newTabSpec(texts[2]).setIndicator(tabviews[2]);
        mTabHost.addTab(tabSpecs[2], MeFragment.class, null);
        mTabIndicators.add(tabviews[2]);


        mTabHost.setOnTabChangedListener(this);
        setTitle(texts[0]);
    }

    @Override
    public void onTabChanged(String tabId) {
        setTitle(tabId);
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

                if (isAppOnForeground()) {
                    mTabHost.onTabChanged("朋友");
                    mTabHost.onTabChanged("消息");
                }

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


}
