package com.example.myProject_HealthyRecipesApp;
//TODO:[目標] 本頁面的功能是紀錄使用者每天的飲食，並計算出食物中的熱量。
//TODO:[目標:完成] 建立 action bar。
//TODO:[目標:完成] 按下 "加入食物" 後，會跳至另一個顯示食物的頁面，讓使用者點選食物。
//TODO:[目標:完成] (從firebase上拉資料)會將食物名稱和所對應的熱量顯示在 textView 上。
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.myProject_HealthyRecipesApp.FoodDataHolder.list;



public class DiaryActivity extends AppCompatActivity {

    private static String[] arr_mealName, arr_foodName;
    private Context context;
    private ListView listView_diary;
    private TextView tv_meal_d, tv_addFood_d, tv_cal_d, tv_food_d, tv_remaining, tv_foodData;
    private HashMap<String, Object> meal_data;
    private String TAG = "diary_activity";
    private List<Map<String, Object>> list_meal;
    private List<Calculate> list_calculate;
    private Calculate data_fromCal;
    private String data;

    //[★★★] 紀錄被點選的位置
    static int clickedItem = -1;

    String name;
    String size;
    String cal;
    String pt;
    String carbs;
    String fat;
    private Double total, cal_c;
    private EditText et_goal;


    //TODO:初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        setTitle("My Diary");
        context = this;
        list = FoodDataHolder.init(getResources());
        list_calculate = Calculate.init();

        //TODO:action bar 1
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        data_fromCal = new Calculate();
        findAndPutData();
    } //end onCreate()

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

    //TODO:[1]在 listView 上顯示早餐、午餐、晚餐、點心(meal name)
    //TODO:[2]在 listView 上顯示由 CalCulate class 傳過來的運算結果
    private void findAndPutData() {
        listView_diary = findViewById(R.id.listView_diary);

        //[2]-1.條件檢查
        if(clickedItem == -1){
            //[1]-1拿到meal name(早午晚點心) & 將meal name放到list中
            //[★★★] listView 初始化
            arr_mealName = getResources().getStringArray(R.array.arr_meal);
            arr_foodName = new String[]{"", "", "", ""};

            list_meal = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < arr_mealName.length; i++) {
                meal_data = new HashMap<String, Object>();
                meal_data.put("MEALNAME", arr_mealName[i]);
                meal_data.put("FOOD", arr_foodName[i]);
                list_meal.add(meal_data);
            }

            //[1]-2.將要顯示的清單存入到 adapter
            final SimpleAdapter adapter = new SimpleAdapter(context, list_meal, R.layout.diary_listview_item_layout,
                    new String[]{"MEALNAME", "FOOD"},
                    new int[]{R.id.tv_meal_d, R.id.tv_food_d});
            //連結adapter
            listView_diary.setAdapter(adapter);

        } else if (clickedItem != -1) {
            //[2]-2.拿資料，先將資料印出來檢查
            Log.d(TAG, "確認資料>>>");
            Log.d(TAG, "getName(D):" + data_fromCal.getName_cal());
            Log.d(TAG, "getSize(D):" + data_fromCal.getSize_cal());
            Log.d(TAG, "getCal(D):" + data_fromCal.getCal_cal());
            Log.d(TAG, "getPt(D):" + data_fromCal.getPt_cal());
            Log.d(TAG, "getCarbs(D):" + data_fromCal.getCarbs_cal());
            Log.d(TAG, "getFat(D):" + data_fromCal.getFat_cal());

            name = data_fromCal.getName_cal();
            size = data_fromCal.getSize_cal().toString();
            cal = data_fromCal.getCal_cal().toString();
            pt = data_fromCal.getPt_cal().toString();
            carbs = data_fromCal.getCarbs_cal().toString();
            fat = data_fromCal.getFat_cal().toString();

            arr_foodName[clickedItem] =
                    arr_foodName[clickedItem] +
                            name + "\n" +
                            size + " g\n" +
                            cal + "  cal\n" +
                            pt + " g\n" +
                            carbs + " g\n" +
                            fat + " g\n";

            //[★★★] listView 初始化
            list_meal = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < arr_mealName.length; i++) {
                meal_data = new HashMap<String, Object>();
                meal_data.put("MEALNAME", arr_mealName[i]);
                meal_data.put("FOOD", arr_foodName[i]);
                list_meal.add(meal_data);

                //1.取得每餐中每樣食物的 cal
                cal_c = Double.parseDouble(cal);
                Log.d(TAG, "cal:"+cal_c);

                //2.將每樣食物的 cal 加總
                total= 0.0;
                total += cal_c;
                Log.d(TAG, "total:"+total);

            }


            //[1]-2.將要顯示的清單存入到 adapter
            final SimpleAdapter adapterNew = new SimpleAdapter(context, list_meal, R.layout.diary_listview_item_layout,
                    new String[]{"MEALNAME", "FOOD"},
                    new int[]{R.id.tv_meal_d, R.id.tv_food_d});
            //連結adapter
            listView_diary.setAdapter(adapterNew);

            //TODO:[未完成]計算機功能
            //3.將每樣食物的 cal 加總顯示在每餐的 tv_cal_d
            tv_cal_d = findViewById(R.id.tv_cal_d);
            try {
                tv_cal_d.setText("");
                Log.d(TAG, "total:"+total.toString());
                tv_cal_d.setText(total.toString());

                //4.加總整天的 tv_cal_d，並將結果顯示在 tv_foodData
                Double totalCal_allDay = Double.parseDouble(tv_cal_d.getText().toString());
                Double total_allDay = 0.0;
                for (int i=0; i<=3; i++){
                    totalCal_allDay += total_allDay;
                    Log.d(TAG, "total_allDay:"+total_allDay);
                }
                tv_foodData = findViewById(R.id.tv_foodData);
                tv_foodData.setText(totalCal_allDay.toString());

                //5.取得使用者輸入的目標熱量 et_goal -> 未輸入:toast
                et_goal = findViewById(R.id.et_goal);
                Double goal = Double.parseDouble(et_goal.getText().toString());

                //6.計算: remaining = goal (et_goal) - cal (tv_foodData)
                Double cal = Double.parseDouble(tv_foodData.toString());
                Double remaining = 0.0;
                remaining = goal - cal;

                //7.將計算結果顯示在 tv_remaining(結束)
                tv_remaining = findViewById(R.id.tv_remaining);
                tv_remaining.setText(remaining.toString());

            } catch (Exception e) {
                Toast.makeText(context, "請輸入每日目標熱量", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }



        //[1]-3 & [2]-5.監聽是listView上哪列item被點選
        listView_diary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //[★★★] 紀錄被點選的項目編號
                clickedItem = position;


                //跳頁
                Intent intent = new Intent(context, FoodDataActivity.class);
                startActivity(intent);
            }
        }); //end listener
    }//end findAndPutData()



}//end activity