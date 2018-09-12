package com.psplog.dailycalli.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.psplog.dailycalli.http.HttpClient;
import com.psplog.dailycalli.R;
import com.psplog.dailycalli.adapter.RecyclerCalliAdapter;
import com.psplog.dailycalli.item.Cali_Item;

import java.util.ArrayList;
import java.util.List;

public class MoreCalliActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_calli);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.primaryTextColor));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#1a237e"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("더보기");

        Intent intent = getIntent();
        final int order = intent.getIntExtra("order",0);

        final int colCount =2;
        final Context context = getBaseContext();
        final RecyclerView recyclerView = findViewById(R.id.recycle_more);
        recyclerView.setHasFixedSize(true);

        GridLayoutManager mLayoutManager = new GridLayoutManager(context, colCount);
        recyclerView.setLayoutManager(mLayoutManager);

        final List<Cali_Item> items=new ArrayList<Cali_Item>();
        class LoadCalliList extends AsyncTask<String,String,Integer>
        {
            List<Cali_Item> tmp;
            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                for(int i=0;i<tmp.size();i++)
                {
                    items.add(tmp.get(i));
                }
                recyclerView.setAdapter(new RecyclerCalliAdapter(context, items, R.layout.activity_main,colCount));
            }

            @Override
            protected Integer doInBackground(String... strings) {
                tmp= HttpClient.getCalliList(order);
                return null;
            }
        }
        new LoadCalliList().execute();


    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
