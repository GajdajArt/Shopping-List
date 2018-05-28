package com.labralab.shoppinglist.presenter.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.labralab.shoppinglist.App;
import com.labralab.shoppinglist.R;
import com.labralab.shoppinglist.model.data.MyListItem;
import com.labralab.shoppinglist.model.data.Product;
import com.labralab.shoppinglist.model.data.Separator;
import com.labralab.shoppinglist.presenter.MainPresenter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pc on 22.05.2018.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    //list of all views
    List<MyListItem> items;
    MainPresenter mainPresenter;
    Context context;

    String currentUri;

    public MainAdapter(List<MyListItem> items, MainPresenter mainPresenter, Context context) {
        this.items = items;
        this.mainPresenter = mainPresenter;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //Подбираем лейаут в зависимости от типа эллемента
        View view;
        switch (viewType) {
            case MyListItem.PRODUCT_LIST_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item, parent, false);
                return new ProductHolder(view);
            case MyListItem.SEPARATOR_LIST_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.separator_list_item, parent, false);
                return new SeparatorHolder(view);
            case MyListItem.CREATE_LIST_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.creating_list_tem, parent, false);
                return new CreatorHolder(view);
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        //В зависимости от типа эллемента подбираем холдер
        switch (getItemViewType(position)) {
            case MyListItem.PRODUCT_LIST_ITEM:

                final ProductHolder productHolder = (ProductHolder) holder;
                final Product item = (Product) items.get(position);
                productHolder.productTitle.setText(item.getTitle());
                productHolder.doneCheckBox.setChecked(item.isBought());

                //Есть ли изображение
                if (item.getImgID() == null) {
                    //если нет изображения
                    productHolder.openButton.setVisibility(View.INVISIBLE);
                    productHolder.isOpen = false;
                    detachView(productHolder.itemImage);
                } else {
                    //Если есть изображение
                    productHolder.openButton.setVisibility(View.VISIBLE);
                    //Обработка нажатия на кнопочку для открывания изображения
                    productHolder.openButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //Открыть изображение
                            if (!productHolder.isOpen) {
                                attachImage(productHolder.itemImage, item.getImgID());
                                productHolder.isOpen = true;
                                productHolder.openButton.setImageResource(R.drawable.two_arrows_pointing_up);
                            //Закрыть изображение
                            } else {
                                detachView(productHolder.itemImage);
                                productHolder.isOpen = false;
                                productHolder.openButton.setImageResource(R.drawable.two_arrows_pointing_up);
                            }
                            notifyItemChanged(position, new Object());
                        }
                    });
                }

                //Проверка открыто или закрыто избражение
                if (productHolder.isOpen){
                    productHolder.openButton.setImageResource(R.drawable.two_arrows_pointing_up);
                }else {
                    productHolder.openButton.setImageResource(R.drawable.two_down_arrows);
                }

                //Проверка куплен эллемент или нет
                if (item.isBought()) {
                    productHolder.productTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    productHolder.productTitle.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
                }


                //Обработка нажатия нажатия doneCheckBox
                productHolder.doneCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Закрыть открытое изображение
                        detachView(productHolder.itemImage);
                        productHolder.isOpen = false;
                        productHolder.openButton.setImageResource(R.drawable.two_down_arrows);
                        notifyItemChanged(position);
                        //Сохранить эллемент в базу данных с новым значением
                        boolean currentState = productHolder.doneCheckBox.isChecked();
                        productHolder.doneCheckBox.setChecked(currentState);
                        item.setBought(currentState);
                        mainPresenter.updateItem(item, position);
                    }
                });


                break;
            //Для холдера - Сепаратора
            case MyListItem.SEPARATOR_LIST_ITEM:
                SeparatorHolder separatorHolder = (SeparatorHolder) holder;
                Separator separator = (Separator) items.get(position);
                separatorHolder.separatorTitle.setText(separator.getTitle());

                break;
            //Для холдера - формы ввода
            case MyListItem.CREATE_LIST_ITEM:
                CreatorHolder creatorHolder = (CreatorHolder) holder;
                creatorHolder.newTitle.setText("");
                creatorHolder.setAdapter(this);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    //Подключить и развернуть изображение
    private void attachImage(ImageView imageView, String uri) {
        if (uri != null) {
            ViewGroup.LayoutParams p = imageView.getLayoutParams();
            p.height = 250;
            Picasso.with(context).load(Uri.parse(uri)).into(imageView);
        }
    }

    //Свернуть и отключить изображение
    public void detachView(ImageView imageView) {
        imageView.setImageBitmap(null);
        ViewGroup.LayoutParams p = imageView.getLayoutParams();
        p.height = 0;
    }

    //Добавить новый эллемент
    public void addItem(int position, MyListItem item) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    //Переместить эллемент
    public void moveItem(int fromPosition, int toPosition) {

        MyListItem item = items.get(fromPosition);
        items.remove(fromPosition);
        items.add(toPosition, item);

        notifyItemMoved(fromPosition, toPosition);

        for (int i = 0; i < items.size(); i++) {
            notifyItemChanged(i);
        }
    }

    //Удалить эллемент
    public void removeItem(int position) {
        if (items.size() > position) {
            items.remove(position);
            notifyItemRemoved(position);
        }

    }

    //Очистить список удалив все эллементы
    public void clearList() {
        while (items.size() > 0) {
            items.remove(0);
            notifyItemRemoved(0);

        }

    }

    //Получение currentUri
    public void setImageUri(String uri) {
        this.currentUri = uri;
    }

    //Отметить все как куплено
    public void allBought() {

        List<Product> result = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            if (items.get(1).getType() == MyListItem.PRODUCT_LIST_ITEM) {
                Product product = (Product) items.get(1);

                if (!product.isBought()) {
                    product.setBought(true);
                    mainPresenter.changeItem(product, 1);
                    result.add(product);
                }
            }
        }
        mainPresenter.updateAll(result);

    }


    //Обертка для всех типовхолдеров
    protected abstract class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    //Холдер для позиций списка покупок
    public class ProductHolder extends ViewHolder {

        boolean isOpen = false;


        @BindView(R.id.openButton)
        ImageButton openButton;
        @BindView(R.id.productTitle)
        TextView productTitle;
        @BindView(R.id.doneCheckBox)
        CheckBox doneCheckBox;
        @BindView(R.id.itemImage)
        ImageView itemImage;


        ProductHolder(View view) {
            super(view);
        }

    }

    //Ходер для сепараторов
    protected class SeparatorHolder extends ViewHolder {
        @BindView(R.id.separatorTitle)
        TextView separatorTitle;

        SeparatorHolder(View view) {
            super(view);
        }
    }

    //Холдер для формочки создания нового эллемента
    public class CreatorHolder extends ViewHolder {

        @BindView(R.id.newIMG)
        ImageButton newIMG;
        @BindView(R.id.newPhoto)
        ImageButton newPhoto;
        @BindView(R.id.newTitle)
        EditText newTitle;
        @BindView(R.id.addButton)
        ImageButton addButton;

        @Inject
        MainPresenter mainPresenter;
        MainAdapter adapter;

        CreatorHolder(View view) {
            super(view);
            App.getAppComponents().inject(this);

        }

        public void setAdapter(MainAdapter mainAdapter) {
            this.adapter = mainAdapter;
        }

        //Обработчики нажатия на кнопки
        @OnClick({R.id.newIMG, R.id.newPhoto, R.id.addButton})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.newIMG:
                    //Добавить фото из галереи
                    mainPresenter.getImage();
                    break;
                case R.id.newPhoto:
                    //Добавить фото с кмеры
                    mainPresenter.makeNewPhoto();
                    break;
                case R.id.addButton:
                    //Сохранить эллемент
                    mainPresenter.addNewItem(newTitle.getText().toString(), adapter.currentUri);
                    break;
            }
        }
    }
}


