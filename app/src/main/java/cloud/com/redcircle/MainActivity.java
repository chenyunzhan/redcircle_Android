package cloud.com.redcircle;

import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.List;

import cloud.com.redcircle.ui.ButtonAwesome;
import cloud.com.redcircle.ui.fragment.BookFragment;
import cloud.com.redcircle.ui.fragment.MeFragment;
import cloud.com.redcircle.ui.fragment.MessageFragment;

public class MainActivity extends BaseActivity implements TabHost.OnTabChangeListener {

    private FragmentTabHost mTabHost;
    private LayoutInflater mLayoutInflater;
    private List<ButtonAwesome> mTabIndicators = new ArrayList<ButtonAwesome>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTabHost();

    }

    private void initTabHost() {
        mLayoutInflater = LayoutInflater.from(this);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.tab_content);
        mTabHost.getTabWidget().setDividerDrawable(null);

        TabHost.TabSpec[] tabSpecs = new TabHost.TabSpec[3];
        String[] texts = new String[3];

        ButtonAwesome[] tabviews = new ButtonAwesome[3];



        ButtonAwesome btnMessage = new ButtonAwesome(this);
        btnMessage.setText(this.getResources().getString(R.string.fa_comment));
        btnMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);

        ButtonAwesome btnBook = new ButtonAwesome(this);
        btnBook.setText(this.getResources().getString(R.string.fa_users));
        btnBook.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);

        ButtonAwesome btnMe = new ButtonAwesome(this);
        btnMe.setText(this.getResources().getString(R.string.fa_user));
        btnMe.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);


//        AwesomeButton b = new AwesomeButton(this,this.getResources().getString(R.string.fa_user));

        tabviews[0] = btnMessage;
        tabviews[1] = btnBook;
        tabviews[2] = btnMe;


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



}
