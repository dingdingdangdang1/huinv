package com.example.asus.huinv;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RateListActivity extends ListActivity implements  Runnable{
String data[]={"one","two","three"};
Handler handler;
    private static final String TAG = "rate";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_rate_list);
        List<String> list1= new ArrayList<String>();
        for(int i=1;i<100;i++){
            list1.add("item"+i);
        }
        /**Thread t= new Thread(this);
        t.start();
        handler =new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==7){
                    List<String> list2= (List<String>)msg.obj;
                    ListAdapter adapter=new ArrayAdapter<String>(RateListActivity.this,android.R.layout.simple_list_item_1,list2);
                    setListAdapter(adapter);
                }
                super.handleMessage(msg);
            }
        };**/
        ListAdapter adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list1);
        setListAdapter(adapter);
    }

    @Override
    public void run() {
        //获取网络数据，放入list带回主线程中
        List<String> relist= new ArrayList<String>();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//用于保存获取的汇率
        Bundle bundle=new Bundle();
        //获取网络数据
        URL url=null;

           /* try {
                url = new URL("http://www.usd-cny.com/bankofchina.htm");
                HttpURLConnection http=(HttpURLConnection)url.openConnection();
                InputStream in=http.getInputStream();

                String html=inputstream2String(in);
                Document doc=Jsoup.parse(html);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }*/
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
                relist.add(td1.text()+"==>"+td2.text());

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Message msg=handler.obtainMessage();
        msg.what=7;//也可以写在 Message msg=handler.obtainMessage(5);这样表示
        msg.obj=relist;
        handler.sendMessage(msg);
    }
}
