package cloud.com.redcircle.adapter.viewholder;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cloud.com.redcircle.R;
import cloud.com.redcircle.api.RedCircleManager;
import cloud.com.redcircle.config.DynamicType;
import cloud.com.redcircle.mvp.presenter.DynamicPresenterImpl;
import cloud.com.redcircle.ui.widget.ClickShowMoreLayout;
import cloud.com.redcircle.ui.widget.CommentWidget;
import cloud.com.redcircle.ui.widget.SuperImageView;

/**
 * Created by 大灯泡 on 2016/2/16.
 * 基本item封装，往后的朋友圈item都会继承本类
 *
 * weblink:http://www.jianshu.com/p/720d5a7c75a7
 */
public abstract class BaseItemDelegate implements BaseItemView<JSONObject>,
        View.OnClickListener,
        View.OnLongClickListener,
        ViewGroup.OnHierarchyChangeListener {

    //中间内容层
    protected RelativeLayout contentLayout;
    protected Activity context;

    //顶部
    protected SuperImageView avatar;
    protected TextView nick;
    protected ClickShowMoreLayout textField;
    protected FrameLayout commentButton;

    //底部
    protected TextView createTime;
    protected LinearLayout commentLayout;


    private DynamicPresenterImpl mPresenter;


    private JSONObject articleModel;

    //评论区的view对象池
    private static final CommentPool COMMENT_TEXT_POOL = new CommentPool(35);

    private int commentPaddintRight=0;

    @Override
    public void onChildViewAdded(View parent, View child) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }


    @Override
    public int getViewRes() {
        return 0;
    }

    @Override
    public void onFindView(@NonNull View parent) {

    }

    @Override
    public void onBindData(int position, @NonNull View v, @NonNull JSONObject data, int dynamicType) {

        this.articleModel = data;


        bindView(v);
        bindShareData(data);
        bindData(position, v, data, dynamicType);
    }

    @Override
    public Activity getActivityContext() {
        return null;
    }

    @Override
    public void setActivityContext(Activity context) {
        this.context = context;
    }

    @Override
    public void setPresenter(DynamicPresenterImpl presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public DynamicPresenterImpl getPresenter() {
        return mPresenter;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 评论按钮
            case R.id.comment_button:
                if (mPresenter != null) {
                    mPresenter.showInputBox(null, articleModel);
                }
                break;
            default:
                break;
        }


        //评论的click
        if (v instanceof CommentWidget) {
            if (mPresenter != null) {
                mPresenter.showInputBox((CommentWidget) v, articleModel);
            }
        }
    }

    @Override
    public void onChildViewRemoved(View parent, View child) {

    }


    /** 绑定共用部分 */
    private void bindView(View v) {
        if (avatar == null) avatar = (SuperImageView) v.findViewById(R.id.avatar);
        if (nick == null) nick = (TextView) v.findViewById(R.id.nick);
        if (contentLayout == null) contentLayout = (RelativeLayout) v.findViewById(R.id.content);
        if (textField == null) textField = (ClickShowMoreLayout) v.findViewById(R.id.item_text_field);
        if (createTime == null) createTime = (TextView) v.findViewById(R.id.create_time);
        if (commentButton == null) commentButton = (FrameLayout) v.findViewById(R.id.comment_button);
        if (commentLayout == null) commentLayout = (LinearLayout) v.findViewById(R.id.comment_layout);

        commentButton.setOnClickListener(this);

    }

    /** 共有数据绑定 */
    private void bindShareData(JSONObject jsonObject) {

        try {

            String urlStr = RedCircleManager.HTTP_BASE_URL + "/downPhotoByPhone?mePhone=" + jsonObject.getString("created_by");
            int type = jsonObject.getInt("type");
            textField.setText(jsonObject.getString("content"));
            if(jsonObject.getString("name").length() > 0) {
                nick.setText(jsonObject.getString("name"));
            } else {
                nick.setText(jsonObject.getString("created_by"));
            }
            avatar.loadImageDefault(urlStr);
            createTime.setText(jsonObject.getString("created_at"));

            if (type == DynamicType.TYPE_ONLY_CHAR) {
                contentLayout.setVisibility(View.GONE);
            }
            else {
                contentLayout.setVisibility(View.VISIBLE);
            }


            setCommentPraiseLayoutVisibility(jsonObject);
            //评论
            addCommentWidget(jsonObject);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /** 是否有点赞或者评论 */
    private void setCommentPraiseLayoutVisibility(JSONObject data) {

        try {
            JSONArray commentArray = data.getJSONArray("comments");

            if(commentArray == null || commentArray.length() == 0) {
                commentLayout.setVisibility(View.GONE);
            } else {
                commentLayout.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void addCommentWidget(JSONObject data) {


        try {
            JSONArray commentArray = data.getJSONArray("comments");

            if(commentArray == null && commentArray.length() == 0) {
                return;
            }

            final int childCount = commentLayout.getChildCount();
            commentLayout.setOnHierarchyChangeListener(this);
            if (childCount < commentArray.length()) {
                //当前的view少于list的长度，则补充相差的view
                int subCount = commentArray.length() - childCount;
                for (int i = 0; i < subCount; i++) {
                    CommentWidget commentWidget = COMMENT_TEXT_POOL.get();
                    if (commentWidget == null) {
                        commentWidget = new CommentWidget(context);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.topMargin = 1;
                        params.bottomMargin = 1;
                        commentWidget.setLayoutParams(params);
                        commentWidget.setPadding(0,0,commentPaddintRight,0);
                        commentWidget.setLineSpacing(4, 1);
                    }
                    commentWidget.setBackgroundDrawable(
                            context.getResources().getDrawable(R.drawable.selector_comment_widget));
                    commentWidget.setOnClickListener(this);
                    commentWidget.setOnLongClickListener(this);
                    commentLayout.addView(commentWidget);
                }
            }else if (childCount > commentArray.length()) {
                //当前的view的数目比list的长度大，则减去对应的view
                commentLayout.removeViews(commentArray.length(), childCount - commentArray.length());
            }

            //绑定数据
            for (int n = 0; n < commentArray.length(); n++) {
                CommentWidget commentWidget = (CommentWidget) commentLayout.getChildAt(n);

                JSONObject commentJSONObject = commentArray.getJSONObject(n);


                if (commentWidget != null) commentWidget.setCommentText(commentJSONObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected abstract void bindData(int position, @NonNull View v, @NonNull JSONObject data, final int dynamicType);


    //=============================================================pool class
    static class CommentPool {
        private CommentWidget[] CommentPool;
        private int size;
        private int curPointer = -1;

        public CommentPool(int size) {
            this.size = size;
            CommentPool = new CommentWidget[size];
        }

        public synchronized CommentWidget get() {
            if (curPointer == -1 || curPointer > CommentPool.length) return null;
            CommentWidget commentTextView = CommentPool[curPointer];
            CommentPool[curPointer] = null;
            //Log.d("itemDelegate","复用成功---- 当前的游标为： "+curPointer);
            curPointer--;
            return commentTextView;
        }

        public synchronized boolean put(CommentWidget commentTextView) {
            if (curPointer == -1 || curPointer < CommentPool.length - 1) {
                curPointer++;
                CommentPool[curPointer] = commentTextView;
                //Log.d("itemDelegate","入池成功---- 当前的游标为： "+curPointer);
                return true;
            }
            return false;
        }

        public void clearPool() {
            for (int i = 0; i < CommentPool.length; i++) {
                CommentPool[i] = null;
            }
            curPointer = -1;
        }
    }
}
