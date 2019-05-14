package com.example.asus.huinv;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MylistActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    List<String> list1= new ArrayList<String>();
    private String TAG="Mylist";
    ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylist);
        /**GridView gridView=(GridView)findViewById(R.id.mylist);
        for(int i=1;i<100;i++){
            list1.add("item"+i);
        }
        ListAdapter adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list1);
        gridView.setAdapter(adapter);**/
        ListView listView=(ListView)findViewById(R.id.mylist);
        for(int i=1;i<100;i++){
            list1.add("item"+i);
        }
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list1);
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.nodata));
        listView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> listv, View view, int position, long id) {
        Log.i(TAG,"onItemClick:"+position);
        Log.i(TAG,"parent:"+listv);
        adapter.remove(listv.getItemAtPosition(position));//仅限于arryadapter
        //adapter.notifyDataSetChanged();数据刷新

    }
}
