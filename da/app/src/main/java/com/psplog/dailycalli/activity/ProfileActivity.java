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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.psplog.dailycalli.http.HttpClient;
import com.psplog.dailycalli.R;
import com.psplog.dailycalli.adapter.RecyclerCalliAdapter;
import com.psplog.dailycalli.item.Cali_Item;
import com.psplog.dailycalli.item.ProfileItem;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.primaryTextColor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#1a237e"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        final ImageView profile_img = findViewById(R.id.profile_img);
        final TextView follower = findViewById(R.id.tv_mypage_count);
        final TextView following = findViewById(R.id.tv_mypage_count2);
        final TextView post = findViewById(R.id.tv_mypage_postcount);
        final TextView content = findViewById(R.id.tv_mypage_content);

        final TextView nickname = findViewById(R.id.tv_mypage_nickname);
        final RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(mLayoutManager);

        final int user_id = getIntent().getIntExtra("user_id", -1);
        class LoadProfile extends AsyncTask<String, String, Integer> {
            ProfileItem tmp;
            List<Cali_Item> tmp1;

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                Glide.with(getApplicationContext()).load(tmp.getUser_img()).apply(bitmapTransform(new CropCircleTransformation())).into(profile_img);
                post.setText(tmp.getPostCount() + "");
                follower.setText(tmp.getFollower() + "");
                following.setText(tmp.getFollowing() + "");
                nickname.setText(tmp.getUser_nickname());

                if (tmp.getUser_intro().equals("null") || tmp.getUser_intro()==null )
                    content.setText("소개글이 없습니다.");
                else
                    content.setText(tmp.getUser_intro());

                getSupportActionBar().setTitle(tmp.getUser_nickname() + "");
                recyclerView.setAdapter(new RecyclerCalliAdapter(getApplicationContext(), tmp1, R.layout.activity_profile, 3));
            }

            @Override
            protected Integer doInBackground(String... strings) {
                tmp = HttpClient.getOtherProfile(user_id);
                tmp1 = HttpClient.getOtherCallijList(user_id);
                return null;
            }
        }
        new LoadProfile().execute();
//        Intent intent = new Intent(ProfileActivity.this,CaillContentActivity.class);
//        intent.
    }


}
