package com.psplog.dailycalli.activity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.psplog.dailycalli.http.HttpClient;
import com.psplog.dailycalli.R;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final TextView tv_email = findViewById(R.id.textVIew_signup_email);
        final TextView tv_pw = findViewById(R.id.textView_signup_pw);
        final TextView tv_nickname = findViewById(R.id.textView_signup_nickname);

        final EditText edit_email = findViewById(R.id.editText_signup_email);
        final EditText edit_pw = findViewById(R.id.editText_signup_pw);
        final EditText edit_pw1 = findViewById(R.id.editText_signup_pw1);
        final EditText edit_nickname = findViewById(R.id.editText_signup_nickname);

        Button bt_sign = findViewById(R.id.button_signup);

        boolean email=false, pw=false, nickname=false;

        edit_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 텍스트 수정될떄 출력
                if(edit_email.getText().length()>4)
                    tv_email.setVisibility(View.VISIBLE);
                class CheckEmail extends AsyncTask<String,String,Integer>
                {
                    String ret;
                    @Override
                    protected void onPostExecute(Integer integer) {
                        super.onPostExecute(integer);
                        if(ret.equals("fail")){
                            tv_email.setText("ERR");
                            tv_email.setTextColor(Color.RED);
                        }
                        else if(ret.equals("duplicate email")) {
                            tv_email.setText("이미 사용중인 이메일입니다.");
                            tv_email.setTextColor(Color.RED);
                        }
                        else if(ret.equals("email available")) {
                            tv_email.setText("사용가능한 이메일입니다.");
                            tv_email.setTextColor(Color.GREEN);
                        }
                        else if(ret.equals("email format is incorrect")) {
                            tv_email.setText("이메일 형식이 맞지 않습니다..");
                            tv_email.setTextColor(Color.RED);
                        }

                    }
                    @Override
                    protected Integer doInBackground(String... strings) {
                        ret = HttpClient.checkEmail(edit_email.getText().toString());
                        return null;
                    }
                }
                new CheckEmail().execute();

            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        edit_pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_pw.getText().length()>4 && edit_pw1.getText().length()>4)
                    tv_pw.setVisibility(View.VISIBLE);
                if(edit_pw.getText().equals(edit_pw1.getText()))
                {
                    tv_pw.setText("비밀번호 일치");
                    tv_pw.setTextColor(Color.GREEN);
                }else
                {
                    tv_pw.setText("비밀번호 다릅니다.");
                    tv_pw.setTextColor(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edit_pw1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_pw.getText().equals(edit_pw1.getText()))
                {
                    tv_pw.setText("비밀번호 일치");
                    tv_pw.setTextColor(Color.GREEN);
                }else
                {
                    tv_pw.setText("비밀번호 다릅니다.");
                    tv_pw.setTextColor(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edit_nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_nickname.getText().length()>4)
                    tv_nickname.setVisibility(View.VISIBLE);
                class CheckNickname extends AsyncTask<String,String,Integer>
                {
                    String ret;
                    @Override
                    protected void onPostExecute(Integer integer) {
                        super.onPostExecute(integer);
                        if(ret.equals("fail")){
                            tv_email.setText("ERR");
                            tv_email.setTextColor(Color.RED);
                        }
                        else if(ret.equals("duplicate nickname")) {
                            tv_email.setText("이미 사용중인 닉네임입니다.");
                            tv_email.setTextColor(Color.RED);
                        }
                        else if(ret.equals("nickname available")) {
                            tv_email.setText("사용가능한 닉네임입니다.");
                            tv_email.setTextColor(Color.GREEN);
                        }
                    }
                    @Override
                    protected Integer doInBackground(String... strings) {
                        ret = HttpClient.checkNickname(edit_email.getText().toString());
                        return null;
                    }
                }
                new CheckNickname().execute();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        bt_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                class SignUp extends AsyncTask<String,String,Integer>
                {
                    String ret;

                    @Override
                    protected void onPostExecute(Integer integer) {
                        super.onPostExecute(integer);
                        finish();
                    }

                    @Override
                    protected Integer doInBackground(String... strings) {
                        ret= HttpClient.signup(edit_email.getText().toString(),edit_pw.getText().toString(),edit_nickname.getText().toString());
                        return null;
                    }
                }
                new SignUp().execute();
            }
        });

    }
}
