package com.jkm.customcontrol.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jkm.customcontrol.R;
import com.jkm.customcontrol.pieview.widegt.AliPayPieView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 功能：CustomControl
 *
 * @author 蒋坤明
 * @time 2016/4/27.
 */
public class AliPayPieActivity extends AppCompatActivity {
    private AliPayPieView aliPayPieView;
    private Random r = new Random();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alipay_pie);
        aliPayPieView = (AliPayPieView) findViewById(R.id.pv);
        List<AliPayPieView.PieChartInfo> data = new ArrayList<>();
        for(int i = 0 ;i<10;i++){
            AliPayPieView.PieChartInfo info = new AliPayPieView.PieChartInfo();
            info.startAngle = i*36;
            info.angle = 36;
            info.color = Color.rgb(r.nextInt(255),r.nextInt(255),r.nextInt(255));
            data.add(info);
        }
        aliPayPieView.setData(data);
    }
}
