package com.labralab.shoppinglist.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import com.labralab.shoppinglist.R;
import com.labralab.shoppinglist.view.MainActivity;

import java.io.File;

/**
 * Created by pc on 27.05.2018.
 */

public class PermissionsUtils {

    public static final int PERMISSION_REQUEST_MACE_PHOTO = 10;
    public static final int PERMISSION_REQUEST_GET_PHOTO = 11;

    MainActivity mainActivity;

    public PermissionsUtils(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    //Проверяем получено ли permission ранее
    public boolean hasPermissions() {

        int res;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        for (String perms : permissions) {
            res = mainActivity.checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    //Вызываем диалог получения permission
    public void requestPerms(int requestCode) {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainActivity.requestPermissions(permissions, requestCode);
        }
    }

    //Показ Snackbar при отказе
    public void showNoStoragePermissionSnackbar(final int requestCode) {

        Snackbar.make(mainActivity.findViewById(R.id.activity_view), "Storage permission isn't granted", Snackbar.LENGTH_LONG)
                .setAction("SETTINGS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openApplicationSettings(requestCode);

                    }
                })
                .show();
    }

    //Вызов диалога настроек
    public void openApplicationSettings(int requestCode) {

        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + mainActivity.getPackageName()));
        mainActivity.startActivityForResult(appSettingsIntent, requestCode);
    }

    //Вызов Snackbar при отказе ранее
    public void requestPermissionWithRationale(final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            final String message = "Storage permission is needed to show files count";
            Snackbar.make(mainActivity.findViewById(R.id.activity_view), message, Snackbar.LENGTH_LONG)
                    .setAction("GRANT", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPerms(requestCode);
                        }
                    })
                    .show();
        } else {
            requestPerms(requestCode);
        }
    }
}