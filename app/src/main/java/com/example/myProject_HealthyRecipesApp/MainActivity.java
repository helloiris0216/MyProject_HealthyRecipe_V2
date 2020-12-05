package com.example.myProject_HealthyRecipesApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        findViews();
        setListener();

    }

    //TODO:監聽bottomNaigation(最下方的action bar)，並設定使用者按下後會跳轉到指定頁面
    private void setListener() {
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    //回首頁
                    case R.id.homePage:
                        Toast.makeText(context, "回首頁", Toast.LENGTH_SHORT).show();
                        Intent intent_home = new Intent(context, HomePageActivity.class);
                        startActivity(intent_home);

                        break;

                    //回個人資訊頁
                    case R.id.user:
                        Toast.makeText(context, "回使用者資訊頁", Toast.LENGTH_SHORT).show();
                        Intent intent_user = new Intent(context, UserInfoActivity.class);
                        startActivity(intent_user);

                        break;

                    //到我的日記頁
                    case R.id.diary:
                        Toast.makeText(context, "我的日記", Toast.LENGTH_SHORT).show();
                        Intent intent_diary = new Intent(context, DiaryActivity.class);
                        startActivity(intent_diary);

                        break;

                } //end switch

                return true;
            } //end onNavigationItemSelected
        }); //end bottomNavigation listener

    } //end setListener


    //TODO:找到元件 -> findViews()
    private void findViews() {
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
    }
}