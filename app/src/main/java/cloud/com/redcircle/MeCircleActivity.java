package cloud.com.redcircle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cloud.com.redcircle.adapter.FriendCircleAdapter;
import cloud.com.redcircle.api.HttpRequestHandler;
import cloud.com.redcircle.api.RedCircleManager;
import cloud.com.redcircle.config.PullMode;
import cloud.com.redcircle.interfaces.OnLoadMoreRefreshListener;
import cloud.com.redcircle.interfaces.OnPullDownRefreshListener;
import cloud.com.redcircle.mvp.presenter.DynamicPresenterImpl;
import cloud.com.redcircle.mvp.view.DynamicView;
import cloud.com.redcircle.ui.widget.CommentWidget;
import cloud.com.redcircle.ui.widget.DotIndicator;
import cloud.com.redcircle.ui.widget.HackyViewPager;
import cloud.com.redcircle.ui.widget.ptrwidget.FriendCirclePtrListView;
import cloud.com.redcircle.utils.FriendCircleAdapterUtil;
import cloud.com.redcircle.utils.InputMethodUtils;
import cloud.com.redcircle.utils.PhotoPagerManager;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by zhan on 16/6/6.
 */
public class MeCircleActivity extends BaseActivity implements DynamicView , View.OnClickListener{


    protected FriendCirclePtrListView mListView;
    protected FriendCircleAdapter mAdapter;
    private DynamicPresenterImpl mPresenter;
    protected List mMomentsInfos = new ArrayList();
    private  Menu mMenu;
    private String circleLevel;
    private int startNO;
    private Boolean isShowPhoto;
    //图片浏览的pager
    private PhotoPagerManager mPhotoPagerManager;


    private String mePhone;


    //input views
    private LinearLayout mInputLayout;
    private EditText mInputBox;
    private TextView mSend;

    private JSONObject articleModel;
    private CommentWidget mCommentWidget;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.acitivity_me_circle);

        mPresenter = new DynamicPresenterImpl(this);

        Bundle bundle = this.getIntent().getExtras();
        circleLevel=bundle.getString("circle_level");

        if("0".equals(circleLevel)) {
            setTitle("相册");
        } else if("1".equals(circleLevel)) {
            setTitle("朋友圈");
        } else if("2".equals(circleLevel)) {
            setTitle("红圈");
        }

        if (this.mePhone == null) {
            this.mePhone=bundle.getString("mePhone");

        }

        if (this.mePhone == null) {
            try {
                mePhone  = mUser.getString("mePhone");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        isShowPhoto = false;



        getArticles();

        mInputLayout = (LinearLayout) findViewById(R.id.ll_input);
        mInputBox = (EditText) findViewById(R.id.ed_input);
        mSend = (TextView) findViewById(R.id.btn_send);

        mSend.setOnClickListener(this);





        bindListView(R.id.listview, null,
                FriendCircleAdapterUtil.getAdapter(this, mMomentsInfos, mPresenter));

        mPhotoPagerManager = PhotoPagerManager.create(this, (HackyViewPager) findViewById(R.id.photo_pager),
                findViewById(R.id.photo_container), (DotIndicator) findViewById(R.id.dot_indicator));

        mListView.setOnDispatchTouchEventListener(new FriendCirclePtrListView.OnDispatchTouchEventListener() {
            @Override
            public boolean OnDispatchTouchEvent(MotionEvent ev) {
                if (mInputLayout.getVisibility() == View.VISIBLE) {
                    mInputLayout.setVisibility(View.GONE);
                    InputMethodUtils.hideInputMethod(mInputBox);
                    return true;
                }
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Bundle bundle = this.getIntent().getExtras();

        if (bundle.getString("mePhone") == null ) {
            mMenu = menu;
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_add_new_event, menu);
        }

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
            this.startNO = 0;
            mMomentsInfos.clear();
            getArticles();
        }
    }


    public void bindListView(int listResId, View headerView, FriendCircleAdapter adapter) {
        this.mAdapter = adapter;
        mListView = (FriendCirclePtrListView) findViewById(listResId);
//        mListView.setRotateIcon(bindRefreshIcon());
        if (headerView != null) mListView.addHeaderView(headerView);

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

        mListView.setHasMore(true);
        mListView.setAdapter(adapter);

    }

    public void onPullDownRefresh() {

        this.startNO = 0;
        this.getArticles();

    }

    public void onLoadMore() {
        this.getArticles();
    }

//    public ImageView bindRefreshIcon() {
//        return (ImageView) findViewById(R.id.rotate_icon);
//    }

    @Override
    public void showPhoto(@NonNull ArrayList<String> photoAddress, @NonNull ArrayList<Rect> originViewBounds, int curSelectedPos) {
        mPhotoPagerManager.showPhoto(photoAddress, originViewBounds, curSelectedPos);
        setTitle("查看照片");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        hiddenEditMenu();
        isShowPhoto = true;
    }


    @Override
    public void showInputBox(CommentWidget commentWidget, JSONObject articleModel) {
//        this.currentDynamicPos = currentDynamicPos;
//        this.mCommentWidget = commentWidget;
//        // 如果点击评论，而评论的创建者为本人，则显示删除评论窗口
//        if (commentWidget!=null){
//            CommentInfo info=commentWidget.getData();
//            if (info.userA.userId==LocalHostInfo.INSTANCE.getHostId()){
//                mDeleteCommentPopup.showPopupWindow();
//                return;
//            }
//        }
//        if (!TextUtils.isEmpty(draftStr)) {
//            mInputBox.setText(draftStr);
//            mInputBox.setSelection(draftStr.length());
//        }
        this.articleModel = articleModel;
        this.mCommentWidget = commentWidget;

        mInputLayout.setVisibility(View.VISIBLE);
        InputMethodUtils.showInputMethod(mInputBox);
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && isShowPhoto){
            mPhotoPagerManager.dismiss();
            isShowPhoto = false;
            return true;
        }
        this.finish();
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:

                String articleId = null;
                String commentTo = null;
                String commentBy = null;
                String content = mInputBox.getText().toString().trim();

                try {
                    articleId = this.articleModel.getString("id");
                    commentTo = "";
                    commentBy = this.mUser.getString("mePhone");


                    if (mCommentWidget != null) {
                        JSONObject info = mCommentWidget.getData();
                        commentTo = info.getString("commentBy");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (!TextUtils.isEmpty(content)) {
//                    mPresenter.addComment(currentDynamicPos, dynamicId, userid, replyId, content);

                    RedCircleManager.addComment(this, articleId, content, commentBy, commentTo, new HttpRequestHandler<JSONObject>() {
                        @Override
                        public void onSuccess(JSONObject data) {
                            startNO = 0;
                            mMomentsInfos.clear();


                            if (mInputLayout.getVisibility() == View.VISIBLE) {
                                mInputBox.setText("");
                                mInputLayout.setVisibility(View.GONE);
                                InputMethodUtils.hideInputMethod(mInputBox);
                            }

                            getArticles();


                        }

                        @Override
                        public void onSuccess(JSONObject data, int totalPages, int currentPage) {

                        }

                        @Override
                        public void onFailure(String error) {

                        }
                    });
                }
                else {
                    Toast.makeText(this, "回复内容不能为空哦",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    public void getArticles() {


        RedCircleManager.getArticles(this, mePhone, circleLevel, String.valueOf(startNO), new HttpRequestHandler<JSONArray>() {
            @Override
            public void onSuccess(JSONArray data) {

                startNO += 10;
//                try {
//                    JSONObject jsonObject = new JSONObject("{\"url\": \"/x/mobsrv/user/list?sortid=DEPT_CODE&proxy=intrust\", \"type\": \"10\", \"method\": \"get\", \"title\": \"\\u6309\\u90e8\\u95e8\"}");
//                    mMomentsInfos.add(jsonObject);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                if(data.length() == 0) {
                    mListView.setHasMore(false);
                }

                if (mListView != null && mListView.getCurMode() == PullMode.FROM_START) {
                    mMomentsInfos.clear();
                }

                if (mListView != null && mListView.getCurMode() == PullMode.FROM_BOTTOM) {
                    mListView.loadmoreCompelete();
                }

                for (int i = 0; i <data.length() ; i++) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = data.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mMomentsInfos.add(jsonObject);
                }



                mListView.refreshComplete();
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
