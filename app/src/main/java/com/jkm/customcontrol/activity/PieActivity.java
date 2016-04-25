package com.jkm.customcontrol.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jkm.customcontrol.R;
import com.jkm.customcontrol.pieview.widegt.PieView;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：CustomControl
 *
 * @author 蒋坤明
 * @time 2016/4/25.
 */
public class PieActivity extends AppCompatActivity{
    private PieView pv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie);
        pv = (PieView) findViewById(R.id.pv);
        List<PieView.Data> list = new ArrayList<>();
        list.add(new PieView.Data("第一块",R.mipmap.icon_normal,R.mipmap.icon_select));
        list.add(new PieView.Data("第二块",R.mipmap.icon_normal,R.mipmap.icon_select));
        list.add(new PieView.Data("第三块",R.mipmap.icon_normal,R.mipmap.icon_select));
        list.add(new PieView.Data("第四块",R.mipmap.icon_normal,R.mipmap.icon_select));
        list.add(new PieView.Data("第五块", R.mipmap.icon_normal,R.mipmap.icon_select));
//        list.add(new PieView.Data("第六块", R.mipmap.icon_normal,R.mipmap.icon_select));
//        list.add(new PieView.Data("第七块", R.mipmap.icon_normal,R.mipmap.icon_select));
//        list.add(new PieView.Data("第八块", R.mipmap.icon_normal,R.mipmap.icon_select));
        pv.setList(list);
        pv.setOnItemSelectListener(new PieView.OnItemSelectListener() {
            @Override
            public void selectPosition(int position, PieView pieView) {
                if (pieView.getList() != null)
                    Toast.makeText(PieActivity.this, pieView.getList().get(position).name, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
