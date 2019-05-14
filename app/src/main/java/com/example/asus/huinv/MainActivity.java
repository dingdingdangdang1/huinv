package com.example.asus.huinv;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,Runnable {
    private static final String TAG = "rate";
    EditText rmb;
TextView show;
Handler handler;
float dollarRate=0.0f;
float euroRate=0.0f;
private String updateDate="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rmb=(EditText)findViewById(R.id.in);
        show=(TextView)findViewById(R.id.show);

        //获取sp中保存的数据
        SharedPreferences sp=  PreferenceManager.getDefaultSharedPreferences(this);//获取sp的另一种方法，这种就不能自己设立配置文件了，一般一个应用只有一个配置文件，不建议多个。
        SharedPreferences sharedPreferences =getSharedPreferences("myrate", Activity.MODE_PRIVATE);//字符串（文件名）以及访问权限
        //有三种访问权限，私有：只能自己读写，其他应用可读不可写，其他应用可读写
        //开始获得数据
        dollarRate= sharedPreferences.getFloat("dollar_rate",0.0f);
        euroRate=sharedPreferences.getFloat("euro_rate",0.0f);
       //写入数据进入这个文件,其实先需要获得sp这个对象，然后再建立一个editor对象
        updateDate=sharedPreferences.getString("update_date","");
//获取当前系统时间
        Date today=Calendar.getInstance().getTime();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        final String todaystr=sdf.format(today);

        Log.i(TAG,"onCreate: sp updateDate="+updateDate);

        if(!todaystr.equals(updateDate)){
            //开启子线程
            Log.i(TAG,"onCreate: 需要更新");
            Thread t= new Thread(this);
            t.start();//让子线程开始运行它就会去运用run方法
        }else{
            Log.i(TAG,"onCreate: 不需要更新");
        }






        handler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==5){//5相当于一个标识符用于验证信息来自这个线程
                   /* String str=(String) msg.obj;//必须是字符串的数据才可以用String强转，如果是日期型的会运行出错
                    show.setText(str);*/
                   Bundle bld=(Bundle) msg.obj;
                    dollarRate=bld.getFloat("dollar-Rate");
                    euroRate=bld.getFloat("euro-Rate");
                    Log.i(TAG, "handleMessage: dollarRate="+dollarRate);
                    Log.i(TAG, "handleMessage: euroRate="+euroRate);

                    //保存更新的日期
                    SharedPreferences sharedPreferences=getSharedPreferences("myrate",Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("update_date",todaystr);
                    editor.putFloat("dollar_rate",dollarRate);
                    editor.putFloat("euro_rate",euroRate);
                    editor.commit();
                    Toast.makeText(MainActivity.this,"汇率已更新",Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };

    }
    public void openOne(View btn){
//打开一个页面activity
        Intent hello = new Intent(MainActivity.this,set.class);
        Intent web = new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.jd.com"));
        Intent dialIntent =  new Intent(Intent.ACTION_CALL,Uri.parse("tel:17381584352" ));//直接拨打电话
        Intent dial =  new Intent(Intent.ACTION_CALL_BUTTON);//跳转到拨号界面
        Intent dia =  new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" ));//跳转到拨号界面，同时传递电话号码
        startActivity(hello);
    }

    @Override
    public void onClick(View v) {
        Intent config = new Intent(MainActivity.this,set.class);
        config.putExtra("dollar_rate",dollarRate);
        config.putExtra("euro_rate",euroRate);
        startActivityForResult(config,1);
    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.menuset){
            Intent config = new Intent(MainActivity.this,set.class);
            config.putExtra("dollar_rate",dollarRate);
            config.putExtra("euro_rate",euroRate);
            startActivityForResult(config,1);
        }
        else if(item.getItemId()==R.id.open_list){
            //打开列表窗口
            Intent list = new Intent(this,RateListActivity.class);
            startActivity(list);
        }
        return true;
    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(resultCode==2 && requestCode==1){
            Bundle bundle=data.getExtras();
            dollarRate=bundle.getFloat("key_dollar",0.1f);
            euroRate=bundle.getFloat("key_euro",0.1f);
            //将新设置的汇率放在sp中
            SharedPreferences sharedPreferences =getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor =sharedPreferences.edit();
            editor.putFloat("dollar_rate",dollarRate);
            editor.putFloat("euro_rate",euroRate);
            editor.commit();//保存功能，也可以使用apply();

        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void run() {

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
               String str1=td1.text();
                String val=td2.text();
                if("美元".equals(str1)){
                bundle.putFloat("dollar-Rate",100f/Float.parseFloat(val));
                }
                else if("欧元".equals(str1)){
                    bundle.putFloat("euro-Rate",100f/Float.parseFloat(val));
                }
            }
           /* for(Element td:tds){
                Log.i(TAG, "run: td="+td);
                Log.i(TAG, "run: html="+ tds.html());
                Log.i(TAG, "run: text="+ tds.text());
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
//bundle中的汇率保存
        //获取msg对象，用于返回主线程
        Message msg=handler.obtainMessage();
        msg.what=5;//也可以写在 Message msg=handler.obtainMessage(5);这样表示
        msg.obj=bundle;
        handler.sendMessage(msg);
    }

    private String inputstream2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = null;
        try {
            in = new InputStreamReader(inputStream, "gb2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (; ; ) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }


}
