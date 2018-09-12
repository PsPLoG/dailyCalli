package com.psplog.dailycalli.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.psplog.dailycalli.http.HttpClient;
import com.psplog.dailycalli.dialog.PreViewDialog;
import com.psplog.dailycalli.R;
import com.psplog.dailycalli.adapter.RecyclerCalliAdapter;
import com.psplog.dailycalli.item.Cali_Item;
import com.psplog.dailycalli.item.CalliLearn_Item;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class LearnContentActivity extends AppCompatActivity {
    AppCompatDialog progressDialog;
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.primaryTextColor));

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("상세보기");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#1a237e"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        final int postId = getIntent().getIntExtra("guide_id", -1);
        final Activity activity = this;

        class DownloadFilesTask extends AsyncTask<String, String, Long> {
            private int type;
            private Context context;
            private ProgressDialog progressBar;
            private File outputFile; //파일명까지 포함한 경로
            private File path;//디렉토리경로

            public DownloadFilesTask(Context context,int type) {
                this.context = context;
                this.type = type;
            }

            //파일 다운로드를 시작하기 전에 프로그레스바를 화면에 보여줍니다.
            @Override
            protected void onPreExecute() { //2
                super.onPreExecute();
                progressBar = new ProgressDialog(LearnContentActivity.this);
                progressBar.setMessage("다운로드중");
                progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressBar.setIndeterminate(true);
                progressBar.setCancelable(true);
                progressBar.show();
            }

            @Override
            protected Long doInBackground(String... string_url) { //3
                int count;
                long FileSize = -1;
                InputStream input = null;
                OutputStream output = null;
                URLConnection connection = null;

                try {
                    URL url = new URL(string_url[0]);
                    connection = url.openConnection();
                    connection.connect();
                    FileSize = connection.getContentLength();
                    input = new BufferedInputStream(url.openStream(), 8192);
                    path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    String[] tmp = string_url[0].split("/");

                    outputFile = new File(path, tmp[4]); //파일명까지 포함함 경로의 File 객체 생성
                    if(outputFile.isFile())
                    {
                        return FileSize;
                    }
                    // SD카드에 저장하기 위한 Output stream
                    output = new FileOutputStream(outputFile);

                    byte data[] = new byte[1024];
                    long downloadedSize = 0;
                    while ((count = input.read(data)) != -1) {
                        //사용자가 BACK 버튼 누르면 취소가능
                        if (isCancelled()) {
                            input.close();
                            return Long.valueOf(-1);
                        }
                        downloadedSize += count;

                        if (FileSize > 0) {
                            float per = ((float) downloadedSize / FileSize) * 100;
                            String str = "다운로드";
                            publishProgress("" + (int) ((downloadedSize * 100) / FileSize), str);

                        }
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
                } catch (Exception e) {
                    Log.e("Error: ", e.getMessage());
                } finally {
                    try {
                        if (output != null)
                            output.close();
                        if (input != null)
                            input.close();
                    } catch (IOException ignored) {
                    }
                }
                return FileSize;
            }

            //다운로드 중 프로그레스바 업데이트
            @Override
            protected void onProgressUpdate(String... progress) { //4
                super.onProgressUpdate(progress);
                progressBar.setIndeterminate(false);
                progressBar.setMax(100);
                progressBar.setProgress(Integer.parseInt(progress[0]));
                progressBar.setMessage(progress[1]);
            }

            //파일 다운로드 완료 후
            @Override
            protected void onPostExecute(Long size) { //5
                super.onPostExecute(size);
                progressBar.dismiss();
                if (size > 0) {
                    //Toast.makeText(getApplicationContext(), "다운로드 완료되었습니다.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LearnContentActivity.this, DrawCalliActivity.class);
                    Log.d("qwe", outputFile + "");
                    if(type ==0) {
                        intent.putExtra("file", outputFile.toString());
                        startActivity(intent);
                    }else {
                        PreViewDialog d = new PreViewDialog(LearnContentActivity.this,activity, outputFile.toString());
                        WindowManager.LayoutParams params = d.getWindow().getAttributes();
                        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                        params.height = 200;
                        d.getWindow().setAttributes( params);
                        d.show();
                    }
                } else
                    Toast.makeText(getApplicationContext(), "다운로드 에러", Toast.LENGTH_LONG).show();
            }

        }


        class LoadContent extends AsyncTask<String, String, Integer> {
            CalliLearn_Item tmp;

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                TextView title = findViewById(R.id.tv_learncontent_title);
                TextView tag = findViewById(R.id.tv_learn_content_tag);
                ImageView user_Img = findViewById(R.id.learn_img);
                RecyclerView recycle = findViewById(R.id.recyclerview);
                Button bt_learn = findViewById(R.id.button2);
                Button previwe = findViewById(R.id.bt_preview);
                previwe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final DownloadFilesTask downloadTask = new DownloadFilesTask(LearnContentActivity.this,1);
                        downloadTask.execute(tmp.getGuide_file());
                    }
                });
                bt_learn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final DownloadFilesTask downloadTask = new DownloadFilesTask(LearnContentActivity.this,0);
                        downloadTask.execute(tmp.getGuide_file());
                    }
                });
                recycle.setHasFixedSize(true);
                GridLayoutManager mLayoutManager = new GridLayoutManager(getApplication(), 3);

                recycle.setLayoutManager(mLayoutManager);

                final List<Cali_Item> items = new ArrayList<Cali_Item>();
                for (int i = 0; i < tmp.getSubItem().size(); i++) {
                    items.add(new Cali_Item(0, tmp.getSubItem().get(i).getCalli_id(), "", tmp.getSubItem().get(i).getCalli_img(), "",""));
                }
                recycle.setAdapter(new RecyclerCalliAdapter(getApplication(), items, R.layout.activity_learn_content, 3));


                if (tmp != null) {
                    title.setText(tmp.getGuide_title());
                    String t = "";
                    for (int i = 0; i < tmp.getGuide_tag().size(); i++)
                        t += "#" + tmp.guide_tag.get(i) + " ";
                    tag.setText(t);
                    Glide.with(getApplication()).load(tmp.getGuide_img()).into(user_Img);
                    //progressOFF();
                } else
                    finish();

            }

            @Override
            protected Integer doInBackground(String... strings) {
                tmp = HttpClient.getLearnContent(postId);
                //progressON(LearnContentActivity.this,"로딩중");
                return null;
            }
        }
        new LoadContent().execute();
    }

    public void progressON(Activity activity, String message) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressSET(message);
        } else {
            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.progress_loading);
            progressDialog.show();
        }
        final ImageView img_loading_frame = (ImageView) progressDialog.findViewById(R.id.iv_frame_loading);
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });
        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }
    }

    public void progressSET(String message) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }
    }

    public void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
