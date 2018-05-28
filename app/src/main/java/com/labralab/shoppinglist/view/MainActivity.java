package com.labralab.shoppinglist.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.labralab.shoppinglist.App;
import com.labralab.shoppinglist.R;
import com.labralab.shoppinglist.presenter.MainPresenter;
import com.labralab.shoppinglist.presenter.adapter.MainAdapter;
import com.labralab.shoppinglist.utils.PermissionsUtils;


import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainView {

    private static final int PERMISSION_REQUEST_MACE_PHOTO = 10;
    private static final int PERMISSION_REQUEST_GET_PHOTO = 11;

    @Inject
    MainPresenter mainPresenter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.mainRV)
    RecyclerView mainRV;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.mainHint)
    TextView mainHint;

    PermissionsUtils permissionsUtils;
    Intent photoPickerIntent;

    private RecyclerView.LayoutManager layoutManager;
    private MainAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        initView();

        App.getAppComponents().inject(this);
        mainPresenter.attachView(this);

        permissionsUtils = new PermissionsUtils(this);
    }

    private void initView() {
        //RecyclerView
        layoutManager = new LinearLayoutManager(this);
        mainRV.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mainRV.setLayoutManager(layoutManager);
        mainRV.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     *  _________Переопределяем ряд стандартных методов AppCompatActivity_________
     */

    //Добавление меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Обработка нажатия на пункты меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.removeAll:

                mainPresenter.removeAll();
                return true;

            case R.id.allBought:

                mainPresenter.allBought();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Обработка нажатия на fab
    @OnClick(R.id.fab)
    public void onViewClicked() {
        mainPresenter.createNewItem();
    }

    //Отсоеденяемся от презентера при уничтожении
    @Override
    protected void onDestroy() {
        mainPresenter.detachView();
        super.onDestroy();
    }

    //Обработка нажатия "назад"
    @Override
    public void onBackPressed() {
        //Если форма ввода существует то сперва удаляем её
        if(mainPresenter.removeCreatingForm()){
            super.onBackPressed();
        }
    }

    //Обработка ответов при вызове startActivityForResult(intent, teg)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            //Результат из галереи
            case MainPresenter.FROM_GALLERY:
                if (resultCode == RESULT_OK) {
                    mainPresenter.setUriToItem(imageReturnedIntent.getData());
                }
                break;
            //Результат из камеры
            case MainPresenter.FROM_CAMERA:
                if (resultCode == RESULT_OK) {
                    mainPresenter.setUriToItem();
                }
            //Результат из камеры после получения PERMISSION
            case PERMISSION_REQUEST_MACE_PHOTO:
                if (resultCode == RESULT_OK) {
                    mainPresenter.setUriToItem();
                }
                break;
            //Результат из галереи после получения PERMISSION
            case PERMISSION_REQUEST_GET_PHOTO:
                if (resultCode == RESULT_OK) {
                    mainPresenter.setUriToItem(imageReturnedIntent.getData());
                }
                break;
        }
    }

    //Ожидание ответа получения PERMISSION
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;

        switch (requestCode) {
            case PermissionsUtils.PERMISSION_REQUEST_GET_PHOTO:
                for (int res : grantResults) {
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;
            case PermissionsUtils.PERMISSION_REQUEST_MACE_PHOTO:
                for (int res : grantResults) {
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }

        if (allowed) {
            if (photoPickerIntent != null) {
                startActivityForResult(photoPickerIntent, requestCode);
            }
        } else {
            // we will give warning to user that they haven't granted permissions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "Storage Permissions denied.", Toast.LENGTH_SHORT).show();

                } else {
                    permissionsUtils.showNoStoragePermissionSnackbar(requestCode);
                }
            }
        }
    }

    /**
     *  _________Методы интерфейса MainView_________
     */

    //Подключение адаптера
    @Override
    public void setAdapter(MainAdapter adapter) {
        this.adapter = adapter;
        mainRV.setAdapter(adapter);
    }

    //Показать FAB
    @Override
    public void showFAB() {
        fab.setVisibility(View.VISIBLE);
    }

    //Спрятать FAB
    @Override
    public void hideFAB() {
        fab.setVisibility(View.INVISIBLE);
    }

    //Показать mainHint
    @Override
    public void showHint() {
        mainHint.setVisibility(View.VISIBLE);
    }

    //Спрятать mainHint
    @Override
    public void hideHit() {
        mainHint.setVisibility(View.INVISIBLE);
    }

    //Вызов диалога для волучения изображения
    @Override
    public void getImageDialog(Intent intent, int teg) {


        photoPickerIntent = intent;
        int permCode = 0;
        switch (teg) {
            case MainPresenter.FROM_CAMERA:
                permCode = PermissionsUtils.PERMISSION_REQUEST_MACE_PHOTO;
                break;
            case MainPresenter.FROM_GALLERY:
                permCode = PermissionsUtils.PERMISSION_REQUEST_GET_PHOTO;
                break;
        }

        //Если разре получено
        if (permissionsUtils.hasPermissions()) {
            startActivityForResult(intent, teg);

            //Если разрешение не получено
        } else {
            permissionsUtils.requestPermissionWithRationale(permCode);
        }
    }

}

