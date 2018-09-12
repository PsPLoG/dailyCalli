package com.samsung.android.sdk.pen.pg.utils;import android.content.Context;import android.util.AttributeSet;import android.widget.RelativeLayout;public class ComposerContainer extends RelativeLayout {    public interface SizeChangeListener {        void onSizeChangeListener(int w, int h, int oldw, int oldh);    }    private SizeChangeListener mSizeChangeListener;    public ComposerContainer(Context context) {        super(context);    }    public ComposerContainer(Context context, AttributeSet attrs) {        super(context, attrs);    }    public ComposerContainer(Context context, AttributeSet attrs, int defStyleAttr) {        super(context, attrs, defStyleAttr);    }    public void setListener(SizeChangeListener listener) {        if (listener != null) {            mSizeChangeListener = listener;        }    }    @Override    protected void onSizeChanged(int w, int h, int oldw, int oldh) {        super.onSizeChanged(w, h, oldw, oldh);        if (mSizeChangeListener != null) {            mSizeChangeListener.onSizeChangeListener(w, h, oldw, oldh);        }    }}