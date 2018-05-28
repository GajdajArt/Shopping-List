package com.labralab.shoppinglist.model.data;

/**
 * Created by pc on 22.05.2018.
 *
 * Обертка для эллементов списка
 */

public interface MyListItem {

    int PRODUCT_LIST_ITEM = 0;
    int SEPARATOR_LIST_ITEM = 1;
    int CREATE_LIST_ITEM = 2;

    int getType();
    void setTitle(String title);
    String getTitle();
}

