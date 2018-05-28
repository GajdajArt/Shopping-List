package com.labralab.shoppinglist.DI;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.annotations.NonNull;

/**
 * Created by pc on 22.05.2018.
 */
@Module
public class AppModule {

    Context appContext;

    public AppModule(@NonNull Context appContext) {
        this.appContext = appContext;
    }

    @Provides
    @Singleton
    Context provideContext(){
        return appContext;
    }
}
