package com.example.demo1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.demo1.HttpClass.HttpLogin;

public class LoginActivity extends AppCompatActivity {
    private static final int LOGIN_JUDGE = 1;
    private EditText username, password;
    private Button buttonLogin,buttonCancel;
    private Context context;
    private final int RequestCode=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context=this;
        setTitle("登入");
        //TODO:找元件
        findViewID();

        //TODO:自定義按鍵監聽,點選取消/登入
        buttonEvent();

    }

    private void findViewID() {
        //imageViewLoginLogo =(ImageView)findViewById(R.id.imageView_login_logo);
        username = (EditText)findViewById(R.id.editText_username);
        password = (EditText)findViewById(R.id.editText_password);
        buttonCancel = (Button)findViewById(R.id.button_cancel);
        buttonLogin = (Button)findViewById(R.id.button_login);
    }

    private void buttonEvent() {
        buttonCancel.setOnClickListener(new MyButton());
        buttonLogin.setOnClickListener(new MyButton());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setup, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.login_item:
                //TODO:跳轉註冊頁面RegisterActivity
                Intent intent = new Intent(context,RegisterActivity.class);
                startActivityForResult(intent,RequestCode);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class MyButton implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.button_cancel){
                username.setText("");
                password.setText("");
            }
            else if(v.getId()==R.id.button_login){
                //TODO:到Servlet驗證帳號密碼 =>返回成功則跳轉到分類選單ClassificationActivity
                //1.先檢查是否有填寫資料
                if(checkDataNull()){//檢查OK
                    //2.連線Servlet
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("login","username: "+username.getText().toString());
                            Log.d("login","password: "+password.getText().toString());
                            String result = HttpLogin.LoginByPost(username.getText().toString(),password.getText().toString());
                            Bundle bundle = new Bundle();
                            bundle.putString("result",result);
                            Message message = new Message();
                            message.setData(bundle);
                            message.what = LOGIN_JUDGE;
                            handler.sendMessage(message);
                        }
                    }).start();
                }else{
                    Toast.makeText(context,"帳號密碼不可為空",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case LOGIN_JUDGE:
                    Bundle bundle = new Bundle();
                    bundle = msg.getData();
                    String result = bundle.getString("result");
                    Log.d("login","result:"+result);
                    if("success".equals(result)){
                        Toast.makeText(context,"登入成功",Toast.LENGTH_SHORT).show();
                        //TODO:登入返回成功=>跳轉分類選單ClassificationActivity
                        Intent intent = new Intent(context,ClassificationActivity.class);
                        startActivity(intent);
                    }else if("fail".equals(result)){
                        Toast.makeText(context,"帳號密碼錯誤!",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };


    //TODO:得到結果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==2){
            username.setText(data.getStringExtra("username"));
            password.setText(data.getStringExtra("password"));
        }
    }

    private boolean checkDataNull() {
        boolean b=true;
        if(username.length()==0 || password.length()==0){
            b=false;
        }
        //TODO:可以增加正則驗證(未實作)
        //TODO:應該加上不能為空字串(未實作)

        return b;
    }
}