package cloud.com.redcircle.ui.adapteritem;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import org.json.JSONObject;

import java.util.ArrayList;

import cloud.com.redcircle.adapter.viewholder.BaseItemDelegate;
import cloud.com.redcircle.config.DynamicType;


/**
 * Created by 大灯泡 on 2016/2/25.
 * 图文的朋友圈item
 * type=13{@link DynamicType}
 */
public class ItemWithImg extends BaseItemDelegate implements AdapterView.OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    protected void bindData(int position, @NonNull View v, @NonNull JSONObject data, int dynamicType) {

    }
}
