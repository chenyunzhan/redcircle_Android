package cloud.com.redcircle.api;

import android.content.Context;

import cloud.com.redcircle.Application;
import cloud.com.redcircle.R;
import cloud.com.redcircle.utils.NetWorkHelper;

/**
 * Created by zhan on 16/4/22.
 */
public enum RedCircleErrorType {
    ErrorSuccess,
    ErrorApiForbidden,
    ErrorNoOnceAndNext;



    public static String errorMessage(Context cxt, RedCircleErrorType type) {
        if (cxt == null)
            cxt = Application.getInstance();
        boolean isNetAvailable = NetWorkHelper.isNetAvailable(cxt);
        if (!isNetAvailable)
            return cxt.getResources().getString(R.string.error_network_disconnect);

        switch (type) {
            case ErrorApiForbidden:
                return cxt.getResources().getString(R.string.error_network_exception);
            default:
                return cxt.getResources().getString(R.string.error_unknown);
        }
    }
}

