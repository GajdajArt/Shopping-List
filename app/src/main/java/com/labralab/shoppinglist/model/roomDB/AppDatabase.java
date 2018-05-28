package com.labralab.shoppinglist.model.roomDB;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.labralab.shoppinglist.model.data.Product;

/**
 * Created by pc on 22.05.2018.
 */

@Database(entities = {Product.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProductDao productDao();
}