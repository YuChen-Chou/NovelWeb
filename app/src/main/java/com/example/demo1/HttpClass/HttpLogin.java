package com.example.demo1.HttpClass;

import com.example.demo1.MyBean.User;

import java.io.IOException;
import java.net.URLEncoder;
import com.example.demo1.MyUtil.HttpConn;

public class HttpLogin {

    //TODO:登入
    public static String LoginByPost(String username, String password) {

        String result = "";
        String connPath = "AppLoginCIServlet";
        String data = "username="+username+"&password="+password;
        HttpConn httpConn = new HttpConn();
        result = httpConn.getHttpConn(connPath,data);


        return result;
    }

    //TODO:註冊
    public static String RegisterByPost(User user){
        String result = "";
        String connPath = "RegisterCIServlet";
        //我们请求的数据
        try {
            String data = "name=" + URLEncoder.encode(user.getName(), "UTF-8") +
                    "&username=" + user.getUsername() +
                    "&password=" + user.getPassword() +
                    "&email=" + user.getEmail() +
                    "&phone=" + user.getPhone() +
                    "&gender=" + user.getGender();
            HttpConn httpConn = new HttpConn();
            result = httpConn.getHttpConn(connPath, data);
        }catch (IOException e){
            e.printStackTrace();
        }

        return result;
    }

}
