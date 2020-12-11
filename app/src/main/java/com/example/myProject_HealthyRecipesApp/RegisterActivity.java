package com.example.myProject_HealthyRecipesApp;
//TODO:使用者按下使用者資訊頁中的 "註冊按鈕" 後會跳轉到此頁面，功能是進行註冊，完成註冊後會回到登入頁面。
//TODO:註冊資訊:username, email, psw, sex, photo
//TODO:填完註冊資訊後 -> 檢查欄位是否皆有填寫
//TODO:將所有資料上傳到 firebase


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Context context;
    private ImageView iv_R;
    private EditText et_username, et_email_R, et_psw_R;
    private Button btn_cancel, btn_register;
    private RadioGroup rg_gender;
    private boolean flagMale, flagFemale;
    private RadioButton rg_male, rg_female;
    private String TAG = "main";

    private FirebaseAuth authControl;
    private Switch switch_psw_R;


    //TODO:初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle("使用者註冊");
        context = this;

        //TODO:設定action bar上的返回鍵 1
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        authControl = FirebaseAuth.getInstance();

        findViews();
        setListener();
    }


    @Override
    protected void onStart() {
        super.onStart();
       // currentUser = authControl.getCurrentUser();

        //updateUI(currentUser);
    }

    //TODO:setListener()
    private void setListener() {

        switch_psw_R.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switch_psw_R.setText("On");
                    et_psw_R.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                } else {
                    switch_psw_R.setText("Off");
                    et_psw_R.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                }
            }
        });

        //button
        btn_cancel.setOnClickListener(new MyButton());
        btn_register.setOnClickListener(new MyButton());

        //radio button
        rg_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rBtn_male_R:
                        flagMale = true;
                        flagFemale = false;
                        Log.d(TAG, "flagMale: "+flagMale);
                        break;

                    case R.id.rBtn_female_R:
                        flagMale = false;
                        flagFemale = true;
                        Log.d(TAG, "flagFemale: "+flagFemale);
                        break;
                }
            }
        });
    }


    //TODO:findViews()
    private void findViews() {

        iv_R = findViewById(R.id.iv_R);

        switch_psw_R = findViewById(R.id.switch_psw_R);

        et_username = findViewById(R.id.editText_username_R);
        et_email_R = findViewById(R.id.editText_email_R);
        et_psw_R = findViewById(R.id.editText_psw_R);

        rg_gender = findViewById(R.id.radioGroup_gender_R);
        rg_male = findViewById(R.id.rBtn_male_R);
        rg_female = findViewById(R.id.rBtn_female_R);


        btn_cancel = findViewById(R.id.btn_cancel_R);
        btn_register = findViewById(R.id.btn_register_R);
    }

    //TODO:設定action bar上的返回鍵 2
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); //關掉目前的activity
                break;
        }
        return super.onOptionsItemSelected(item);
    } //end onOptionsItemSelected()


    //TODO:MyButton()，監聽 btn_cancel & btn_register
    private class MyButton implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_cancel_R:
                    et_email_R.setText("");
                    et_psw_R.setText("");
                    et_username.setText("");
                    rg_gender.clearCheck();
                    flagMale = false;
                    flagFemale = false;

                    break;

                //TODO:註冊的功能
                case R.id.btn_register_R:
                    if (et_email_R.length() == 0 || et_psw_R.length() == 0 || et_username.length() == 0) {
                        Toast.makeText(context, "請輸入帳號密碼與暱稱", Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        //如果有輸入帳號與密碼，就將editText上的文字存放到變數email & psw中
                        final String username = et_username.getText().toString();
                        final String email = et_email_R.getText().toString();
                        final String psw = et_psw_R.getText().toString();

                        Log.d(TAG, "username:"+username + " email:"+email + " psw:"+psw);
                        //利用帳號與密碼進行註冊:createUser，並監聽authControl
                        //currentUser = authControl.getCurrentUser();
                        authControl.createUserWithEmailAndPassword(email, psw).addOnCompleteListener((Activity) context
                                , new OnCompleteListener<AuthResult>() {
                            
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        Log.d(TAG, "onComplete: ");

                                        //如果註冊成功
                                        if (task.isSuccessful()) {
                                            Toast.makeText(context, "註冊成功", Toast.LENGTH_SHORT).show();

                                            //取得使用者的帳號與密碼
                                            FirebaseUser user = authControl.getCurrentUser();
                                            updateUI(user, username, email, psw);  //自訂方法

                                            //如果註冊失敗
                                        } else {
                                            Toast.makeText(context, "註冊失敗", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }); //end Listener()

                    } //end else
                    break;
            }   //end switch
        }   //end onClick
    }   //end MyButton()


    //Change UI according to user data.
    public void updateUI(FirebaseUser account, String username, String email, String psw){

        if(account != null){
            Toast.makeText(this,"登入成功",Toast.LENGTH_LONG).show();
            startActivity(new Intent(context,UserInfoActivity.class));

        }else {
            Toast.makeText(this,"登入失敗",Toast.LENGTH_LONG).show();
        }


        //TODO:將註冊的資料(暱稱、帳號、密碼上傳到 firebase_realtime database)
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("user");

        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("username", username);
        data.put("email", email);
        data.put("password", psw);

        Task<Void> result = myRef.child("").push().setValue(data);
        result.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "資料上傳成功", Toast.LENGTH_SHORT).show();
            }
        });

        result.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "資料上傳失敗", Toast.LENGTH_SHORT).show();
            }
        });

        //TODO:將註冊的資料照片上傳到 firebase storage

    }


}