package com.labralab.shoppinglist;

import android.app.Application;
import android.content.Context;

import com.labralab.shoppinglist.DI.AppComponents;
import com.labralab.shoppinglist.DI.AppModule;
import com.labralab.shoppinglist.DI.DaggerAppComponents;
import com.labralab.shoppinglist.DI.DatabaseModule;
import com.labralab.shoppinglist.DI.PresenterModule;

/**
 * Created by pc on 22.05.2018.
 */

public class App extends Application {

    static AppComponents appComponents;
    private Context context;


    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        appComponents = DaggerAppComponents.builder()
                .appModule(new AppModule(context))
                .databaseModule(new DatabaseModule())
                .presenterModule(new PresenterModule())
                .build();

    }

    public static AppComponents getAppComponents(){
        return appComponents;
    }
}
