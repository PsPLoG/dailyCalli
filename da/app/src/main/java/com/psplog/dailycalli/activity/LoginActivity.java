package com.psplog.dailycalli.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.psplog.dailycalli.http.FileManagement;
import com.psplog.dailycalli.http.HttpClient;
import com.psplog.dailycalli.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageView image = findViewById(R.id.imageView2);
        LinearLayout lay = findViewById(R.id.lay);
        TextView tv = findViewById(R.id.textView_signup);
        final EditText email = findViewById(R.id.editText_login_email);
        final EditText pw = findViewById(R.id.editText_login_pw);
        Button login = findViewById(R.id.button_login);
        final HttpClient http = new HttpClient();


        Animation translate = AnimationUtils.loadAnimation(this, R.anim.login);
        image.startAnimation(translate);
        final FileManagement fileio = new FileManagement(getApplication());
        class LoginCheck extends AsyncTask<String,Integer, String>
        {
            Dialog dialog;
            String ret;
            boolean check=false;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                check=true;
                dialog = new ProgressDialog(LoginActivity.this);
                dialog.setTitle("로그인중");
                dialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                dialog.dismiss();
                Log.d("asd",fileio.loadLoginData()+" "+check+" "+ret+"token"+HttpClient.token);
                if(fileio.loadLoginData() && check)
                {
                    Intent tn = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(tn);
                    finish();
                } else
                {
                    Toast toast=Toast.makeText(getApplication(), "인터넷 상태나 ID,PASS를확인해주세요", Toast.LENGTH_SHORT);
                    toast.show();
                }
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    ret = http.login(fileio.token);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(!ret.equals("success"))
                    check=false;
                return null;
            }
        }

        if(fileio.loadLoginData()) {
            new LoginCheck().execute();
        }else
        {
            lay.setVisibility(View.VISIBLE);
            Animation in = AnimationUtils.loadAnimation(this, R.anim.transrate);
            lay.startAnimation(in);
        }




        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    class LoginAsync extends AsyncTask<String,String,Integer>
                    {
                        Toast toast;
                        String ret;
                        @Override
                        protected void onPostExecute(Integer integer) {
                            super.onPostExecute(integer);
                            if(ret.equals("success"))
                            {
                                if(fileio.loadLoginData()==false) {
                                    fileio.token = http.token;
                                    fileio.saveLoginData();
                                }
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                toast = Toast.makeText(getApplication(),"아이디나 비밀번호를 확인해주세요.",Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }

                        @Override
                        protected Integer doInBackground(String... strings) {
                            try {
                                ret = http.login(email.getText().toString(),pw.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                }
                new LoginAsync().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }


            }
        });

    }
    private static final int PEMISSION_REQUEST_CODE = 1;
    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT  < Build.VERSION_CODES.M) {
            return false;
        }
        List<String> permissionList = new ArrayList<String>(Arrays.asList(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE));
        if(PackageManager.PERMISSION_GRANTED == checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            permissionList.remove(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(PackageManager.PERMISSION_GRANTED == checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)){
            permissionList.remove(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(permissionList.size()>0) {
            requestPermissions(permissionList.toArray(new String[permissionList.size()]), PEMISSION_REQUEST_CODE);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PEMISSION_REQUEST_CODE) {
            if (grantResults != null ) {
                for(int i= 0; i< grantResults.length;i++){
                    if(grantResults[i]!= PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(getApplicationContext(),"permission: " + permissions[i] + " is denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
