package com.labralab.shoppinglist.DI;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.labralab.shoppinglist.model.roomDB.AppDatabase;
import com.labralab.shoppinglist.presenter.MainPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by pc on 22.05.2018.
 */

@Module
public class PresenterModule {


    @Provides
    @Singleton
    MainPresenter provideMainPresenter(AppDatabase appDatabase, Context context){
        return new MainPresenter(appDatabase, context);
    }

}
