package cloud.com.redcircle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.pizidea.imagepicker.AndroidImagePicker;
import com.pizidea.imagepicker.bean.ImageItem;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import cloud.com.redcircle.api.HttpRequestHandler;
import cloud.com.redcircle.api.RedCircleManager;

/**
 * Created by zhan on 16/6/6.
 */
public class AddArticleActivity extends BaseActivity implements  AndroidImagePicker.OnImagePickCompleteListener {

    private GridView gridView;              //网格显示缩略图
    private Bitmap bmp;                      //导入临时图片
    private ArrayList<HashMap<String, Object>> imageItem;
    private SimpleAdapter simpleAdapter;     //适配器
    private List<ImageItem> items;
    private EditText editText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article);

        setTitle("");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        gridView = (GridView) findViewById(R.id.gridView1);
        editText = (EditText) findViewById(R.id.editText1);

                /*
         * 载入默认图片添加图片加号
         * 通过适配器实现
         * SimpleAdapter参数imageItem为数据源 R.layout.griditem_addpic为布局
         */
        //获取资源图片加号
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.gridview_addpic);
        imageItem = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("itemImage", bmp);
        imageItem.add(map);
        simpleAdapter = new SimpleAdapter(this,
                imageItem, R.layout.griditem_addpic,
                new String[] { "itemImage"}, new int[] { R.id.imageView1});
        /*
         * HashMap载入bmp图片在GridView中不显示,但是如果载入资源ID能显示 如
         * map.put("itemImage", R.drawable.img);
         * 解决方法:
         *              1.自定义继承BaseAdapter实现
         *              2.ViewBinder()接口实现
         *  参考 http://blog.csdn.net/admin_/article/details/7257901
         */
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                // TODO Auto-generated method stub
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView i = (ImageView)view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        gridView.setAdapter(simpleAdapter);



                /*
         * 监听GridView点击事件
         * 报错:该函数必须抽象方法 故需要手动导入import android.view.View;
         */
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                if( imageItem.size() > 9) { //第一张为默认图片
                    Toast.makeText(AddArticleActivity.this, "图片数9张已满", Toast.LENGTH_SHORT).show();
                }
                else if(position == 0) { //点击图片位置为+ 0对应0张图片
                    Toast.makeText(AddArticleActivity.this, "添加图片", Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent();
                    AndroidImagePicker.getInstance().setSelectMode(AndroidImagePicker.Select_Mode.MODE_MULTI);
                    AndroidImagePicker.getInstance().setShouldShowCamera(true);
                    intent.setClass(AddArticleActivity.this,PickerActivity.class);
                    startActivityForResult(intent, 1);
                    //通过onResume()刷新数据
                }
                else {
//                    dialog(position);
                    //Toast.makeText(MainActivity.this, "点击第"+(position + 1)+" 号图片",
                    //      Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_article, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
                break;
            case R.id.do_send_article_action:// 点击返回图标事件
                this.addArticle();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
        AndroidImagePicker.getInstance().setOnImagePickCompleteListener(this);
    }

    @Override
    public void onImagePickComplete(List<ImageItem> items) {

        this.items = items;

//        Bitmap addbmp=BitmapFactory.decodeFile(pathImage);

        for (int i = 0; i < items.size(); i++) {
            ImageItem item = items.get(i);
            HashMap<String, Object> map = new HashMap<String, Object>();
            Bitmap addbmp=BitmapFactory.decodeFile(item.path);
            map.put("itemImage", addbmp);
            imageItem.add(map);

        }


//        map.put("itemImage", addbmp);
//        imageItem.add(map);
        simpleAdapter = new SimpleAdapter(this,
                imageItem, R.layout.griditem_addpic,
                new String[] { "itemImage"}, new int[] { R.id.imageView1});
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                // TODO Auto-generated method stub
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView i = (ImageView)view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        gridView.setAdapter(simpleAdapter);
        simpleAdapter.notifyDataSetChanged();
        //刷新后释放防止手机休眠后自动添加

    }


    private  void addArticle() {
        File[] sourceList = new File[0];
        File[] thumbList = new File[0];


        if (this.items != null && this.items.size() > 0) {
            sourceList = new File[this.items.size()];
            thumbList = new File[this.items.size()];


            for (int i=0; i<this.items.size(); i++) {
                ImageItem item = items.get(i);
                List list = this.generateFile(item.path);
                sourceList[i] = (File) list.get(0);
                thumbList[i] = (File) list.get(1);

            }
        }



        try {

            String mePhone = mUser.getString("mePhone");
            RedCircleManager.addArticle(this, sourceList, thumbList, mePhone, this.editText.getText().toString(), new HttpRequestHandler<JSONObject>() {
                @Override
                public void onSuccess(JSONObject data) {
                    setResult(Activity.RESULT_OK);
                    finish();
                }

                @Override
                public void onSuccess(JSONObject data, int totalPages, int currentPage) {

                }

                @Override
                public void onFailure(String error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private List generateFile (String targetPath) {

        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();


        File imageFileSource = new File(getCacheDir(), uuid1.toString());
        File imageFileThumb = new File(getCacheDir(), uuid2.toString());

        try {

            InputStream is = new FileInputStream(new File(targetPath));

            // 读取图片。
//            InputStream is = getAssets().open("emmy.jpg");

            Bitmap bmpSource = BitmapFactory.decodeStream(is);

            imageFileSource.createNewFile();

            FileOutputStream fosSource = new FileOutputStream(imageFileSource);

            // 保存原图。
            bmpSource.compress(Bitmap.CompressFormat.JPEG, 100, fosSource);

            // 创建缩略图变换矩阵。
            Matrix m = new Matrix();
            m.setRectToRect(new RectF(0, 0, bmpSource.getWidth(), bmpSource.getHeight()), new RectF(0, 0, 160, 160), Matrix.ScaleToFit.CENTER);

            // 生成缩略图。
            Bitmap bmpThumb = Bitmap.createBitmap(bmpSource, 0, 0, bmpSource.getWidth(), bmpSource.getHeight(), m, true);

            imageFileThumb.createNewFile();

            FileOutputStream fosThumb = new FileOutputStream(imageFileThumb);

            // 保存缩略图。
            bmpThumb.compress(Bitmap.CompressFormat.JPEG, 60, fosThumb);

        } catch (IOException e) {
            e.printStackTrace();
        }


        List list = new ArrayList();
        list.add(imageFileSource);
        list.add(imageFileThumb);

        return list;
    }
}
