package com.example.demo1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo1.HttpClass.HttpGetData;
import com.example.demo1.MyBean.Chapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentActivity extends AppCompatActivity {

    private TextView textView_cont;
    private String TAG="content",type;
    private Button button_next,button_pre,button_index;
    private static final int LOGIN_JUDGE = 1;
    private Context context;
    private int chapter_id,novel_id,start_id,end_id;
    private boolean flag_prePage=true,flag_nextPage=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        List<Chapter> list;
        context=this;

        Intent intent = getIntent();
        String chapterName=intent.getStringExtra("name");
        chapter_id=intent.getIntExtra("chapter_id",1);
        novel_id=intent.getIntExtra("novel_id",1);
        start_id=intent.getIntExtra("start_id",1);
        end_id=intent.getIntExtra("end_id",1);
        type=intent.getStringExtra("type");

        //TODO:找元件
        findViewID();
        //TODO:自定義按鍵監聽,點選上/下頁
        buttonEvent();

        //TODO:呼叫servlet拿到該小說的章節內容
        MyThread(novel_id,chapter_id,type);
    }

    private void MyThread(final int novel_id, final int chapter_id, final String type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = HttpGetData.getContentByNovelChapter(novel_id,chapter_id,type);
                Bundle bundle = new Bundle();
                bundle.putString("result",result);
                Message message = new Message();
                message.setData(bundle);
                message.what = LOGIN_JUDGE;
                handler.sendMessage(message);
            }
        }).start();
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
                    StringToHashMap(result);
                    break;
            }
        }
    };
    private void StringToHashMap(String result) {
        try {
            JSONArray jsonArray = new JSONArray(result);
            List<Chapter> list = new ArrayList<Chapter>();
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jobj = jsonArray.optJSONObject(i);
                //裝到ChapterBean-方便後續使用
                Chapter nc = new Chapter();
                nc.setContent(jobj.optString("content"));
                nc.setNovel_id(jobj.optInt("novel_id"));
                nc.setId(jobj.optInt("id"));
                nc.setName(jobj.optString("name"));
                list.add(nc);
            }

            setTitle("章節>"+list.get(0).getName());
            //顯示內容
            textView_cont.setText(list.get(0).getContent());
            chapter_id = list.get(0).getId();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void buttonEvent() {
        button_next.setOnClickListener(new MyButton());
        button_pre.setOnClickListener(new MyButton());
        button_index.setOnClickListener(new MyButton());
    }


    private void findViewID() {
        textView_cont=(TextView)findViewById(R.id.textView_cont);
        button_next=(Button)findViewById(R.id.button_next);
        button_pre=(Button)findViewById(R.id.button_pre);
        button_index=(Button)findViewById(R.id.button_index);
    }

    private class MyButton implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //上一頁
            if(v.getId()==R.id.button_pre){
                //1.先判斷是否在第一頁=>不是的話呼叫上一個章節id
                checkHasPrePage();
            }
            //下一頁
            else if(v.getId()==R.id.button_next){
                //判斷是否在最後一頁=>不是的話呼叫上一個章節id
                checkHasNextPage();
            }
            else if(v.getId()==R.id.button_index){
                //跳轉到章節選單
                finish();
            }
        }
    }

    private void checkHasPrePage() {
        if(start_id==chapter_id){
            Toast.makeText(context,"已經沒有上一章節了!",Toast.LENGTH_SHORT).show();
        }else{
            //呼叫上一章節
            //MyThread(novel_id,chapter_id,"pre");
            Intent intent = new Intent(context,ContentActivity.class);
            intent.putExtra("chapter_id",chapter_id);
            intent.putExtra("novel_id",novel_id);
            intent.putExtra("start_id",start_id);
            intent.putExtra("end_id",end_id);
            intent.putExtra("type","pre");
            startActivity(intent);
            finish();
        }

    }

    private void checkHasNextPage() {
        if(end_id==chapter_id){
            Toast.makeText(context,"已經沒有下一章節了!",Toast.LENGTH_SHORT).show();
        }else{
            //呼叫下一章節
            Intent intent = new Intent(context,ContentActivity.class);
            intent.putExtra("chapter_id",chapter_id);
            intent.putExtra("novel_id",novel_id);
            intent.putExtra("start_id",start_id);
            intent.putExtra("end_id",end_id);
            intent.putExtra("type","next");
            startActivity(intent);
            finish();
        }

    }
}
