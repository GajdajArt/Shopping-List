package com.labralab.shoppinglist.DI;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.labralab.shoppinglist.model.roomDB.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by pc on 22.05.2018.
 */
@Module
public class DatabaseModule {


    @Provides
    @Singleton
    AppDatabase provideDatabase(Context context){
        return Room.databaseBuilder(context,
                AppDatabase.class, "database").build();
    }


}
