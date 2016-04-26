package cloud.com.redcircle.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cloud.com.redcircle.R;

/**
 * Created by zhan on 16/4/25.
 */
public class MessageFragment extends BaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);
//        mPagerSlidingTabStrip = (PagerSlidingTabStrip) rootView.findViewById(R.id.pager_tabstrip);
//        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
//        mEmptyText = (TextView) rootView.findViewById(R.id.empty_layout);
        return rootView;
    }


}
