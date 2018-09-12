package com.psplog.dailycalli.dialog;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.psplog.dailycalli.R;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pen.Spen;
import com.samsung.android.sdk.pen.SpenSettingEraserInfo;
import com.samsung.android.sdk.pen.SpenSettingPenInfo;
import com.samsung.android.sdk.pen.document.SpenInvalidPasswordException;
import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.document.SpenObjectStroke;
import com.samsung.android.sdk.pen.document.SpenObjectTextBox;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.document.SpenUnsupportedTypeException;
import com.samsung.android.sdk.pen.document.SpenUnsupportedVersionException;
import com.samsung.android.sdk.pen.engine.SpenColorPickerListener;
import com.samsung.android.sdk.pen.engine.SpenReplayListener;
import com.samsung.android.sdk.pen.engine.SpenSurfaceView;
import com.samsung.android.sdk.pen.engine.SpenTouchListener;
import com.samsung.android.sdk.pen.settingui.SpenSettingEraserLayout;
import com.samsung.android.sdk.pen.settingui.SpenSettingPenLayout;

import java.io.IOException;

public class PreViewDialog extends Dialog {
    Context content;
    public PreViewDialog(Context context, Activity activity,String filepath) {
        super(context);
        this.content=context;
        this.activity=activity;
        this.filepath=filepath;
    }


    public static final int SDK_VERSION = Build.VERSION.SDK_INT;
    private static final int ADD_BACKGROUND_REQUEST = 1;
    private final int REQUEST_CODE_SELECT_IMAGE_BACKGROUND = 100;

    private Context mContext;
    private Activity activity;
    private SpenNoteDoc mSpenNoteDoc;
    private SpenPageDoc mSpenPageDoc;
    private SpenSurfaceView mSpenSurfaceView;
    private SpenSettingPenLayout mPenSettingView;
    private SpenSettingEraserLayout mEraserSettingView;

    private ImageView mPenBtn;
    private ImageView mEraserBtn;
    private ImageView mUndoBtn;
    private ImageView mRedoBtn;
    private ImageView mBgImgBtn;
    private ImageView mPlayBtn;
    private Button exitBtn;

    private Rect rect;
    private int mToolType = SpenSurfaceView.TOOL_SPEN;
    private String filepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_dialog);
        mContext = content;
        // Initialize Spen
        boolean isSpenFeatureEnabled = false;
        Spen spenPackage = new Spen();
        try {
            spenPackage.initialize(content);
            isSpenFeatureEnabled = spenPackage.isFeatureEnabled(Spen.DEVICE_PEN);
        } catch (SsdkUnsupportedException e) {
            if (com.samsung.android.sdk.pen.pg.tool.SDKUtils.processUnsupportedException(activity, e) == true) {
                return;
            }
        } catch (Exception e1) {
            Toast.makeText(mContext, "Cannot initialize Spen.", Toast.LENGTH_SHORT).show();
            e1.printStackTrace();
        }

        FrameLayout spenViewContainer = (FrameLayout) findViewById(R.id.spenViewContainer);
        RelativeLayout spenViewLayout = (RelativeLayout) findViewById(R.id.spenViewLayout);

        // Create PenSettingView
        mPenSettingView = new SpenSettingPenLayout(content, "", spenViewLayout);

        // Create EraserSettingView
        mEraserSettingView = new SpenSettingEraserLayout(content, "", spenViewLayout);

        spenViewContainer.addView(mPenSettingView);
        spenViewContainer.addView(mEraserSettingView);

        // Create SpenSurfaceView
        mSpenSurfaceView = new SpenSurfaceView(mContext);
        if (mSpenSurfaceView == null) {
            Toast.makeText(mContext, "Cannot create new SpenSurfaceView.", Toast.LENGTH_SHORT).show();
            this.dismiss();
        }
        mSpenSurfaceView.setToolTipEnabled(true);
        spenViewLayout.addView(mSpenSurfaceView);
        mPenSettingView.setCanvasView(mSpenSurfaceView);
        mEraserSettingView.setCanvasView(mSpenSurfaceView);

        Display display = activity.getWindowManager().getDefaultDisplay();
        rect = new Rect();
        display.getRectSize(rect);
        // Create SpenNoteDoc
        try {
            mSpenNoteDoc = new SpenNoteDoc(mContext, rect.height(),  rect.height());
        } catch (IOException e) {
            Toast.makeText(mContext, "Cannot create new NoteDoc", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            this.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            this.dismiss();
        }
        // Add a Page to NoteDoc, get an instance, and set it to the member variable.
        mSpenPageDoc = mSpenNoteDoc.appendPage();
        mSpenPageDoc.setBackgroundColor(0xFFD6E6F5);
        mSpenPageDoc.clearHistory();
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

        exitBtn = findViewById(R.id.bt_preview_exit);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        selectButton(mPenBtn);

        mSpenSurfaceView.setBlankColor(0xacb8dc);
        mSpenPageDoc.startRecord();

        if (isSpenFeatureEnabled == false) {
            mToolType = SpenSurfaceView.TOOL_FINGER;
            Toast.makeText(mContext, "Device does not support Spen. \n You can draw stroke by finger.",
                    Toast.LENGTH_SHORT).show();
        } else {
            mToolType = SpenSurfaceView.TOOL_SPEN;
        }
        mSpenSurfaceView.setToolTypeAction(mToolType, SpenSurfaceView.ACTION_STROKE);
        startLearn(filepath);
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

            mSpenPageDoc.setHistoryListener(mHistoryListener);
            mSpenSurfaceView.setPageDoc(mSpenPageDoc, true);
            mSpenSurfaceView.setReplaySpeed(2);
            mSpenSurfaceView.update();
            mSpenSurfaceView.startReplay();
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

    private SpenTouchListener onPreTouchSurfaceViewListener = new SpenTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // TODO Auto-generated method stub
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    enableButton(false);
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    enableButton(true);
                    break;
            }
            return false;
        }
    };

    private final View.OnClickListener mPenBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // When Spen is in stroke (pen) mode
            if (mSpenSurfaceView.getToolTypeAction(mToolType) == SpenSurfaceView.ACTION_STROKE) {
                // If PenSettingView is open, close it.
                if (mPenSettingView.isShown()) {
                    mPenSettingView.setVisibility(View.GONE);
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

    private final View.OnClickListener mEraserBtnClickListener = new View.OnClickListener() {
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

    private final View.OnClickListener mBgImgBtnClickListener = new View.OnClickListener() {
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

    private final View.OnClickListener mPlayBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            closeSettingView();
            setBtnEnabled(false);
            if (mSpenSurfaceView.getHeight() < mSpenSurfaceView.getCanvasHeight() * mSpenSurfaceView.getZoomRatio()) {
                float mRatio = (float) mSpenSurfaceView.getHeight() / (float) mSpenSurfaceView.getCanvasHeight();
                mSpenSurfaceView.setZoom(0, 0, mRatio);
            }
            mSpenSurfaceView.cancelStroke();
            mSpenSurfaceView.startReplay();
        }
    };

    private final View.OnClickListener undoNredoBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mSpenPageDoc == null) {
                return;
            }
            // Undo button is clicked
            if (v.equals(mUndoBtn)) {
                if (mSpenPageDoc.isUndoable()) {
                    SpenPageDoc.HistoryUpdateInfo[] userData = mSpenPageDoc.undo();
                    mSpenSurfaceView.updateUndo(userData);
                }
                // Redo button is clicked
            } else if (v.equals(mRedoBtn)) {
                if (mSpenPageDoc.isRedoable()) {
                    SpenPageDoc.HistoryUpdateInfo[] userData = mSpenPageDoc.redo();
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

    private final SpenSettingEraserLayout.EventListener mEraserListener = new SpenSettingEraserLayout.EventListener() {
        @Override
        public void onClearAll() {
            // ClearAll button action routines of EraserSettingView
            mSpenPageDoc.removeAllObject();
            mSpenSurfaceView.update();
        }
    };

    private final SpenPageDoc.HistoryListener mHistoryListener = new SpenPageDoc.HistoryListener() {
        @Override
        public void onCommit(SpenPageDoc page) {
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

    private final SpenReplayListener mReplayListener = new SpenReplayListener() {

        @Override
        public void onProgressChanged(int progress, int id) {
        }

        @Override
        public void onCompleted() {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Enable the button when replay is complete.
                    setBtnEnabled(true);
                    mUndoBtn.setEnabled(mSpenPageDoc.isUndoable());
                    mRedoBtn.setEnabled(mSpenPageDoc.isRedoable());
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

    private void callGalleryForInputImage(int nRequestCode) {
        // Get an image from Gallery.
        try {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            activity.startActivityForResult(galleryIntent, nRequestCode);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, "Cannot find gallery.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    public void dismiss() {
        super.dismiss();
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
    }


    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasReadStorePermission() {
        if (SDK_VERSION < 23) {
            return true;
        }
        return PackageManager.PERMISSION_GRANTED == activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
    }
    @TargetApi(Build.VERSION_CODES.M)
    public void requestReadStorePermission() {
        if (SDK_VERSION < 23) {
            return;
        }
        activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ADD_BACKGROUND_REQUEST);
    }

}