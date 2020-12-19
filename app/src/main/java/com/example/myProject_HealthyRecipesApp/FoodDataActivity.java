package com.example.myProject_HealthyRecipesApp;
//TODO:[目標] 本頁的目的是顯示 JSON 資料
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

import static com.example.myProject_HealthyRecipesApp.FoodDataHolder.list;


public class FoodDataActivity extends AppCompatActivity {

    private String TAG = "foodData_activity";
    private ListView lv_fooddata;
    private Context context;
    private ArrayList<Map<String, Object>> dataList;
    private SimpleAdapter adapter;
    private TextView tv_foodData;
    private String[] arr_food;
    private List<Map<String, Object>> list_food;
    private TextView tv_dialog_addFood;
    private EditText et_dialog_weight;
    private TextView tv_name_f, tv_size_f, tv_cal_f, tv_pt_f, tv_carbs_f, tv_fat_f;


    private static Double weight;

    public Double getWeight(){

        return weight;
    }

    //TODO:初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fooddata);

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



    //TODO:findAndPutData():抓出JSON上食物的資料(資料由FoodDataHolder傳過來)
    private void findAndPutData() {


        //get list(contains GSON)
        Log.d(TAG, "list"+list);    //確認有取到list
        Log.d(TAG, "list size:"+list.size());   //確認拿到list大小


        List<Map<String, Object>> list_use= new ArrayList<Map<String, Object>>();    //建立符合simpleAdapter所需要的list

        //TODO:1.先將接收過來的list內的資料取出來存成個別的list
        ArrayList<String> name_list = new ArrayList<String>();
        ArrayList<Double> size_list = new ArrayList<Double>();
        ArrayList<Double> cal_list = new ArrayList<Double>();
        ArrayList<Double> pt_list = new ArrayList<Double>();
        ArrayList<Double> carbs_list = new ArrayList<Double>();
        ArrayList<Double> fat_list = new ArrayList<Double>();
        for (int i=0; i<list.size(); i++){
            name_list.add(list.get(i).getName());
            size_list.add(list.get(i).getServing_size());
            cal_list.add(list.get(i).getCal());
            pt_list.add(list.get(i).getProtein());
            carbs_list.add(list.get(i).getCarbs());
            fat_list.add(list.get(i).getFat());
        }

        //TODO:2.在將字串陣列存入到list_map中。*注意:map的放法是一個key對應一個值，一條listView上就是顯示一個值。
        for (int i=0; i<name_list.size(); i++) {
            Map<String, Object> list_map = new HashMap<String, Object>();   //建立MAP來裝list
            list_map.put("NAME", name_list.get(i));
            list_map.put("SIZE", size_list.get(i)+" g");
            list_map.put("CAL", cal_list.get(i)+" cal");
            list_map.put("PROTEIN", pt_list.get(i)+" g");
            list_map.put("CARBS", carbs_list.get(i)+" g");
            list_map.put("FAT", fat_list.get(i)+" g");

            Log.d(TAG, "list_map:"+list_map);
            Log.d(TAG, "KEY:"+list_map.keySet());
            list_use.add(list_map);
        }
        Log.d(TAG, "list_use:"+list_use);

        //TODO:3.setAdapter
        SimpleAdapter test_adapter = new SimpleAdapter(context, list_use, R.layout.fooddata_listview_item_layout,
                new String[]{"NAME", "SIZE", "CAL", "PROTEIN", "CARBS", "FAT"},
                new int[] {R.id.tv_name_f,R.id.tv_size_f, R.id.tv_cal_f, R.id.tv_pt_f, R.id.tv_carbs_f, R.id.tv_fat_f});
        lv_fooddata.setAdapter(test_adapter);

    } //end findAndPutData()



    //TODO:setListener()
    private void setListener(){
        lv_fooddata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                arr_food = getResources().getStringArray(R.array.arr_food);
                int index = 0;
                for (int i=0; i<arr_food.length; i++){
                    index = (int) id;
                }

                dialog(arr_food[index]);
            }
        });
    }


    //TODO:dialog()
    private Double dialog(String arr_food) {

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

                        //[目的]使用者輸入 weight 後，在 Calculate class做運算
                        weight = Double.parseDouble(et_dialog_weight.getText().toString());
                        Log.d(TAG, "weight:"+weight);
                        Intent intent = new Intent(context, DiaryActivity.class);
//                        intent.putExtra("WEIGHT", weight);
                        startActivity(intent);

                    }
                });

        builder.setNegativeButton("取消", null);
        builder.create().show();
        return weight;  //TODO:將weight傳給 Calculate class運算
    }   //end dialog()


    //TODO:findViews();
    private void findViews() {
        lv_fooddata = findViewById(R.id.lv_food_data);
        tv_name_f = findViewById(R.id.tv_name_f);
        tv_size_f = findViewById(R.id.tv_size_f);
        tv_cal_f = findViewById(R.id.tv_cal_f);
        tv_pt_f = findViewById(R.id.tv_pt_f);
        tv_carbs_f = findViewById(R.id.tv_carbs_f);
        tv_fat_f = findViewById(R.id.tv_fat_f);

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