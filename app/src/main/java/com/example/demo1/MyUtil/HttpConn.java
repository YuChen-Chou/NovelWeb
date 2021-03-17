package com.example.demo1.MyUtil;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConn {
    private String ip="http://192.168.43.77:8080/";
    private String root = "NovelsWeb/";
    //str:代表要連接的路徑
    public String getHttpConn(String connPath,String data){
        String connAddress = ip+root+connPath;
        Log.d("conn", "getHttpConn: "+connAddress);
        String result="";
        try{
            URL url = new URL(connAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            //設置超時
            conn.setReadTimeout(6000);
            conn.setConnectTimeout(6000);
            conn.setUseCaches(false);//設置不緩存

            //獲取輸出流
            OutputStream out = conn.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            out.close();
            conn.connect();

            if (conn.getResponseCode() == 200) {
                //Log.d("http","返回OK");
                //得到響應的輸入流物件
                InputStream is = conn.getInputStream();
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                int len = 0;
                byte[] buffer = new byte[1024];
                //循環讀取
                while ((len = is.read(buffer)) >= 0) {
                    message.write(buffer, 0, len);
                }
                //釋放資源
                is.close();
                message.close();
                result = new String(message.toByteArray());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
