package com.example.demo1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.demo1.HttpClass.HttpGetData;
import com.example.demo1.MyBean.Novels;
import com.example.demo1.MyBean.NovelsClass;
import com.example.demo1.MyUtil.ImgUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NovlesListActivity extends AppCompatActivity {
    private String TAG = "novelsList";
    private Context context;
    private static final int LOGIN_JUDGE = 1;
    private int class_id;
    private int pageNow,pageSize;
    private ListView listViewNovelsList;
    private ArrayList<Map<String, Object>> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novles_list);
        context = this;

        //TODO:找元件
        findViewID();
        //TODO:用於顯示ListView
        itemList = new ArrayList<Map<String,Object>>();

        Intent intent = getIntent();
        String className = intent.getStringExtra("name");
        class_id = intent.getIntExtra("class_id",0);
        pageNow = 1;
        pageSize = 10;
        Log.d(TAG, "class_id: "+class_id);
        setTitle(className);

        //TODO:呼叫servlet拿到該類別的小說清單
        MyThread();

    }

    private void MyThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: class_id:"+class_id);
                String result = HttpGetData.getNovelsByClass(pageNow,pageSize,class_id);
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
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case LOGIN_JUDGE:
                    Bundle bundle = new Bundle();
                    bundle = msg.getData();
                    String result = bundle.getString("result");
                    Log.d(TAG, "handleMessage111: "+result);
                    StringToHashMap(result);
                    break;
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void StringToHashMap(String result) {
        try {
            JSONArray jsonArray = new JSONArray(result);
            List<Novels> list = new ArrayList<Novels>();
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jobj = jsonArray.optJSONObject(i);
                //裝到NovelsBean
                Novels nc = new Novels();
                nc.setName(jobj.optString("novelName"));
                nc.setAuthor(jobj.optString("novelAuthor"));
                nc.setNovel_id(jobj.optInt("novelId"));
                nc.setIntroduction(jobj.optString("novelIntroduction"));
                nc.setPic(jobj.optString("novelPic"));
                list.add(nc);
            }
            //轉成HashMap
            for(Novels n: list){
                Map<String,Object> data = new HashMap<>();
                data.put("novel_id",n.getNovel_id());
                data.put("name",n.getName());
                data.put("author",n.getAuthor());
                data.put("intro",n.getIntroduction());
                data.put("image",n.getPic());
                Log.d(TAG, "image: "+n.getPic());
                itemList.add(data);
            }

            SimpleAdapter adapter = new SimpleAdapter(context, itemList, R.layout.layout_novels_item,
                    new String[]{"name","author"},
                    new int[]{R.id.textView_novel_name,R.id.textView_novel_author});
            listViewNovelsList.setAdapter(adapter);

            //監聽ListView:點選跳轉該類別資料
            listViewNovelsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Map<String,Object> item = (Map<String,Object>)parent.getItemAtPosition(position);
                    //這邊item.get(的值) 來自data.put(放入的)值
                    String name = (String) item.get("name");
                    String author = (String) item.get("author");
                    String intro = (String) item.get("intro");
                    String image = (String) item.get("image");
                    int novel_id = Integer.parseInt(item.get("novel_id").toString());
                    Intent intent = new Intent(context,ChapterActivity.class);
                    intent.putExtra("novel_id",novel_id);
                    intent.putExtra("name",name);
                    intent.putExtra("author",author);
                    intent.putExtra("intro",intro);
                    intent.putExtra("image",image);
                    intent.putExtra("index",position);
                    startActivity(intent);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void findViewID() {
        listViewNovelsList=(ListView)findViewById(R.id.listView_novels_list);

    }
}
