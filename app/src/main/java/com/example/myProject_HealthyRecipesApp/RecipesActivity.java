package com.example.myProject_HealthyRecipesApp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class RecipesActivity extends AppCompatActivity {

    private ImageView iv;
    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        int img = intent.getIntExtra("image", 0);
        Log.d("intent", "title:"+title);
        Log.d("intent", "img:"+img);
        tv_title = (TextView) findViewById(R.id.tv_title_recipes);
        tv_title.setText(title);
        iv = (ImageView)findViewById(R.id.iv_recipes);
        iv.setImageResource(img);


    }
}