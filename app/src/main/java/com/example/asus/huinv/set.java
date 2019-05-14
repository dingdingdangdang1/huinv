package com.example.asus.huinv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class set extends AppCompatActivity {
    EditText dollartext;
    EditText eurotext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        Intent intent=getIntent();
        float dollar=intent.getFloatExtra("dollar_rate",0.0f);
        float euro=intent.getFloatExtra("euro_rate",0.0f);
        dollartext=(EditText)findViewById(R.id.newd);
        eurotext=(EditText)findViewById(R.id.newe);//传输到的文本框
        dollartext.setText(String.valueOf(dollar));
        eurotext.setText(String.valueOf(euro));
    }
    public void save(View v) {
//获取新的值
        float newdollar=Float.parseFloat(dollartext.getText().toString());
        float neweuro=Float.parseFloat(eurotext.getText().toString());
        Intent intent=getIntent();
        Bundle bdl=new Bundle();
        bdl.putFloat("key_dollar",newdollar);
        bdl.putFloat("key_euro",neweuro);
        intent.putExtras(bdl);
        setResult(2,intent);
        finish();
    }

}
