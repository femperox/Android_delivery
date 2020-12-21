package com.example.gui_android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.rgb;

public class order_info extends AppCompatActivity {

    int newOrder = rgb(205,225,201);
    int currentOrder = rgb(201,210,225);
    int doneOrder = rgb(226,226,226);


    // процедура, меняющая цвет самой верхней плашки
    void changeColorStatusBar(int color)
    {  if (Build.VERSION.SDK_INT >= 21)
       { Window window = getWindow();
         window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
         window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
         window.setStatusBarColor(color);
       }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_order_info);


        // получаем ответ с предыдущего окна
        Intent int_get = getIntent();
        String stateOfOrder = getIntent().getStringExtra("Состояние заказа");
        int idOrder = getIntent().getIntExtra("Номер заказа", -1);


        // узнаем айди тулбара
        Toolbar toolbar = findViewById(R.id.toolbarInfo);

        toolbar.setTitle("Заказ №"+idOrder);

        //узнаём айди кнопки
        Button btn = findViewById(R.id.buttonOrder);
        TextView tvPrice = findViewById(R.id.tvPrice);

        int price = idOrder - 6000;

        tvPrice.setText( Integer.toString(price) );




        if (stateOfOrder.equals("Новое"))
        {  toolbar.setBackgroundColor(newOrder);
           toolbar.setTitleTextColor(BLACK);
           btn.setText("Принять");
           btn.setTextColor(BLACK);
           btn.setBackgroundColor(newOrder);
           changeColorStatusBar(newOrder);
        }
        else if (stateOfOrder.equals("Выполняется"))
        {  toolbar.setBackgroundColor(currentOrder);
           toolbar.setTitleTextColor(BLACK);
           btn.setText("Завершить");
           btn.setTextColor(BLACK);
           btn.setBackgroundColor(currentOrder);
           changeColorStatusBar(currentOrder);
        }
        else
        { toolbar.setBackgroundColor(doneOrder);
          toolbar.setTitleTextColor(BLACK);
          btn.setVisibility(View.GONE);
          changeColorStatusBar(doneOrder);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {  int result =0;
               if (stateOfOrder.equals("Новое"))
               { btn.setText("Принять");
                 btn.setBackgroundColor(currentOrder);
                 toolbar.setBackgroundColor(currentOrder);
                 changeColorStatusBar(currentOrder);

                 result = currentOrder;
               }
               else if (stateOfOrder.equals("Выполняется"))
               { btn.setText("Завершить");
                 btn.setBackgroundColor(doneOrder);
                 toolbar.setBackgroundColor(doneOrder);
                 changeColorStatusBar(doneOrder);

                 result = doneOrder;

               }

               Intent resultIntent = new Intent();
               resultIntent.putExtra("Номер заказа", idOrder);
               resultIntent.putExtra("Цвет", result);
               resultIntent.putExtra("Сумма", price );

               if (result != 0) setResult(RESULT_OK, resultIntent);
               else setResult(RESULT_CANCELED, resultIntent);

               finish();


            }});

        TextView tvID = findViewById(R.id.tvID);
        tvID.setText(String.valueOf(idOrder));
    }



}