package com.example.myProject_HealthyRecipesApp;
//TODO:[目標]取得FoodDataActivity傳出的 weight，並在此類別進行運算，最後將運算結果傳到DiaryActivity中的tv顯示


import android.util.Log;

import java.util.List;
import static com.example.myProject_HealthyRecipesApp.FoodDataHolder.list;

public class Calculate {

    private static String TAG="calculate";
    private static List<Calculate> list_Cal;
    private  static Double weight_num=10.0;


    public Double getWeight(){
        return weight_num;
    }

    public  static List<Calculate> init(){

        FoodDataActivity fda = new FoodDataActivity();
        weight_num = fda.getWeight();
        Log.d(TAG, "weight_num:"+weight_num);


        return list_Cal;
    }   //end init()

}
