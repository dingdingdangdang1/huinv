package com.example.asus.huinv;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RateList2Activity extends ListActivity implements  Runnable,AdapterView.OnItemLongClickListener {
    Handler handler;
    private static final String TAG="rl";
    private List<HashMap<String,String>> listItems;//数据
    private SimpleAdapter listItemAdapter;//适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.list_item);
        initListView();
        Thread t= new Thread(this);
        t.start();
        handler =new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==7){
                    listItems= (List<HashMap<String,String>>)msg.obj;
                    listItemAdapter = new SimpleAdapter(RateList2Activity.this,listItems,
                            R.layout.list_item,
                            new String[] {"ItemTitle","ItemDetail"},
                            new int[] {R.id.itemtitle,R.id.itemdate}
                    );
                    setListAdapter(listItemAdapter);
                }
                super.handleMessage(msg);
            }
        };
        getListView().setOnItemLongClickListener(this);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                HashMap<String,String> map= (HashMap<String,String>)getListView().getItemAtPosition(position);
                String titlestr=map.get("ItemTitle");
                String detailstr=map.get("ItemDetail");
                TextView title=(TextView)view.findViewById(R.id.itemtitle);
                TextView detail=(TextView)view.findViewById(R.id.itemdate);
                String title2=String.valueOf(title.getText());
                String detail2=String.valueOf(detail.getText());

                //打开新的页面传入参数
                Intent rateCalc;
                rateCalc = new Intent(RateList2Activity.this,RateCalculatectivity.class);
                rateCalc.putExtra("title",titlestr);
                rateCalc.putExtra("rate",Float.parseFloat(detailstr));
                startActivity(rateCalc);



            }
        });
    }
    private  void initListView(){
        listItems=new ArrayList<HashMap<String,String>>();
        for (int i=1;i<1;i++){
            HashMap<String,String> map=new HashMap<String,String>();
            map.put("ItemTitle","Rate:"+i);
            map.put("ItemDetail","detail:"+i);
            listItems.add(map);

            listItemAdapter = new SimpleAdapter(this,listItems,
                    R.layout.list_item,
                    new String[] {"ItemTitle","ItemDetail"},
                    new int[] {R.id.itemtitle,R.id.itemdate}
            );

        }
    }

    @Override
    public void run() {
        //获取网络数据，放入list带回主线程中
        ArrayList<HashMap<String,String>> relist=new ArrayList<HashMap<String,String>>();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//用于保存获取的汇率
        Bundle bundle=new Bundle();
        //获取网络数据
        URL url=null;
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            //doc=Jsoup.parse(html);
            Log.i(TAG, "run: "+doc.title());
            Elements tables=doc.getElementsByTag("table");

           /*int i=1;
           for(Element table:tables){
                Log.i(TAG, "run: table["+i+"]="+table);
                i++;
            }*/
            Element table5=tables.get(5);
            //Log.i(TAG, "run: table5="+table5);
            //获取id中的数据

            Elements tds=table5.getElementsByTag("td");
            for(int i=0;i<tds.size();i+=8){
                Element td1=tds.get(i);
                Element td2=tds.get(i+5);
                Log.i(TAG, "run: "+ td1.text()+"==>"+td2.text());
                HashMap<String,String> map=new HashMap<String,String>();
                map.put("ItemTitle",td1.text());
                map.put("ItemDetail",td2.text());
                relist.add(map);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Message msg=handler.obtainMessage();
        msg.what=7;//也可以写在 Message msg=handler.obtainMessage(5);这样表示
        msg.obj=relist;
        handler.sendMessage(msg);

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        Log.i(TAG,"OnItemLongClickListener:长按列表项position"+position);
        //删除操作

        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage("请确认是否删除当前数据").setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG,"onclick:对话框事件处理");
                listItems.remove(position);
                listItemAdapter.notifyDataSetChanged();

            }
        })
                .setNegativeButton("否",null);
        builder.create().show();
        return true;//如果是true就不会在执行短按时间。是判断短按事件是否生效
    }
}
