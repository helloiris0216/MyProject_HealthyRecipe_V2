package com.example.myProject_HealthyRecipesApp;
//TODO:[目標] 本頁的目的是顯示 realtime database 上的資料
//TODO:[目標] 點選食物後，會跳出對話框，使用者須填寫食物的重量，對話框有 "取消" 和 "確認"的按鈕
//TODO:[目標] 按下確認後會跳回 DiaryActivity


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Food_databaseActivity extends AppCompatActivity {

    private String TAG = "food";
    private ListView lv_food_database;
    private Context context;
    private ArrayList<Map<String, Object>> dataList;
    private SimpleAdapter adapter;
    private TextView tv_foodData;
    private String[] arr_food;
    private List<Map<String, Object>> list_food;
    private TextView tv_dialog_addFood;
    private EditText et_dialog_weight;

    //TODO:初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_database);

        context = this;

        //TODO:action bar 1
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        findViews();
        findAndPutData();
        setListener();
        //getDBData();

    } //end onCreate()


    //TODO:[未完成] getDBData()
//    private void getDBData() {
//        Log.d(TAG, "getDBDate: start");
//
//        //get data
//        Intent intent = this.getIntent();
//        Bundle bundle = intent.getExtras();
//        String[] str = bundle.getStringArray("food_arr");
//
//        //put data
//        if (str != null) {
//            HashMap<String, Object> mapData = new HashMap<String, Object>();
//            dataList = new ArrayList<Map<String, Object>>();
//
//            for (int i=0; i<str.length; i++) {
//
//                Log.d(TAG, "i=" + i + " str[i]:"+str[i]);
//
//                switch (i) {
//                    case 0:
//                        mapData.put("food_name", str[i]);
//
//                    case 1:
//                        mapData.put("food_serving_size", str[i]);
//
//                    case 2:
//                        mapData.put("food_pt", str[i]);
//                        break;
//                }
//                    dataList.add(mapData);
//            }
//
//
//            setAdapter();
//
//        } else {
//            Log.d(TAG, "no data");
//        }
//
//
//        Log.d(TAG, "setDBData: end");
//    } //end setDBDate()


    private void findAndPutData() {

        arr_food = getResources().getStringArray(R.array.arr_food);
        list_food = new ArrayList<Map<String, Object>>();

        //put data
        for (int i = 0; i < arr_food.length; i++) {
            HashMap<String, Object> food_data = new HashMap<String, Object>();
            food_data.put("arr_food", arr_food[i]);

            Log.d(TAG, "food name:" + arr_food);

            list_food.add(food_data);
        }


        //從firebase上撈出資料



        setAdapter();
    } //end findAndPutData()


    //TODO:setAdapter()
    private void setAdapter(){
        adapter = new SimpleAdapter(context,list_food, R.layout.food_database_listview_layout, new String[]{"arr_food"}
                ,new int[]{R.id.tv_foodData});
        lv_food_database.setAdapter(adapter);
    }


    //TODO:setListener()
    private void setListener(){
        lv_food_database.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int index = 0;
                for (int i=0; i<arr_food.length; i++){
                    index = (int) id;
                }

                dialog(arr_food[index]);

                Toast.makeText(context, arr_food[index], Toast.LENGTH_SHORT).show();
            }
        });
    }


    //TODO:dialog()
    private void dialog(String arr_food) {

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_layout, (ViewGroup) findViewById(R.id.dialog_id));

        tv_dialog_addFood = view.findViewById(R.id.tv_dialog_addFood);
        et_dialog_weight = view.findViewById(R.id.et_dialog_weight);
        tv_dialog_addFood.setText(arr_food);
        et_dialog_weight.setText("");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("請填寫重量")
                .setView(view)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String weight = et_dialog_weight.getText().toString();
                        Intent intent = new Intent(context, DiaryActivity.class);
                        intent.putExtra("WEIGHT", weight);
                        startActivity(intent);

                    }
                });

        builder.setNegativeButton("取消", null);
        builder.create().show();

    }   //end dialog()


    //TODO:findViews();
    private void findViews() {
        lv_food_database = findViewById(R.id.lv_food_database);
        tv_foodData = findViewById(R.id.tv_foodData);
    }


    //TODO:action bar 2
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}