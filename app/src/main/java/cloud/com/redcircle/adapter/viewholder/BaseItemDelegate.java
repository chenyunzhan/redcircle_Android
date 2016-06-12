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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cloud.com.redcircle.R;
import cloud.com.redcircle.api.RedCircleManager;
import cloud.com.redcircle.config.DynamicType;
import cloud.com.redcircle.mvp.presenter.DynamicPresenterImpl;
import cloud.com.redcircle.ui.widget.ClickShowMoreLayout;
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

    }

    @Override
    public DynamicPresenterImpl getPresenter() {
        return null;
    }

    @Override
    public void onClick(View v) {

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

    }

    /** 共有数据绑定 */
    private void bindShareData(JSONObject jsonObject) {

        try {

            String urlStr = RedCircleManager.HTTP_BASE_URL + "/downPhotoByPhone?mePhone=" + jsonObject.getString("created_by");
            int type = jsonObject.getInt("type");
            textField.setText(jsonObject.getString("content"));
            nick.setText(jsonObject.getString("created_by"));
            avatar.loadImageDefault(urlStr);

            if (type == DynamicType.TYPE_ONLY_CHAR) {
                contentLayout.setVisibility(View.GONE);
            }
            else {
                contentLayout.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected abstract void bindData(int position, @NonNull View v, @NonNull JSONObject data, final int dynamicType);

}
