package com.example.demo1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo1.HttpClass.HttpGetData;
import com.example.demo1.MyBean.Chapter;
import com.example.demo1.MyUtil.ImgUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChapterActivity extends AppCompatActivity {

    private static final String TAG = "Chapter";
    private ListView chap_list;
    private TextView textView_intro;
    private ImageView imageView_p;
    private ArrayList<Map<String, Object>> itemList;
    private int novel_id;
    private static final int LOGIN_JUDGE = 1;
    private Context context;
    private String imagePath;
    //private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);
        context=this;

        Intent intent = getIntent();
        novel_id = intent.getIntExtra("novel_id",1);
        String name = intent.getStringExtra("name");
        String author = intent.getStringExtra("author");
        String intro = intent.getStringExtra("intro");
        imagePath = intent.getStringExtra("image");

        setTitle("書名>"+name);
        //TODO:找元件
        findViewID();
        //TODO:用於顯示ListView
        itemList = new ArrayList<Map<String,Object>>();
        textView_intro.setText("簡介:\n"+intro);
        //TODO:呼叫servlet拿到該小說的章節內容
        MyThread();
    }

    private void MyThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = HttpGetData.getChapterByNovel(novel_id);
                Bitmap bitmap = ImgUtils.getBitmapFromURL(imagePath);
                Bundle bundle = new Bundle();
                bundle.putString("result",result);
                bundle.putParcelable("bitmap",bitmap);
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
                    Bitmap bitmap = bundle.getParcelable("bitmap");
                    StringToHashMap(result);
                    //設置預設圖片
                    if(bitmap==null){
                        imageView_p.setImageResource(R.drawable.imgdefault);
                    }else{
                        imageView_p.setImageBitmap(bitmap);
                    }
                    break;
            }
        }
    };

    private void StringToHashMap(String result) {
        try {
            JSONArray jsonArray = new JSONArray(result);
            final List<Chapter> list = new ArrayList<Chapter>();
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jobj = jsonArray.optJSONObject(i);
                //裝到ChapterBean-方便後續使用
                Chapter nc = new Chapter();
                nc.setName(jobj.optString("name"));
                nc.setNovel_id(jobj.optInt("novel_id"));
                nc.setId(jobj.optInt("id"));

                //start_id
                nc.setStart_id(jsonArray.optJSONObject(0).optInt("id"));
                //end_id
                nc.setEnd_id(jsonArray.optJSONObject(jsonArray.length()-1).optInt("id"));
                list.add(nc);
            }
            //轉成HashMap
            for(Chapter n: list){
                Map<String,Object> data = new HashMap<>();
                data.put("novel_id",n.getNovel_id());
                data.put("name",n.getName());
                data.put("id",n.getId());
                data.put("start_id",n.getStart_id());
                data.put("end_id",n.getEnd_id());
                itemList.add(data);
            }

            //得到的資料放到ListView
            SimpleAdapter adapter = new SimpleAdapter(context, itemList, R.layout.layout_chapter_list,
                    new String[]{"name"},
                    new int[]{R.id.textView_chapter});
            chap_list.setAdapter(adapter);

            //監聽ListView:點選跳轉該類別資料
            chap_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Map<String,Object> item = (Map<String,Object>)parent.getItemAtPosition(position);
                    //這邊item.get(的值) 來自data.put(放入的)值
                    String name = (String) item.get("name");
                    int novel_id = Integer.parseInt(item.get("novel_id").toString());
                    int chapter_id = Integer.parseInt(item.get("id").toString());
                    int start_id = Integer.parseInt(item.get("start_id").toString());
                    int end_id = Integer.parseInt(item.get("end_id").toString());
                    Intent intent = new Intent(context,ContentActivity.class);
                    intent.putExtra("name",name);
                    intent.putExtra("novel_id",novel_id);
                    intent.putExtra("chapter_id",chapter_id);
                    intent.putExtra("start_id",start_id);
                    intent.putExtra("end_id",end_id);
                    intent.putExtra("type","now");
                    startActivity(intent);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void findViewID() {
        chap_list=(ListView)findViewById(R.id.chap_list);
        textView_intro=(TextView)findViewById(R.id.textView_intro);
        imageView_p=(ImageView)findViewById(R.id.imageView_p);
    }
}
