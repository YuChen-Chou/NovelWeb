package com.example.demo1.HttpClass;


import android.util.Log;

import com.example.demo1.MyUtil.HttpConn;

public class HttpGetData {
    //找servlet獲取分類資料
    public static String getClassData(){
        String connPath="NovelClassificationCIServlet";
        String data="type=getClassData";
        HttpConn httpConn = new HttpConn();
        String result="";
        result = httpConn.getHttpConn(connPath,data);

        return result;
    }

    public static String getNovelsByClass(int pageNow, int pageSize,int class_id){
        String connPath="NovelsListByClassServlet";
        String data="type=getNovelsList&pageNow="+pageNow+"&pageSize="+pageSize+"&class_id="+class_id;
        Log.d("conn", "data: "+data);
        HttpConn httpConn = new HttpConn();
        String result="";
        result = httpConn.getHttpConn(connPath,data);
        Log.d("conn", "result: "+result);
        return result;
    }

    //獲取小說章節
    public static String getChapterByNovel(int novel_id){
        String connPath="NovelsChapterCIServlet";
        String data="novel_id="+novel_id;
        Log.d("conn", "data: "+data);
        HttpConn httpConn = new HttpConn();
        String result="";
        result = httpConn.getHttpConn(connPath,data);
        Log.d("conn", "result: "+result);
        return result;
    }

    //獲取小說內容/上一章節/下一章節
    public static String getContentByNovelChapter(int novel_id, int id,String type){
        String connPath="NovelsContentCIServlet";
        String data="type="+type+"&novel_id="+novel_id+"&id="+id;
        Log.d("conn", "data: "+data);
        HttpConn httpConn = new HttpConn();
        String result="";
        result = httpConn.getHttpConn(connPath,data);
        Log.d("conn", "result: "+result);
        return result;
    }



}
