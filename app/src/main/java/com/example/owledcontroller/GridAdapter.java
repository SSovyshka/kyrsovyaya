package com.example.owledcontroller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> flowerName;
    int[] image;

    LayoutInflater inflater;

    // Конструктор класса GridAdapter
    public GridAdapter(Context context, ArrayList<String> flowerName, int[] image) {
        this.context = context;
        this.flowerName = flowerName;
        this.image = image;
    }

    @Override
    public int getCount() {
        // Возвращает общее количество элементов в адаптере
        return flowerName.size();
    }

    @Override
    public Object getItem(int i) {
        // Возвращает элемент данных по указанной позиции
        return null;
    }

    @Override
    public long getItemId(int i) {
        // Возвращает идентификатор элемента данных по указанной позиции
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // Если LayoutInflater ещё не инициализирован, инициализируем его
        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Если View не создана, создаем ее из макета grid_item
        if (view == null)
            view = inflater.inflate(R.layout.grid_item, null);

        // Находим элементы ImageView и TextView в макете grid_item
        ImageView imageView = view.findViewById(R.id.item_image);
        TextView textView = view.findViewById(R.id.item_text);

        // Устанавливаем изображение и текст для текущего элемента
        imageView.setImageResource(image[i]);
        textView.setText(flowerName.get(i));

        // Возвращаем View, представляющую текущий элемент в GridView
        return view;
    }
}