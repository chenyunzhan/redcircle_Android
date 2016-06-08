package cloud.com.redcircle.adapter;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by 大灯泡 on 2016/2/16.
 * 朋友圈适配器
 */
public class FriendCircleAdapter extends CircleBaseAdapter<JSONObject> {

    public FriendCircleAdapter(Activity context, Builder<JSONObject> mBuilder) {
        super(context, mBuilder);
    }

    @Override
    public int getItemViewType(int position) {

        JSONObject jsonObject = getItem(position);

        int type = -1;
        try {
            type = jsonObject.getInt("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  type;
    }
}
