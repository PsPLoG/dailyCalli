package com.psplog.dailycalli.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.psplog.dailycalli.http.HttpClient;
import com.psplog.dailycalli.R;
import com.psplog.dailycalli.adapter.RecyclerCalliAdapter;
import com.psplog.dailycalli.item.Cali_Item;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.primaryTextColor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#1a237e"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        final RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);

        GridLayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        final EditText search = findViewById(R.id.searchView2);

        class LoadCalliList extends AsyncTask<String,String,Integer>
        {
            List<Cali_Item> tmp;
            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                recyclerView.setAdapter(new RecyclerCalliAdapter(getApplicationContext(), tmp, R.layout.activity_main,2));
            }

            @Override
            protected Integer doInBackground(String... strings) {
                tmp= HttpClient.getSearchjList(search.getText().toString(),1);
                return null;
            }
        }
        search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    new LoadCalliList().execute();
                    return true;
                }
                return false;
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
