package cloud.com.redcircle;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cloud.com.redcircle.adapter.FriendCircleAdapter;
import cloud.com.redcircle.interfaces.OnLoadMoreRefreshListener;
import cloud.com.redcircle.interfaces.OnPullDownRefreshListener;
import cloud.com.redcircle.mvp.presenter.DynamicPresenterImpl;
import cloud.com.redcircle.ui.widget.ptrwidget.FriendCirclePtrListView;
import cloud.com.redcircle.utils.FriendCircleAdapterUtil;
import cn.smssdk.SMSSDK;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by zhan on 16/6/6.
 */
public class MeCircleActivity extends BaseActivity{


    protected FriendCirclePtrListView mListView;
    protected FriendCircleAdapter mAdapter;
    private DynamicPresenterImpl mPresenter;
    protected List<JSONObject> mMomentsInfos = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("相册");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.acitivity_me_circle);

        mPresenter = new DynamicPresenterImpl();


        try {
            JSONObject jsonObject = new JSONObject("{\"url\": \"/x/mobsrv/user/list?sortid=DEPT_CODE&proxy=intrust\", \"type\": \"10\", \"method\": \"get\", \"title\": \"\\u6309\\u90e8\\u95e8\"}");
            mMomentsInfos.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        bindListView(R.id.listview, null,
                FriendCircleAdapterUtil.getAdapter(this, mMomentsInfos, mPresenter));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_new_event, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
                break;
            case R.id.add_article_action:// 点击返回图标事件
                Intent intent = new Intent(this, AddArticleActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public void bindListView(int listResId, View headerView, FriendCircleAdapter adapter) {
        this.mAdapter = adapter;
        mListView = (FriendCirclePtrListView) findViewById(listResId);
//        mListView.setRotateIcon(bindRefreshIcon());
        if (headerView != null) mListView.addHeaderView(headerView);
        mListView.setAdapter(adapter);

        mListView.setOnPullDownRefreshListener(new OnPullDownRefreshListener() {
            @Override
            public void onRefreshing(PtrFrameLayout frame) {
                onPullDownRefresh();
            }
        });
        mListView.setOnLoadMoreRefreshListener(new OnLoadMoreRefreshListener() {
            @Override
            public void onRefreshing(PtrFrameLayout frame) {
                onLoadMore();
            }
        });
    }

    public void onPullDownRefresh() {
//        mCircleRequest.setStart(0);
//        mCircleRequest.execute();
    }

    public void onLoadMore() {
//        mCircleRequest.execute();
    }
}
