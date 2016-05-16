package cloud.com.redcircle.utils;

import android.graphics.Color;
import android.os.CountDownTimer;

import info.hoang8f.widget.FButton;

/**
 * Created by zhan on 16/5/16.
 */
public class TimeCount extends CountDownTimer {

    public FButton button;

    public TimeCount(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
    }
    @Override
    public void onFinish() {//计时完毕时触发
        button.setText("重新获取");
        button.setButtonColor(Color.RED);
        button.setClickable(true);
    }
    @Override
    public void onTick(long millisUntilFinished){//计时过程显示
        button.setClickable(false);
        button.setText(millisUntilFinished /1000+"秒");
        button.setButtonColor(Color.rgb(205,205,205));
    }
}