package com.example.myProject_HealthyRecipesApp;
//TODO:[目標]取得FoodDataActivity傳出的 weight，並在此類別進行運算，最後將運算結果傳到DiaryActivity中的tv顯示


import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import static com.example.myProject_HealthyRecipesApp.FoodDataHolder.list;

public class Calculate {

    private static String TAG="calculate";
    private static List<Calculate> list_Cal;
    private  static Double weight_num=10.0;


    public Double getWeight(){
        return weight_num;
    }

    //0.設定 weight_num 的 setter
    public void setWeight(Double weightValue){weight_num = weightValue;}

    public  static List<Calculate> init(){

        FoodDataActivity fda = new FoodDataActivity();
        weight_num = fda.getWeight();
//        Log.d(TAG, "weight_num(C):"+weight_num);

        //新增部分[開始]>>>>>>>>>>>>>>>>
        //1.為 list_Cal 配置記憶體
        list_Cal = new ArrayList<Calculate>();

        //2.宣告一個要放進 list_Cal 的 Calculate 物件，此物件名為 myCal
        //由於 list_Cal 是 List<Calculate> 類型，所以此 List 內的元素物件是 Calculate 類型
        Calculate myCal = new Calculate();

        //3.初始化 myCal，即給予初始值，此處為 0.0
        //這邊用 SETTER 來初始化
        myCal.setWeight(0.0);

        //4.將已賦值的 myCal 加入 list_Cal
        list_Cal.add(myCal);

        //5.印出來看看
        Log.d(TAG, "weight_num(C):"+list_Cal.get(0).getWeight());
        //新增部分[結束]<<<<<<<<<<<<<<<<

        return list_Cal;
    }   //end init()

}
