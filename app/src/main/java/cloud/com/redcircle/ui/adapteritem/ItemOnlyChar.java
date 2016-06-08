package cloud.com.redcircle.ui.adapteritem;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import org.json.JSONObject;

import cloud.com.redcircle.R;
import cloud.com.redcircle.adapter.viewholder.BaseItemDelegate;
import cloud.com.redcircle.config.DynamicType;
import cloud.com.redcircle.mvp.presenter.DynamicPresenterImpl;

/**
 * Created by 大灯泡 on 2016/2/25.
 * 只有文字的朋友圈item
 * type=10{@link DynamicType}
 *
 */
public class ItemOnlyChar extends BaseItemDelegate {

    @Override
    public int getViewRes() {
        return R.layout.dynamic_item_only_char;
    }

    @Override
    protected void bindData(int position, @NonNull View v, @NonNull JSONObject data, int dynamicType) {

    }
}
