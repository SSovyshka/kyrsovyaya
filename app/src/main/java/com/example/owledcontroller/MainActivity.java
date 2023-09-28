package com.example.owledcontroller;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.owledcontroller.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding; // Объявление переменной для привязки к макету

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater()); // Привязка макета к активности
        setContentView(binding.getRoot()); // Установка корневого представления макета в активности

        // Массив с названиями цветов
        String[] flowerName = {"Rose", "Lily", "lotus", "Jasmine"};

        // Массив с ресурсами изображений цветов
        int[] flowerImages = {R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground};

        // Создание адаптера для заполнения сетки (GridView)
        GridAdapter gridAdapter = new GridAdapter(MainActivity.this, flowerName, flowerImages);

        // Установка адаптера для GridView
        binding.gridView.setAdapter(gridAdapter);

        // Обработчик нажатий на элементы GridView
        binding.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Вывод всплывающего сообщения при нажатии на элемент
                Toast.makeText(MainActivity.this, "You clicked on " + flowerName[i], Toast.LENGTH_SHORT).show();
            }
        });
    }
}