package com.example.myProject_HealthyRecipesApp;
//TODO:[目標] 本頁面的功能是紀錄使用者每天的飲食，並計算出食物中的營養素和熱量。
//TODO:[目標:完成] 建立 action bar。
//TODO:[目標:完成] 按下 "加入食物" 後，會跳至另一個顯示食物的頁面，讓使用者點選食物。
//TODO:[目標] (從firebase上拉資料)會將食物名稱和所對應的熱量顯示在 textView 上。
//TODO:[目標] 顯示每餐食物的總熱量。
//TODO:[目標:完成] 將一天的總熱量做加總。
//TODO:[目標] 日記需要有修改和刪除的功能，修改->menu；刪除->長按 listView。
//TODO:[目標] 將使用者的輸入儲存 (firebase or preference)


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import static com.example.myProject_HealthyRecipesApp.FoodDataHolder.list;


public class DiaryActivity extends AppCompatActivity {

    private String[] arr_mealName;
    private Context context;
    private ListView listView_diary;
    private TextView tv_meal_d, tv_addFood_d, tv_cal_d, tv_food_d,tv_remaining, tv_foodData ;
    private HashMap<String, Object> meal_data;
    private String TAG = "diary_activity";
    private List<Map<String, Object>> list_meal;
    private List<Calculate> list_calculate;
    private Calculate data_fromCal;
    private String data;
    private String name, size, cal, pt, carbs, fat;
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


        listView_diary = findViewById(R.id.listView_diary);
        findAndPutData();


    }//end onCreate()


    //TODO:action bar 2
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }//end onOptionsItemSelected()


    //TODO:[1]在 listView 上顯示早餐、午餐、晚餐、點心(meal name)
    //TODO:[2]在 listView 上顯示由 CalCulate class 傳過來的運算結果
    private void findAndPutData() {

        //[1]-1拿到meal name(早午晚點心) & 將meal name放到list中
        arr_mealName = getResources().getStringArray(R.array.arr_meal);
        list_meal = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < arr_mealName.length; i++) {
            meal_data = new HashMap<String, Object>();
            meal_data.put("MEALNAME", arr_mealName[i]);

            list_meal.add(meal_data);
        }


        //[2]-1.條件檢查
        data_fromCal = new Calculate();
        if (data_fromCal == null) {
            //[1]-2.將要顯示的清單存入到 adapter
            SimpleAdapter adapter = new SimpleAdapter(context, list_meal, R.layout.diary_listview_item_layout,
                    new String[]{"MEALNAME"},
                    new int[]{R.id.tv_meal_d});
            listView_diary.setAdapter(adapter);

        } else {
            //[2]-2.拿資料，先將資料印出來檢查
            name = data_fromCal.getName_cal();
            size = data_fromCal.getSize_cal().toString();
            cal = data_fromCal.getCal_cal().toString();
            pt = data_fromCal.getPt_cal().toString();
            carbs = data_fromCal.getCarbs_cal().toString();
            fat = data_fromCal.getFat_cal().toString();

            Log.d(TAG, "getName(D):" + name);
            Log.d(TAG, "getSize(D):" + size);
            Log.d(TAG, "getCal(D):" + cal);
            Log.d(TAG, "getPt(D):" + pt);
            Log.d(TAG, "getCarbs(D):" + carbs);
            Log.d(TAG, "getFat(D):" + fat);

            //[2]-3.將資料放入map中
            //List<Map<String, Object>> list_data = new ArrayList<Map<String, Object>>();
            final String[] arr_data = {name, size, cal, pt, carbs, fat};
            for (int i = 0; i < arr_data.length; i++) {
                //HashMap<String, Object> map = new HashMap<String, Object>();
                meal_data = new HashMap<String, Object>();
                meal_data.put("DATA", arr_data[i]);

                list_meal.add(meal_data);
            }
            Log.d(TAG, "list_meal:" + list_meal);


            //[2]-4.將資料存到 adapter
            final SimpleAdapter adp = new SimpleAdapter(context, list_meal, R.layout.diary_listview_item_layout,
                                          new String[]{"DATA"},
                                          new int[]{R.id.tv_food_d});
            listView_diary.setAdapter(adp);


            //[1]-3 & [2]-5.監聽是listView上哪列item被點選
            listView_diary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    //[1]-4.顯示內容
                    tv_food_d = adapterView.findViewById(R.id.tv_food_d);
                    Toast.makeText(context, "我的" + arr_mealName[position], Toast.LENGTH_SHORT).show();
                    calculate(view);

                    //判斷是否要跳轉到 food database 頁面
                    if (tv_food_d.length() != 0) {
                        //[1]-5.跳轉(結束)
                        tv_food_d.setText("");
                        Intent intent = new Intent(context, FoodDataActivity.class);
                        startActivity(intent);
                    } else {
                        //TODO:[Q]解決在指定的item列上顯示資料內容
                        Log.d(TAG, "item counts:" + listView_diary.getChildCount());

                        //[2]-6.(未解決)將使用者點選的內容顯示在 tv 上
                        tv_food_d.setText(name + "\n");
                        tv_food_d.append(size + " g\n");
                        tv_food_d.append(cal + " cal\n");
                        tv_food_d.append(pt + " g\n" + "");
                        tv_food_d.append(carbs + " g\n");
                        tv_food_d.append(fat + " g\n");
                    }
                }

            });//end listener
        }
    } //end findAndPutData()



    //TODO:計算機功能
    private void calculate(View view){
        //1.取得每餐的 tv_cal_d
        tv_cal_d = view.findViewById(R.id.tv_cal_d);
        Double cal = Double.parseDouble(tv_cal_d.getText().toString());
        Log.d(TAG, "double_cal:"+cal);

        //2.將每餐熱量(cal)的加總顯示在 tv_foodData
        Double total = 0.0;
        int item_counts =  listView_diary.getChildCount();
        for (int i=0; i<item_counts; i++){
            total += cal;
        }
        Log.d(TAG, "total:"+total);
        tv_foodData = findViewById(R.id.tv_foodData);
        tv_foodData.setText(total.toString());

        //3.取得使用者輸入的每日熱量目標
        et_goal = findViewById(R.id.et_goal);
        try {
            Double goal = Double.parseDouble(et_goal.getText().toString());
            Log.d(TAG, "goal:"+goal);

            //4.計算 goal - cal = remaining
            Double remaining = goal - cal;
            Log.d(TAG, "remaining:"+remaining);

            //5.將計算結果顯示在 tv_remaining(結束)
            tv_remaining = findViewById(R.id.tv_remaining);
            tv_remaining.setText(remaining.toString());

        } catch (NumberFormatException e) {
            Toast.makeText(context, "請輸入每日熱量目標", Toast.LENGTH_SHORT).show();
        }
    }//end calculate()



}   //end activity