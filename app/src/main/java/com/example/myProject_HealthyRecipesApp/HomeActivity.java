package com.example.myProject_HealthyRecipesApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private Context context;
    private ConstraintLayout constraint;
    private RecyclerView recyclerView;
    private int[] img_array = {R.drawable.low_cal01, R.drawable.low_cal02, R.drawable.low_cal03,
                               R.drawable.low_cal04, R.drawable.low_cal05, R.drawable.low_cal06,
                               R.drawable.low_cal07, R.drawable.low_cal08, R.drawable.low_cal09};
    private List<Map<String, Object>> img_list;
    private RecyclerView recyclerView_02;
    private int[] img_array_02 = {R.drawable.high_pt01, R.drawable.high_pt02, R.drawable.high_pt03,
                                  R.drawable.high_pt04, R.drawable.high_pt05, R.drawable.high_pt06,
                                  R.drawable.high_pt07, R.drawable.high_pt08, R.drawable.high_pt09};
    private List<Map<String, Object>> img_list_02;

    private String TAG="main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = this;

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        constraint = findViewById(R.id.constraint);
        constraint.getBackground().setAlpha(125);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_home);
        recyclerView_02 = (RecyclerView) findViewById(R.id.recyclerView02);

        setRecyclerView();


        setListener();


    }

    private void setRecyclerView() {
        //01
        img_list = new ArrayList<>();
        for(int i=0; i<img_array.length; i++){
            Map<String, Object> img_map = new HashMap<String, Object>();
            img_map.put("image", img_array[i]);
            img_list.add(img_map);
        }
        Log.d(TAG, "img.size:"+img_list.size());


        LinearLayoutManager myManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(myManager);
        LinearAdapter myAdapter = new LinearAdapter(this, img_list);
        recyclerView.setAdapter(myAdapter);

        //02
        img_list_02 = new ArrayList<>();
        for(int i=0; i<img_array_02.length; i++){
            Map<String, Object> img_map = new HashMap<String, Object>();
            img_map.put("image", img_array_02[i]);
            img_list_02.add(img_map);
        }
        Log.d(TAG, "img.size:"+img_list_02.size());


        LinearLayoutManager myManager_02 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView_02.setLayoutManager(myManager_02);
        LinearAdapter_pt myAdapter_02 = new LinearAdapter_pt(this, img_list_02);
        recyclerView_02.setAdapter(myAdapter_02);

    }


    //TODO:監聽bottomNaigation(最下方的action bar)，並設定使用者按下後會跳轉到指定頁面
    private void setListener() {
        bottomNavigation.setItemIconTintList(null);
        bottomNavigation.setItemTextColor(null);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    //回首頁
                    case R.id.homePage:

                        Toast.makeText(context, "回首頁", Toast.LENGTH_SHORT).show();
                        Intent intent_home = new Intent(context, HomeActivity.class);
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



}