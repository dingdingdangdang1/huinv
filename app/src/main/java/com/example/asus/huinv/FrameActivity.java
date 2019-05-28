package com.example.asus.huinv;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class FrameActivity extends FragmentActivity {
    private Fragment mFragments[];
    private RadioGroup radiogroup;
    private FragmentManager fragmentManager;//任务切换的管理器
    private FragmentTransaction fragmentTransaction;//事务管理，什么时候开始结束
    private RadioButton rtbHome,rtbFun,rtbSet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_fragment);
        mFragments=new Fragment[3];
        fragmentManager=getSupportFragmentManager();
        mFragments[0]=fragmentManager.findFragmentById(R.id.fragment_main);
        mFragments[1]=fragmentManager.findFragmentById(R.id.fragment_func);
        mFragments[2]=fragmentManager.findFragmentById(R.id.fragment_set);
        fragmentTransaction=
fragmentManager.beginTransaction().hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]);
        fragmentTransaction.show(mFragments[0]).commit();
        radiogroup=(RadioGroup)findViewById(R.id.bottomGroup);
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.i("radioGroup","checkId="+checkedId);
                fragmentTransaction=
fragmentManager.beginTransaction().hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]);
                switch (checkedId){
                    case R.id.radioHome:
                        fragmentTransaction.show(mFragments[0]).commit();
                        break;
                    case R.id.radioFunc:
                        fragmentTransaction.show(mFragments[1]).commit();
                        break;
                    case R.id.radioSet:
                        fragmentTransaction.show(mFragments[2]).commit();
                        break;
                default:
                    break;
                }

            }
        });
    }
}
