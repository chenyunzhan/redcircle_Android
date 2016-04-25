package cloud.com.redcircle.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cloud.com.redcircle.R;
import cloud.com.redcircle.ui.ButtonAwesome;
import cloud.com.redcircle.ui.TextAwesome;

/**
 * Created by zhan on 16/4/25.
 */
public class MeFragment extends BaseFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_me, container, false);

        return rootView;
    }
}
