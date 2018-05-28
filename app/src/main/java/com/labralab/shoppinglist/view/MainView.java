package com.labralab.shoppinglist.view;

import android.content.Intent;
import android.view.View;

import com.labralab.shoppinglist.presenter.adapter.MainAdapter;
import com.labralab.shoppinglist.utils.PermissionsUtils;

/**
 * Created by pc on 22.05.2018.
 */

public interface MainView {

    void setAdapter(MainAdapter adapter);
    void showFAB();
    void hideFAB();
    void showHint();
    void hideHit();
    void getImageDialog(Intent intent, int teg);

}
