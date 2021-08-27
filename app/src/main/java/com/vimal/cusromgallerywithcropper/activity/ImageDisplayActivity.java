package com.vimal.cusromgallerywithcropper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.vimal.cusromgallerywithcropper.R;
import com.vimal.cusromgallerywithcropper.adapter.MyListAdapter;
import com.vimal.cusromgallerywithcropper.callback.CallbackItemTouch;
import com.vimal.cusromgallerywithcropper.callback.MyItemTouchHelperCallback;
import com.vimal.cusromgallerywithcropper.model.GalleryImage;

import java.util.ArrayList;

public class ImageDisplayActivity extends AppCompatActivity implements CallbackItemTouch, MyListAdapter.OnShareClickedListener {

    ArrayList<GalleryImage> pathList = new ArrayList<>();
    MyListAdapter adapter;
    int editpos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        if (getIntent().getExtras() != null) {
            pathList = (ArrayList<GalleryImage>) getIntent().getSerializableExtra("imagelist");
        }


        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(ImageDisplayActivity.this, 2));
        if (pathList.size() > 0) {
            adapter = new MyListAdapter(ImageDisplayActivity.this, pathList);
            recyclerView.setAdapter(adapter);
            adapter.setOnShareClickedListener(this);
            ItemTouchHelper.Callback callback = new MyItemTouchHelperCallback(ImageDisplayActivity.this);// create MyItemTouchHelperCallback
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback); // Create ItemTouchHelper and pass with parameter the MyItemTouchHelperCallback
            touchHelper.attachToRecyclerView(recyclerView); // Attach ItemTouchHelper to RecyclerView
        }

    }

    @Override
    public void itemTouchOnMove(int oldPosition, int newPosition) {
        pathList.add(newPosition, pathList.remove(oldPosition));
        adapter.notifyItemMoved(oldPosition, newPosition); //notifies changes in adapter, in this case use the notifyItemMoved
    }

    @Override
    public void ShareClicked(int pos, String path) {
        editpos = pos;
        Log.e("vml", path);
        Log.e("vml", editpos + " editpos");

        ArrayList<GalleryImage> pathListedit = new ArrayList<>();
        pathListedit.add(new GalleryImage(path, false));

        Intent intent = new Intent(ImageDisplayActivity.this, CropImageActivity.class);
        intent.putExtra("imagelist", pathListedit);
        intent.putExtra("edit", true);
        startActivityForResult(intent, 2);// Activity is started with requestCode 2
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 2) {
            if (data != null) {
                String message = data.getStringExtra("MESSAGE");
                pathList.get(editpos).setImagepath(message);
                adapter.notifyItemChanged(editpos);
            }
        }

        Log.e("vml", pathList.size() + " size");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ImageDisplayActivity.this, MainActivity.class);
        startActivity(intent);
    }
}