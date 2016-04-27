package com.jkm.customcontrol;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jkm.customcontrol.activity.AliPayPieActivity;
import com.jkm.customcontrol.activity.PieActivity;
import com.jkm.customcontrol.pieview.widegt.AliPayPieView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.btn_pie:
                intent = new Intent(this, PieActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_alipay_pie:
                intent = new Intent(this, AliPayPieActivity.class);
                startActivity(intent);
                break;

        }
    }
}
