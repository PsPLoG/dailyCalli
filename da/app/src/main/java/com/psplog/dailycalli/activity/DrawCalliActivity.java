package com.psplog.dailycalli.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v13.view.inputmethod.InputContentInfoCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.psplog.dailycalli.R;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.composer.SpenActionListener;
import com.samsung.android.sdk.pen.Spen;
import com.samsung.android.sdk.pen.document.SpenInvalidPasswordException;
import com.samsung.android.sdk.pen.document.SpenObjectBase;
import com.samsung.android.sdk.pen.document.SpenObjectImage;
import com.samsung.android.sdk.pen.document.SpenObjectStroke;
import com.samsung.android.sdk.pen.document.SpenObjectTextBox;
import com.samsung.android.sdk.pen.document.SpenUnsupportedTypeException;
import com.samsung.android.sdk.pen.document.SpenUnsupportedVersionException;
import com.samsung.android.sdk.pen.engine.SpenDrawListener;
import com.samsung.android.sdk.pen.engine.SpenLayeredReplayListener;
import com.samsung.android.sdk.pen.engine.SpenTouchListener;
import com.samsung.android.sdk.pen.SpenSettingEraserInfo;
import com.samsung.android.sdk.pen.SpenSettingPenInfo;
import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.document.SpenPageDoc.HistoryListener;
import com.samsung.android.sdk.pen.document.SpenPageDoc.HistoryUpdateInfo;
import com.samsung.android.sdk.pen.engine.SpenColorPickerListener;
import com.samsung.android.sdk.pen.engine.SpenReplayListener;
import com.samsung.android.sdk.pen.engine.SpenSurfaceView;
import com.samsung.android.sdk.pen.pg.tool.SDKUtils;
import com.samsung.android.sdk.pen.settingui.SpenSettingEraserLayout;
import com.samsung.android.sdk.pen.settingui.SpenSettingEraserLayout.EventListener;
import com.samsung.android.sdk.pen.settingui.SpenSettingPenLayout;

public class DrawCalliActivity extends Activity {
    public static final int SDK_VERSION = Build.VERSION.SDK_INT;
    private static final int ADD_BACKGROUND_REQUEST = 1;
    private final int REQUEST_CODE_SELECT_IMAGE_BACKGROUND = 100;

    private Context mContext;
    private SpenNoteDoc mSpenNoteDoc;
    private SpenPageDoc mSpenPageDoc;
    private SpenPageDoc mPreSpenPageDoc;
    private SpenSurfaceView mSpenSurfaceView;
    private SpenSettingPenLayout mPenSettingView;
    private SpenSettingEraserLayout mEraserSettingView;

    private ImageView mPenBtn;
    private ImageView mEraserBtn;
    private ImageView mUndoBtn;
    private ImageView mRedoBtn;
    private ImageView mBgImgBtn;
    private ImageView mPlayBtn;

    private float lastPanX, lastPanY;
    private float matchRatio;
    private Rect rect;
    private boolean checkEndPen;
    private boolean startReplay=false;
    private int mToolType = SpenSurfaceView.TOOL_SPEN;
    private File mFilePath;
    private ImageView mSaveFileBtn;
    private ImageView mLoadFileBtn;

    private int learnProgress=0;
    private ArrayList<SpenObjectBase> list = new ArrayList<SpenObjectBase>();
    private ArrayList<SpenObjectBase> userList = new ArrayList<SpenObjectBase>();
    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_calli);
        mContext = this;

        // Initialize Spen
        boolean isSpenFeatureEnabled = false;
        Spen spenPackage = new Spen();
        try {
            spenPackage.initialize(this);
            isSpenFeatureEnabled = spenPackage.isFeatureEnabled(Spen.DEVICE_PEN);
        } catch (SsdkUnsupportedException e) {
            if (SDKUtils.processUnsupportedException(this, e) == true) {
                return;
            }
        } catch (Exception e1) {
            Toast.makeText(mContext, "Cannot initialize Spen.", Toast.LENGTH_SHORT).show();
            e1.printStackTrace();
            finish();
        }
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SPen/";
        mFilePath = new File(filePath);
        if (!mFilePath.exists()) {
            if (!mFilePath.mkdirs()) {
                Toast.makeText(mContext, "Save Path Creation Error", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        FrameLayout spenViewContainer = (FrameLayout) findViewById(R.id.spenViewContainer);
        RelativeLayout spenViewLayout = (RelativeLayout) findViewById(R.id.spenViewLayout);

        // Create PenSettingView
        mPenSettingView = new SpenSettingPenLayout(getApplicationContext(), "", spenViewLayout);

        // Create EraserSettingView
        mEraserSettingView = new SpenSettingEraserLayout(getApplicationContext(), "", spenViewLayout);

        spenViewContainer.addView(mPenSettingView);
        spenViewContainer.addView(mEraserSettingView);

        // Create SpenSurfaceView
        mSpenSurfaceView = new SpenSurfaceView(mContext);
        if (mSpenSurfaceView == null ) {
            Toast.makeText(mContext, "Cannot create new SpenSurfaceView.", Toast.LENGTH_SHORT).show();
            finish();
        }
        mSpenSurfaceView.setToolTipEnabled(true);
        spenViewLayout.addView(mSpenSurfaceView);
        mPenSettingView.setCanvasView(mSpenSurfaceView);
        mEraserSettingView.setCanvasView(mSpenSurfaceView);
        // Get the dimension of the device screen.
        Display display = getWindowManager().getDefaultDisplay();
        rect = new Rect();
        display.getRectSize(rect);
        // Create SpenNoteDoc
        try {
            mSpenNoteDoc = new SpenNoteDoc(mContext, rect.width(), rect.height());
        } catch (IOException e) {
            Toast.makeText(mContext, "Cannot create new NoteDoc", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

        // Add a Page to NoteDoc, get an instance, and set it to the member variable.
        mSpenSurfaceView.setBlankColor(0xacb8dc);
        mSpenPageDoc = mSpenNoteDoc.appendPage();
        mSpenPageDoc.setBackgroundColor(0xFFFFFFFF);
        mSpenPageDoc.clearHistory();
        String path = mContext.getFilesDir().getPath();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.learn);
        saveBitmapToFileCache(bitmap, path + "/learn.png");
//        mSpenPageDoc.setBackgroundImageMode(SpenPageDoc.BACKGROUND_IMAGE_MODE_FIT);
//        mSpenPageDoc.setBackgroundImage(path + "/learn.png");
        // Set PageDoc to View
        mSpenSurfaceView.setPageDoc(mSpenPageDoc, true);

        initSettingInfo();
        // Register the listener
        mSpenSurfaceView.setColorPickerListener(mColorPickerListener);
        mSpenSurfaceView.setPreTouchListener(onPreTouchSurfaceViewListener);
        mSpenSurfaceView.setReplayListener(mReplayListener);
        mSpenPageDoc.setHistoryListener(mHistoryListener);
        mEraserSettingView.setEraserListener(mEraserListener);

        // Set a button
        mPenBtn = (ImageView) findViewById(R.id.penBtn);
        mPenBtn.setOnClickListener(mPenBtnClickListener);

        mEraserBtn = (ImageView) findViewById(R.id.eraserBtn);
        mEraserBtn.setOnClickListener(mEraserBtnClickListener);

        mUndoBtn = (ImageView) findViewById(R.id.undoBtn);
        mUndoBtn.setOnClickListener(undoNredoBtnClickListener);
        mUndoBtn.setEnabled(mSpenPageDoc.isUndoable());

        mRedoBtn = (ImageView) findViewById(R.id.redoBtn);
        mRedoBtn.setOnClickListener(undoNredoBtnClickListener);
        mRedoBtn.setEnabled(mSpenPageDoc.isRedoable());

        mBgImgBtn = (ImageView) findViewById(R.id.bgImgBtn);
        mBgImgBtn.setOnClickListener(mBgImgBtnClickListener);

        mPlayBtn = (ImageView) findViewById(R.id.playBtn);
        mPlayBtn.setOnClickListener(mPlayBtnClickListener);

        mSaveFileBtn = (ImageView) findViewById(R.id.saveFileBtn);
        mSaveFileBtn.setOnClickListener(mSaveFileBtnClickListener);

        mLoadFileBtn = (ImageView) findViewById(R.id.loadFileBtn);
        mLoadFileBtn.setOnClickListener(mLoadFileBtnClickListener);
        selectButton(mPenBtn);
        mSpenPageDoc.startRecord();

        try {
            mSpenPageDoc.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isSpenFeatureEnabled == false) {
            mToolType = SpenSurfaceView.TOOL_FINGER;
        } else {
            mToolType = SpenSurfaceView.TOOL_SPEN;
        }
        mSpenSurfaceView.setToolTypeAction(mToolType, SpenSurfaceView.ACTION_STROKE);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        try {
//                            Thread.sleep(3000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                });
//            }
//        }).start();

        String filepath= getIntent().getStringExtra("file");
        startLearn(filepath);
    }

    private void setAniZoom(final float centerX, final float centerY, float ratio)
    {
        final float r=ratio-1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        for (int i = 0; i < 16; i++) {
                            mSpenSurfaceView.setZoom(centerX,centerY,1.0f+(r*(i/16f)));
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();
    }
    public void setPan(float x, float y)
    {
        Log.d("asd","x : "+x+" y : "+ y);
        lastPanX=x;
        lastPanY=y;
        mSpenSurfaceView.setPan(new PointF(x,y));
    }
    private boolean similarBitmap(SpenObjectBase a,SpenObjectBase b)
    {
        PointF A[] = ((SpenObjectStroke)a).getPoints();
        PointF B[] = ((SpenObjectStroke)b).getPoints();

        Log.d("jaccard"," jaccard : "+jaccardSimilarity(A,B));
        if(jaccardSimilarity(A,B)>=0.5) {
            matchRatio+=jaccardSimilarity(A,B);
            return false;
        }
        else
            return true;
    }
    static private PointF[] trimFloat(PointF[] a)
    {
        int acc=20;
        for(int i=0;i<a.length;i++)
        {
            a[i].x=((int)a[i].x/acc)*acc;
            a[i].y=((int)a[i].y/acc)*acc;
        }
        return a;
    }
    static private double jaccardSimilarity(PointF[] a, PointF[] b) {
        Set<PointF> s1 = new HashSet<PointF>();
        a = trimFloat(a);
        b = trimFloat(b);
        for (int i = 0; i < a.length; i++) {
            s1.add(a[i]);
        }
        Set<PointF> s2 = new HashSet<PointF>();
        for (int i = 0; i < b.length; i++) {
            s2.add(b[i]);
        }

        final int sa = s1.size();
        final int sb = s2.size();
        s1.retainAll(s2);
        final int intersection = s1.size();
        return 1d / (sa + sb - intersection) * intersection;
    }
    public void startReplayPostion(int positon)
    {
        mSpenSurfaceView.startReplay();
        mSpenSurfaceView.pauseReplay();
        int objectSize = mSpenPageDoc.getObjectList().size();
        if (objectSize > 1)
            mSpenSurfaceView.setReplayPosition(mSpenPageDoc.getObjectList().size() - 1);
        mSpenSurfaceView.resumeReplay();
    }
    public void nextReplay(int num)
    {
        // replay가 진행중이면 중지
        if(mSpenSurfaceView.getReplayState()==SpenSurfaceView.REPLAY_STATE_PLAYING)
            return;

        if(num>=list.size()) {
            String rating="";
            if(matchRatio/list.size()>=0.6)
                rating="★★★";
            else if(matchRatio/list.size()>=0.55)
                rating="★★";
            else if(matchRatio/list.size()>=0.5)
                rating="★";
            mSpenPageDoc.removeObject(mSpenPageDoc.getObject(mSpenPageDoc.getObjectList().size()-2));
            mSpenSurfaceView.update();
            Toast.makeText(mContext, "완성! 축하드려요. 점수는 "+rating+"입니다~", Toast.LENGTH_SHORT).show();
            Log.d("asd", "배우기 종료");
            return ;
        }
        learnProgress++;
        if(mSpenPageDoc.getObjectList().size()>1) {
            // 일치율이 부족할경우 다시 보여줌
            if (similarBitmap(mSpenPageDoc.getObject(mSpenPageDoc.getObjectList().size() - 1), mSpenPageDoc.getObject(mSpenPageDoc.getObjectList().size() - 2))) {
                Toast.makeText(mContext, "다시 그려주세요.", Toast.LENGTH_SHORT).show();
                HistoryUpdateInfo[] userData = mSpenPageDoc.undo();
                mSpenSurfaceView.updateUndo(userData);
                learnProgress--;
                return;
            }else
                mSpenPageDoc.removeObject(mSpenPageDoc.getObject(mSpenPageDoc.getObjectList().size() - 2));
        }
        mSpenPageDoc.appendObject(list.get(num));
        mSpenSurfaceView.update();

        final int index = num;
        final float coX=540;
        final float coY=930;
        final float getX=mSpenSurfaceView.getPan().x;
        final float getY=mSpenSurfaceView.getPan().y;
        final float ratio = mSpenSurfaceView.getZoomRatio();

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        float targetX=0,targetY=0;
                        int fps=20;
                        for (int i = 0; i < 16; i++) {
                            targetX = (list.get(index).getRect().centerX()-getX-(coX/ratio))*(i/16f);
                            targetY = (list.get(index).getRect().centerY()-getY-(coY/ratio))*(i/16f);
                            setPan(getX+targetX,getY+targetY);
                            //Log.d("asd","w : "+coX+" h : "+coY+"ratio"+mSpenSurfaceView.getZoomRatio());
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        // setPan(list.get(index).getRect().centerX()-180,list.get(index).getRect().centerY()-310);
                        startReplayPostion(0);
                    }
                });
            }
        }).start();
    }


    static public void saveBitmapToFileCache(Bitmap bitmap, String strFilePath) {
        // Save images from resources as a file, which can be set as a background image.
        File file = new File(strFilePath);
        OutputStream out = null;

        if (file.exists() == true) {
            return;
        }
        try {
            file.createNewFile();
            out = new FileOutputStream(file);

            if (strFilePath.endsWith(".jpg")) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } else {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void initSettingInfo() {
        // Initialize Pen settings
        SpenSettingPenInfo penInfo = new SpenSettingPenInfo();
        penInfo.color = Color.BLUE;
        penInfo.size = 10;
        mSpenSurfaceView.setPenSettingInfo(penInfo);
        mPenSettingView.setInfo(penInfo);

        // Initialize Eraser settings
        SpenSettingEraserInfo eraserInfo = new SpenSettingEraserInfo();
        eraserInfo.size = 30;
        mSpenSurfaceView.setEraserSettingInfo(eraserInfo);
        mEraserSettingView.setInfo(eraserInfo);
    }
    private void setSmartScale(boolean enable) {

        // Set the region for Smart Zoom
        int width, height, w1, h1, w9, h9;
        width = mSpenSurfaceView.getWidth();
        height = mSpenSurfaceView.getHeight();
        w1 = (int) (width * 0.1);
        h1 = (int) (height * 0.1);
        w9 = (int) (width * 0.9);
        h9 = (int) (height * 0.9);

        // Set the region for Smart Scale
        Rect zoomRegion = new Rect(w1, h1, w9, h9);
        mSpenSurfaceView.setSmartScaleEnabled(enable, zoomRegion, 8, 500, 1.5f);
    }


    private final OnClickListener mSmartZoomBtnClickListener =
            new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isSmartScaleEnabled =
                            !mSpenSurfaceView.isSmartScaleEnabled();
                    setSmartScale(isSmartScaleEnabled);
                }
            };
    private SpenTouchListener onPreTouchSurfaceViewListener = new SpenTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // TODO Auto-generated method stub
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    enableButton(false);
                    checkEndPen=false;
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    enableButton(true);
                    // TouchUP 일때
                    // 다음 단계로
                    checkEndPen=true;

                    Log.d("asd","touchUp X: "+event.getX()+" Y:"+event.getY()+"current ratio : "+mSpenSurfaceView.getZoomRatio());

                    break;
            }
            return false;
        }
    };

    private final OnClickListener mPenBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // When Spen is in stroke (pen) mode
            if (mSpenSurfaceView.getToolTypeAction(mToolType) == SpenSurfaceView.ACTION_STROKE) {
                // If PenSettingView is open, close it.
                if (mPenSettingView.isShown()) {
                    mPenSettingView.setVisibility(View.GONE);
                    //CardView cardView = findViewById(R.id.cardView);
                    // If PenSettingView is not open, close it.
                } else {
                    mPenSettingView.setViewMode(SpenSettingPenLayout.VIEW_MODE_NORMAL);
                    mPenSettingView.setVisibility(View.VISIBLE);
                }
                // If Spen is not in stroke (pen) mode, change it to stroke mode.
            } else {
                int curAction = mSpenSurfaceView.getToolTypeAction(SpenSurfaceView.TOOL_FINGER);
                mSpenSurfaceView.setToolTypeAction(mToolType, SpenSurfaceView.ACTION_STROKE);
                int newAction = mSpenSurfaceView.getToolTypeAction(SpenSurfaceView.TOOL_FINGER);
                if (mToolType == SpenSurfaceView.TOOL_FINGER) {
                    if (curAction != newAction) {
                        selectButton(mPenBtn);
                    }
                } else {
                    selectButton(mPenBtn);
                }
            }
        }
    };

    private final OnClickListener mEraserBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // When Spen is in eraser mode
            if (mSpenSurfaceView.getToolTypeAction(mToolType) == SpenSurfaceView.ACTION_ERASER) {
                // If EraserSettingView is open, close it.
                if (mEraserSettingView.isShown()) {
                    mEraserSettingView.setVisibility(View.GONE);
                    // If EraserSettingView is not open, close it.
                } else {
                    mEraserSettingView.setVisibility(View.VISIBLE);
                }
                // If Spen is not in eraser mode, change it to eraser mode.
            } else {
                selectButton(mEraserBtn);
                mSpenSurfaceView.setToolTypeAction(mToolType, SpenSurfaceView.ACTION_ERASER);
            }
        }
    };

    private final OnClickListener mBgImgBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(hasReadStorePermission()){
                closeSettingView();
                mSpenSurfaceView.cancelStroke();
                callGalleryForInputImage(REQUEST_CODE_SELECT_IMAGE_BACKGROUND);
            } else {
                requestReadStorePermission();
            }
        }
    };

    private final OnClickListener mPlayBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //learnProgress =1;
            closeSettingView();
            setBtnEnabled(false);
            // 시작 버튼누르면 리셋
//            learnProgress=0;
//            mSpenSurfaceView.cancelStroke();
//            mSpenSurfaceView.startReplay();
//            nextReplay(learnProgress);
//            startReplay=true;
            startReplayPostion(0);
        }
    };

    private final OnClickListener undoNredoBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mSpenPageDoc == null) {
                return;
            }
            // Undo button is clicked
            if (v.equals(mUndoBtn)) {
                if (mSpenPageDoc.isUndoable()) {
                    if(mSpenPageDoc.getObjectList().size()>1){
                        HistoryUpdateInfo[] userData = mSpenPageDoc.undo();
                        mSpenSurfaceView.updateUndo(userData);
                        userData = mSpenPageDoc.undo();
                        mSpenSurfaceView.updateUndo(userData);
                        learnProgress--;
                    }

                }
                // Redo button is clicked
            } else if (v.equals(mRedoBtn)) {
                if (mSpenPageDoc.isRedoable()) {
                    HistoryUpdateInfo[] userData = mSpenPageDoc.redo();
                    mSpenSurfaceView.updateRedo(userData);
                }
            }
        }
    };

    private final SpenColorPickerListener mColorPickerListener = new SpenColorPickerListener() {
        @Override
        public void onChanged(int color, int x, int y) {
            // Set the color from the Color Picker to the setting view.
            if (mPenSettingView != null) {
                SpenSettingPenInfo penInfo = mPenSettingView.getInfo();
                penInfo.color = color;
                mPenSettingView.setInfo(penInfo);
            }
        }
    };

    private final EventListener mEraserListener = new EventListener() {
        @Override
        public void onClearAll() {
            // ClearAll button action routines of EraserSettingView
            mSpenPageDoc.removeAllObject();
            mSpenSurfaceView.update();
        }
    };

    private final HistoryListener mHistoryListener = new HistoryListener() {
        @Override
        public void onCommit(SpenPageDoc page) {
            Log.d("asd","onCommit");
            if(checkEndPen) // TOUCHUP일떄 리플레이를 보여줌
            {
                checkEndPen=false;
                nextReplay(learnProgress);
            }
        }

        @Override
        public void onUndoable(SpenPageDoc page, boolean undoable) {
            // Enable or disable the button according to the availability of undo.
            mUndoBtn.setEnabled(undoable);
        }

        @Override
        public void onRedoable(SpenPageDoc page, boolean redoable) {
            // Enable or disable the button according to the availability of redo.
            mRedoBtn.setEnabled(redoable);
        }
    };

    private final SpenLayeredReplayListener mReplayListener = new SpenLayeredReplayListener() {

        @Override
        public void onProgressChanged(int progress,int la, int id) {
        }

        @Override
        public void onCompleted() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Enable the button when replay is complete.
                    setBtnEnabled(true);
                    mUndoBtn.setEnabled(mSpenPageDoc.isUndoable());
                    mRedoBtn.setEnabled(mSpenPageDoc.isRedoable());
                    setPan(lastPanX,lastPanY);

                }
            });

        }
    };

    private void enableButton(boolean isEnable) {
        mPenBtn.setEnabled(isEnable);
        mEraserBtn.setEnabled(isEnable);
        mBgImgBtn.setEnabled(isEnable);
        mPlayBtn.setEnabled(isEnable);
        mUndoBtn.setEnabled(isEnable && mSpenPageDoc.isUndoable());
        mRedoBtn.setEnabled(isEnable && mSpenPageDoc.isRedoable());
    }

    private void selectButton(View v) {
        // Enable or disable the button according to the current mode.
        mPenBtn.setSelected(false);
        mEraserBtn.setSelected(false);

        v.setSelected(true);

        closeSettingView();
    }

    private void closeSettingView() {
        // Close all the setting views.
        mEraserSettingView.setVisibility(SpenSurfaceView.GONE);
        mPenSettingView.setVisibility(SpenSurfaceView.GONE);
    }

    private void setBtnEnabled(boolean clickable) {
        // Enable or disable all the buttons.
        mPenBtn.setEnabled(clickable);
        mEraserBtn.setEnabled(clickable);
        mUndoBtn.setEnabled(clickable);
        mRedoBtn.setEnabled(clickable);
        mBgImgBtn.setEnabled(clickable);
        mPlayBtn.setEnabled(clickable);
    }

    private final OnClickListener mSaveFileBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(checkPermission()){
                return;
            }
            mSpenSurfaceView.closeControl();

            closeSettingView();
            saveNoteFile(false);
        }
    };

    private final OnClickListener mLoadFileBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(checkPermission()){
                return;
            }
            mSpenSurfaceView.closeControl();

            closeSettingView();
            loadNoteFile();
        }
    };
    private void loadNoteFile() {
        // Load the file list.
        final String fileList[] = setFileList();
        if (fileList == null) {
            return;
        }

        // Prompt Load File dialog.
        new AlertDialog.Builder(mContext).setTitle("Select file")
                .setItems(fileList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strFilePath = mFilePath.getPath() + '/' + fileList[which];
                        Log.d("file ", "getPath :"+mFilePath.getPath()+" file "+fileList[which]);
                        try {
                            SpenObjectTextBox.setInitialCursorPos(SpenObjectTextBox.CURSOR_POS_END);
                            // Create NoteDoc with the selected file.
                            SpenNoteDoc tmpSpenNoteDoc = new SpenNoteDoc(mContext, strFilePath, rect.width(),
                                    SpenNoteDoc.MODE_READ_ONLY, true);
                            mSpenNoteDoc.close();
                            mSpenNoteDoc = tmpSpenNoteDoc;
                            if (mSpenNoteDoc.getPageCount() == 0) {
                                mSpenPageDoc = mSpenNoteDoc.appendPage();
                            } else {
                                mSpenPageDoc = mSpenNoteDoc.getPage(mSpenNoteDoc.getLastEditedPageIndex());
                            }
                            list.clear();
                            for(int i=0;i<mSpenPageDoc.getObjectList().size();i++)
                            {
                                SpenObjectStroke tmp = new SpenObjectStroke();
                                tmp.copy(mSpenPageDoc.getObjectList().get(i));
                                tmp.setColor(0xcfcfcf);
                                list.add(tmp);
                            }
                            matchRatio=0;
                            learnProgress=0;
                            mSpenPageDoc.setHistoryListener(mHistoryListener);
                            mSpenSurfaceView.setPageDoc(mSpenPageDoc, true);
                            mSpenSurfaceView.setReplaySpeed(2);
                            mSpenSurfaceView.update();
                            mSpenPageDoc.startRecord();

//                            // 1초후 리플레이 자동 시작
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    runOnUiThread(new Runnable() {
//                                        public void run() {
//                                            try {
//                                                Thread.sleep(1000);
//                                            } catch (InterruptedException e) {
//                                                e.printStackTrace();
//                                            }
//                                            setAniZoom(list.get(0).getRect().centerX(),list.get(0).getRect().centerY(),2.5f);
//                                            learnProgress=0;
//                                            mSpenSurfaceView.cancelStroke();
//                                            mSpenSurfaceView.startReplay();
//                                            nextReplay(learnProgress);
//                                            startReplay=true;
//                                        }
//                                    });
//
//                                }
//                            }).start();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                            for (int i = 0; i < 16; i++) {
                                                mSpenSurfaceView.setZoom(list.get(0).getRect().centerX(),list.get(0).getRect().centerY(),1.0f+(2.5f*(i/16f)));
                                                try {
                                                    Thread.sleep(20);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            lastPanX=mSpenSurfaceView.getPan().x;
                                            lastPanX=mSpenSurfaceView.getPan().y;
                                            learnProgress=0;
                                            mSpenPageDoc.removeAllObject();
                                            mSpenSurfaceView.cancelStroke();
                                            mSpenSurfaceView.startReplay();
                                            nextReplay(learnProgress);
                                            startReplay=true;
                                        }
                                    });
                                }
                            }).start();

                            Toast.makeText(mContext, "Successfully loaded noteFile.", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Toast.makeText(mContext, "Cannot open this file.", Toast.LENGTH_LONG).show();
                        } catch (SpenUnsupportedTypeException e) {
                            Toast.makeText(mContext, "This file is not supported.", Toast.LENGTH_LONG).show();
                        } catch (SpenInvalidPasswordException e) {
                            Toast.makeText(mContext, "This file is locked by a password.", Toast.LENGTH_LONG).show();
                        } catch (SpenUnsupportedVersionException e) {
                            Toast.makeText(mContext, "This file is the version that does not support.",
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(mContext, "Failed to load noteDoc.", Toast.LENGTH_LONG).show();
                        }
                    }
                }).show();
    }

    private void startLearn(String filepath)
    {
        try {

            Log.d("file ", "getPath :"+filepath);
            SpenObjectTextBox.setInitialCursorPos(SpenObjectTextBox.CURSOR_POS_END);
            // Create NoteDoc with the selected file.
            SpenNoteDoc tmpSpenNoteDoc = new SpenNoteDoc(mContext, filepath, rect.width(),
                    SpenNoteDoc.MODE_READ_ONLY, true);
            mSpenNoteDoc.close();
            mSpenNoteDoc = tmpSpenNoteDoc;
            if (mSpenNoteDoc.getPageCount() == 0) {
                mSpenPageDoc = mSpenNoteDoc.appendPage();
            } else {
                mSpenPageDoc = mSpenNoteDoc.getPage(mSpenNoteDoc.getLastEditedPageIndex());
            }
            list.clear();
            for(int i=0;i<mSpenPageDoc.getObjectList().size();i++)
            {
                SpenObjectStroke tmp = new SpenObjectStroke();
                tmp.copy(mSpenPageDoc.getObjectList().get(i));
                tmp.setColor(0xcfcfcf);

                if(i==0)
                {
                    mSpenSurfaceView.getPenSettingInfo().advancedSetting=tmp.getAdvancedPenSetting();
                    mSpenSurfaceView.getPenSettingInfo().color=tmp.getColor();
                    mSpenSurfaceView.getPenSettingInfo().size=tmp.getPenSize();

                    mPenSettingView.getInfo().advancedSetting=tmp.getAdvancedPenSetting();
                    mPenSettingView.getInfo().color=tmp.getColor();
                    mPenSettingView.getInfo().size=tmp.getPenSize();

                }
                //Log.d("peninfo",tmp.getAdvancedPenSetting());
                list.add(tmp);

            }
            matchRatio=0;
            learnProgress=0;
            mSpenPageDoc.setHistoryListener(mHistoryListener);
            mSpenSurfaceView.setPageDoc(mSpenPageDoc, true);
            mSpenSurfaceView.setReplaySpeed(2);
            mSpenSurfaceView.update();
            mSpenPageDoc.startRecord();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            for (int i = 0; i < 16; i++) {
                                mSpenSurfaceView.setZoom(list.get(0).getRect().centerX(),list.get(0).getRect().centerY(),1.0f+(2.0f*(i/16f)));
                                try {
                                    Thread.sleep(20);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            lastPanX=mSpenSurfaceView.getPan().x;
                            lastPanX=mSpenSurfaceView.getPan().y;
                            learnProgress=0;
                            mSpenPageDoc.removeAllObject();
                            mSpenSurfaceView.cancelStroke();
                            mSpenSurfaceView.startReplay();
                            nextReplay(learnProgress);
                            startReplay=true;
                        }
                    });
                }
            }).start();

            Toast.makeText(mContext, "Successfully loaded noteFile.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(mContext, "Cannot open this file.", Toast.LENGTH_LONG).show();
        } catch (SpenUnsupportedTypeException e) {
            Toast.makeText(mContext, "This file is not supported.", Toast.LENGTH_LONG).show();
        } catch (SpenInvalidPasswordException e) {
            Toast.makeText(mContext, "This file is locked by a password.", Toast.LENGTH_LONG).show();
        } catch (SpenUnsupportedVersionException e) {
            Toast.makeText(mContext, "This file is the version that does not support.",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Failed to load noteDoc.", Toast.LENGTH_LONG).show();
        }
    }
    private String[] setFileList() {
        // Call the file list under the directory in mFilePath.
        if (!mFilePath.exists()) {
            if (!mFilePath.mkdirs()) {
                Toast.makeText(mContext, "Save Path Creation Error", Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        // Filter in spd and png files.
        File[] fileList = mFilePath.listFiles(new txtFileFilter());
        if (fileList == null) {
            Toast.makeText(mContext, "File does not exist.", Toast.LENGTH_SHORT).show();
            return null;
        }

        int i = 0;
        String[] strFileList = new String[fileList.length];
        for (File file : fileList) {
            strFileList[i++] = file.getName();
        }

        return strFileList;
    }
    static class txtFileFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return (name.endsWith(".spd") || name.endsWith(".png"));
        }
    }
    private boolean saveNoteFile(final boolean isClose) {
        // Prompt Save File dialog to get the file name
        // and get its save format option (note file or image).
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.save_file_dialog, (ViewGroup) findViewById(R.id.layout_root));

        AlertDialog.Builder builderSave = new AlertDialog.Builder(mContext);
        builderSave.setTitle("Enter file name");
        builderSave.setView(layout);

        final EditText inputPath = (EditText) layout.findViewById(R.id.input_path);
        inputPath.setText("Note");

        builderSave.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final RadioGroup selectFileExt = (RadioGroup) layout.findViewById(R.id.radioGroup);

                // Set the save directory for the file.
                String saveFilePath = mFilePath.getPath() + '/';
                String fileName = inputPath.getText().toString();
                if (!fileName.equals("")) {
                    saveFilePath += fileName;
                    int checkedRadioButtonId = selectFileExt.getCheckedRadioButtonId();
                    if (checkedRadioButtonId == R.id.radioNote) {
                        saveFilePath += ".spd";
                        saveNoteFile(saveFilePath);
                    } else if (checkedRadioButtonId == R.id.radioImage) {
                        saveFilePath += ".png";
                        captureSpenSurfaceView(saveFilePath);
                    } else {
                    }
                    if (isClose) {
                        finish();
                    }
                } else {
                    Toast.makeText(mContext, "Invalid filename !!!", Toast.LENGTH_LONG).show();
                }
            }
        });
        builderSave.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isClose) {
                    finish();
                }
            }
        });

        AlertDialog dlgSave = builderSave.create();
        dlgSave.show();

        return true;
    }

    private boolean saveNoteFile(String strFileName) {
        try {
            // Save NoteDoc
            mSpenNoteDoc.save(strFileName, false);
            Toast.makeText(mContext, "Save success to " + strFileName, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(mContext, "Cannot save NoteDoc file.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void callGalleryForInputImage(int nRequestCode) {
        // Get an image from Gallery.
        try {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, nRequestCode);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, "Cannot find gallery.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    private void captureSpenSurfaceView(String strFileName) {

        // Capture the view
        Bitmap imgBitmap = mSpenSurfaceView.captureCurrentView(true);
        if (imgBitmap == null) {
            Toast.makeText(mContext, "Capture failed." + strFileName, Toast.LENGTH_SHORT).show();
            return;
        }

        OutputStream out = null;
        try {
            // Create FileOutputStream and save the captured image.
            out = new FileOutputStream(strFileName);
            imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            // Save the note information.
            mSpenNoteDoc.save(out, false);
            out.close();
            Toast.makeText(mContext, "Captured images were stored in the file" + strFileName, Toast.LENGTH_SHORT)
                    .show();
        } catch (IOException e) {
            File tmpFile = new File(strFileName);
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
            Toast.makeText(mContext, "Failed to save the file.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            File tmpFile = new File(strFileName);
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
            Toast.makeText(mContext, "Failed to save the file.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        imgBitmap.recycle();
    }
    private static final int PEMISSION_REQUEST_CODE = 1;
    public boolean checkPermission() {
        if (SDK_VERSION < 23) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(mContext, "Cannot find the image", Toast.LENGTH_SHORT).show();
                return;
            }

            // Process background image request
            if (requestCode == REQUEST_CODE_SELECT_IMAGE_BACKGROUND) {
                // Get the image's URI and set the file path to the background image.
                Uri imageFileUri = data.getData();
                String imagePath = SDKUtils.getRealPathFromURI(mContext, imageFileUri);
                mSpenPageDoc.setBackgroundImage(imagePath);
                mSpenSurfaceView.update();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSpenNoteDoc != null && mSpenPageDoc.isRecording()) {
            mSpenPageDoc.stopRecord();
        }

        if (mPenSettingView != null) {
            mPenSettingView.close();
        }
        if (mEraserSettingView != null) {
            mEraserSettingView.close();
        }
        if (mSpenSurfaceView != null) {
            if (mSpenSurfaceView.getReplayState() == SpenSurfaceView.REPLAY_STATE_PLAYING) {
                mSpenSurfaceView.stopReplay();
            }
            mSpenSurfaceView.close();
            mSpenSurfaceView = null;
        }

        if (mSpenNoteDoc != null) {
            try {
                mSpenNoteDoc.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSpenNoteDoc = null;
        }
    };

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasReadStorePermission() {
        if (SDK_VERSION < 23) {
            return true;
        }
        return PackageManager.PERMISSION_GRANTED == checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
    }
    @TargetApi(Build.VERSION_CODES.M)
    public void requestReadStorePermission() {
        if (SDK_VERSION < 23) {
            return;
        }
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ADD_BACKGROUND_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ADD_BACKGROUND_REQUEST) {
            if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mBgImgBtnClickListener.onClick(mBgImgBtn);
            }
        }
    }

}