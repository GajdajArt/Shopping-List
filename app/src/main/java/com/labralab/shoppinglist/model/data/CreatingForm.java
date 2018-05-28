package com.labralab.shoppinglist.model.data;

import android.net.Uri;

/**
 * Created by pc on 24.05.2018.
 */

public class CreatingForm implements MyListItem {

    private int imageUri;

    public int getImageUri() {
        return imageUri;
    }

    public void setImageUri(int imageUri) {
        this.imageUri = imageUri;
    }


    @Override
    public int getType() {
        return MyListItem.CREATE_LIST_ITEM;
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public String getTitle() {
        return null;
    }
}
