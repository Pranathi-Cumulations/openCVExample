package com.example.opencvex;

import static org.opencv.imgproc.Imgproc.rectangle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity {

    Button camera, select;
    ActivityResultLauncher<Intent> selectActivityResultLauncher;
    ActivityResultLauncher<Intent> clickActivityResultLauncher;
    ImageView imageView;
    Bitmap bitmap;
    Mat mat;
    int SELECT_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(OpenCVLoader.initDebug()){
            Log.d("Loaded", "success");
        }
        else{
            Log.d("Loaded", "error");
        }

        camera = findViewById(R.id.camera);
        select = findViewById(R.id.select);
        imageView = findViewById(R.id.imageView);
        getPermissions();
        openSomeActivityForResult();
        clickSomeActivityForResult();

        select.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                //Launch activity to get result
                selectActivityResultLauncher.launch(intent);

            }

        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                clickActivityResultLauncher.launch(intent);
            }
        });
    }

    private void getPermissions() {
        if(checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA},102);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode ==102 && grantResults.length>0){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED)
            {
                getPermissions();
            }
        }
    }

    public void openSomeActivityForResult() {
        selectActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            Uri uri = data.getData();
                            imageView.setImageURI(uri);

                        }
                    }
                });

    }

    public void clickSomeActivityForResult() {
        clickActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            bitmap = (Bitmap) result.getData().getExtras().get("data");
                            mat = new Mat();
                            int w = mat.cols();
                            Utils.bitmapToMat(bitmap, mat);
//                            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);
                            Imgproc.rectangle(mat,  new Point(30, 30), new Point(120,140),new Scalar(0, 0, 255), 4);
                            Utils.matToBitmap(mat, bitmap);
                            imageView.setImageBitmap(bitmap);

                        }
                    }
                });

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == SELECT_CODE && data != null) {
//            Log.d("here", "here");
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
//                imageView.setImageBitmap(bitmap);
//
//                mat = new Mat();
//                Utils.bitmapToMat(bitmap, mat);
//
//                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);
//
//
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
}