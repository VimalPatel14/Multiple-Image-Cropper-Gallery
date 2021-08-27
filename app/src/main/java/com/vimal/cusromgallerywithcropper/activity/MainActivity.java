package com.vimal.cusromgallerywithcropper.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.vimal.cusromgallerywithcropper.R;
import com.vimal.cusromgallerywithcropper.model.GalleryImage;
import com.vimal.cusromgallerywithcropper.utils.MediaLoader;
import com.vimal.mylibrary.Helpers;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.widget.Widget;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<AlbumFile> selectimagearray = new ArrayList<>();
    ImageView imgswipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectimagearray.clear();


        imgswipe = findViewById(R.id.imgswipe);
        Glide.with(this).load(R.raw.wait).into(imgswipe);

        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(new MediaLoader())
                .build());

        findViewById(R.id.image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();

                Helpers.Method();
            }
        });

        Deletefiles(Environment.getExternalStorageDirectory().toString() + "/Android/data/com.vimal.cusromgallerywithcropper/cache");
    }


    private void selectImage() {
        Album.image(MainActivity.this)
                .multipleChoice()
                .camera(true)
                .columnCount(3)
                .selectCount(6)
                .widget(
                        Widget.newDarkBuilder(this)
                                .title("Select Image")
                                .build()
                )
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(@NonNull ArrayList<AlbumFile> result) {


//                        CreateDialog(result);

                        ArrayList<GalleryImage> galleryImages = new ArrayList<>();

                        for (int i=0;i<result.size();i++){
                            galleryImages.add(new GalleryImage(result.get(i).getPath(),false));
                        }

                        Intent intentpass = new Intent(MainActivity.this, CropImageActivity.class);
                        intentpass.putExtra("imagelist", galleryImages);
                        intentpass.putExtra("edit", false);
                        startActivity(intentpass);
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                        Toast.makeText(MainActivity.this, R.string.canceled, Toast.LENGTH_LONG).show();
                    }
                })
                .start();
    }

    public void ResizeImage(String orginalpath, String filename) {
        Log.e("vml", orginalpath + " orginalpathc");
        File dir = new File(Environment.getExternalStorageDirectory().toString() + "/Android/data/com.vimal.cusromgallerywithcropper/cache");
        if (!dir.exists()) {
            dir.mkdir();
        }
        Bitmap b = BitmapFactory.decodeFile(orginalpath);
        Bitmap out = resizeBitmapFitXY(720, 1280, b);


        File file = new File(dir, "resize" + filename + ".png");
        if (file.exists()) {
            file.delete();
        }
        AlbumFile newdata = new AlbumFile();
        newdata.setPath(file.getPath());
        selectimagearray.add(newdata);
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            out.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            b.recycle();
            out.recycle();
        } catch (Exception e) {
        }
    }

    public Bitmap resizeBitmapFitXY(int width, int height, Bitmap bitmap) {
        Bitmap background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        float originalWidth = bitmap.getWidth(), originalHeight = bitmap.getHeight();
        Canvas canvas = new Canvas(background);
        float scale, xTranslation = 0.0f, yTranslation = 0.0f;
        if (originalWidth > originalHeight) {
            scale = height / originalHeight;
            xTranslation = (width - originalWidth * scale) / 2.0f;
        } else {
            scale = width / originalWidth;
            yTranslation = (height - originalHeight * scale) / 2.0f;
        }
        Matrix transformation = new Matrix();
        transformation.postTranslate(xTranslation, yTranslation);
        transformation.preScale(scale, scale);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(bitmap, transformation, paint);
        return background;
    }

    public void CreateDialog(ArrayList<AlbumFile> result){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);

        Button btn_custom = dialog.findViewById(R.id.btn_custom);
        Button btn_auto = dialog.findViewById(R.id.btn_auto);

        btn_custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intentpass = new Intent(MainActivity.this, CropImageActivity.class);
                intentpass.putExtra("imagelist", result);
                intentpass.putExtra("edit", false);
                startActivity(intentpass);
                Log.e("vml", result.size() + " image" + result);
            }
        });

        btn_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                imgswipe.setVisibility(View.VISIBLE);
                findViewById(R.id.image).setVisibility(View.GONE);
                selectimagearray.clear();

                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        for (int i = 0; i < result.size(); i++) {
                            ResizeImage(result.get(i).getPath(), i + "");

                            if (i == result.size() - 1) {
                                imgswipe.setVisibility(View.GONE);
                                Intent intent = new Intent(MainActivity.this, ImageDisplayActivity.class);
                                intent.putExtra("imagelist", selectimagearray);
                                startActivity(intent);
                            }
                        }
                    }
                }, 100);


            }
        });

        dialog.show();
    }

    public void Deletefiles(String path){
        File dir = new File(path);
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dir, children[i]).delete();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}