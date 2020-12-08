package com.example.myProject_HealthyRecipesApp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiaryActivity extends AppCompatActivity {

    private String[] arr_mealName;
    private Context context;
    private ListView listView_diary;
    private TextView tvMeal;
    private HashMap<String, Object> meal_data;
    private String TAG="main";
    private List<Map<String, Object>> list_meal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        setTitle("My Diary");
        context = this;
        findViews();
        findAndPutData();

    } //end onCreate()


    //TODO:拿到meal name的資料並顯示在listView上
    //拿到meal name(早午晚點心) & 將meal name 放到list中
    private void findAndPutData() {
        arr_mealName = getResources().getStringArray(R.array.arr_meal);
        list_meal = new ArrayList<Map<String, Object>>();

        //put data
        for (int i=0; i<arr_mealName.length; i++){
            meal_data = new HashMap<String, Object>();
            meal_data.put("MEALNAME", arr_mealName[i]);

            Log.d(TAG, "meal name:"+arr_mealName);

            list_meal.add(meal_data);
        }

        setAdapter();
    }


    //TODO:setAdapter()-將變數顯示在listView layout中
    private void setAdapter() {
        Log.d(TAG, "setAdaptor: ok");

        SimpleAdapter adapter = new SimpleAdapter(context,list_meal,R.layout.listview_layout,
                new String[]{"MEALNAME"}, new int[]{R.id.tv_meal});

        listView_diary.setAdapter(adapter);
    } //end setAdapter()


    //TODO:findViews()
    private void findViews() {
        listView_diary = (ListView) findViewById(R.id.listView_diary);
        tvMeal = (TextView) findViewById(R.id.tv_meal);
    }
}