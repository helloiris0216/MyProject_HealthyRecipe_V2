package com.example.myProject_HealthyRecipesApp;
//TODO:[目標:完成] 接收JSON資料，並將解析好的資料傳出

import android.content.res.Resources;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FoodDataHolder {
    public static List<FoodDataHolder> list;
    private static String TAG="FoodData_Holder";

    private String food_name;
    private Double food_cal;
    private Double food_protein;
    private Double food_carbs;
    private Double food_fat;
    private Double food_serving_size;

//    FoodDataHolder(String food_cal, String food_carbs, String food_fat,
//                       String food_name, String food_protein, String food_serving_size){
//
//            this.food_cal = food_cal;
//            this.food_carbs = food_carbs;
//            this.food_fat = food_fat;
//            this.food_name = food_name;
//            this.food_protein = food_protein;
//            this.food_serving_size = food_serving_size;
//        }


    public String getName() {
        return food_name;
    }

    public Double getCal() {
        return food_cal;
    }

    public Double getProtein() {
        return food_protein;
    }

    public Double getCarbs() {
        return food_carbs;
    }

    public Double getFat() {
        return food_fat;
    }

    public Double getServing_size() {
        return food_serving_size;
    }



    public static List<FoodDataHolder> init(Resources res){

        InputStream is = res.openRawResource(R.raw.fooddata);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String jsonString = writer.toString();
        Log.d(TAG, "jsonString:"+jsonString);


        //利用GSON將資料傳出去
        Gson gson = new Gson();

        Type type = new TypeToken<ArrayList<FoodDataHolder>>() {}.getType();
        list = gson.fromJson(jsonString, type);
        Log.d(TAG, "list:"+list);

        return list;

    }   //end init()
}   //end
