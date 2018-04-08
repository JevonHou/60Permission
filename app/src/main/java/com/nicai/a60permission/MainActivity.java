package com.nicai.a60permission;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnOpenCamera;
    private static final int REQUEST_PERMISSION_CAMERA_CODE = 001;

    /**
     * 开启拍照页面
     */
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnOpenCamera = findViewById(R.id.btn_open_camera);
        mBtnOpenCamera.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open_camera:
                System.out.println("openCamera按下");
                Toast.makeText(MainActivity.this, "openCamera按下", Toast.LENGTH_LONG);
                openCamera();
                break;
            default:
                break;
        }
    }

    private void openCamera() {
        //  判断是否是6.0(api23)以上系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // api大于等于23时，判断是否具有拍照权限
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                // 具有拍照权限，调用拍照方法
                takePicture();
            } else {
                //  不具有拍照权限，申请拍照权限
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CAMERA_CODE);
            }
        } else {
            //  api小于23时直接拍照
            takePicture();
        }
    }

    /**
     * 拍照
     */
    private void takePicture() {
        // 用隐式意图调用系统相机拍照
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 将文件转换为uri
        File sdCard = Environment.getExternalStorageDirectory();
        System.out.println("sdCard：" + sdCard);
        // 7.0以上报错
//        Uri uri = Uri.fromFile(new File(sdCard + "/a/aa/aaa.jpg"));

        Uri uri = FileProvider.getUriForFile(MainActivity.this,
                "com.nicai.a60permission.fileprovider",
                new File(sdCard + "/a/aa/aaa.jpg"));
        System.out.println(uri.toString());
        // 告诉系统拍照文件的路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // 开启系统拍照界面
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (REQUEST_PERMISSION_CAMERA_CODE == requestCode) {
            if (grantResults.length >= 1) {
                //  相机权限
                int cameraResult = grantResults[0];
                if (cameraResult == PackageManager.PERMISSION_GRANTED) {
                    //  具有拍照权限，调用相机
                    takePicture();
                } else {
                    // 不具有相关权限，给用户相应的提醒，让用户去设置里开启权限
                    System.out.println("请开启相机权限后使用");
                    Toast.makeText(MainActivity.this, "请开启相机权限后使用", Toast.LENGTH_LONG);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 判断是哪个页面开启的
        if (CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE == requestCode) {
            // 说明是拍照页面
            Uri uri = data.getData();
            String path = uri.toString();
            System.out.println("照片保存位置为：" + path);
            Toast.makeText(MainActivity.this, "照片保存位置为：" + path, Toast.LENGTH_LONG);
        }
    }
}
