package cloud.com.redcircle.mvp.view;

import android.graphics.Rect;
import android.support.annotation.NonNull;

import org.json.JSONObject;

import java.util.ArrayList;

import cloud.com.redcircle.ui.widget.CommentWidget;


/**
 * Created by 大灯泡 on 2016/3/17.
 * mvp-视图层
 * 仅用于更新/展示数据和部分的用户交互
 */
public interface DynamicView {

//    // 点赞刷新
//    void refreshPraiseData(int currentDynamicPos,
//                           @CommonValue.PraiseState int praiseState, @NonNull List<UserInfo> praiseList);
//
//    // 评论刷新
//    void refreshCommentData(int currentDynamicPos, @NonNull List<CommentInfo> commentList);

    // 评论框展示
    void showInputBox(int currentDynamicPos, CommentWidget commentWidget, JSONObject articleModel);

    // 浏览图片
    void showPhoto(
            @NonNull ArrayList<String> photoAddress, @NonNull ArrayList<Rect> originViewBounds, int curSelectedPos);
}
