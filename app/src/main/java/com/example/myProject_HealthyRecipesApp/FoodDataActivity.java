package com.example.myProject_HealthyRecipesApp;
//TODO:[目標:完成] 本頁的目的是顯示 JSON 資料
//TODO:[目標:完成] 點選食物後，會跳出對話框，使用者須填寫食物的重量，對話框有 "取消" 和 "確認"的按鈕
//TODO:[目標:完成] 按下確認後會跳回 DiaryActivity


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
    private String[] arr_food;
    private TextView tv_dialog_addFood;
    private EditText et_dialog_weight;


    private static Double weight;
    private ArrayList<String> name_list;
    private ArrayList<Double> size_list, pt_list, cal_list, carbs_list, fat_list;
    private static HashMap<String, Object> map;


    public HashMap<String, Object> getMap(){
        return map;
    }

    public Double getWeight(){
        return weight;
    }

    //TODO:初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fooddata);

        context = this;
        setTitle("食物列表");

        //TODO:action bar 1
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        lv_fooddata = findViewById(R.id.lv_food_data);
        findAndPutData();
        setListener();

    } //end onCreate()


    //TODO:findAndPutData():抓出JSON上食物的資料(資料由FoodDataHolder傳過來)
    private void findAndPutData() {
        //1.印出確認取得的list是否正確
        Log.d(TAG, "list"+list);    //確認有取到list
        Log.d(TAG, "list size:"+list.size());   //確認拿到list大小

        List<Map<String, Object>> list_use= new ArrayList<Map<String, Object>>();    //建立符合simpleAdapter所需要的list

        //2.將接收過來的list內的資料取出來存成個別的list
        name_list = new ArrayList<String>();
        size_list = new ArrayList<Double>();
        cal_list = new ArrayList<Double>();
        pt_list = new ArrayList<Double>();
        carbs_list = new ArrayList<Double>();
        fat_list = new ArrayList<Double>();
        for (int i=0; i<list.size(); i++){
            name_list.add(list.get(i).getName());
            size_list.add(list.get(i).getServing_size());
            cal_list.add(list.get(i).getCal());
            pt_list.add(list.get(i).getProtein());
            carbs_list.add(list.get(i).getCarbs());
            fat_list.add(list.get(i).getFat());
        }

        //3.將個別的list存入到list_map中。*注意:map的放法是一個key對應一個值，一條listView上就是顯示一個值。
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
        //4.印出來確認是否存放成功
        Log.d(TAG, "list_use:"+list_use);

        //5.將 list 設定給 adapter
        SimpleAdapter test_adapter = new SimpleAdapter(context, list_use, R.layout.fooddata_listview_item_layout,
                new String[]{"NAME", "SIZE", "CAL", "PROTEIN", "CARBS", "FAT"},
                new int[] {R.id.tv_name_f,R.id.tv_size_f, R.id.tv_cal_f, R.id.tv_pt_f, R.id.tv_carbs_f, R.id.tv_fat_f});
        lv_fooddata.setAdapter(test_adapter);

    } //end findAndPutData()



    //5.監聽 listView
    private void setListener(){
        lv_fooddata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //6-1.顯示對話框:取得對話框內容，將內容賦值給字串陣列
                arr_food = getResources().getStringArray(R.array.arr_food);

                //6-2.顯示對話框:將使用者點選的位置賦值給 index
                int index = (int) id;

                //6-3.顯示對話框:將arr_food[index]傳出，並呼叫對話框的方法
                dialog(arr_food[index]);

                //[目的]將使用者點選的資料傳到Calculate class做計算:用map傳。
                //7.將使用者點選的 index 賦值給 變數i
                int i = index;
                //8.建立 map
                map = new HashMap<String, Object>();

                //9.將個別 list 的內容取出並放入 map
                map.put("NAME", name_list.get(i));
                map.put("SIZE", size_list.get(i));
                map.put("CAL", cal_list.get(i));
                map.put("PROTEIN", pt_list.get(i));
                map.put("CARBS", carbs_list.get(i));
                map.put("FAT", fat_list.get(i));

                //10.印出檢查(結束)
                Log.d(TAG, "name_list[i]:"+name_list.get(i));
                Log.d(TAG, "map_sent:"+map);

            }
        });
    }//end setListener()


    //TODO:設定對話框的方法
    private Double dialog(String arr_food) {

        //6-4.先將 layout 吹出
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_layout, (ViewGroup) findViewById(R.id.dialog_id));

        tv_dialog_addFood = view.findViewById(R.id.tv_dialog_addFood);
        et_dialog_weight = view.findViewById(R.id.et_dialog_weight);

        //6-5.將6-3傳入的陣列元素顯示在對話框
        tv_dialog_addFood.setText(arr_food);
        et_dialog_weight.setText("");

        //6-6.跳出對話框讓使用者輸入食物的重量
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("請填寫重量")
                .setView(view)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //[目的]使用者輸入 weight 後，在 Calculate class做運算
                        //6-7.取得使用者輸入的重量並賦值給weight
                        weight = Double.parseDouble(et_dialog_weight.getText().toString());
                        Log.d(TAG, "weight(FDA):"+weight);

                        //6-8.跳轉到 DiaryActivity
                        Intent intent = new Intent(context, DiaryActivity.class);
                        startActivity(intent);
                    }
                });

        //6-10.設定對話框的取消按鈕
        builder.setNegativeButton("取消", null);
        //6-11.建立並顯示對話框
        builder.create().show();

        //6-9.將weight傳出到 Calculate class 進行後續的運算(結束)
        return weight;
    }//end dialog()


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

}//end