package com.labralab.shoppinglist.model.data;

/**
 * Created by pc on 24.05.2018.
 */

public class Separator implements MyListItem {


    private String  title;

    @Override
    public int getType() {
        return MyListItem.SEPARATOR_LIST_ITEM;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
