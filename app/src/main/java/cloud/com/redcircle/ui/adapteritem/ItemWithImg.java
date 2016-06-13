package cloud.com.redcircle.ui.adapteritem;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cloud.com.redcircle.R;
import cloud.com.redcircle.adapter.GridViewAdapter;
import cloud.com.redcircle.adapter.viewholder.BaseItemDelegate;
import cloud.com.redcircle.api.RedCircleManager;
import cloud.com.redcircle.config.DynamicType;
import cloud.com.redcircle.ui.widget.NoScrollGridView;

/**
 * Created by 大灯泡 on 2016/2/25.
 * 图文的朋友圈item
 * type=13{@link DynamicType}
 */
public class ItemWithImg extends BaseItemDelegate implements AdapterView.OnItemClickListener {
    private static final String TAG = "ItemWithImg";

    private NoScrollGridView mNoScrollGridView;
    private GridViewAdapter mGridViewAdapter;

    private ArrayList<String> mUrls = new ArrayList<>();
    private ArrayList<Rect> mRects = new ArrayList<>();

    public ItemWithImg() {}

    @Override
    public int getViewRes() {
        return R.layout.dynamic_item_with_img;
    }

    @Override
    public void onFindView(@NonNull View parent) {
        if (mNoScrollGridView == null) mNoScrollGridView = (NoScrollGridView) parent.findViewById(R.id.item_grid);
        if (mNoScrollGridView.getOnItemClickListener() == null) {
            mNoScrollGridView.setOnItemClickListener(this);
        }
    }

    @Override
    protected void bindData(int position, @NonNull View v, @NonNull JSONObject data, int dynamicType) {


        String[] images = null;
        List<String> imagesList = new ArrayList<String>();
        List<String> originalImagesList = new ArrayList<String>();


        try {
            images = data.getString("images").split("#");
            for (int i = 0; i < images.length ; i++) {

                String urlStr = RedCircleManager.HTTP_BASE_URL + "/downPhotoByPhone?mePhone=" + images[i] + "&type=thumbnail";
                String originalUrlStr = RedCircleManager.HTTP_BASE_URL + "/downPhotoByPhone?mePhone=" + images[i];

                imagesList.add(urlStr);
                originalImagesList.add(originalUrlStr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        if (imagesList == null || imagesList.size() == 0 || mNoScrollGridView == null) return;
        mUrls.clear();
        mUrls.addAll(originalImagesList);
        if (mNoScrollGridView.getAdapter() == null) {
            mGridViewAdapter = new GridViewAdapter(context, imagesList);
            mNoScrollGridView.setAdapter(mGridViewAdapter);
        }
        if (imagesList.size() == 4) {
            mNoScrollGridView.setNumColumns(2);
        }
        else {
            mNoScrollGridView.setNumColumns(3);
        }
        mGridViewAdapter.reSetData(imagesList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final int childCount = parent.getChildCount();
        mRects.clear();
        try {
            if (childCount >= 0) {
                for (int i = 0; i < childCount; i++) {
                    View v = parent.getChildAt(i);
                    Rect bound = new Rect();
                    v.getGlobalVisibleRect(bound);
                    mRects.add(bound);
                }
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "view可能为空哦");
        }
        getPresenter().shoPhoto(mUrls, mRects, position);
    }
}