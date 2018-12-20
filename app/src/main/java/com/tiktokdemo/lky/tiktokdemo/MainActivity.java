package com.tiktokdemo.lky.tiktokdemo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.tiktokdemo.lky.tiktokdemo.record.RecordVideoActivity;
import com.tiktokdemo.lky.tiktokdemo.record.VideoCropActivity;
import com.tiktokdemo.lky.tiktokdemo.record.bean.MusicBean;
import com.tiktokdemo.lky.tiktokdemo.utils.AppUtil;
import com.tiktokdemo.lky.tiktokdemo.utils.FileUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String LOCAL_MUSIC_NAME = "RISE.mp3";
    private final String LOCAL_VIDEO_NAME = "RBB.mp4";
    private String mLocalMusicPath = Constant.PIC_FILE + File.separator + LOCAL_MUSIC_NAME;
    private String mLocalVideoPath = Constant.PIC_FILE + File.separator + LOCAL_VIDEO_NAME;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.main_record_btn).setOnClickListener(this);
        findViewById(R.id.main_crop_btn).setOnClickListener(this);
        requestPermissions();
    }
    @Override public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_record_btn:
                if(!FileUtils.checkFileExits(mLocalMusicPath)){
                    FileUtils.copyFileFromAssets(this,LOCAL_MUSIC_NAME,Constant.PIC_FILE);
                }
                MusicBean searchMusicResultBean = new MusicBean();
                searchMusicResultBean.setMusicId(1);
                searchMusicResultBean.setUrl(mLocalMusicPath);
                searchMusicResultBean.setLocalPath(mLocalMusicPath);
                searchMusicResultBean.setName(LOCAL_MUSIC_NAME);
                Intent intent = new Intent(this, RecordVideoActivity.class);
                intent.putExtra("MusicBean",searchMusicResultBean);
                startActivity(intent);
                break;
            case R.id.main_crop_btn:
                if(!FileUtils.checkFileExits(mLocalVideoPath)){
                    FileUtils.copyFileFromAssets(this,LOCAL_VIDEO_NAME,Constant.PIC_FILE);
                }
                Intent cropIntent = new Intent(this, VideoCropActivity.class);
                cropIntent.putExtra("mCurrentVideoPath",mLocalVideoPath);
                startActivity(cropIntent);
                break;
        }
    }


    private void requestPermissions() {
        String[] permissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        applyPermissions(permissions);
    }


    protected PermissionTask permissionTask;
    protected OnPermissionDenyListener mOnPermissionDenyListener;
    protected OnCompleteListener onCompleteListener;
    public static final int ACTIVITY_PERMISSION_CODE = 1000;
    public static final int ACTIVITY_PERMISSIONS_CODE = 1500;

    protected void setPermissionTask(PermissionTask task) {
        this.permissionTask = task;
    }

    protected void applyPermission(String permission, PermissionTask task, OnPermissionDenyListener onPermissionDenyListener) {
        applyPermission(permission, task, onPermissionDenyListener, null);
    }

    protected void applyPermission(String permission,
                                   PermissionTask task,
                                   OnPermissionDenyListener onPermissionDenyListener
            , OnCompleteListener onCompleteListener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (task != null) {
                task.operate();
            }
            return;
        }
        this.mOnPermissionDenyListener = onPermissionDenyListener;
        this.onCompleteListener = onCompleteListener;
        setPermissionTask(task);
        permission(permission);
    }

    protected void applyPermissions(String[] permissions) {
        List<String> result = new ArrayList<>();
        if (permissions != null && permissions.length > 0) {
            for (String permission : permissions) {
                if (!(ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED)) {
                    result.add(permission);
                }
            }
            if (result.size() > 0)
                ActivityCompat.requestPermissions(this, result.toArray(new String[result.size()]), ACTIVITY_PERMISSIONS_CODE);
        }
    }


    protected void applyPermission(String permission, PermissionTask task) {
        applyPermission(permission, task, null);
    }


    protected void permission(String permission) {
        if (!(ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                //已经禁止提示了
                if (mOnPermissionDenyListener != null) {
                    mOnPermissionDenyListener.onPermissionDeny();
                }
                Toast.makeText(AppUtil.getApplicationContext(), String.format(getString(R.string.permission_prohibit_tip), getRefuseMsg(permission)), Toast.LENGTH_SHORT).show();
                onComplete();
                return;
            }
            requestPermission(permission);
        } else {
            if (permissionTask != null) {
                permissionTask.operate();
            }
            onComplete();
        }
    }

    private void onComplete() {
        if (onCompleteListener != null) {
            onCompleteListener.onPermissionComplete();
        }
    }

    private void requestPermission(String permission) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, ACTIVITY_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACTIVITY_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //用户同意授权
                    if (permissionTask != null) {
                        permissionTask.operate();
                    }
                    onComplete();
                } else {
                    //用户拒绝授权
                    if (mOnPermissionDenyListener != null) {
                        mOnPermissionDenyListener.onPermissionDeny();
                    }
                    if (permissions == null || permissions.length == 0) {
                        Toast.makeText(AppUtil.getApplicationContext(), String.format(getString(R.string.permission_prohibit_tip), getString(R.string.permission_default)), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AppUtil.getApplicationContext(), String.format(getString(R.string.permission_prohibit_tip), getRefuseMsg(permissions[0])), Toast.LENGTH_SHORT).show();
                    }

                    onComplete();
                }
                break;
        }
    }



    public interface PermissionTask {
        void operate();
    }

    public interface OnPermissionDenyListener {
        void onPermissionDeny();
    }

    public interface OnCompleteListener {
        void onPermissionComplete();
    }

    private String getRefuseMsg(String permission) {
        String permissionStr = getString(R.string.permission_default);
        if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionStr = getString(R.string.permission_location);
        } else if (permission.equals(Manifest.permission.CAMERA)) {
            permissionStr = getString(R.string.permission_camera);
        } else if (permission.equals(Manifest.permission.RECORD_AUDIO)) {
            permissionStr = getString(R.string.permission_record);
        } else if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionStr = getString(R.string.permission_storage);
        } else if (permission.equals(Manifest.permission.CALL_PHONE)) {
            permissionStr = getString(R.string.permission_phone);
        }
        return permissionStr;
    }

}
