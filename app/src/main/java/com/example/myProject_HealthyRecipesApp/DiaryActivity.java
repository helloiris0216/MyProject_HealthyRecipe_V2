package com.example.myProject_HealthyRecipesApp;
//TODO:[總目標] 本頁面的功能是紀錄使用者每天的飲食，並計算出食物中的熱量。
//TODO:[目標1:完成] 建立 action bar。
//TODO:[目標2:完成] 按下 "加入食物" 後，會跳至另一個顯示食物的頁面，讓使用者點選食物。
//TODO:[目標3:完成] (從firebase上拉資料)會將食物名稱和所對應的熱量顯示在 textView 上。
//TODO:[目標4:完成] 顯示每餐食物的總熱量。
//TODO:[目標5:完成] 將一天的總熱量做加總。
//TODO:[目標6] 日記需要有修改和刪除的功能，修改->menu；刪除->長按 listView。


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.myProject_HealthyRecipesApp.FoodDataHolder.list;


public class DiaryActivity extends AppCompatActivity {

    private static String[] arr_mealName, arr_foodData, arr_foodCal;
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
    //紀錄是否有新增項目
    static Boolean hasClickedItem = false;

    String name;
    String size;
    String cal;
    String pt;
    String carbs;
    String fat;
    private Double cal_c;
    private EditText et_goal;
    static View v;
    private BottomNavigationView bottomNavigation;
    static Double total = 0.0, goal = 0.0, br_total = 0.0, lc_total = 0.0, dn_total = 0.0, sn_total = 0.0;
    private AdapterView<?> parent;
    private static View viewNew;
    private int positionNew;
    private Button btn_ok;


    //TODO:初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        setTitle("我的日記");
        context = this;
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation_d);
        tv_foodData = findViewById(R.id.tv_foodData);
        btn_ok = findViewById(R.id.btn_ok);
        et_goal =  findViewById(R.id.et_goal);

        setNavigation();

        list = FoodDataHolder.init(getResources());
        list_calculate = Calculate.init();

        //TODO:action bar 1
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);


        data_fromCal = new Calculate();
        findAndPutData();
        //calculate();
        tv_food_d = viewNew.findViewById(R.id.tv_food_d);   //在外面呼叫 listView 中的 view，就可以找到元件了
        Intent come = this.getIntent();
        if (come.getIntExtra("BACK_FROM_DIARY", 0) == 123) {
            Log.i("XXXXX", "Come from myself");
        } else {
            Log.i("XXXXX", "Come from other");
            setPref();
            display_pref();
        }


        //TODO:[1]計算機:請使用者輸入目標熱量，並做偏好設定、計算熱量盈餘
        //1.取得 ui 元件
        et_goal = findViewById(R.id.et_goal);
        if (et_goal.getText().toString().length() == 0) {
            //2.跳出吐司
            Toast.makeText(context, "請輸入每日熱量目標", Toast.LENGTH_SHORT).show();
        }

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
    //TODO:[2]在 listView 上顯示由 Calculate class 傳過來的運算結果
    //TODO:[3]在 listView 上顯示熱量的計算結果
    private void findAndPutData() {
        listView_diary = findViewById(R.id.listView_diary);

        //[1]-1 & [2]-1.條件檢查
        if (clickedItem == -1) {
            //使用者從未選取項目則做

            //[1]-2拿到meal name(早午晚點心) & 將meal name放到list中
            //[★★★] listView 初始化
            arr_mealName = getResources().getStringArray(R.array.arr_meal);
            arr_foodData = new String[]{"", "", "", ""};
            arr_foodCal = new String[]{"", "", "", ""};

            list_meal = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < arr_mealName.length; i++) {
                meal_data = new HashMap<String, Object>();
                meal_data.put("MEALNAME", arr_mealName[i]);
                meal_data.put("FOOD", arr_foodData[i]);
                meal_data.put("CAL", arr_foodCal[i]);
                list_meal.add(meal_data);
            }

            //[1]-3.將要顯示的清單存入到 adapter
            final SimpleAdapter adapter = new SimpleAdapter(context, list_meal, R.layout.diary_listview_item_layout,
                    new String[]{"MEALNAME", "FOOD", "CAL"},
                    new int[]{R.id.tv_meal_d, R.id.tv_food_d, R.id.tv_cal_d});
            //[1]-4.連結adapter
            listView_diary.setAdapter(adapter);

        } else if (!hasClickedItem) {
            /**
             * 此區塊為新增程式碼
             */
            //使用者離開頁面時無選取項目則做

            name = data_fromCal.getName_cal();
            size = data_fromCal.getSize_cal().toString();
            cal = data_fromCal.getCal_cal().toString();
            pt = data_fromCal.getPt_cal().toString();
            carbs = data_fromCal.getCarbs_cal().toString();
            fat = data_fromCal.getFat_cal().toString();

            //[2]-3.陣列初始化
            DecimalFormat df = new DecimalFormat("0.0");    //將顯示內容取小數點後兩位
            arr_foodData[clickedItem] =
                    arr_foodData[clickedItem];

            //[2]-4 & [3]-1.取得食物的 cal
            Log.d(TAG, "確認資料>>>");
            cal_c = Double.parseDouble(data_fromCal.getCal_cal().toString());
            Log.d(TAG, "cal_c:" + cal_c);

            //印出一天熱量
            Log.d(TAG, "total:" + total);

            //[3]-3.將加總後的 total 直接顯示在 tv_foodData
            tv_foodData.setText(df.format(total));

            //[★★★] listView 初始化
            //[1]-5 & [2]-6.將陣列元素放進 list 內
            list_meal = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < arr_mealName.length; i++) {
                meal_data = new HashMap<String, Object>();
                meal_data.put("MEALNAME", arr_mealName[i]);
                meal_data.put("FOOD", arr_foodData[i]);
                meal_data.put("CAL", arr_foodCal[i]);
                list_meal.add(meal_data);
            }

        } else if (hasClickedItem) {
            //使用者離開頁面時有選取項目則做

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

            //[2]-3.陣列初始化
            DecimalFormat df = new DecimalFormat("0.0");    //將顯示內容取小數點後兩位
            arr_foodData[clickedItem] =
                    arr_foodData[clickedItem] +
                            "\n品名: " + name + "\n" +
                            "份量: " + df.format(Double.parseDouble(size)) + " g\n" +
                            "卡路里: " + df.format(Double.parseDouble(cal)) + "  cal\n" +
                            "蛋白質: " + df.format(Double.parseDouble(pt)) + " g\n" +
                            "碳水化合物: " + df.format(Double.parseDouble(carbs)) + " g\n" +
                            "脂質: " + df.format(Double.parseDouble(fat)) + " g\n";


            //[2]-4 & [3]-1.取得食物的 cal
            Log.d(TAG, "確認資料>>>");
            cal_c = Double.parseDouble(data_fromCal.getCal_cal().toString());
            Log.d(TAG, "cal_c:" + cal_c);

            //[3]-2.計算一天的熱量
            total += cal_c;
            Log.d(TAG, "total:" + total);

            //[2]-5.將四餐的 cal 個別取出後存放在個別的變數內，利用 clickedItem 做判斷
            switch (clickedItem) {
                case 0:
                    //先將 br_total初始化為0.0，在將食物的 cal 相加 :
                    // br_total=0.0
                    // br_total = br_total + cal_c
                    br_total += cal_c;

                    //[2]-6.將個別的 total 賦值給陣列
                    arr_foodCal[clickedItem] = df.format(br_total);
                    break;
                case 1:
                    lc_total += cal_c;
                    arr_foodCal[clickedItem] = df.format(lc_total);
                    break;
                case 2:
                    dn_total += cal_c;
                    arr_foodCal[clickedItem] = df.format(dn_total);
                    break;
                case 3:
                    sn_total += cal_c;
                    arr_foodCal[clickedItem] = df.format(sn_total);
                    break;
            }


            //[3]-3.將加總後的 total 直接顯示在 tv_foodData
            tv_foodData.setText(df.format(total));


            //[★★★] listView 初始化
            //[1]-5 & [2]-6.將陣列元素放進 list 內
            list_meal = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < arr_mealName.length; i++) {
                meal_data = new HashMap<String, Object>();
                meal_data.put("MEALNAME", arr_mealName[i]);
                meal_data.put("FOOD", arr_foodData[i]);
                meal_data.put("CAL", arr_foodCal[i]);
                list_meal.add(meal_data);
            }

        }//end != -1

        //[1]-6 & 2-[7].將要顯示的清單存入到 adapter
        final SimpleAdapter adapterNew = new SimpleAdapter(context, list_meal, R.layout.diary_listview_item_layout,
                new String[]{"MEALNAME", "FOOD", "CAL"},
                new int[]{R.id.tv_meal_d, R.id.tv_food_d, R.id.tv_cal_d});
        //連結adapter
        listView_diary.setAdapter(adapterNew);
        viewNew = adapterNew.getView(positionNew, v, parent); //利用 adapter 內建的方法來取得監聽 listView 後所產生的參數


        //[1]-7 & [2]-8.監聽是listView上哪列item被點選
        listView_diary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //[★★★] 紀錄被點選的項目編號
                clickedItem = position;

                //TODO:將 listView 參數取出
                positionNew = position;
                parent = adapterView;
                viewNew = view;

                //設為"有選取項目"狀態
                hasClickedItem = true;

                //跳頁
                Intent intent = new Intent(context, FoodDataActivity.class);
                startActivity(intent);

            }
        }); //end listener
    }//end findAndPutData()

    @Override
    protected void onResume() {
        super.onResume();
        calculate();

    }


    //TODO:[2]請使用者輸入目標熱量，並做偏好設定、計算機功能
    //在onResume()呼叫
    //3.將每樣食物的 cal 加總顯示在每餐的 tv_cal_d
    private void calculate() {
        //1.獲取使用者輸入

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_goal.getText().toString().length()==0){
                    Toast.makeText(context, "請輸入每日的目標熱量", Toast.LENGTH_SHORT).show();

                } else {
                    goal = Double.parseDouble(et_goal.getText().toString());
                    Log.d(TAG, "goal:" + goal);


                    //3. remaining = goal - total
                    Double remaining = 0.0;
                    remaining = goal - total;
                    Log.d(TAG, "remaining:" + remaining.toString());

                    //4.將 remaining 顯示在 tv_remaining
                    tv_remaining = findViewById(R.id.tv_remaining);
                    tv_remaining.setText(remaining.toString());
                }

            }
        });
    }//end calculate()


    //TODO:偏好設定
    //onCreate()呼叫
    private void setPref() {
        //1.建立欲存放的變數的 pref & 將變數寫入建立好的 pref
        //[1] 計算機
        SharedPreferences pref_goal = getSharedPreferences("cal_goal", MODE_PRIVATE);
        pref_goal.edit().putString("GOAL", goal.toString()).commit();
        Log.d(TAG, "pref:" + pref_goal.toString());

        //[2]listView tv
        SharedPreferences pref_tvContent = getSharedPreferences("tv_content", MODE_PRIVATE);
        Log.d(TAG, "TV_BR(setPref):" + tv_food_d.getText().toString());   //確認要放入的資料

        pref_tvContent.edit().putString("TV_BR", tv_food_d.getText().toString()).commit();

    }

    //onPause()呼叫
    private void display_pref() {
        //2.取得偏好設定
        //[1] 計算機
        //3.將 pref 設定給 et_goal
        String goal_str = getSharedPreferences("cal_goal", MODE_PRIVATE).getString("GOAL", "");
        Log.d(TAG, "goal_str:" + goal_str);
        if (et_goal.getText().toString().length() == 0) {
            Log.d(TAG, "et_goal.getText().length(): " + tv_food_d.getText().length());

            et_goal.setText(goal_str);
        }else{
            et_goal.setText("no data to show");
        }

        //[2] listView tv
        String tv_br = getSharedPreferences("tv_content", MODE_PRIVATE).getString("TV_BR", "");
        Log.d(TAG, "TV_BR(savePref):" + tv_br);

        if (tv_food_d.getText().toString().length() == 0) {
            Log.d(TAG, "tv_food_d.getText().length(): " + tv_food_d.getText().length());

            tv_food_d.setText(tv_br);
        }else{
            tv_food_d.setText("no data to show");
        }

    }//end display_pref()


    //TODO:監聽bottomNaigation(最下方的action bar)，並設定使用者按下後會跳轉到指定頁面
    private void setNavigation() {
        bottomNavigation.setItemIconTintList(null);
        bottomNavigation.setItemTextColor(null);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    //回首頁
                    case R.id.homePage:
                        Toast.makeText(context, "回首頁", Toast.LENGTH_SHORT).show();
                        Intent intent_home = new Intent(context, HomePageActivity.class);
                        startActivity(intent_home);
                        //設為"無選取項目"狀態
                        hasClickedItem = false;
                        break;

                    //回個人資訊頁
                    case R.id.user:
                        Toast.makeText(context, "回使用者資訊頁", Toast.LENGTH_SHORT).show();
                        Intent intent_user = new Intent(context, UserInfoActivity.class);
                        startActivity(intent_user);
                        //設為"無選取項目"狀態
                        hasClickedItem = false;
                        break;

                    //到我的日記頁
                    case R.id.diary:
                        Toast.makeText(context, "我的日記", Toast.LENGTH_SHORT).show();
                        Intent intent_diary = new Intent(context, DiaryActivity.class);
                        intent_diary.putExtra("BACK_FROM_DIARY", 123);
                        startActivity(intent_diary);
                        //設為"無選取項目"狀態
                        hasClickedItem = false;
                        break;

                } //end switch

                return true;
            } //end onNavigationItemSelected
        }); //end bottomNavigation listener
    } //end setListener


}//end activity