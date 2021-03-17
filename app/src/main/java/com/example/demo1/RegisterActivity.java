package com.example.demo1;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.demo1.MyBean.User;
import com.example.demo1.HttpClass.HttpLogin;


public class RegisterActivity extends AppCompatActivity {

    private int ResultCode = 2;
    private final static int REGISTER_JUDGE = 2;
    private EditText name,username,password,email,phone;
    private Switch switchGender;
    private Button buttonRegister,buttonCancel;
    private String gender;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("註冊");
        context = this;

        //TODO:找元件
        findViewID();

        //TODO:自定義性別參數監聽方法
        switchSexListener();

        //TODO:自定義按鍵監聽
        buttonEvent();
    }

    private void buttonEvent() {
        buttonCancel.setOnClickListener(new MyButton());
        buttonRegister.setOnClickListener(new MyButton());
    }

    private void findViewID() {
        name=(EditText)findViewById(R.id.editText_name);
        username=(EditText)findViewById(R.id.editText_r_username);
        password=(EditText)findViewById(R.id.editText_r_password);
        email=(EditText)findViewById(R.id.editText_email);
        phone=(EditText)findViewById(R.id.editText_phone);

        switchGender = (Switch)findViewById(R.id.switch_gender);
        buttonRegister = (Button)findViewById(R.id.button_register);
        buttonCancel = (Button)findViewById(R.id.button_r_cancel);
    }

    private void switchSexListener() {
        switchGender.setChecked(false);
        gender ="M";
        switchGender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    gender ="F";
                    switchGender.setText("F");
                }else {
                    gender ="M";
                    switchGender.setText("M");
                }
                Log.d("main","sexData="+ gender);
            }
        });
    }

    private class MyButton implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.button_cancel){
                name.setText("");
                username.setText("");
                password.setText("");
                email.setText("");
                phone.setText("");
                switchGender.setChecked(false);
                gender ="M";
            }else if(v.getId()==R.id.button_register){
                //TODO:實現註冊.註冊成功返回登入頁面
                //1.檢查資料不能為空
                final User user = new User();
                if(checkDataNull()){
                    //TODO:檢查資料正確(未實作)
                    //2.資料存到UserBean
                    user.setName(name.getText().toString());
                    user.setUsername(username.getText().toString());
                    user.setPassword(password.getText().toString());
                    user.setEmail(email.getText().toString());
                    user.setPhone(phone.getText().toString());
                    user.setGender(gender);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String result = HttpLogin.RegisterByPost(user);
                            Bundle bundle = new Bundle();
                            bundle.putString("result",result);
                            Message msg = new Message();
                            msg.what = REGISTER_JUDGE;
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        }
    }

    private boolean checkDataNull() {
        boolean b=true;
        //TODO:不能為空字串
        if(name.length()==0 || username.length()==0 || password.length()==0 ||email.length()==0 || phone.length()==0) {
            b = false;
            Toast.makeText(context, "欄位不可為空!", Toast.LENGTH_SHORT).show();
        }
        return b;
    }

    //TODO:註冊成功,返回帳號密碼
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case REGISTER_JUDGE:{
                    Bundle bundle = new Bundle();
                    bundle = msg.getData();
                    String result = bundle.getString("result");

                    try {
                        if (result.equals("success")) {
                            Toast.makeText(context,"註冊成功,請登入!",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("username",username.getText().toString());
                            intent.putExtra("password",password.getText().toString());
                            setResult(ResultCode,intent);//向上一级发送数据
                            finish();
                        }else if("fail".equals(result)){
                            Toast.makeText(context,"帳號或信箱已經被使用",Toast.LENGTH_SHORT).show();
                        }
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    };

}