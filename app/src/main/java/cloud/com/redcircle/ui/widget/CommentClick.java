package cloud.com.redcircle.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import cloud.com.redcircle.utils.UIHelper;


/**
 * Created by 大灯泡 on 2016/2/23.
 * 评论点击事件
 */
public class CommentClick extends ClickableSpanEx {
    private Context mContext;
    private int textSize;
    private String mUserInfo;

    private CommentClick() {}

    private CommentClick(Builder builder) {
        super(builder.color,builder.clickEventColor);
        mContext = builder.mContext;
        mUserInfo = builder.mUserInfo;
        this.textSize = builder.textSize;
    }

    @Override
    public void onClick(View widget) {
        if (mUserInfo!=null)
            Toast.makeText(mContext, "当前用户名是： " + mUserInfo,
                    Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setTextSize(textSize);
    }

    public static class Builder {
        private int color;
        private Context mContext;
        private int textSize=16;
        private String mUserInfo;
        private int clickEventColor;

        public Builder(Context context, @NonNull String info) {
            mContext = context;
            mUserInfo=info;
        }

        public Builder setTextSize(int textSize) {
            this.textSize = UIHelper.sp2px(mContext,textSize);
            return this;
        }

        public Builder setColor(int color) {
            this.color = color;
            return this;
        }

        public Builder setClickEventColor(int color){
            this.clickEventColor=color;
            return this;
        }

        public CommentClick build() {
            return new CommentClick(this);
        }
    }
}
