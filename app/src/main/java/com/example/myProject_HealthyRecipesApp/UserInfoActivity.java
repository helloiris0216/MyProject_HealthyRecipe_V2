package com.example.myProject_HealthyRecipesApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//TODO:使用者點選bottom navigation中的user，會跳至此頁面
public class UserInfoActivity extends AppCompatActivity {

    private ImageView iv;
    private EditText etEmail, etPsw;
    private Switch switchPsw;
    private Button btnLogin, btnRegester, btnLogout;
    private FirebaseAuth authControl;
    private String TAG="main";
    private FirebaseUser user;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        setTitle("My Information");
        //TODO:設定action bar上的返回鍵 1
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);


        findViews();
        setListener();

        //TODO:取得firebase的console:https://fir-app-60599.firebaseio.com/class
        //所有的方法都放在library內，要建立物件來使用這些方法
        authControl = FirebaseAuth.getInstance();
        Log.d(TAG, "authControl:"+ authControl);
    }

    //TODO:設定action bar上的返回鍵 2
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish(); //關掉目前的activity
                break;
        }
        return super.onOptionsItemSelected(item);
    } //end onOptionsItemSelected()

    //TODO:監聽switch & button
    private void setListener() {
        switchPsw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    switchPsw.setText("On");
                    etPsw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                } else {
                    switchPsw.setText("Off");
                    etPsw.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT);
                }
            }
        });

        btnLogin.setOnClickListener(new MyButton());
        btnLogout.setOnClickListener(new MyButton());
        btnRegester.setOnClickListener(new MyButton());
    } //end setListener()

    //TODO:findViewById()
    private void findViews() {
        iv = (ImageView) findViewById(R.id.imageView);

        etEmail = (EditText)findViewById(R.id.editText_email);
        etPsw = (EditText) findViewById(R.id.editText_psw);

        switchPsw = (Switch)findViewById(R.id.switch_psw);

        btnLogin = (Button)findViewById(R.id.btn_login);
        btnRegester = (Button)findViewById(R.id.btn_register);
        btnLogout = (Button)findViewById(R.id.btn_logout);
    }


    //TODO:建立內部類別MyButton()，使用者點選登入、登出、註冊後的動作
    private class MyButton implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_login:
                    Toast.makeText(UserInfoActivity.this, "登入成功", Toast.LENGTH_SHORT).show();
                    //如果使用者沒有輸入帳號與密碼，就跳出吐司
                    if (etEmail.length()==0 || etPsw.length()==0){
                        Toast.makeText(UserInfoActivity.this, "Please fill field.", Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        //如果有輸入帳號與密碼，就將editText上的文字存放到變數email & psw中
                        String email = etEmail.getText().toString();
                        String psw = etPsw.getText().toString();

                        //TODO:currentUser,因為firebase一次只能一個人登入，所以如果有人登入firebase，就將他登出(強迫上一位使用者登出)。
                        currentUser = authControl.getCurrentUser();
                        if (currentUser!=null){
                            authControl.signOut();
                        }

                        //使用帳號與密碼登入:signInWith，並監聽authControl
                        authControl.signInWithEmailAndPassword(email, psw)
                                .addOnCompleteListener(UserInfoActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(UserInfoActivity.this, "登入成功", Toast.LENGTH_SHORT).show();

                                            //登入後取得使用者的資料
                                            user = authControl.getCurrentUser();

                                        } else {
                                            Toast.makeText(UserInfoActivity.this, "登入失敗", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                    } //end case R.id.button_login - if
                    break;

                case R.id.btn_logout:
                    Toast.makeText(UserInfoActivity.this, "登出", Toast.LENGTH_SHORT).show();
                    //如果現在有使用者登入的話，按下登出鈕就登出
                    if (currentUser!=null){
                        etEmail.setText("");
                        etPsw.setText("");
                        Toast.makeText(UserInfoActivity.this, "登出成功", Toast.LENGTH_SHORT).show();

                        authControl.signOut();

                    } //end button_logout
                    break;

                case R.id.btn_register:
                    if (etEmail.length()==0 || etPsw.length()==0){
                        Toast.makeText(UserInfoActivity.this, "請輸入帳號密碼", Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        //如果有輸入帳號與密碼，就將editText上的文字存放到變數email & psw中
                        String email = etEmail.getText().toString();
                        String psw = etPsw.getText().toString();

                        //利用帳號與密碼進行註冊:createUser，並監聽authControl
                        authControl.createUserWithEmailAndPassword(email, psw).addOnCompleteListener(UserInfoActivity.this
                                , new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                //如果註冊成功
                                if (task.isSuccessful()){
                                    Toast.makeText(UserInfoActivity.this, "註冊成功", Toast.LENGTH_SHORT).show();

                                    //取得使用者的帳號與密碼
                                    //user = authControl.getCurrentUser();
                                    //DisplayUser();  //自訂方法

                                //如果註冊失敗
                                } else {
                                    Toast.makeText(UserInfoActivity.this, "註冊失敗", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }); //end Listener()
                        break;
                    } //end else
            } //end switch
        } //end onClick()
    } //end MyButton()


//    //TODO:建立存取使用者的資料並顯示在畫面上的自訂方法
//    private void DisplayUser() {
//        //存取資料
//        String name = user.getDisplayName();
//        String email = user.getEmail();
//        String uid = user.getUid();
//
//
//
//    } //end DisplayUser()

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //關閉程式時讓使用者登出
        authControl.signOut();
    }
}