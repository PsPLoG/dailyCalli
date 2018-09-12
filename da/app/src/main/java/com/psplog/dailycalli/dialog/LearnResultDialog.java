package com.psplog.dailycalli.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.psplog.dailycalli.R;

public class LearnResultDialog extends Dialog {
    private Context context;
    private Activity activity;
    private String rate;
    public LearnResultDialog(@NonNull Context context, Activity activity, String rate) {
        super(context);
        this.context = context;
        this.activity=activity;
        this.rate = rate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learn_result_dialog);

        Button share = findViewById(R.id.bt_share);
        Button exit = findViewById(R.id.bt_exit);

        ImageView s1 = findViewById(R.id.s1);
        ImageView s2 = findViewById(R.id.s2);
        ImageView s3 = findViewById(R.id.s3);

        Animation translate = AnimationUtils.loadAnimation(context, R.anim.resultstar);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                activity.finish();;
            }
        });
        if(rate.equals("★"))
        {
            s1.setVisibility(View.VISIBLE);
            s1.startAnimation(translate);
        }else if(rate.equals("★★"))
        {
            s1.setVisibility(View.VISIBLE);
            s2.setVisibility(View.VISIBLE);
            s1.startAnimation(translate);
            s2.startAnimation(translate);

        }
        else if(rate.equals("★★★"))
        {
            s1.setVisibility(View.VISIBLE);
            s2.setVisibility(View.VISIBLE);
            s3.setVisibility(View.VISIBLE);
            s1.startAnimation(translate);
            s2.startAnimation(translate);
            s3.startAnimation(translate);
        }

    }
}
