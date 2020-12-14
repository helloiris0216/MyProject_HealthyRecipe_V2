package com.example.myProject_HealthyRecipesApp;
//TODO:[目標] 本頁面的功能是紀錄使用者每天的飲食，並計算出食物中的營養素和熱量。
//TODO:[目標] 建立 action bar。
//TODO:[目標] 按下 "加入食物" 後，會跳至另一個顯示食物的頁面，讓使用者點選食物。
//TODO:[目標] (從firebase上拉資料)會將食物名稱和所對應的熱量顯示在 textView 上。
//TODO:[目標] 顯示每餐食物的總熱量。
//TODO:[目標] 將一天的總熱量做加總。
//TODO:[目標] 日記需要有修改和刪除的功能，修改->menu；刪除->長按 listView。


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

public class DiaryActivity extends AppCompatActivity {

    private String[] arr_mealName;
    private Context context;
    private ListView listView_diary;
    private TextView tv_meal;
    private HashMap<String, Object> meal_data;
    private String TAG = "diary activity";
    private List<Map<String, Object>> list_meal;
    private TextView tv_addFood;
    private TextView tv_cal;
    private TextView tv_food;


    //TODO:初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        setTitle("My Diary");
        context = this;

        //TODO:action bar 1
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);


        findViews();
        findAndPutData();
        setListener();
        getData();


    } //end onCreate()


    //TODO:getData()
    private void getData() {

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.listview_layout, (ViewGroup) findViewById(R.id.list_item_id));
        tv_meal = view.findViewById(R.id.tv_meal);
        tv_addFood = view.findViewById(R.id.tv_addFood);
        tv_cal = view.findViewById(R.id.tv_cal);
        tv_food = view.findViewById(R.id.tv_food);

        tv_food.setText("");
        tv_food.setText("123");

        Intent intent = getIntent();
        String weight = intent.getStringExtra("WEIGHT");
        Log.d(TAG, "weight:"+weight);

        //將資料顯示在textView上
        tv_food.setText("");
        tv_food.append(weight);

    }   //end getData()


    //TODO:setListener()
    private void setListener() {

        //TODO:知道是哪個項目(listView)被按
        listView_diary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                //利用吐司顯示使用者點選的是早餐 or 午餐 or 晚餐 or 點心
                Log.d(TAG, "onItemClick: index=" + id);
                int index = 0;
                for (int i=0; i<arr_mealName.length; i++){
                    index = (int)id;
                }

                Log.d(TAG, "arr_mealName[index]:"+arr_mealName[index]);
                Toast.makeText(context, "我的"+arr_mealName[index], Toast.LENGTH_SHORT).show();

                //使用者點選listView後跳轉到 food database 頁面
                Intent intent = new Intent(context, Food_databaseActivity.class);
                startActivity(intent);

            }
        });

    } //end setListener()


    //TODO:action bar 2
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    //TODO:拿到meal name的資料並顯示在listView上
    private void findAndPutData() {
        //拿到meal name(早午晚點心) & 將meal name放到list中
        arr_mealName = getResources().getStringArray(R.array.arr_meal);
        list_meal = new ArrayList<Map<String, Object>>();

        //put data
        for (int i = 0; i < arr_mealName.length; i++) {
            meal_data = new HashMap<String, Object>();
            meal_data.put("MEALNAME", arr_mealName[i]);

            Log.d(TAG, "meal name:" + arr_mealName);

            list_meal.add(meal_data);
        }

        setAdapter();
    } //end findAndPutData()


    //TODO:setAdapter()-將變數顯示在listView layout中
    private void setAdapter() {
        Log.d(TAG, "setAdaptor: ok");

        SimpleAdapter adapter = new SimpleAdapter(context, list_meal, R.layout.listview_layout,
                new String[]{"MEALNAME"}, new int[]{R.id.tv_meal});

        listView_diary.setAdapter(adapter);
    } //end setAdapter()


    //TODO:findViews()
    private void findViews() {
        listView_diary = findViewById(R.id.listView_diary);
    }
}