package com.example.mleykin.handlerthread;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> StoragePaths;
    private Subscription subscription = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }
        else {
            String[] projection = new String[] {
                    MediaStore.Images.Media.DATA,
            };

            Uri ImagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Cursor cur = managedQuery(ImagesUri, projection,"",null,"");

            StoragePaths = new ArrayList<String>();
            if (cur.moveToFirst()) {
                int dataColumn = cur.getColumnIndex(MediaStore.Images.Media.DATA);

                do {
                    StoragePaths.add(cur.getString(dataColumn));
                } while (cur.moveToNext());
            }
            cur.close();
        }
    }

    public void onclickStart(View v) {
        if (subscription != null) {
            subscription.unsubscribe();
        }

        Collections.shuffle(StoragePaths);
        Observable<String> observableImagePaths = Observable.from(StoragePaths);
        subscription = observableImagePaths.subscribeOn(Schedulers.io()).doOnNext(s -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Path -> {
                    try {
                        Bitmap currentBitmap = BitmapFactory.decodeFile(Path);
                        ImageView ivRandom = (ImageView)findViewById(R.id.ivRandom);
                        ivRandom.setImageBitmap(currentBitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    public void onclickStop(View v) {
        subscription.unsubscribe();
        ImageView ivRandom = (ImageView)findViewById(R.id.ivRandom);
        ivRandom.setImageResource(android.R.color.transparent);
    }
}
