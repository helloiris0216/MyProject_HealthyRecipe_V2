package com.example.myProject_HealthyRecipesApp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DiaryActivity extends AppCompatActivity {

    private String[] mealName;
    private Context context;
    private ArrayAdapter<String> adaptor;
    private ListView listView_diary;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        setTitle("My Diary");
        context = this;
        findViews();



        //TODO:拿到meal name(早午晚點心)
        mealName = getResources().getStringArray(R.array.arr_meal);




        //將變數顯示在listView layout中
        adaptor = new ArrayAdapter<String>(context, R.layout.listview_layout, mealName);

        listView_diary.setAdapter(adaptor);

    }

    private void findViews() {
        listView_diary = (ListView) findViewById(R.id.listView_diary);
       // tvMeal = (TextView) find
    }
}