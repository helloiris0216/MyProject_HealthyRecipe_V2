package com.example.myProject_HealthyRecipesApp;
//TODO:[目標:完成] 按下導覽列中的 "user" 會跳轉至此頁面，功能是讓使用者登入 & 登出用。
//TODO:[目標:完成] 使用者輸入 email & psw ，按下 "登入按鈕" 後先檢查使用者的欄位是否都有輸入
//TODO:[目標:完成] 接著檢查輸入的資料是否符合firebase上的資料
//TODO:[目標] 都ok後，將 photo 從 firebase 下載至 imageView上顯示，並跳出吐司 : 哈囉，username
//TODO:[目標] 使用者登入後才會有存取資料的功能(日記)

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

//TODO:使用者點選bottom navigation中的user，會跳至此頁面
public class UserInfoActivity extends AppCompatActivity {

    private ImageView iv;
    private EditText etEmail, etPsw;
    private Switch switchPsw;
    private Button btnLogin, btnRegister, btnLogout;
    private String TAG = "main";
    private FirebaseAuth authControl;

    private FirebaseUser currentUser;

    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 1888;
    private Bitmap theImage;
    private StorageReference mStorageRef;
    private Context context;



    //TODO:設定初始值
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        context = this;
        setTitle("My Information");

        //TODO:設定action bar上的返回鍵 1
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);



        findViews();
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

        iv.setOnClickListener(new MyButton());
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
                                                setDBData();

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

                    break;

                case R.id.iv:
                    button_press(v);
                    break;

            } //end switch
        } //end onClick()
    } //end MyButton()


//    //TODO[未完成]:建立自訂方法，存取使用者的資料(照片)並顯示在畫面上
//    private void DisplayUser() {
//        //存取資料
//        FirebaseUser user = authControl.getCurrentUser();
//        String email = user.getEmail();
//
//    } //end DisplayUser()

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //關閉程式時讓使用者登出
        authControl.signOut();
    }


    //TODO:取得使用者的拍照權限
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            theImage = (Bitmap) data.getExtras().get("data");
            iv.setImageBitmap(theImage);
        }
    }   //end onActivityResult()

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
    }   //end onRequestPermissionsResult()


    //TODO[未完成]:setDBData() -> read data form firebase(food_database)
    private void setDBData() {
        Log.d(TAG, "setDBData: start");

        //取得 realtime database 目前的狀態
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("food_databse");

        myRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()){
                    HashMap<String, Object> map = new HashMap<String, Object>();

                    String food_name = (String) ds.child("food_name").getValue();
                    String food_serving_size = (String) ds.child("food_serving_size").getValue();
                    String food_cal = (String) ds.child("food_cal").getValue();
                    String food_pt = (String) ds.child("food_protein").getValue();
                    String food_carbs = (String) ds.child("food_carbs").getValue();
                    String food_fat = (String) ds.child("food_fat").getValue();

                    Log.d(TAG, "food_name:"+food_name);
                    Log.d(TAG, "food_serving_size:"+food_serving_size);
                    Log.d(TAG, "food_cal:"+food_cal);
                    Log.d(TAG, "food_pt:"+food_pt);
                    Log.d(TAG, "food_carbs:"+food_carbs);
                    Log.d(TAG, "food_fat:"+food_fat);

                    String[] food_arr = {food_name, food_serving_size, food_cal, food_pt, food_carbs, food_fat};
                    Intent intent = new Intent(context, FoodDataActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArray("food_arr", food_arr);
                    intent.putExtras(bundle);
                    for (String str : food_arr){
                        Log.d(TAG, "food_arr:"+str);
                    }

                    startActivity(intent);



//                    //使用map放
//                    map.put("food_name", food_name);
//                    map.put("food_serving_size", food_serving_size);
//                    map.put("food_cal", food_cal);
//                    map.put("food_pt", food_pt);
//                    map.put("food_carbs", food_carbs);
//                    map.put("food_fat", food_fat);
//
//                    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//                    list.add(map);
//
//                    Intent intent = new Intent(context, FoodDataActivity.class);
//                    Bundle bundle = new Bundle();
//
//                    ArrayList bundleList = new ArrayList();
//                    bundleList.add(list);
//                    bundle.putParcelableArrayList("list", bundleList);
//                    intent.putExtras(bundle);







//                    //自己檢查用
//                    if (food_name.length()!=0) {
//                        Log.d(TAG, "food_name:" + food_name);
//                        map.put("food_name", food_name);
//                    } else {
//                        Log.d(TAG, "data doesn't exist");
//                    }



//                    Log.d(TAG, "food_arr:"+food_arr[0]);
//                    int i;
//                    for(i=0; i<food_arr.length; i++) {
//                        switch (food_arr[i]) {
//                            case :
//                                break;
//
//                        }
//                    }




                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }   //end setDBData()




    //TODO:使用者按下同意拍照後，進行拍照，[未完成]上傳，將照片顯示在iv上的動作
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void button_press(View v) {
        Toast.makeText(context, "拍照", Toast.LENGTH_SHORT).show();

        //確認使用者是否按下同意
        if (this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            String[] permission = {Manifest.permission.CAMERA};
            this.requestPermissions(permission, MY_CAMERA_PERMISSION_CODE);


        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            this.startActivityForResult(cameraIntent, CAMERA_REQUEST);

            //將使用者的照片上傳到雲端
            Boolean isTook;
            isTook = true;
            if (isTook) {
                Toast.makeText(context, "upload....", Toast.LENGTH_SHORT).show();


                //將ImageView 中的圖片化為  byte 陣列
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                //將照片壓縮到 byteArray
                theImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                //final long total = blob.length;
                byte[] blob = stream.toByteArray(); //將壓縮檔轉成byte[]

                //將照片存在子節點
                StorageReference leaf = mStorageRef.child("user_pic.jpg");
                UploadTask task = leaf.putBytes(blob);
                task.addOnSuccessListener(
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(context, "upload....成功", Toast.LENGTH_LONG).show();
                            }
                        }
                );
                task.addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "upload....失敗", Toast.LENGTH_LONG).show();
                            }
                        }
                );
                isTook = false;
            } //end if(isTook)

        } //end if(checkSelfPermission)

       // busy.setVisibility(View.VISIBLE);




//        task.addOnCompleteListener(
//
//                new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        MainActivity.this.runOnUiThread(
//                                new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        busy.setVisibility(View.INVISIBLE);
//                                    }
//                                }
//                        );
//                    }
//                }
//        );    //end addOnCompleteListener()
//
//        task.addOnProgressListener(
//                new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                        long byte_to_go = taskSnapshot.getBytesTransferred();
//                        //Log.i("上傳", "送出 " + byte_to_go + " BYTEs");
//
//                        final double every_time_percent = (double) byte_to_go / total;
//                        Log.i("上傳", "送出 " + every_time_percent * 100 + " %");
//                        MainActivity.this.runOnUiThread(
//                                new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        progress.setProgress((int) (every_time_percent * 100));
//                                    }
//                                }
//                        );
//
//                    }
//                }
//        );  //end addOnProgressListener()

    }   //end button_press()



}