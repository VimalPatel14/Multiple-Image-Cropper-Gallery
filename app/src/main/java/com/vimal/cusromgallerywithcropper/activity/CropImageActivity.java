package com.vimal.cusromgallerywithcropper.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.theartofdev.edmodo.cropper.CropImageView;
import com.vimal.cusromgallerywithcropper.R;
import com.vimal.cusromgallerywithcropper.adapter.CropAdapter;
import com.vimal.cusromgallerywithcropper.adapter.ThumbnailAdapter;
import com.vimal.cusromgallerywithcropper.callback.RecyclerItemClickListener;
import com.vimal.cusromgallerywithcropper.model.GalleryImage;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CropImageActivity extends AppCompatActivity implements CropImageView.OnCropImageCompleteListener, CropAdapter.OnShareClickedListener {

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    ArrayList<GalleryImage> pathList = new ArrayList<>();
    //    ArrayList<GalleryImage> selectimagearray = new ArrayList<>();
    int imgsel = 0;
    private CropImageView mCropImageView;
    public static boolean isCrop = false;
    ImageView done, doneall;
    Boolean edit = false;
    RecyclerView recyclerviewcategory;
    CropAdapter cropadapter;

    private Bitmap image;
    private Bitmap filteredImage;
    private List<ThumbnailItem> thumbnailsList = new ArrayList<>();
    private RecyclerView recyclerFilters;
    private ThumbnailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        findViewById();
//        selectimagearray.clear();
        pathList.clear();

        if (getIntent().getExtras() != null) {
            edit = getIntent().getBooleanExtra("edit", false);
            imgsel = 0;
            if (edit) {
                pathList = (ArrayList<GalleryImage>) getIntent().getSerializableExtra("imagelist");
            } else {
                pathList = (ArrayList<GalleryImage>) getIntent().getSerializableExtra("imagelist");
            }


            setImageUri(pathList.get(0).getImagepath());

            if (pathList.size() > 0) {


                image = GetBitmapFromPath(pathList.get(0).getImagepath());

                cropadapter = new CropAdapter(CropImageActivity.this, pathList);
                LinearLayoutManager HorizontalLayoutcat = new LinearLayoutManager(CropImageActivity.this, LinearLayoutManager.HORIZONTAL, false);
                recyclerviewcategory.setLayoutManager(HorizontalLayoutcat);
                recyclerviewcategory.setAdapter(cropadapter);
                cropadapter.setOnShareClickedListener(this);
                recyclerviewcategory.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_right_to_left));
            }


//            img_crop.setBackground(null);
//            img_crop.setScaleType(CropImageView.ScaleType.FIT_CENTER);

// start cropping activity for pre-acquired image saved on the device

        }

        configRecyclerView();

    }

    public void findViewById() {

        Uri myUri = Uri.fromFile(new File(getExternalCacheDir(), "image.jpeg"));
        Log.e("vml", myUri + " myUri");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Your toolbar is now an action bar and you can use it like you always do, for example:
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                onBackPressed();

                isCrop = true;


            }
        });

        recyclerviewcategory = findViewById(R.id.recyclerviewcategory);

        doneall = findViewById(R.id.doneall);
        done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri myUri = Uri.fromFile(new File(getExternalCacheDir(), "image" + imgsel + ".jpeg"));
                mCropImageView.saveCroppedImageAsync(myUri);
                pathList.get(imgsel).setImagepath(myUri.getPath());
                pathList.get(imgsel).setAutocrop(true);

            }
        });

        mCropImageView = findViewById(R.id.CropImageView);
        mCropImageView.setFixedAspectRatio(true);
        mCropImageView.setMinCropResultSize(720, 1280);
        mCropImageView.setAutoZoomEnabled(false);
        mCropImageView.setGuidelines(CropImageView.Guidelines.ON);
        mCropImageView.setAspectRatio(9, 16);
        mCropImageView.setOnCropImageCompleteListener(this::onCropImageComplete);

        doneall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < pathList.size(); i++) {
                    if (!pathList.get(i).getAutocrop()) {
                        ResizeImage(pathList.get(i).getImagepath(), i + "", i);
                    }
                }
                Intent intent = new Intent(CropImageActivity.this, ImageDisplayActivity.class);
                intent.putExtra("imagelist", pathList);
                startActivity(intent);
            }
        });


    }

    public void setImageUri(String path) {
        mCropImageView.setImageUriAsync(Uri.fromFile(new File(path)));
    }


    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        if (edit) {
            Intent intent = new Intent();
            intent.putExtra("MESSAGE", pathList.get(0).getImagepath());
            setResult(2, intent);
            finish();//finishing activity
        } else {
            cropadapter.notifyItemChanged(imgsel);
            imgsel = imgsel + 1;
            if (imgsel < pathList.size()) {
                mCropImageView.invalidate();
                mCropImageView.setImageUriAsync(null);
                setImageUri(pathList.get(imgsel).getImagepath());
                image = GetBitmapFromPath(pathList.get(imgsel).getImagepath());
                recoverFiltersList();
            } else {

                for (int i = 0; i < pathList.size(); i++) {
                    if (!pathList.get(i).getAutocrop()) {
                        ResizeImage(pathList.get(i).getImagepath(), i + "", i);
                    }
                }
                Intent intent = new Intent(CropImageActivity.this, ImageDisplayActivity.class);
                intent.putExtra("imagelist", pathList);
                startActivity(intent);
            }
        }


    }

    public void ResizeImage(String orginalpath, String filename, int pos) {
        Log.e("vml", orginalpath + " orginalpathc");
        File dir = new File(Environment.getExternalStorageDirectory().toString() + "/Android/data/com.vimal.cusromgallerywithcropper/cache");
        if (!dir.exists()) {
            dir.mkdir();
        }
        Bitmap b = BitmapFactory.decodeFile(orginalpath);
        Bitmap out = resizeBitmapFitXY(720, 1280, b);


        File file = new File(dir, "image" + filename + ".jpeg");
        if (file.exists()) {
            file.delete();
        }
        pathList.get(pos).setAutocrop(true);
        pathList.get(pos).setImagepath(file.getPath());

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


    @Override
    public void ShareClicked(int pos) {
        imgsel = pos;
        image = GetBitmapFromPath(pathList.get(pos).getImagepath());
        setImageUri(pathList.get(pos).getImagepath());


        recoverFiltersList();


    }

    private void recoverFiltersList() {
        ThumbnailsManager.clearThumbs();
        thumbnailsList.clear();

        // Config default template image filter
        ThumbnailItem noFilterItem = new ThumbnailItem();
        noFilterItem.image = image;
        noFilterItem.filterName = "No filter";
        ThumbnailsManager.addThumb(noFilterItem);

        // List all library filters
        List<Filter> filters = FilterPack.getFilterPack(this);
        for (Filter filter : filters) {
            ThumbnailItem item = new ThumbnailItem();
            item.image = image;
            item.filterName = filter.getName();
            item.filter = filter;

            ThumbnailsManager.addThumb(item);
        }

        thumbnailsList.addAll(ThumbnailsManager.processThumbs(this));
        adapter = new ThumbnailAdapter(CropImageActivity.this, thumbnailsList);
        recyclerFilters.setAdapter(adapter);
    }


    private void configRecyclerView() {
        recyclerFilters = findViewById(R.id.recyclerFilters);

        recyclerFilters.setHasFixedSize(true);
        // Layout Manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerFilters.setLayoutManager(layoutManager);
        // Adapter
        recoverFiltersList();

        setRecyclerViewListener();

    }

    private void setRecyclerViewListener() {
        recyclerFilters.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerFilters,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                filteredImage = image.copy(Bitmap.Config.ARGB_8888, true);
                                Filter filter = thumbnailsList.get(position).filter;
                                mCropImageView.setImageBitmap(filter.processFilter(filteredImage));
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            }
                        }
                )
        );
    }

    public Bitmap GetBitmapFromPath(String path) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


}