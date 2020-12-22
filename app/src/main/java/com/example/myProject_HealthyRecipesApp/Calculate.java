package com.example.myProject_HealthyRecipesApp;
//TODO:[目標:完成]取得FoodDataActivity傳出的 weight，並在此類別進行運算，最後將運算結果傳到DiaryActivity中的tv顯示


import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;



public class Calculate {

    private static String TAG="calculate";
    private static List<Calculate> list_Cal;
    private  static Double weight_num;
    private static String name;
    private static Double size, cal, pt, carbs, fat;


    //0.設定資料的 setter
    public void setData(String name_c, Double size_c, Double cal_c, Double pt_c, Double carbs_c, Double fat_c){
        //weight_num = weightValue;
        name = name_c;
        size = size_c;
        cal = cal_c;
        pt = pt_c;
        carbs = carbs_c;
        fat = fat_c;
    }

    public  static List<Calculate> init() {

        FoodDataActivity fda = new FoodDataActivity();
        weight_num = fda.getWeight();
        Log.d(TAG, "weight_num:" + weight_num);


        //新增部分[開始]>>>>>>>>>>>>>>>>
        //1.為 list_Cal 配置記憶體
        list_Cal = new ArrayList<Calculate>();

        Log.d(TAG, "fda.getMap::"+fda.getMap());


        //2.宣告一個要放進 list_Cal 的 Calculate 物件，此物件名為 myCal
        //由於 list_Cal 是 List<Calculate> 類型，所以此 List 內的元素物件是 Calculate 類型
        Calculate myCal = new Calculate();

        //3.初始化 myCal，即給予初始值，此處為 0.0
        //這邊用 SETTER 來初始化
        if (weight_num==null){
            myCal.setData("和牛", 100.0, 189.0, 19.2, 7.1,8.4);
        }else {

            //3-1 將從FoodDataActivity丟過來的資料取出來
            Log.d(TAG, "fda.getMap().size():"+fda.getMap().size());

            name = (String) fda.getMap().get("NAME");
            size = (Double) fda.getMap().get("SIZE");
            cal = (Double) fda.getMap().get("CAL");
            pt = (Double) fda.getMap().get("PROTEIN");
            carbs = (Double) fda.getMap().get("CARBS");
            fat = (Double) fda.getMap().get("FAT");
            Log.d(TAG, "name:"+name);
            Log.d(TAG, "size:"+size);
            Log.d(TAG, "cal:"+cal);
            Log.d(TAG, "pt:"+pt);
            Log.d(TAG, "carbs:"+carbs);
            Log.d(TAG, "fat:"+fat);

            //3-2 計算
            size = (weight_num * size) / 100;
            cal = (weight_num * cal) / 100;
            pt = (weight_num * pt) / 100;
            carbs = (weight_num * carbs) / 100;
            fat = (weight_num * fat) / 100;

            //3-3 印出來看看
            Log.d(TAG, "size:"+size);
            Log.d(TAG, "cal:"+cal);
            Log.d(TAG, "pt:"+pt);
            Log.d(TAG, "carbs:"+carbs);
            Log.d(TAG, "pt:"+fat);

        }
        //4.賦值給 myCal
        myCal.setData(name, size, cal, pt, carbs, fat);

        //5.將已賦值的 myCal 加入 list_Cal
        list_Cal.add(myCal);


        //6.印出來看看
        Log.d(TAG, "list_cal size:"+list_Cal.size());
       // Log.d(TAG, "weight_num(C):" + list_Cal.get(0).getWeight());
        //新增部分[結束]<<<<<<<<<<<<<<<<

        return list_Cal;
    }

    //7.設定取得屬性的方法
    public String getName_cal(){return name;}
    public Double getSize_cal(){return size;}
    public Double getCal_cal(){return cal;}
    public Double getPt_cal(){return pt;}
    public Double getCarbs_cal(){return carbs;}
    public Double getFat_cal(){return fat;}

}