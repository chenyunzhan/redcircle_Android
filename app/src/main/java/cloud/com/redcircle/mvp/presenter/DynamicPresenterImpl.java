package cloud.com.redcircle.mvp.presenter;

import android.graphics.Rect;
import android.support.annotation.NonNull;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cloud.com.redcircle.mvp.view.DynamicView;
import cloud.com.redcircle.ui.widget.CommentWidget;

/**
 * Created by 大灯泡 on 2016/3/17.
 * mvp - 引导层
 * 用于接收view的操作命令分发到model层实现
 */
public class DynamicPresenterImpl  {

    private DynamicView mView;


    public DynamicPresenterImpl(DynamicView view) {
        mView = view;
//        mModel = new DynamicModelImpl(this);
    }

    // 跳转到图片展示
    public void shoPhoto(@NonNull ArrayList<String> photoAddress, @NonNull ArrayList<Rect> originViewBounds, int
            curSelectedPos){
        mView.showPhoto(photoAddress,originViewBounds,curSelectedPos);
    }

    //=============================================================
    // 展示输入框
    public void showInputBox(CommentWidget commentWidget, JSONObject articleModel){
        mView.showInputBox(commentWidget, articleModel);
    }

}
