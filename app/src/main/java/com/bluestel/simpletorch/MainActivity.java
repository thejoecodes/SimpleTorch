package com.bluestel.simpletorch;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 50;
    public ImageView bImageView;
    private boolean flashLightStatus = false;
    private CameraManager bCameraManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(checkPermission()) bImageView.setOnClickListener(new View.OnClickListener() {
            final boolean hasCameraFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

            @Override
            public void onClick(View v) {
                if (hasCameraFlash) {
                    if (flashLightStatus)
                        flashLightOff();
                    else
                        flashLightOn();
                }else
                        Toast.makeText(MainActivity.this, "No flash available on your device",
                                Toast.LENGTH_SHORT).show();

            }


            private void flashLightOn() {
                bCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

                try {
                    String CameraId = bCameraManager.getCameraIdList()[0];
                    bCameraManager.setTorchMode(CameraId, true);
                    flashLightStatus = true;
                    bImageView.setImageResource(R.drawable.btn_switch_on);
                    }catch (CameraAccessException e){

                }
            }

            private void flashLightOff() {

                bCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

                try {
                    String CameraId = bCameraManager.getCameraIdList()[0];
                    bCameraManager.setTorchMode(CameraId, false);
                    flashLightStatus = false;
                    bImageView.setImageResource(R.drawable.btn_torch_off);
                }catch (CameraAccessException e){

                }

            }
        });
        else {
            requestPermissions();
        }
    }
    private boolean checkPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            !=PackageManager.PERMISSION_GRANTED){
            //Camera permision not granted
            return false;
        }else {
            return true;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), "You Can Switch On The Light", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),"Sorry, You Cannot Use the Light", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        !=PackageManager.PERMISSION_GRANTED){
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions();
                                            }
                                        }
                                    });
                        }
                    }
                }
        }
    }

    private void showMessageOKCancel(String s, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(s)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}

