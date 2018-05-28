package com.labralab.shoppinglist.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by pc on 22.05.2018.
 */

@Entity
public class Product implements MyListItem {

    @NonNull
    @PrimaryKey
    private String title;
    private boolean bought;
    private String imgID;

    public String getImgID() {
        return imgID;
    }

    public void setImgID(String imgID) {
        this.imgID = imgID;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getType() {
        return MyListItem.PRODUCT_LIST_ITEM;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

}
