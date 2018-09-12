package com.psplog.dailycalli.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.psplog.dailycalli.http.HttpClient;
import com.psplog.dailycalli.R;
import com.psplog.dailycalli.adapter.RecyclerCommentAdapter;
import com.psplog.dailycalli.item.CalliContent_Item;
import com.psplog.dailycalli.item.Comment_item;

import java.util.List;

public class CaillContentActivity extends AppCompatActivity {
    LinearLayout layout;


    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caill_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.primaryTextColor));
        layout = findViewById(R.id.liner);
        toolbar.setTitleTextColor(getResources().getColor(R.color.primaryTextColor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#1a237e"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("상세보기");

        final int postId = getIntent().getIntExtra("postid", -1);

        final Button like = findViewById(R.id.bt_like);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                class LoadContent extends AsyncTask<String, String, Integer> {
                    boolean tmp;

                    @Override
                    protected void onPostExecute(Integer integer) {
                        super.onPostExecute(integer);
                        if (tmp) {
                            //like.setBackgroundResource(R.drawable.outline_favorite_black_48);
                            final Drawable upArrow = getResources().getDrawable(R.drawable.outline_favorite_black_48);
                            upArrow.setColorFilter(Color.parseColor("#ff524d"), PorterDuff.Mode.SRC_ATOP);
                            Animation translate = AnimationUtils.loadAnimation(getApplication(), R.anim.rotate);
                            like.startAnimation(translate);
                            like.setBackground(upArrow);
                        } else {
                            Animation translate = AnimationUtils.loadAnimation(getApplication(), R.anim.rotate);
                            like.startAnimation(translate);

                            like.setBackgroundResource(R.drawable.outline_favorite_border_black_48);
                        }
                    }

                    @Override
                    protected Integer doInBackground(String... strings) {
                        tmp = HttpClient.setLike(postId);
                        return null;
                    }
                }
                new LoadContent().execute();
            }
        });
        class LoadContent extends AsyncTask<String, String, Integer> {
            CalliContent_Item tmp;

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                TextView nickName = findViewById(R.id.textView_content_nickname);
                TextView contentText = findViewById(R.id.textview_carditem_content);

                TextView tv_tag = findViewById(R.id.textview_carditem_tag);
                TextView date = findViewById(R.id.textView_content_date);
                ImageView user_Img = findViewById(R.id.image_content_user_img);
                ImageView calli_Img = findViewById(R.id.imageview_content_calli);

                if (tmp != null) {
                    nickName.setText(tmp.getUser_nickname());
                    nickName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(CaillContentActivity.this, ProfileActivity.class);
                            intent.putExtra("user_id", tmp.getUser_id());
                            startActivity(intent);
                        }
                    });
                    String[] tag = tmp.getCalli_tag().split(" ");
                    String stag = "";
                    for (int i = 0; i < tag.length; i++) {
                        stag += "#" + tag[i] + " ";
                    }
                    contentText.setText(tmp.getCalli_txt());
                    tv_tag.setText(stag);
                    Glide.with(getApplication()).load(tmp.getUser_img()).into(user_Img);
                    Glide.with(getApplication()).load(tmp.getCalli_img()).into(calli_Img);
                    date.setText(tmp.getCalli_date());
                    if (tmp.isLikeBool()) {
                        //like.setBackgroundResource(R.drawable.outline_favorite_black_48);
                        final Drawable upArrow = getResources().getDrawable(R.drawable.outline_favorite_black_48);
                        upArrow.setColorFilter(Color.parseColor("#ff524d"), PorterDuff.Mode.SRC_ATOP);
                        like.setBackground(upArrow);
                    } else
                        like.setBackgroundResource(R.drawable.outline_favorite_border_black_48);
                } else
                    finish();

            }

            @Override
            protected Integer doInBackground(String... strings) {
                tmp = HttpClient.getCalliContent(postId);
                return null;
            }
        }
        new LoadContent().execute();

        final RecyclerView recyclerView = findViewById(R.id.recyclercomment);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);

        class LoadCommentList extends AsyncTask<String, String, Integer> {
            List<Comment_item> tmp;

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                recyclerView.setAdapter(new RecyclerCommentAdapter(getApplicationContext(), tmp, R.layout.activity_caill_content));
            }

            @Override
            protected Integer doInBackground(String... strings) {
                tmp = HttpClient.getCommentList(postId);
                return null;
            }
        }
        final LinearLayout layout = findViewById(R.id.liner);
        final EditText edit = findViewById(R.id.edit_comment);
        final TextView bt = findViewById(R.id.bt_comment);
        new LoadCommentList().execute();
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        edit.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) CaillContentActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(edit.getWindowToken(), 0);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Snackbar.make(view, "", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//                layout.setVisibility(View.VISIBLE);
//                fab.setVisibility(View.GONE);
//                edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                    @Override
//                    public void onFocusChange(View v, boolean hasFocus) {
//                        if (!hasFocus)
//                            layout.setVisibility(View.GONE);
//                    }
//                });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                class SendComment extends AsyncTask<String, String, Integer> {
                    boolean tmp;

                    @Override
                    protected void onPostExecute(Integer integer) {
                        if (tmp) {
                            edit.setText("");
                            //fab.setVisibility(View.VISIBLE);
                            //layout.setVisibility(View.GONE);
                            new LoadCommentList().execute();
                            InputMethodManager inputMethodManager = (InputMethodManager) CaillContentActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(edit.getWindowToken(), 0);

                        } else {
                            Toast.makeText(getApplication(), "댓글 작성 실패", Toast.LENGTH_SHORT);
                        }
                    }

                    @Override
                    protected Integer doInBackground(String... strings) {
                        tmp = HttpClient.sendComment(postId, edit.getText().toString(), 0);
                        return null;
                    }
                }
                new SendComment().execute();
            }
        });
        edit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    class SendComment extends AsyncTask<String, String, Integer> {
                        boolean tmp;

                        @Override
                        protected void onPostExecute(Integer integer) {
                            if (tmp) {
                                edit.setText("");
                                //fab.setVisibility(View.VISIBLE);
                                //layout.setVisibility(View.GONE);
                                new LoadCommentList().execute();
                                InputMethodManager inputMethodManager = (InputMethodManager) CaillContentActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(edit.getWindowToken(), 0);

                            } else {
                                Toast.makeText(getApplication(), "댓글 작성 실패", Toast.LENGTH_SHORT);
                            }
                        }

                        @Override
                        protected Integer doInBackground(String... strings) {
                            tmp = HttpClient.sendComment(postId, edit.getText().toString(), 0);
                            return null;
                        }
                    }
                    new SendComment().execute();
                    return true;
                }
                return false;
            }
        });
//
//            }
//        });
    }

}
