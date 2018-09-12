package com.psplog.dailycalli.activity;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.psplog.dailycalli.http.HttpClient;
import com.psplog.dailycalli.R;

import java.io.File;

public class UploadCalliActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_calli);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#1a237e"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("게시글 작성");


        final ImageView image = findViewById(R.id.img_upload);
        final Button bt_cancle = findViewById(R.id.bt_upload_cancle);
        final Button bt_send = findViewById(R.id.bt_upload_send);
        final EditText title = findViewById(R.id.edit_upload_title);
        final EditText content = findViewById(R.id.edit_upload_content);


        final int calli_trace = 0;//getIntent().getIntExtra("calli_trace",-1);
        final int guide_id = 0;//getIntent().getIntExtra("guide_id",-1);
        final String up_image = getIntent().getStringExtra("image");
        final String nickname = HttpClient.user_nickname;
        final String drawData = getIntent().getStringExtra("drawData");
        final File fImage = new File(up_image);
        final File fdrawData = new File(drawData);

        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        image.setImageBitmap(BitmapFactory.decodeFile(up_image));
//    {
//        "calli_txt": "여름 맞이 캘리입니다.",
//            "calli_trace": 1,
//            "guide_id": 3,
//            "calli_title": "산들산들바람"
//        "calli_tag": ["산뜻함","따뜻함"],
//        "image": 이미지,
//            "drawData" : 파일1, 파일2, ...
//    }
        class LoadProfile extends AsyncTask<String, String, Integer> {
            boolean result;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                bt_send.setEnabled(false);
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if (result) {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                } else {
                    bt_send.setEnabled(true);
                    Toast.makeText(getApplication(), "업로드 실패", Toast.LENGTH_SHORT);
                }

            }

            @Override
            protected Integer doInBackground(String... strings) {

                result = HttpClient.uploadCalli(content.getText().toString(), calli_trace, guide_id, content.getText().toString(), "", fImage, fdrawData, nickname);
                return null;
            }
        }

        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().length() != 0 && content.getText().length() != 0)
                    new LoadProfile().execute();
                else
                    Toast.makeText(getApplication(),"내용을 입력해주세요.",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
