package cloud.com.redcircle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cloud.com.redcircle.adapter.FriendCircleAdapter;
import cloud.com.redcircle.api.HttpRequestHandler;
import cloud.com.redcircle.api.RedCircleManager;
import cloud.com.redcircle.interfaces.OnLoadMoreRefreshListener;
import cloud.com.redcircle.interfaces.OnPullDownRefreshListener;
import cloud.com.redcircle.mvp.presenter.DynamicPresenterImpl;
import cloud.com.redcircle.mvp.view.DynamicView;
import cloud.com.redcircle.ui.widget.DotIndicator;
import cloud.com.redcircle.ui.widget.HackyViewPager;
import cloud.com.redcircle.ui.widget.ptrwidget.FriendCirclePtrListView;
import cloud.com.redcircle.utils.FriendCircleAdapterUtil;
import cloud.com.redcircle.utils.PhotoPagerManager;
import cn.smssdk.SMSSDK;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by zhan on 16/6/6.
 */
public class MeCircleActivity extends BaseActivity implements DynamicView {


    protected FriendCirclePtrListView mListView;
    protected FriendCircleAdapter mAdapter;
    private DynamicPresenterImpl mPresenter;
    protected List mMomentsInfos = new ArrayList();
    private  Menu mMenu;
    private String circleLevel;

    //图片浏览的pager
    private PhotoPagerManager mPhotoPagerManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setTitle("相册");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.acitivity_me_circle);

        mPresenter = new DynamicPresenterImpl(this);


        getArticles();


        bindListView(R.id.listview, null,
                FriendCircleAdapterUtil.getAdapter(this, mMomentsInfos, mPresenter));

        mPhotoPagerManager = PhotoPagerManager.create(this, (HackyViewPager) findViewById(R.id.photo_pager),
                findViewById(R.id.photo_container), (DotIndicator) findViewById(R.id.dot_indicator));

        Intent intent = this.getIntent();
        circleLevel=intent.getStringExtra("circle_level");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
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
//                startActivity(intent);
                startActivityForResult(intent, 1);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            getArticles();
        }
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

    @Override
    public void showPhoto(@NonNull ArrayList<String> photoAddress, @NonNull ArrayList<Rect> originViewBounds, int curSelectedPos) {
        mPhotoPagerManager.showPhoto(photoAddress, originViewBounds, curSelectedPos);
        setTitle("查看照片");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        hiddenEditMenu();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            mPhotoPagerManager.dismiss();
            return true;
        }
        return false;
    }


    public void getArticles() {
        String mePhone = null;
        try {
            mePhone  = mUser.getString("mePhone");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RedCircleManager.getArticles(this, mePhone, circleLevel, new HttpRequestHandler<JSONArray>() {
            @Override
            public void onSuccess(JSONArray data) {
//                try {
//                    JSONObject jsonObject = new JSONObject("{\"url\": \"/x/mobsrv/user/list?sortid=DEPT_CODE&proxy=intrust\", \"type\": \"10\", \"method\": \"get\", \"title\": \"\\u6309\\u90e8\\u95e8\"}");
//                    mMomentsInfos.add(jsonObject);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                mMomentsInfos.clear();

                for (int i = 0; i <data.length() ; i++) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = data.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mMomentsInfos.add(jsonObject);
                }

                mAdapter.notifyDataSetChanged();


            }

            @Override
            public void onSuccess(JSONArray data, int totalPages, int currentPage) {

            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    public void hiddenEditMenu(){
        if(null != mMenu){
            for (int i = 0; i < mMenu.size(); i++){
                mMenu.getItem(i).setVisible(false);
                mMenu.getItem(i).setEnabled(false);
            }
        }
    }

    public void showEditMenu(){
        if(null != mMenu){
            for (int i = 0; i < mMenu.size(); i++){
                mMenu.getItem(i).setVisible(true);
                mMenu.getItem(i).setEnabled(true);
            }
        }
    }

}
