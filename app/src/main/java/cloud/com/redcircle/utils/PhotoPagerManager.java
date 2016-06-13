package cloud.com.redcircle.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;

import cloud.com.redcircle.MeCircleActivity;
import cloud.com.redcircle.adapter.PhotoBoswerPagerAdapter;
import cloud.com.redcircle.ui.widget.DotIndicator;
import cloud.com.redcircle.ui.widget.HackyViewPager;


/**
 * Created by 大灯泡 on 2016/4/12.
 * 相册展示的管理类
 */
public class PhotoPagerManager implements PhotoBoswerPagerAdapter.OnPhotoViewClickListener,ViewPager.OnPageChangeListener {

    private Context mContext;
    private PhotoBoswerPagerAdapter adapter;
    private HackyViewPager pager;

    private Rect finalBounds;
    private Point globalOffset;

    private DotIndicator mDotIndicator;

    private View container;

    private PhotoPagerManager(Context context, HackyViewPager pager, View container, DotIndicator dotIndicator) {
        if (container != null) {
            finalBounds = new Rect();
            globalOffset = new Point();
            this.mContext = context;
            this.container = container;
            this.pager = pager;
            this.mDotIndicator=dotIndicator;

            this.pager.addOnPageChangeListener(this);
            adapter = new PhotoBoswerPagerAdapter(context);
            adapter.setOnPhotoViewClickListener(this);
        }
        else {
            throw new IllegalArgumentException("PhotoPagerManager >>> container不能为空哦");
        }
    }

    public static PhotoPagerManager create(Context context, HackyViewPager pager, View container,DotIndicator dotIndicator) {
        return new PhotoPagerManager(context, pager, container,dotIndicator);
    }

    public void showPhoto(
            @NonNull ArrayList<String> photoAddress, @NonNull ArrayList<Rect> originViewBounds, int curSelectedPos) {
        adapter.resetDatas(photoAddress, originViewBounds);
        pager.setAdapter(adapter);
        mDotIndicator.setDotViewNum(photoAddress.size());
        mDotIndicator.setCurrentSelection(curSelectedPos);
        pager.setCurrentItem(curSelectedPos);
        pager.setLocked(photoAddress.size() == 1);
        container.getGlobalVisibleRect(finalBounds, globalOffset);
        showPhotoPager(originViewBounds, curSelectedPos);
    }

    private AnimatorSet curAnimator;

    private void showPhotoPager(@NonNull ArrayList<Rect> originViewBounds, int curSelectedPos) {
        Rect startBounds = originViewBounds.get(curSelectedPos);

        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        float ratio = calculateRatio(startBounds, finalBounds);

        pager.setPivotX(0);
        pager.setPivotY(0);

        container.setVisibility(View.VISIBLE);

        final AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(pager, View.X, startBounds.left, finalBounds.left))
           .with(ObjectAnimator.ofFloat(pager, View.Y, startBounds.top, finalBounds.top))
           .with(ObjectAnimator.ofFloat(pager, View.SCALE_X, ratio, 1f))
           .with(ObjectAnimator.ofFloat(pager, View.SCALE_Y, ratio, 1f));
        set.setDuration(400);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                curAnimator = set;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                curAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                curAnimator = null;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }

    @Override
    public void onPhotoViewClick(View view, Rect originBound, int curPos) {

        MeCircleActivity meCircleActivity = (MeCircleActivity) mContext;
        meCircleActivity.setTitle("相册");
        ActionBar actionBar = meCircleActivity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        meCircleActivity.showEditMenu();


        //如果展开动画没有展示完全就关闭，那么就停止展开动画进而执行退出动画
        if (curAnimator != null) {
            curAnimator.cancel();
        }

        container.getGlobalVisibleRect(finalBounds, globalOffset);

        originBound.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        float ratio = calculateRatio(originBound, finalBounds);

        pager.setPivotX(0);
        pager.setPivotY(0);

        final AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(pager, View.X, originBound.left))
           .with(ObjectAnimator.ofFloat(pager, View.Y, originBound.top+150))
           .with(ObjectAnimator.ofFloat(pager, View.SCALE_X, 1f, ratio))
           .with(ObjectAnimator.ofFloat(pager, View.SCALE_Y, 1f, ratio));

        set.setDuration(400);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                curAnimator = set;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                curAnimator = null;
                container.clearAnimation();
                container.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                curAnimator = null;
                container.clearAnimation();
                container.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }

    private float calculateRatio(Rect startBounds, Rect finalBounds) {
        float ratio;
        if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            ratio = (float) startBounds.height() / finalBounds.height();
            float startWidth = ratio * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        }
        else {
            // Extend start bounds vertically
            ratio = (float) startBounds.width() / finalBounds.width();
            float startHeight = ratio * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }
        return ratio;
    }

    public void destroy() {
        adapter.destroy();
        mContext = null;
        adapter = null;
        pager = null;
        finalBounds = null;
        globalOffset = null;
        container = null;
    }

    //=============================================================pager change listener↓↓↓

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mDotIndicator.setCurrentSelection(position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public void dismiss () {
        adapter.dismiss();
    }
}
