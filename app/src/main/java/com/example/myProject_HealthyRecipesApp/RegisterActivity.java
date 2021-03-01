package com.example.myProject_HealthyRecipesApp;
//TODO:[目標:完成] 使用者按下使用者資訊頁中的 "註冊按鈕" 後會跳轉到此頁面，功能是進行註冊，完成註冊後會回到登入頁面。
//TODO:[目標:完成] 註冊資訊:username, email, psw, sex, photo
//TODO:[目標:完成]填完註冊資訊後 -> 檢查欄位是否皆有填寫
//TODO:[目標:完成] 將所有資料上傳到 firebase


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 1888;
    private Context context;
    private EditText et_username, et_email_R, et_psw_R;
    private Button btn_cancel, btn_register, btn_take;
    private RadioGroup rg_gender;
    private boolean flagMale, flagFemale;
    private RadioButton rg_male, rg_female;
    private String TAG = "register activity";
    private ImageView iv_user_pic;

    private FirebaseAuth authControl;
    private Switch switch_psw_R;
    private BottomNavigationView bottomNavigation;
    private Bitmap theImage;
    private StorageReference mStorageRef;
    private ConstraintLayout constraint;


    //TODO:初始化
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle("使用者註冊");
        context = this;

        //TODO:設定action bar上的返回鍵 1
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        //TODO:取得firebase的console:https://fir-app-60599.firebaseio.com/class
        //所有的方法都放在library內，要建立物件來使用這些方法
        mStorageRef = FirebaseStorage.getInstance().getReference();
        authControl = FirebaseAuth.getInstance();
        Log.d(TAG, "authControl:" + authControl);


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
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setListener() {
        //監聽switch btn
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
        btn_take.setOnClickListener(new MyButton());

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
        });//end radio btn

        //監聽bottomNaigation
        bottomNavigation.setItemIconTintList(null);
        bottomNavigation.setItemTextColor(null);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    //回首頁
                    case R.id.homePage:
                        Toast.makeText(context, "回首頁", Toast.LENGTH_SHORT).show();
                        Intent intent_home = new Intent(context, HomeActivity.class);
                        startActivity(intent_home);
                        finish();
                        break;

                    //回個人資訊頁
                    case R.id.user:
                        Toast.makeText(context, "回使用者資訊頁", Toast.LENGTH_SHORT).show();
                        Intent intent_user = new Intent(context, UserInfoActivity.class);
                        startActivity(intent_user);
                        finish();
                        break;

                    //到我的日記頁
                    case R.id.diary:
                        Toast.makeText(context, "我的日記", Toast.LENGTH_SHORT).show();
                        Intent intent_diary = new Intent(context, DiaryActivity.class);
                        startActivity(intent_diary);
                        finish();
                        break;
                }
                return true;
            } //end onNavigationItemSelected
        }); //end bottomNavigation listener
    }//end setListener()


    //TODO:findViews()
    private void findViews() {
        switch_psw_R = findViewById(R.id.switch_psw_R);

        et_username = findViewById(R.id.editText_username_R);
        et_email_R = findViewById(R.id.editText_email_R);
        et_psw_R = findViewById(R.id.editText_psw_R);

        rg_gender = findViewById(R.id.radioGroup_gender_R);
        rg_male = findViewById(R.id.rBtn_male_R);
        rg_female = findViewById(R.id.rBtn_female_R);

        btn_cancel = findViewById(R.id.btn_cancel_R);
        btn_register = findViewById(R.id.btn_register_R);
        btn_take = findViewById(R.id.btn_take);
        iv_user_pic = findViewById(R.id.iv_userPic);

        constraint = findViewById(R.id.constraint_res);
        constraint.getBackground().setAlpha(125);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation_r);
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


    //TODO:MyButton()方法
    @RequiresApi(api = Build.VERSION_CODES.M)
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
                        Toast.makeText(context, "請填入您的資料", Toast.LENGTH_SHORT).show();
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

                case R.id.btn_take:
                    Toast.makeText(context, "拍照", Toast.LENGTH_SHORT).show();

                    //確認使用者是否按下同意
                    if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        String[] permission = {Manifest.permission.CAMERA};
                        RegisterActivity.this.requestPermissions(permission, MY_CAMERA_PERMISSION_CODE);


                    } else {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        RegisterActivity.this.startActivityForResult(cameraIntent, CAMERA_REQUEST);

                    } //end if(checkSelfPermission)
                    break;

            }   //end switch
        }   //end onClick
    }   //end MyButton()


    //Change UI according to user data.
    public void updateUI(FirebaseUser account, String username, String email, String psw){

        if(account != null) {
            //Toast.makeText(this, "登入成功", Toast.LENGTH_LONG).show();
            startActivity(new Intent(context, UserInfoActivity.class));
        }

        //TODO:將註冊的資料(暱稱、帳號、密碼、照片上傳到 firebase_realtime database)
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("user");
//
//        HashMap<String, Object> data = new HashMap<String, Object>();
//        data.put("username", username);
//        data.put("email", email);
//        data.put("password", psw);
//
//        Task<Void> result = myRef.child("").push().setValue(data);
//        result.addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(context, "資料上傳成功", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        result.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(context, "資料上傳失敗", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        //busy.setVisibility(View.VISIBLE);
//        Toast.makeText(context, "上傳中....", Toast.LENGTH_LONG).show();


        /**將ImageView 中的圖片化為  byte 陣列*/
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] blob;//ContentValues 需要放入圖片,但必須是byte[]形式

        //Bitmap物件有一個函數compress,可以對圖片進行壓縮並儲存到ByteArrayOutputStream物件中
        theImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        blob = stream.toByteArray(); //ByteArrayOutputStream物件有一個toByteArray函數可以轉出byte[]

        StorageReference leaf = mStorageRef.child("user_pic.jpg");
        UploadTask task = leaf.putBytes(blob);

        //監聽任務(上傳)是否成功
        task.addOnSuccessListener(
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(context, "上傳成功", Toast.LENGTH_LONG).show();
                    }
                }
        );
        //監聽任務(上傳)是否失敗
        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "上傳失敗", Toast.LENGTH_LONG).show();
                    }
                }
        );

    }// end updateUI()


    //TODO:取得使用者的拍照權限 & 設定照片
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            theImage = (Bitmap) data.getExtras().get("data");
            iv_user_pic.setImageBitmap(theImage);
        }
    }//end onActivityResult()



    //當使用者按下同意就跑這個函數
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }//end onRequestPermissionsResult()



}//end