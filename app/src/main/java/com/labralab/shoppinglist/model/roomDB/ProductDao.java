package com.labralab.shoppinglist.model.roomDB;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.labralab.shoppinglist.model.data.Product;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Created by pc on 22.05.2018.
 */

@Dao
public interface ProductDao {

    @Query("SELECT * FROM product")
    Single<List<Product>> getAll();

    @Query("SELECT * FROM product WHERE bought = 0")
    Single<List<Product>> getProductList();

    @Query("SELECT * FROM product WHERE bought = 1")
    Single<List<Product>> getBoughtList();

    @Insert
    void insert(Product product);

    @Update
    void update(Product product);

    @Update
    void updateAll(List<Product> list);

}
