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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cloud.com.redcircle.R;
import cloud.com.redcircle.adapter.viewholder.BaseItemDelegate;
import cloud.com.redcircle.api.RedCircleManager;
import cloud.com.redcircle.config.DynamicType;
import cloud.com.redcircle.ui.widget.ForceClickImageView;
import cloud.com.redcircle.utils.ImgUtil;
import cloud.com.redcircle.utils.UIHelper;

/**
 * Created by 大灯泡 on 2016/2/27.
 * 图文（单张）的朋友圈item
 * type=9{@link DynamicType}
 */
public class ItemWithImgSingle extends BaseItemDelegate {

    private int maxWidth;
    private int maxHeight;
    private ForceClickImageView mImageView;
    private float ratio;
    int width = 0;

    private ArrayList<String> mUrls=new ArrayList<>();
    private ArrayList<Rect> mRects=new ArrayList<>();

    public ItemWithImgSingle() {}

    @Override
    public void onFindView(@NonNull View parent) {
        if (mImageView == null) mImageView = (ForceClickImageView) parent.findViewById(R.id.img_single);
        mImageView.setOnClickListener(this);
        if (maxWidth == 0) {
            maxWidth = UIHelper.getScreenPixWidth(context) - UIHelper.dipToPx(context, 90f);
        }
        if (maxHeight == 0) {
            maxHeight = UIHelper.dipToPx(context, 175f);
        }
        ratio = maxWidth / maxHeight;
    }


    @Override
    protected void bindData(int position, @NonNull View v, @NonNull JSONObject data, int dynamicType) {


        String urlStr = null;
        String[] images = null;
        List<String> imagesList = new ArrayList<String>();
        try {

            images = data.getString("images").split("#");

            for (int i = 0; i < images.length ; i++) {
                imagesList.add(images[i]);
            }

            urlStr = RedCircleManager.HTTP_BASE_URL + "/downPhotoByPhone?mePhone=" + imagesList.get(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String imgUrl = urlStr;
        mUrls.clear();
        mUrls.addAll(imagesList);
        if (!TextUtils.isEmpty(imgUrl)) {
            Glide.with(context).load(imgUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    if (resource.getWidth() >= maxWidth) {
                        width = maxWidth;
                    }
                    else {
                        width = resource.getWidth();
                    }
                    try {
                        mImageView.setImageBitmap(ImgUtil.ScaleBitmap(resource, width, (int) (width * ratio)));
                    } catch (Exception e) {
                        e.printStackTrace();
//                        Log.e("ItemWithImgSingle", "有可能是原图被回收了。该动态的nick=  " + data.userInfo.nick);
                    }
                }
            });
        }
    }

    @Override
    public int getViewRes() {
        return R.layout.dynamic_item_with_img_single;
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_single:
                Rect bound=new Rect();
                v.getGlobalVisibleRect(bound);
                mRects.clear();
                mRects.add(0,bound);
                int pos=0;
//                getPresenter().shoPhoto(mUrls,mRects,pos);
                break;
        }
    }
}
