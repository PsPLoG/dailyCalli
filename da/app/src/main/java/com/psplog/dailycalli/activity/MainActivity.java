package com.psplog.dailycalli.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.psplog.dailycalli.http.FileManagement;
import com.psplog.dailycalli.http.HttpClient;
import com.psplog.dailycalli.R;
import com.psplog.dailycalli.adapter.RecyclerCalliAdapter;
import com.psplog.dailycalli.adapter.RecyclerLearnAdapter;
import com.psplog.dailycalli.item.Cali_Item;
import com.psplog.dailycalli.item.CalliLearn_Item;
import com.psplog.dailycalli.item.ProfileItem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class MainActivity extends AppCompatActivity {
    private MainActivity.SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar!= null){
            toolbar.setTitleTextColor(getResources().getColor(R.color.primaryTextColor));
            toolbar.setTitle("");
        }
        final Drawable upArrow =  toolbar.getOverflowIcon();
        upArrow.setColorFilter(Color.parseColor("#1a237e"), PorterDuff.Mode.SRC_ATOP);
        toolbar.setOverflowIcon(upArrow);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
        mSectionsPagerAdapter = new MainActivity.SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);

        final BottomNavigationView bottomView = findViewById(R.id.bottomnavi);
        bottomView.setBackgroundResource(R.color.white);
        BottomNavigationViewHelper.removeShiftMode(bottomView);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Drawable upArrow;
                int p=0;
                for(int i=0;i<bottomView.getMenu().size();i++)
                {
                    upArrow = bottomView.getMenu().getItem(i).getIcon();
                    upArrow.setColorFilter(Color.parseColor("#acb8dc"), PorterDuff.Mode.SRC_ATOP);
                    getSupportActionBar().setHomeAsUpIndicator(upArrow);
                    bottomView.getMenu().getItem(i).setIcon(upArrow);
                }
                if(position>1)
                    p=position+1;
                else
                    p=position;
                upArrow = bottomView.getMenu().getItem(p).getIcon();
                upArrow.setColorFilter(Color.parseColor("#1a237e"), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
                bottomView.getMenu().getItem(p).setIcon(upArrow);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setAdapter(mSectionsPagerAdapter);

        bottomView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Drawable upArrow;
                for(int i=0;i<bottomView.getMenu().size();i++)
                {
                    upArrow = bottomView.getMenu().getItem(i).getIcon();
                    upArrow.setColorFilter(Color.parseColor("#acb8dc"), PorterDuff.Mode.SRC_ATOP);
                    getSupportActionBar().setHomeAsUpIndicator(upArrow);
                    bottomView.getMenu().getItem(i).setIcon(upArrow);
                }
                upArrow = item.getIcon();
                upArrow.setColorFilter(Color.parseColor("#1a237e"), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
                item.setIcon(upArrow);

                switch (item.getItemId()) {
                    case R.id.action_one:
                        mViewPager.setCurrentItem(0);
                        return true;
                    case R.id.action_two:
                        mViewPager.setCurrentItem(1);
                        return true;
                    case R.id.action_three:
                        Intent intent = new Intent(MainActivity.this,FreeDrawCalliActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.action_four:
                        mViewPager.setCurrentItem(2);
                        return true;
                    case R.id.action_five:
                        mViewPager.setCurrentItem(3);
                        return true;
                }

                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        for(int i=0;i<menu.size();i++) {
            final Drawable upArrow =  menu.getItem(i).getIcon();
            upArrow.setColorFilter(Color.parseColor("#1a237e"), PorterDuff.Mode.SRC_ATOP);
            menu.getItem(i).setIcon(upArrow);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            new FileManagement(getApplicationContext()).removeLoginData();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }else if(id == R.id.action_search)
        {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
            return true;
        }

            return super.onOptionsItemSelected(item);
    }


    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final int MAIN_CAILLI_LIST = 1;
        private static final int MAIN_CAILLI_LEARN = 2;
        private static final int MAIN_CAILLI_DRAW = 3;
        private static final int MAIN_CAILLI_ALERT = 3;
        private static final int MAIN_CAILLI_MY = 4;

        public PlaceholderFragment() {
        }
        public static MainActivity.PlaceholderFragment newInstance(int sectionNumber) {
            MainActivity.PlaceholderFragment fragment = new MainActivity.PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }
        public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

            private int halfSpace;

            public SpacesItemDecoration(int space) {
                this.halfSpace = space / 2;
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = halfSpace;
                outRect.bottom = halfSpace;
                outRect.left = halfSpace;
                outRect.right = halfSpace;
            }
        }
        //------------------------------------------------------------------------------------------
        // ViewPager Adaper
        //------------------------------------------------------------------------------------------
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int sectionNumber= getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView=null;
            if(sectionNumber == MAIN_CAILLI_LIST)
            {
                rootView = inflater.inflate(R.layout.fragment_mainpage, container, false);
                final Context context = rootView.getContext();
                final RecyclerView recyclerView = rootView.findViewById(R.id.mrecycle_view);
                recyclerView.setHasFixedSize(true);

                final int colcount =4;
                GridLayoutManager mLayoutManager = new GridLayoutManager(context, colcount);
                mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if(position%(colcount+1)==0)
                            return colcount;
                        else return 1;
                    }
                });
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.addItemDecoration(new SpacesItemDecoration(0));

                final List<Cali_Item> items=new ArrayList<Cali_Item>();
                class LoadCalliList extends AsyncTask<String,String,Integer>
                {
                    List<Cali_Item> tmp;
                    @Override
                    protected void onPostExecute(Integer integer) {
                        super.onPostExecute(integer);
                        for(int i=0;i<tmp.size();i++)
                        {
                            Log.d("asd","2caid"+tmp.get(i).getCalli_id());
                            items.add(tmp.get(i));
                        }
                        recyclerView.setAdapter(new RecyclerCalliAdapter(context, items, R.layout.activity_main,colcount));
                    }

                    @Override
                    protected Integer doInBackground(String... strings) {
                        tmp= HttpClient.getCalliList();
                        return null;
                    }
                }
                new LoadCalliList().execute();

            }
            else if(sectionNumber ==  MAIN_CAILLI_LEARN)
            {
                rootView = inflater.inflate(R.layout.fragment_learn, container, false);
                final Context context = rootView.getContext();
                final RecyclerView recyclerView = rootView.findViewById(R.id.mrecycle_view1);
                recyclerView.setHasFixedSize(true);
                GridLayoutManager mLayoutManager = new GridLayoutManager(context, 1);

                recyclerView.setLayoutManager(mLayoutManager);
                final List<CalliLearn_Item> items=new ArrayList<CalliLearn_Item>();
                class LoadLearnList extends AsyncTask<String,String,Integer>
                {
                    List<CalliLearn_Item> tmp;
                    @Override
                    protected void onPostExecute(Integer integer) {
                        super.onPostExecute(integer);
                        for(int i=0;i<tmp.size();i++)
                        {
                            items.add(tmp.get(i));
                        }
                        recyclerView.setAdapter(new RecyclerLearnAdapter(context, items, R.layout.activity_main));
                    }

                    @Override
                    protected Integer doInBackground(String... strings) {
                        tmp= HttpClient.getLearnList();
                        return null;
                    }
                }
                new LoadLearnList().execute();
            }

            else if(sectionNumber ==  MAIN_CAILLI_ALERT)
            {
                rootView = inflater.inflate(R.layout.fragment_search, container, false);
            }
            else if(sectionNumber ==  MAIN_CAILLI_MY)
            {
                rootView = inflater.inflate(R.layout.fragment_mypage, container, false);
                final ImageView profile_img = rootView.findViewById(R.id.profile_img);
                final TextView follower = rootView.findViewById(R.id.tv_mypage_count);
                final TextView following = rootView.findViewById(R.id.tv_mypage_count2);
                final TextView post = rootView.findViewById(R.id.tv_mypage_postcount);
                final TextView content = rootView.findViewById(R.id.tv_mypage_content);

                final TextView nickname = rootView.findViewById(R.id.tv_mypage_nickname);
                final Context context = rootView.getContext();
                final RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
                recyclerView.setHasFixedSize(true);
                GridLayoutManager mLayoutManager = new GridLayoutManager(context, 3);
                recyclerView.setLayoutManager(mLayoutManager);

                class LoadProfile extends AsyncTask<String,String,Integer>
                {
                    ProfileItem tmp;
                    List<Cali_Item> tmp1;
                    @Override
                    protected void onPostExecute(Integer integer) {
                        super.onPostExecute(integer);
                        Glide.with(getContext()).load(tmp.getUser_img()).apply(bitmapTransform(new CropCircleTransformation())).into(profile_img);
                        post.setText(tmp.getPostCount()+"");
                        follower.setText(tmp.getFollower()+"");
                        following.setText(tmp.getFollowing()+"");

                        nickname.setText(tmp.getUser_nickname());
                        if (tmp.getUser_intro().equals("null") || tmp.getUser_intro()==null )
                            content.setText("소개글이 없습니다.");
                        else
                            content.setText(tmp.getUser_intro());

                        recyclerView.setAdapter(new RecyclerCalliAdapter(context, tmp1, R.layout.activity_main,3));
                    }

                    @Override
                    protected Integer doInBackground(String... strings) {
                        tmp= HttpClient.getProfile();
                        tmp1=HttpClient.getMyCallijList();
                        return null;
                    }
                }
                new LoadProfile().execute();
            }
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return MainActivity.PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }
    }

}

class BottomNavigationViewHelper {

    @SuppressLint("RestrictedApi")
    static void removeShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field");
        } catch (IllegalAccessException e) {
            Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode");
        }
    }
}
