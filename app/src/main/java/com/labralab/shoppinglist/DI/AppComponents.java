package com.labralab.shoppinglist.DI;

import android.support.v7.widget.RecyclerView;

import com.labralab.shoppinglist.presenter.adapter.MainAdapter;
import com.labralab.shoppinglist.view.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by pc on 22.05.2018.
 */
@Singleton
@Component (modules = {AppModule.class, DatabaseModule.class, PresenterModule.class})
public interface AppComponents {

    void inject(MainActivity mainActivity);
    void inject(MainAdapter.CreatorHolder viewHolder);
    void inject(MainAdapter.ProductHolder viewHolder);
}
