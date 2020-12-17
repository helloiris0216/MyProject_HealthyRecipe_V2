package com.example.myProject_HealthyRecipesApp;

import android.util.Log;

import java.util.List;
import static com.example.myProject_HealthyRecipesApp.FoodDataHolder.list;

public class Calculate {

    private static String TAG="calculate";

    public static List<Calculate> init(){

        FoodDataActivity fda = new FoodDataActivity();
        String weight = fda.getWeight();
        Log.d(TAG, "init() weight:"+weight);


        return list_Cal;
    }   //end init()

}
