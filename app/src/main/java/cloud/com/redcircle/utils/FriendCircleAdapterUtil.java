package cloud.com.redcircle.utils;

import android.app.Activity;
import android.support.annotation.NonNull;

import org.json.JSONObject;

import java.util.List;

import cloud.com.redcircle.adapter.CircleBaseAdapter;
import cloud.com.redcircle.adapter.FriendCircleAdapter;
import cloud.com.redcircle.config.DynamicType;
import cloud.com.redcircle.mvp.presenter.DynamicPresenterImpl;
import cloud.com.redcircle.ui.adapteritem.ItemOnlyChar;
import cloud.com.redcircle.ui.adapteritem.ItemShareWeb;
import cloud.com.redcircle.ui.adapteritem.ItemWithImg;
import cloud.com.redcircle.ui.adapteritem.ItemWithImgSingle;


/**
 * Created by 大灯泡 on 2016/2/25.
 * 朋友圈adapter工具类
 */
public class FriendCircleAdapterUtil {

    public static FriendCircleAdapter getAdapter(Activity context, List<JSONObject> datas) {
        FriendCircleAdapter.Builder<JSONObject> builder = new CircleBaseAdapter.Builder<>(datas).addType(DynamicType.TYPE_ONLY_CHAR, ItemOnlyChar.class)
                                                                                                 .addType(DynamicType.TYPE_WITH_IMG,ItemWithImg.class)
                                                                                                 .addType(DynamicType.TYPE_SHARE_WEB,ItemShareWeb.class)
                                                                                                 .addType(DynamicType.TYPE_IMG_SINGLE,ItemWithImgSingle.class)
                                                                                                 .build();
        return new FriendCircleAdapter(context, builder);
    }
    public static FriendCircleAdapter getAdapter(Activity context, List<JSONObject> datas,@NonNull DynamicPresenterImpl
                                                 presenter) {
        FriendCircleAdapter.Builder<JSONObject> builder = new CircleBaseAdapter.Builder<>(datas).setPresenter(presenter)
                                                                                                 .addType(DynamicType.TYPE_ONLY_CHAR, ItemOnlyChar.class)
                                                                                                 .addType(DynamicType.TYPE_WITH_IMG,ItemWithImg.class)
                                                                                                 .addType(DynamicType.TYPE_SHARE_WEB,ItemShareWeb.class)
                                                                                                 .addType(DynamicType.TYPE_IMG_SINGLE,ItemWithImgSingle.class)
                                                                                                 .build();
        return new FriendCircleAdapter(context, builder);
    }
}
