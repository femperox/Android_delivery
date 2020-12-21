package com.example.gui_android;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gui_android.ui.DialogClass;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.RelativeLayout.LayoutParams;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.graphics.Color.rgb;
import static androidx.core.app.NotificationCompat.*;

// када будет уже сервер
// баланс за всё время просто итератором пройтись по выполненным заказам и просуммировать их
// понять как сохранить историю заказов


public class MainActivity extends AppCompatActivity implements DialogClass.DialogClassListener {

    private AppBarConfiguration mAppBarConfiguration;

    // кнопочка для создания заказа (тупо для тестинга)
    private Button btn;
    private LinearLayout linearLayout;
    private final int IDS = 6000;
    private int countId = 1;
    private int balance = 0;
    private String login;

    // залогинен ли человек...
    private boolean logged = false;
    private String username = "";
    private String password = "";

    private SharedPreferences sharedpreferences;


    // диалог для логина
    DialogClass loginDialog;

    // заказы
    List<Integer> orderList = new ArrayList<>();


    // цвета
    int newOrder = rgb(205,225,201);
    int newOrderSec = rgb(213,232,210);
    int currentOrder = rgb(201,210,225);
    int currentOrderSec = rgb(209,218,233);
    int doneOrder = rgb(226,226,226);
    int doneOrderSec = rgb(231,231,231);

    // тексты на выезжаюзем меню
    TextView tvName;
    TextView tvBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (savedInstanceState != null) countId = savedInstanceState.getInt("Count_id", R.layout.activity_main);




        setContentView(R.layout.activity_main);
        createNotificationChannel();
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("Count_id", countId);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }



    @Override
    protected void onStart() {
        super.onStart();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ----------- тест заказов
        btn = (Button) findViewById(R.id.button_new);
        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);


        // параметры для расстояния между каждыми заказами
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        params.setMargins(3, 1, 3, 25);

        // обработчик нажатия на кнопку для тестинга
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout ll = new LinearLayout(getApplicationContext());

                // сама штука для нового заказа
                ll.setBackgroundColor(rgb(212, 233, 209));
                ll.setOrientation(LinearLayout.VERTICAL);


                // текст с новым!!!!
                TextView tv = new TextView(getApplicationContext());
                tv.setTextSize(20);
                tv.setText("Новое");

                // штука с текстом о заказе
                LinearLayout textInfo = new LinearLayout(getApplicationContext());
                textInfo.setOrientation(LinearLayout.VERTICAL);
                textInfo.setBackgroundColor(rgb(205, 225, 200));

                TextView tvOrder = new TextView(getApplicationContext());
                TextView tvCustomer = new TextView(getApplicationContext());
                TextView tvTelephone = new TextView(getApplicationContext());
                TextView tvAdress = new TextView(getApplicationContext());
                TextView tvCargo = new TextView(getApplicationContext());
                TextView tvDate = new TextView(getApplicationContext());


                tvOrder.setTextSize(15);
                tvCustomer.setText("Заказчик: ");
                tvTelephone.setText("Телефон: ");
                tvAdress.setText("Адрес: ");
                tvCargo.setText("Груз: ");
                tvDate.setText("Дата: ");

                textInfo.addView(tvOrder);
                textInfo.addView(tvCustomer);
                textInfo.addView(tvTelephone);
                textInfo.addView(tvAdress);
                textInfo.addView(tvCargo);
                textInfo.addView(tvDate);

                int orderId = IDS + countId;
                ll.setLayoutParams(params);
                ll.setId(orderId);
                tv.setId((orderId)*10);
                textInfo.setId((orderId)*100);
                tvOrder.setText("Заказ №"+orderId);
                orderList.add(orderId);

                countId++;
                ll.addView(tv);
                ll.addView(textInfo);
                linearLayout.addView(ll,1);

                String orderText = "Груз: прикольный \n Адрес: хороший";
                notification(orderId,orderText);

                ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {   Intent intNew = new Intent(MainActivity.this, order_info.class);

                        intNew.putExtra("Состояние заказа", tv.getText());
                        intNew.putExtra("Номер заказа",ll.getId());


                        startActivityForResult(intNew, 1);


                    }});


            }
        });


        // создаём боковое меню
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.login, R.id.logout).setDrawerLayout(drawer).build();



        loginDialog = new DialogClass();
        // листенер нажатия на кнопки в боковом меню
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.login) loginDialog.show(getFragmentManager(), "dlg1");
                else
                {   hideItem(navigationView, R.id.logout);
                    showItem(navigationView, R.id.login);
                    logged = false;
                    hideUserInfo();
                    btn.setVisibility(View.GONE);

                    //для автологина
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.remove("username");
                    editor.remove("balance");
                    editor.commit();

                }

                return true;
            }
        });


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);


        // для авто логина
        sharedpreferences=getApplicationContext().getSharedPreferences("Preferences", 0);
        login = sharedpreferences.getString("username", null);

        if (login != null)
        {   balance = sharedpreferences.getInt("balance", -1);

            showUserInfo();

            hideItem(navigationView, R.id.login);
            showItem(navigationView, R.id.logout);

            btn.setVisibility(View.VISIBLE);
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1)
        {   if (resultCode == RESULT_OK)
            { int idOrder = data.getIntExtra("Номер заказа",0);
              int newColor = data.getIntExtra("Цвет", 0);
              int sum = data.getIntExtra("Сумма",0);

              LinearLayout ll = (LinearLayout) findViewById(idOrder);
              ll.setBackgroundColor(newColor);

              TextView tv = (TextView) findViewById(idOrder*10);
              LinearLayout textLl = (LinearLayout) findViewById(idOrder*100);


              // ставим вспомогательные цвета, а также плашки на записи
              if (newColor == currentOrder)
              { tv.setText("Выполняется");
                textLl.setBackgroundColor(currentOrderSec);

                // удаляем все уведы о новых заказах
                NotificationManager notificationManager =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancelAll();

                //удаляем все заказы  пометкой "новое"
                Iterator<Integer> orderIterator = orderList.iterator();
                while (orderIterator.hasNext())
                {  int order = orderIterator.next();

                   tv = findViewById(order*10);

                   if ((order != idOrder) && (tv.getText().equals("Новое")))
                   { ll = findViewById(order);
                     ll.setVisibility(View.GONE);
                     ll.removeAllViews();
                   }

                   orderIterator.remove();
                }

                // новые заказы принимать не можем!
                btn.setVisibility(View.GONE);

              }
              else if (newColor == doneOrder)
              { tv.setText("Выполнено");
                textLl.setBackgroundColor(doneOrderSec);

                //обновляем баланс за всё время
                balance += sum;
                tvBalance = findViewById(R.id.textBalance);
                tvBalance.setText("Баланс за всё время: "+balance+"р.");

                // новые заказы принимать можем!
                btn.setVisibility(View.VISIBLE);
              }

              NavigationView navigationView = findViewById(R.id.nav_view);
              navigationView.setItemTextColor(ColorStateList.valueOf(Color.BLACK));


            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //убрать кнопочку на боковом меню...
    private void hideItem(NavigationView nav, int item)
    {   Menu nav_Menu = nav.getMenu();
        nav_Menu.findItem(item).setVisible(false);
    }

    //показать кнопочку на боковом меню...
    private void showItem(NavigationView nav, int item)
    {   Menu nav_Menu = nav.getMenu();
        nav_Menu.findItem(item).setVisible(true);

    }



    // убрать имя и баланс
    private void hideUserInfo()
    {   tvName = findViewById(R.id.textName);
        tvBalance = findViewById(R.id.textBalance);
        tvName.setVisibility(View.GONE);
        tvBalance.setVisibility(View.GONE);
    }

    // показать имя и баланс
    private void showUserInfo()
    {   View header = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);

        tvName = header.findViewById(R.id.textName);
        tvBalance = header.findViewById(R.id.textBalance);

        tvBalance.setText("Баланс за всё время: "+balance+"р.");
        tvName.setText(login);

        tvName.setVisibility(View.VISIBLE);
        tvBalance.setVisibility(View.VISIBLE);
    }




    // работа со входом в систему
    @Override
    public void getTextFromDialog(boolean result, String username)
    { if (result)
      {
        logged = true;

        NavigationView navigationView = findViewById(R.id.nav_view);
        hideItem(navigationView, R.id.login);
        showItem(navigationView, R.id.logout);

        tvName = findViewById(R.id.textName);
        tvName.setText(username);


        showUserInfo();

        btn.setVisibility(View.VISIBLE);

        //для автологина
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("username", username);
        editor.putInt("balance", balance);
        editor.commit();
      }
      else Toast.makeText(getApplicationContext(),"Пользователь не найден",Toast.LENGTH_LONG);

    }



    // создаём уведомление о новом заказе
    private void notification(int orderId, String orderText)
    {  	// Чтобы из уведа можно было открыть подробную инфу о заказе
        Intent resultIntent = new Intent(this, MainActivity.class);

        resultIntent.putExtra("Состояние заказа", "Новое");
        resultIntent.putExtra("Номер заказа",orderId);


        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 1, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(MainActivity.this, "Orders")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Новый заказ №"+orderId)
                    .setContentText(orderText)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(MainActivity.this);

        notificationManager.notify(orderId, builder.build());
    }

    // штука-дрюка, которая создаёт канал, который принимает уведы от нашего приложения
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NewOrder";
            String description = "Channel for new orders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Orders", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}