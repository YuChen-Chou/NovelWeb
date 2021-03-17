package com.example.demo1.MyUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImgUtils {
    private static String ip="http://192.168.43.77:8080/";
    private static String root = "NovelsWeb/img/";
    //讀取網路圖片，型態為Bitmap
    public static Bitmap getBitmapFromURL(String imageUrl){
        try
        {
            URL url = new URL(ip+root+imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
