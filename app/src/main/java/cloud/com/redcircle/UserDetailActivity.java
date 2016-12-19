package cloud.com.redcircle;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import cloud.com.redcircle.api.HttpRequestHandler;
import cloud.com.redcircle.api.RedCircleManager;
import io.rong.imkit.RongIM;

/**
 * Created by zhan on 16/6/14.
 */
public class UserDetailActivity extends BaseActivity implements View.OnClickListener {

    private String friendPhone;
    private String mePhone;
    protected String friendName;

    private TextView nameTextView;
    private TextView sexTextView;
    private TextView phoneTextView;
    private TextView recommandLanguageView;
    private ImageView photoImageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        setTitle("详细资料");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        this.friendPhone = intent.getStringExtra("friendPhone");
        this.mePhone = intent.getStringExtra("mePhone");



        nameTextView = (TextView) findViewById(R.id.txt_fragment_user_name);
        sexTextView = (TextView) findViewById(R.id.txt_fragment_user_tagline);
        phoneTextView = (TextView) findViewById(R.id.txt_fragment_user_description);
        recommandLanguageView = (TextView) findViewById(R.id.txt_recommand_language);
        final Button sendMessageButton = (Button) findViewById(R.id.send_message_btn);
        final Button dialMobileButton = (Button) findViewById(R.id.dial_mobile_btn);
        photoImageView = (ImageView) findViewById(R.id.header_logo_fragment_user);
        final LinearLayout seePhotoLinearLayout = (LinearLayout) findViewById(R.id.see_photp_layout);
        final LinearLayout addRecommandLayout = (LinearLayout) findViewById(R.id.add_recommand_language_layout);

        sendMessageButton.setOnClickListener(this);
        dialMobileButton.setOnClickListener(this);
        seePhotoLinearLayout.setOnClickListener(this);
        addRecommandLayout.setOnClickListener(this);
        photoImageView.setOnClickListener(this);

        this.initData();

    }


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case 0:
                    this.initData();
                    break;
                default:
                    break;
            }


        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_message_btn:
                //启动会话界面
                if (RongIM.getInstance() != null) {

                    String itemTitle = null;
                    if (!"暂未填写".equals(friendName) && friendName.length() > 0) {
                        itemTitle = friendName;
                    } else {
                        itemTitle = friendPhone;
                    }

                    RongIM.getInstance().startPrivateChat(this, this.friendPhone, itemTitle);

                }

                break;
            case R.id.dial_mobile_btn:


                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                } else {

                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + this.friendPhone));
                    startActivity(intent);
                }


                break;
            case R.id.add_recommand_language_layout:

                try {
                    if (this.mePhone.equals(mUser.getString("mePhone"))) {

                        Intent intent2 = new Intent(this, ModifyMeFriendActivity.class);
                        intent2.putExtra("friendPhone",this.friendPhone);
                        intent2.putExtra("mePhone",this.mePhone);
                        startActivityForResult(intent2,0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                break;

            case R.id.see_photp_layout:
                Intent intent3 = new Intent(this, MeCircleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("circle_level","0");
                bundle.putString("mePhone",this.friendPhone);
                intent3.putExtras(bundle);
                startActivityForResult(intent3,0);
                break;

            case R.id.header_logo_fragment_user:
                Intent intent4 = new Intent(this, PhotoActivity.class);
                Uri uri = Uri.parse((RedCircleManager.HTTP_BASE_URL + "/downPhotoByPhone?mePhone=" + this.friendPhone) + "&type=original&random=" + Math.random());
                intent4.putExtra("photo", uri);
                startActivity(intent4);

        }
    }


    public void initData() {
        RedCircleManager.getUserRelationById(this,
                mePhone,
                this.friendPhone,
                new HttpRequestHandler<JSONObject>() {

                    @Override
                    public void onSuccess(JSONObject data) {

                        try {


                            friendName = data.getString("name");

                            if ("null".equals(friendName)) {
                                friendName = "暂未填写";
                            }


                            String sex = data.getString("sex");

                            if ("null".equals(sex)) {
                                sex = "暂未填写";
                            }

                            nameTextView.setText(friendName);
                            sexTextView.setText(sex);
                            phoneTextView.setText(friendPhone);
                            recommandLanguageView.setText(data.getString("recommend_language"));
                            ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
                            imageLoader.displayImage(RedCircleManager.HTTP_BASE_URL + "/downPhotoByPhone?mePhone=" + data.getString("me_phone") + "&type=thumbnail", photoImageView);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(JSONObject data, int totalPages, int currentPage) {

                    }

                    @Override
                    public void onFailure(String error) {
//                        MessageUtils.showErrorMessage(LoginActivity.this, error);
                        showProgressBar(false);
                    }
                });
    }
}
