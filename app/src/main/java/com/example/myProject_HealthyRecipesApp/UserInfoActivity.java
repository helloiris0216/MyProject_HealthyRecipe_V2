package com.example.myProject_HealthyRecipesApp;
//TODO:[目標:完成] 按下導覽列中的 "user" 會跳轉至此頁面，功能是讓使用者登入 & 登出用。
//TODO:[目標:完成] 使用者輸入 email & psw ，按下 "登入按鈕" 後先檢查使用者的欄位是否都有輸入
//TODO:[目標:完成] 接著檢查輸入的資料是否符合firebase上的資料
//TODO:[目標] 都ok後，將 photo 從 firebase 下載至 imageView上顯示，並跳出吐司 : 哈囉，username
//TODO:[目標] 使用者登入後才會有存取資料的功能(日記)

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ProgressBar;
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

import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

//TODO:使用者點選bottom navigation中的user，會跳至此頁面
public class UserInfoActivity extends AppCompatActivity {

    private ImageView iv;
    private EditText etEmail, etPsw;
    private Switch switchPsw;
    private Button btnLogin, btnRegister, btnLogout, btnDownload;
    private String TAG = "main";
    private FirebaseAuth authControl;

    private FirebaseUser currentUser;


    private Bitmap theImage;
    private StorageReference mStorageRef;
    private Context context;
    private BottomNavigationView bottomNavigation;
    private ProgressBar progress;


    //TODO:設定初始值
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        context = this;
        setTitle("我的資訊");


        //TODO:設定action bar上的返回鍵 1
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        findViews();
        progress.setVisibility(View.INVISIBLE);
        setListener();


        //TODO:取得firebase的console:https://fir-app-60599.firebaseio.com/class
        //所有的方法都放在library內，要建立物件來使用這些方法
        mStorageRef = FirebaseStorage.getInstance().getReference();
        authControl = FirebaseAuth.getInstance();
        Log.d(TAG, "authControl:" + authControl);


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


    //TODO:setListener()
    private void setListener() {
        switchPsw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchPsw.setText("On");
                    etPsw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                } else {
                    switchPsw.setText("Off");
                    etPsw.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                }
            }
        });

        btnLogin.setOnClickListener(new MyButton());
        btnLogout.setOnClickListener(new MyButton());
        btnRegister.setOnClickListener(new MyButton());
        btnDownload.setOnClickListener(new MyButton());

        //TODO:監聽bottomNaigation(最下方的action bar)，並設定使用者按下後會跳轉到指定頁面
        bottomNavigation.setItemIconTintList(null);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    //回首頁
                    case R.id.homePage:
                        Toast.makeText(context, "回首頁", Toast.LENGTH_SHORT).show();
                        Intent intent_home = new Intent(context, HomePageActivity.class);
                        startActivity(intent_home);

                        break;

                    //回個人資訊頁
                    case R.id.user:
                        Toast.makeText(context, "回使用者資訊頁", Toast.LENGTH_SHORT).show();
                        Intent intent_user = new Intent(context, UserInfoActivity.class);
                        startActivity(intent_user);

                        break;

                    //到我的日記頁
                    case R.id.diary:
                        Toast.makeText(context, "我的日記", Toast.LENGTH_SHORT).show();
                        Intent intent_diary = new Intent(context, DiaryActivity.class);
                        startActivity(intent_diary);

                        break;

                } //end switch

                return true;
            } //end onNavigationItemSelected
        }); //end bottomNavigation listener
    } //end setListener()


    //TODO:findViewById()
    private void findViews() {
        iv = (ImageView) findViewById(R.id.iv);

        etEmail = (EditText) findViewById(R.id.editText_email);
        etPsw = (EditText) findViewById(R.id.editText_psw);

        switchPsw = (Switch) findViewById(R.id.switch_psw);

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnLogout = (Button) findViewById(R.id.btn_logout);
        btnDownload = (Button)findViewById(R.id.btn_download);

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation_i);

        progress = (ProgressBar) findViewById(R.id.progressBar);

    }


    //TODO:建立內部類別MyButton()，使用者點選登入、登出、註冊後的動作
    private class MyButton implements View.OnClickListener {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                //登入
                case R.id.btn_login:
                    //如果使用者沒有輸入帳號與密碼，就跳出吐司
                    if (etEmail.length() == 0 || etPsw.length() == 0) {
                        Toast.makeText(UserInfoActivity.this, "Please fill field.", Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        //如果有輸入帳號與密碼，就將editText上的文字存放到變數email & psw中
                        String email = etEmail.getText().toString();
                        String psw = etPsw.getText().toString();

                        //currentUser,因為firebase一次只能一個人登入，所以如果有人登入firebase，就將他登出(強迫上一位使用者登出)。
                        currentUser = authControl.getCurrentUser();
                        if (currentUser != null) {
                            authControl.signOut();
                        }

                        //使用帳號與密碼登入:signInWith，並監聽authControl
                        else {
                            authControl.signInWithEmailAndPassword(email, psw)
                                    .addOnCompleteListener(UserInfoActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(UserInfoActivity.this, "登入成功", Toast.LENGTH_SHORT).show();
                                                //setDBData();

                                                //登入後切換到 homepage
                                                Intent intent = new Intent(context, MainActivity.class);
                                                startActivity(intent);

                                                //TODO[未完成]:使用者登入後將照片從firebase上抓下來並顯示在imageView

                                            } else {
                                                Toast.makeText(UserInfoActivity.this, "登入失敗", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                        }

                    } //end case R.id.button_login - if
                    break;

                //登出
                case R.id.btn_logout:
                    Toast.makeText(UserInfoActivity.this, "登出", Toast.LENGTH_SHORT).show();
                    //如果現在有使用者登入的話，按下登出鈕就登出
                    if (currentUser != null) {
                        etEmail.setText("");
                        etPsw.setText("");
                        Toast.makeText(UserInfoActivity.this, "登出成功", Toast.LENGTH_SHORT).show();

                        authControl.signOut();

                    } //end button_logout
                    break;


                //註冊
                case R.id.btn_register:

                    Intent intent = new Intent(context, RegisterActivity.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.btn_download:
                    progress.setVisibility(View.VISIBLE);
                    Log.i("下載", "1 開始....");
                    StorageReference leaf = mStorageRef.child("user_pic.jpg");

                    File file = null;
                    try {
                        file = File.createTempFile("user_pic", "jpg");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    final FileDownloadTask task = leaf.getFile(file);
                    task.addOnProgressListener(
                            new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    //Log.i("下載", "已下載:" + taskSnapshot.getBytesTransferred());
                                    final int percent = (int) ((double) taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount() * 100);
                                    Log.i("下載", "已下載:" + percent + " %");
                                    UserInfoActivity.this.runOnUiThread(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    progress.setProgress(percent);
                                                }
                                            }
                                    );

                                }
                            }
                    );
                    final File finalFile = file;
                    task.addOnSuccessListener(
                            new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(finalFile));
                                    iv.setImageBitmap(bitmap);
                                    progress.setVisibility(View.INVISIBLE);
                                }
                            }
                    );

                    break;

            } //end switch
        } //end onClick()
    } //end MyButton()


    @Override
    protected void onDestroy() {
        super.onDestroy();

        //關閉程式時讓使用者登出
        authControl.signOut();
    }



}// end