package cloud.com.redcircle.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;

import java.io.FileNotFoundException;

import cloud.com.redcircle.LoginActivity;
import cloud.com.redcircle.ModifyActivity;
import cloud.com.redcircle.R;
import cloud.com.redcircle.api.RedCircleManager;
import cloud.com.redcircle.ui.ButtonAwesome;
import cloud.com.redcircle.ui.TextAwesome;
import cloud.com.redcircle.utils.AccountUtils;
import cloud.com.redcircle.utils.FileUtils;

/**
 * Created by zhan on 16/4/25.
 */
public class MeFragment extends BaseFragment implements View.OnClickListener {


    private TextView phoneNumberTextView;
    private TextView usernameTextView;
    private TextView sexTextView;
    private ImageView photoImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_me, container, false);

        View logoutTextView = rootView.findViewById(R.id.login_logout_txt);
        phoneNumberTextView = (TextView) rootView.findViewById(R.id.phone_number_txt);
        usernameTextView = (TextView) rootView.findViewById(R.id.username_txt);
        sexTextView = (TextView) rootView.findViewById(R.id.sex_txt);
        photoImageView = (ImageView) rootView.findViewById(R.id.photo_imageView);


        RelativeLayout phoneCell = (RelativeLayout)rootView.findViewById(R.id.name_layout);
        RelativeLayout sexCell = (RelativeLayout)rootView.findViewById(R.id.sex_layout);
        RelativeLayout photoCell = (RelativeLayout)rootView.findViewById(R.id.photo_layout);




        logoutTextView.setOnClickListener(this);
        phoneCell.setOnClickListener(this);
        sexCell.setOnClickListener(this);
        photoCell.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case 0:
                    this.initData();
                    break;
                case 2:
                    Uri uri = data.getData();
                    Log.e("uri", uri.toString());
                    ContentResolver cr = this.getActivity().getContentResolver();
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        ImageView imageView = (ImageView) getActivity().findViewById(R.id.photo_imageView);
                /* 将Bitmap设定到ImageView */
                        imageView.setImageBitmap(bitmap);

                        String path = FileUtils.getPathByUri4kitkat(this.getActivity(),uri);

                        String name = phoneNumberTextView.getText().toString() + path.substring(path.lastIndexOf("."));

                        RedCircleManager.uploadFile(this.getActivity(),path,name);
                    } catch (FileNotFoundException e) {
                        Log.e("Exception", e.getMessage(),e);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    break;
                default:
                    break;
            }


        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.login_logout_txt:

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.settings_dialog_hint)
                        .setMessage(R.string.settings_logout_or_not)
                        .setPositiveButton(R.string.title_confirm_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RedCircleManager.logout(getActivity());
                                AccountUtils.removeAll(getActivity());
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton(R.string.title_confirm_cancel, null).show();

                break;
            case R.id.name_layout:
                Intent intent = new Intent(getActivity(), ModifyActivity.class);
                intent.putExtra("name", "姓名");
                startActivityForResult(intent,0);
                break;
            case R.id.sex_layout:
                Intent intent1 = new Intent(getActivity(), ModifyActivity.class);
                intent1.putExtra("name", "性别");
                startActivityForResult(intent1,0);
                break;
            case R.id.photo_layout:
                Intent local = new Intent();
                local.setType("image/*");
                local.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(local, 2);
                break;
        }

    }

    private void initData() {
        if (mIsLogin) {
            try {
                phoneNumberTextView.setText(mUser.getString("mePhone"));
                usernameTextView.setText(mUser.getString("name"));
                sexTextView.setText(mUser.getString("sex"));
                ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
                imageLoader.displayImage(RedCircleManager.HTTP_BASE_URL + "/downPhotoByPhone?mePhone=18706734109", photoImageView);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
