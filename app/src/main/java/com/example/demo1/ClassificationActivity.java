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
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.demo1.HttpClass.HttpGetData;
import com.example.demo1.MyBean.NovelsClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassificationActivity extends AppCompatActivity {

    private ListView listViewClassinfo;
    private String TAG = "class";
    private Context context;
    private static final int LOGIN_JUDGE = 1;
    private ArrayList<Map<String, Object>> itemList;
    private int[] images = {R.drawable.img01, R.drawable.img02, R.drawable.img03,
            R.drawable.img04, R.drawable.img05, R.drawable.img06,
            R.drawable.img07, R.drawable.img08, R.drawable.img09,
            R.drawable.img10, R.drawable.img11, R.drawable.img12,
            R.drawable.img13, R.drawable.img14, R.drawable.img15,
            R.drawable.img16, R.drawable.img17, R.drawable.img18};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classification);
        setTitle("小說類別");
        context=this;
        //TODO:找元件
        findViewID();

        //TODO:用於顯示ListView
        itemList = new ArrayList<Map<String,Object>>();

        //連線Servlet
        MyThread();
    }

    private void MyThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = HttpGetData.getClassData();
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
            List<NovelsClass> list = new ArrayList<NovelsClass>();
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jobj = jsonArray.getJSONObject(i);
                //裝到classBean-方便後續使用
                NovelsClass nc = new NovelsClass();
                nc.setClass_id(jobj.getInt("class_id"));
                nc.setClassName(jobj.getString("class"));
                list.add(nc);
            }
            //轉成HashMap
            int i=0;
            for(NovelsClass n: list){
                Map<String,Object> data = new HashMap<>();
                data.put("name",n.getClassName());
                data.put("class_id",n.getClass_id());
                data.put("image",images[i]);
                i++;
                itemList.add(data);
            }

            //得到的資料放到ListView
            SimpleAdapter adapter = new SimpleAdapter(context, itemList, R.layout.layout_classinfo_item,
                    new String[]{"name","image"},
                    new int[]{R.id.textView_classinfo,R.id.imageView_bag});
            listViewClassinfo.setAdapter(adapter);

            //監聽ListView:點選跳轉該類別資料
            listViewClassinfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Map<String,Object> item = (Map<String,Object>)parent.getItemAtPosition(position);
                    String name = (String) item.get("name");
                    int class_id = Integer.parseInt(item.get("class_id").toString());
                    Intent intent = new Intent(context,NovlesListActivity.class);
                    intent.putExtra("class_id",class_id);
                    intent.putExtra("name",name);
                    intent.putExtra("index",position);
                    startActivity(intent);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void findViewID() {
        listViewClassinfo=(ListView)findViewById(R.id.listView_classinfo);
    }
}
