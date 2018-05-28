package com.labralab.shoppinglist.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.labralab.shoppinglist.R;
import com.labralab.shoppinglist.model.data.CreatingForm;
import com.labralab.shoppinglist.model.data.MyListItem;
import com.labralab.shoppinglist.model.data.Product;
import com.labralab.shoppinglist.model.data.Separator;
import com.labralab.shoppinglist.model.roomDB.AppDatabase;
import com.labralab.shoppinglist.presenter.adapter.MainAdapter;
import com.labralab.shoppinglist.view.MainView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by pc on 22.05.2018.
 */

public class MainPresenter {

    public static final int FROM_GALLERY = 0;
    public static final int FROM_CAMERA = 1;

    Context context;
    AppDatabase db;
    MainView mainView;

    MainAdapter adapter;

    List<MyListItem> items;

    //BaseItems
    CreatingForm creatingForm;
    Separator headerSeparator;
    Separator footerSeparator;

    //Счетчик эллементов "нужно купить"
    int needToBuyListSize;
    //Счетчик эллементов "уже курлено"
    int alreadyBoughtListSize;

    //Буфер для хранения ссылки на изображение
    String imgPath;


    public MainPresenter(AppDatabase appDatabase, Context context) {

        this.db = appDatabase;
        this.context = context;

        items = new ArrayList<>();

        //Получаем списки из базы данных
        getData();

        //Создаем адаптер передавя в него список
        adapter = new MainAdapter(items, this, context);
    }

    //Связываение mainView и mainPresenter
    public void attachView(MainView mainView) {
        this.mainView = mainView;

        mainView.setAdapter(adapter);

        //Определяем состояние mainHint и FAB
        if(needToBuyListSize != 0 || alreadyBoughtListSize != 0){
            mainView.hideHit();
        }
        if(creatingForm != null){
            mainView.hideFAB();
        }
    }

    //Отсоиденение mainView и mainPresenter
    public void detachView() {
        mainView = null;

    }

    //Создаем эллемент формы для создания нового эллемента списка
    //и добавляем его в список
    public void createNewItem() {
        creatingForm = new CreatingForm();
        adapter.addItem(0, creatingForm);
        mainView.hideFAB();
    }

    //Удалаение эллемента формы если он существует и возвращаем колбек
    public boolean removeCreatingForm(){
        if(creatingForm != null){
            creatingForm = null;
            adapter.removeItem(0);
            mainView.showFAB();
            return false;
        }else {
            return true;
        }
    }

    //Добавление нового эллемента в базу данных
    public void addNewItem(String t, String uri) {
        //Если назание не пустое
        if (!t.isEmpty()) {

            final Product product = new Product();
            product.setTitle(t);

            //Если добавленна ссылка на изображение
            if (uri != null) {
                product.setImgID(uri);
            }

            //Производим асинхронное добавление в базу данных
            Completable.fromAction(new Action() {
                @Override
                public void run() throws Exception {
                    db.productDao().insert(product);
                }
            }).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        //При удачном добавлении производим ряд действий
                        @Override
                        public void onComplete() {

                            adapter.removeItem(0);
                            creatingForm = null;
                            needToBuyListSize++;
                            addSeparators();
                            adapter.addItem(1, product);

                            mainView.showFAB();
                            mainView.hideHit();
                        }

                        //В случае ошибки базы данных вызывем Toast
                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(context, "\n" +
                                    "Database Error", Toast.LENGTH_SHORT).show();
                        }
                    });
            //Если же название не введено то просто уберанем форму ввода
        } else {
            creatingForm = null;
            adapter.removeItem(0);
            mainView.showFAB();
        }

    }

    //Изменение переметров эллемента списка в базе данных
    public void updateItem(final Product product, final int position) {

        //Производим асинхронное изменение
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                db.productDao().update(product);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        //В случае успеха производим соответсвующие измененя в текущем адаптере
                        //и в сопутсвующих переменных
                        changeItem(product, position);
                    }

                    //В случае ошибки базы данных вызывем Toast
                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Производим изменения в текущем адаптере и в сопутсвующих переменных
    public void changeItem(Product product, int position) {

        //Есзи изменено на куплено...
        if (product.isBought()) {

            alreadyBoughtListSize++;
            addSeparators();
            needToBuyListSize--;
            adapter.moveItem(position, needToBuyListSize + 2);
            removeSeparators();

            //Если изменено на "еще не куплено"...
        } else {

            alreadyBoughtListSize--;
            needToBuyListSize++;
            addSeparators();
            int pos = position;
            if (needToBuyListSize == 1) {
                pos++;
            }

            adapter.moveItem(pos, 1);
            removeSeparators();
        }
    }

    //редактируем все необходимые эллементы списка в базе данных
    public void updateAll(final List<Product> list) {

        //Производим асинхронное редактирование
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                db.productDao().updateAll(list);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                    }

                    //В случае ошибки базы данных вызывем Toast
                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Проверяем необходимость добавления верхнего или нижнего сепаратора, и производим добавление
    private void addSeparators() {

        if (needToBuyListSize > 0 && headerSeparator == null) {
            headerSeparator = new Separator();
            headerSeparator.setTitle(context.getString(R.string.shopping_list));

            if (adapter != null) {
                adapter.addItem(0, headerSeparator);
            } else {
                items.add(0, headerSeparator);
            }
        }

        if (alreadyBoughtListSize > 0 && footerSeparator == null) {
            footerSeparator = new Separator();
            footerSeparator.setTitle(context.getString(R.string.bought_list));

            int pos = 0;
            if (needToBuyListSize > 0) {
                pos = needToBuyListSize + 1;
            }

            if (adapter != null) {
                adapter.addItem(pos, footerSeparator);
            } else {
                items.add(pos, footerSeparator);
            }
        }
    }

    //Проверяем необходиость удаления верхнего или нижнего сепаратора и производим удуление
    private void removeSeparators() {

        if (needToBuyListSize == 0 && headerSeparator != null) {
            headerSeparator = null;
            items.remove(headerSeparator);
            adapter.removeItem(0);
        }

        if (alreadyBoughtListSize == 0 && footerSeparator != null) {
            footerSeparator = null;
            items.remove(footerSeparator);
            adapter.removeItem(needToBuyListSize + 1);
        }
    }

    //Очистка всего списка в базе данных
    public void removeAll() {

        //Обнуление счетчиков
        alreadyBoughtListSize = 0;
        needToBuyListSize = 0;

        //очистка списка в адаптере
        adapter.clearList();

        //Асинхронная очистка базы данных
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                db.clearAllTables();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
        removeSeparators();

        mainView.showHint();
    }

    //Выхов метода "все куплено" в адаптере
    public void allBought() {
        adapter.allBought();
    }


    //Получение списка покупок из базы данных
    public void getData() {

        db.productDao().getProductList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Product>>() {
                    @Override
                    public void accept(List<Product> products) throws Exception {
                        //сохраняем количество эллементов в переменной
                        needToBuyListSize = products.size();
                        addSeparators();
                        items.addAll(products);
                        //Получеме список уже купленных товаров
                        getBought();

                        //Если нужно вызываем подсказку
                        if (needToBuyListSize > 0) {
                            mainView.hideHit();
                        }

                    }
                });
    }

    //Получение списка уже купленных товаров
    private void getBought() {

        db.productDao().getBoughtList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Product>>() {
                    @Override
                    public void accept(List<Product> products) throws Exception {
                        if (!products.isEmpty()) {
                            //сохраняем количество эллементов в переменной
                            alreadyBoughtListSize = products.size();
                            addSeparators();
                            items.addAll(products);

                            //Если нужно вызываем подсказку
                            if (alreadyBoughtListSize > 0) {
                                mainView.hideHit();
                            }
                        }
                    }
                });
    }

    //Генерируем Uri для сохранения картинко
    public Uri setImageUri(){

        File image = createImageFile();
        Uri imgUri = Uri.fromFile(image);
        imgPath = imgUri.toString();
        return imgUri;
    }

    //Генерирум файл для хранения изображения
    private File createImageFile()  {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",   /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    //Вызываем галерею для фыбора изображения
    public void getImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        mainView.getImageDialog(photoPickerIntent, FROM_GALLERY);
    }

    //Вызываем камеру для фото
    public void makeNewPhoto(){
        Intent photoPickerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
        mainView.getImageDialog(photoPickerIntent, FROM_CAMERA);
    }

    //Передача uri изображения в адаптер для сохранения
    public void setUriToItem(Uri uri) {
        adapter.setImageUri(uri.toString());
    }
    public void setUriToItem(){
        adapter.setImageUri(imgPath);
    }
}
