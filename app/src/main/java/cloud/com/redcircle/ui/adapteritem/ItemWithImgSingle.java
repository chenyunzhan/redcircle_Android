package cloud.com.redcircle.ui.adapteritem;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONObject;

import java.util.ArrayList;

import cloud.com.redcircle.adapter.viewholder.BaseItemDelegate;
import cloud.com.redcircle.config.DynamicType;

/**
 * Created by 大灯泡 on 2016/2/27.
 * 图文（单张）的朋友圈item
 * type=9{@link DynamicType}
 */
public class ItemWithImgSingle extends BaseItemDelegate {


    @Override
    protected void bindData(int position, @NonNull View v, @NonNull JSONObject data, int dynamicType) {

    }
}
