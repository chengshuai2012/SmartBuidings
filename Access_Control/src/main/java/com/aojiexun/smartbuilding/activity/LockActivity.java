package com.aojiexun.smartbuilding.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.usb.UsbDeviceConnection;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aojiexun.smartbuilding.BaseApplication;
import com.aojiexun.smartbuilding.R;
import com.aojiexun.smartbuilding.component.MyMessageReceiver;
import com.aojiexun.smartbuilding.controller.FaceInController;
import com.aojiexun.smartbuilding.gpiotest.Gpio;
import com.aojiexun.smartbuilding.request.NotSuccess;
import com.aojiexun.smartbuilding.response.AllFace;
import com.aojiexun.smartbuilding.response.CardIDBean;
import com.aojiexun.smartbuilding.rkgpio.GPIOEnum;
import com.aojiexun.smartbuilding.serialport_util.NewLockerSerialportUtil;
import com.aojiexun.smartbuilding.setting.TtsSettings;
import com.aojiexun.smartbuilding.utils.APKVersionCodeUtils;
import com.aojiexun.smartbuilding.utils.FaceDB;
import com.aojiexun.smartbuilding.view.ExitAlertDialog;
import com.arcsoft.ageestimation.ASAE_FSDKEngine;
import com.arcsoft.ageestimation.ASAE_FSDKError;
import com.arcsoft.ageestimation.ASAE_FSDKVersion;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKVersion;
import com.arcsoft.genderestimation.ASGE_FSDKEngine;
import com.arcsoft.genderestimation.ASGE_FSDKError;
import com.arcsoft.genderestimation.ASGE_FSDKVersion;
import com.friendlyarm.FriendlyThings.HardwareControler;
import com.guo.android_extend.java.AbsLoop;
import com.guo.android_extend.java.ExtByteArrayOutputStream;
import com.guo.android_extend.tools.CameraHelper;
import com.guo.android_extend.widget.CameraFrameData;
import com.guo.android_extend.widget.CameraGLSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;


import com.orhanobut.logger.Logger;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.aojiexun.smartbuilding.activity.BindFaceActivity.imageToBase64;

/**
 * Created by 30541 on 2018/3/12.
 */
public class LockActivity extends BaseAppCompatActivity implements CameraSurfaceView.OnCameraListener, View.OnTouchListener, Camera.AutoFocusCallback, NewLockerSerialportUtil.onReadCardListener, FaceInController.FaceInControllerListener {
    public static final String ACTION_UPDATEUI = "com.link.cloud.dataTime";
    private static final String TAG = "LockActivity";
    private final static int MSG_SHOW_LOG = 0;
    // 默认本地发音人
    public static String voicerLocal = "xiaoyan";
    private static String ACTION_USB_PERMISSION = "com.android.USB_PERMISSION";
    private final int MSG_REFRESH_LIST = 0;
    public MesReceiver mesReceiver;
    // 语音合成对象
    public SpeechSynthesizer mTts;
    /**
     * 合成回调监听。
     */
    public SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
        }

        @Override
        public void onSpeakPaused() {
        }

        @Override
        public void onSpeakResumed() {
        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {
        }

        @Override
        public void onCompleted(SpeechError speechError) {
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
        }
    };
    @Bind(R.id.head_text_01)
    TextView head_text_01;
    @Bind(R.id.head_text_02)
    TextView head_text_02;
    @Bind(R.id.head_text_03)
    TextView head_text_03;
    @Bind(R.id.versionName)
    TextView versionName;
    String deviceId;
    BaseApplication baseApplication;
    String gpiostr;
    String userUid;
    SharedPreferences userinfo;
    String gpiotext = "";
    ExitAlertDialog exitAlertDialog;
    boolean isWorkFinish = false;
    EditText code_mumber;
    String pwdmodel = "1";
    String cardFrid;
    //认证一个手指模板,当比对成功且得分大于自定义认证阈值时返回true，否则返回false;
    LinearLayout setting_ll;
    ConnectivityManager connectivityManager;
    boolean isRequest = false;
    AFT_FSDKVersion version = new AFT_FSDKVersion();
    AFT_FSDKEngine engine = new AFT_FSDKEngine();
    ASAE_FSDKVersion mAgeVersion = new ASAE_FSDKVersion();
    ASAE_FSDKEngine mAgeEngine = new ASAE_FSDKEngine();
    ASGE_FSDKVersion mGenderVersion = new ASGE_FSDKVersion();
    ASGE_FSDKEngine mGenderEngine = new ASGE_FSDKEngine();
    List<AFT_FSDKFace> result = new ArrayList<>();
    int mCameraID;
    int mCameraRotate;
    boolean mCameraMirror;
    byte[] mImageNV21 = null;
    FRAbsLoop mFRAbsLoop = null;
    AFT_FSDKFace mAFT_FSDKFace = null;
    String idCard, timeCreate, image;
    int deraction = 0;
    int recindex = 0;
    byte[] clone = null;
    private UsbDeviceConnection usbDevConn;
    // 本地发音人列表
    private String[] localVoicersEntries;
    private String[] localVoicersValue;
    // 云端/本地选择按钮
    private RadioGroup mRadioGroup;
    // 串口名称
    private String PATH = "/dev/ttysWK0";
    // 波特率
    private int BAUDRATE = 57600;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private Toast mToast;
    private SharedPreferences mSharedPreferences;
    private Realm realm;
    private String card = "";
    private MyMessageRevicer myMessageRevicer;
    private FaceInController faceInController;
    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip(getResources().getString(R.string.mTts_stating_error) + code);
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };
    private CameraSurfaceView mSurfaceView;
    private CameraGLSurfaceView mGLSurfaceView;

    public void save(){
        NotSuccess notSuccess = new NotSuccess();
        notSuccess.setIdCard(idCard);
        notSuccess.setImage(image);
        notSuccess.setTimeCreate(timeCreate);
        notSuccess.setCard(card);
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(notSuccess);
            }
        });
        realm.close();
    }
    private Camera mCamera;
    private int mWidth, mHeight, mFormat;
    private long firstTime = 0;
    private long thridTime = 0;
    Handler workHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 101:
                    long secondTime = System.currentTimeMillis();
                    if (secondTime - thridTime > 3000) {
                        thridTime = secondTime;
                        mTts.startSpeaking(getResources().getString(R.string.successful_open), mTtsListener);
                    }
                    SharedPreferences sharedPreferences = getSharedPreferences("user_info", 0);
//                    gpiostr = sharedPreferences.getString("gpiotext", "");
//                    Logger.e("LockAcitvity" + "===========" + gpiostr);
//                    try {
//                        Gpio.gpioInt(gpiostr);
//                        Thread.sleep(400);
//                        Gpio.set(gpiostr, 48);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    Gpio.set(gpiostr, 49);
//                   // DoolLockUtil.Instance().openDoorDelay(5 * 1000);
                    Log.e(TAG, "handleMessage: "+"" );
                    HardwareControler.setGPIOValue(33,GPIOEnum.HIGH);
                    Gpio.set(gpiostr, 49);
                    break;


            }
        }
    };
    private long time = 0;
    DataOutputStream dos;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        baseApplication = (BaseApplication) getApplication();
        // 初始化合成对象

        Log.e(TAG, Camera.getNumberOfCameras() + ">>>>>>>>>>>>>>>>");
        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
        mSharedPreferences = getSharedPreferences(TtsSettings.PREFER_NAME, Activity.MODE_PRIVATE);
        exitAlertDialog = new ExitAlertDialog(this);
        exitAlertDialog.setCanceledOnTouchOutside(false);
        exitAlertDialog.setCancelable(false);
        setParam();
        time = 1000L * 60L * 60L * 24L * 30L;
        PackageInfo pi = null;
        HardwareControler.setGPIOValue(33,GPIOEnum.LOW);
        try {
            pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName.setText(pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mEngineType = SpeechConstant.TYPE_LOCAL;
        mTts.startSpeaking("初始化成功", mTtsListener);
        realm = Realm.getDefaultInstance();
        faceInController = new FaceInController(this);
        faceInController.getRfid("");
        faceInController.getAllFace();

    }

    /**
     * 参数设置
     *
     * @return
     */
    public void setParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        //设置使用本地引擎
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        //设置发音人资源路径
        mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
        //设置发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, voicerLocal);
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "50"));
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"));
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "50"));
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"));

        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }

    //获取发音人资源路径
    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "tts/" + LockActivity.voicerLocal + ".jet"));
        return tempBuffer.toString();
    }

    public void showTip(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
    }

    @Override
    protected void initData() {
        TextView textView = findView(R.id.versionName);
        textView.setText(APKVersionCodeUtils.getVerName(this));
//        code_mumber = (EditText) findViewById(R.id.code_mumber1);
//        code_mumber.setFocusable(true);
//        code_mumber.setCursorVisible(true);
//        code_mumber.setFocusableInTouchMode(true);
//        code_mumber.requestFocus();
    }

    @Override
    public void onMainErrorCode(String msg, int errorCode) {
        cardFrid = null;
        card = null;
        mTts.startSpeaking(msg,mTtsListener);
    }

    @Override
    public void onMainFail(Throwable e, boolean isNetWork) {
        cardFrid = null;
        card = null;
        if(isNetWork){
            mTts.startSpeaking("网络异常",mTtsListener);
        }else {
            mTts.startSpeaking("解析异常",mTtsListener);
        }
    }

    @Override
    public void FridSuccess(ArrayList<CardIDBean> fridBean) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CardIDBean> all = realm.where(CardIDBean.class).findAll();
        if (fridBean.size() == 1) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealm(fridBean);
                }
            });
        } else {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    all.deleteAllFromRealm();
                    realm.copyToRealm(fridBean);
                }
            });
        }

        realm.close();
    }
    ArrayList<AllFace> allFaceList = new ArrayList<>();
    @Override
    public void getAllFaceSuccess(ArrayList<AllFace> allFaces) {
        Log.e(TAG, "getAllFaceSuccess: "+allFaces.size());
        allFaceList.addAll(allFaces);
        //int size = ((BaseApplication) getApplicationContext().getApplicationContext()).mFaceDB.mFaceList.size();
       // Log.e(TAG, "getAllFaceSuccess: "+size);
//        if(allFaces.size()==size){
//
//        }else if(allFaces.size()>size){
//            Count=size;
//            faceInController.downloadFile(allFaces.get(Count).getId_card(), allFaces.get(Count).getFace_url());
//        }
        allFaceList.addAll(allFaces);
        if(allFaces.size()>0){
            faceInController.downloadFile(allFaces.get(0).getId_card(), allFaces.get(0).getFace_url());
        }




    }

    @Override
    public void reportSuccess(CardIDBean cardIDBean) {
        cardFrid = null;
        card = null;
        Realm realm  = Realm.getDefaultInstance();
        if(isReportNotSuccess){
            NotSuccess first = realm.where(NotSuccess.class).findFirst();
            if(first!=null){

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        first.deleteFromRealm();
                    }
                });
            }
        }
        RealmResults<NotSuccess> all = realm.where(NotSuccess.class).findAll();
            if(all.size()>0){
                faceInController.reportNotSuccess(all.get(0).getIdCard(),all.get(0).getImage(),all.get(0).getTimeCreate());
            }
        realm.close();
    }
    Boolean isReportNotSuccess =false;
    @Override
    public void reportNotSuccessSuccess(CardIDBean cardIDBean) {
        Realm realm  = Realm.getDefaultInstance();
        NotSuccess first = realm.where(NotSuccess.class).findFirst();
        isReportNotSuccess =true;
        if(first!=null){

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    first.deleteFromRealm();
                }
            });
        }
        RealmResults<NotSuccess> all = realm.where(NotSuccess.class).findAll();
        if(all.size()>0){
            faceInController.report(all.get(0).getIdCard(),all.get(0).getImage(),all.get(0).getTimeCreate());
        }
        realm.close();
    }

    @Override
    public void onReportError() {
        save();
        cardFrid = null;
        card = null;
    }

    @Override
    public void onReportNotSuccessError() {

    }
    int Count = 0;
    @Override
    public void onComplete() {
        Count++;
            if(Count<allFaceList.size()){
                faceInController.downloadFile(allFaceList.get(Count).getId_card(), allFaceList.get(Count).getFace_url());
            }else {


            }


    }

    @Override
    public void dealSwingCard(String cardNum) {
        android.util.Log.e("dealSwingCard: ", cardNum);
        cardFrid = cardNum;
        try {
            String substring = cardNum.substring(cardNum.length() - 10, cardNum.length() - 4);
            card = Integer.parseInt(substring, 16) + "";
            if (card.length() == 7) {
                card = "0" + card;
            }
            if (card.length() == 6) {
                card = "00" + card;
            }
            if (card.length() == 5) {
                card = "000" + card;
            }
            if (card.length() == 4) {
                card = "0000" + card;
            }
            if (card.length() == 3) {
                card = "00000" + card;
            }
            Log.e(TAG, "dealSwingCard: " + card);
        } catch (Exception e) {

        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mCameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
        //NewLockerSerialportUtil_2.init(this, PATH, BAUDRATE, this);
        mCameraRotate = 0;
        mCameraMirror = false;
        mWidth = 640;
        mHeight = 480;
        mFormat = ImageFormat.NV21;
        SharedPreferences userInfo = getSharedPreferences("user_info", 0);
        String repwd = userInfo.getString("devicepwd", "");
        if (TextUtils.isEmpty(repwd)) {
            userInfo.edit().putString("devicepwd", "666666").commit();
        }
        HardwareControler.setGPIOValue(33,GPIOEnum.LOW);
        mGLSurfaceView = (CameraGLSurfaceView) findViewById(R.id.glsurfaceView);
        mGLSurfaceView.setOnTouchListener(LockActivity.this);
        mSurfaceView = (CameraSurfaceView) findViewById(R.id.surfaceView);
        mSurfaceView.setOnCameraListener(LockActivity.this);
        mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, mCameraMirror, mCameraRotate);
        mSurfaceView.debug_print_fps(true, false);
        AFT_FSDKError err = engine.AFT_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.ft_key, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT, 16, 5);
        Log.d(TAG, "AFT_FSDK_InitialFaceEngine =" + err.getCode());
        err = engine.AFT_FSDK_GetVersion(version);
        Log.d(TAG, "AFT_FSDK_GetVersion:" + version.toString() + "," + err.getCode());

        ASAE_FSDKError error = mAgeEngine.ASAE_FSDK_InitAgeEngine(FaceDB.appid, FaceDB.ag_key);
        Log.d(TAG, "ASAE_FSDK_InitAgeEngine =" + error.getCode());
        error = mAgeEngine.ASAE_FSDK_GetVersion(mAgeVersion);
        Log.d(TAG, "ASAE_FSDK_GetVersion:" + mAgeVersion.toString() + "," + error.getCode());

        ASGE_FSDKError error1 = mGenderEngine.ASGE_FSDK_InitgGenderEngine(FaceDB.appid, FaceDB.sx_key);
        Log.d(TAG, "ASGE_FSDK_InitgGenderEngine =" + error1.getCode());
        error1 = mGenderEngine.ASGE_FSDK_GetVersion(mGenderVersion);
        Log.d(TAG, "ASGE_FSDK_GetVersion:" + mGenderVersion.toString() + "," + error1.getCode());
        ((BaseApplication) getApplicationContext().getApplicationContext()).mFaceDB.loadFaces();
        mFRAbsLoop = new FRAbsLoop();
        mFRAbsLoop.start();
        mesReceiver = new MesReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BaseApplication.ACTION_UPDATEUI);
        registerReceiver(mesReceiver, intentFilter);
        myMessageRevicer = new MyMessageRevicer();
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(MyMessageReceiver.TAG);
        registerReceiver(myMessageRevicer, intentFilter2);
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }


    @Override
    protected void onResume() {
        Logger.e("resume");
        userinfo = getSharedPreferences("user_info", MODE_MULTI_PROCESS);
        String gpio = userinfo.getString("gpiotext", null);
        deviceId = userinfo.getString("deviceId", "");
        if (gpio == null) {
            userinfo.edit().putString("gpiotext", "1067").commit();
        }
        if (isWorkFinish) {
            //  workHandler.sendEmptyMessage(19);
            isWorkFinish = false;
        }
        gpiotext = userinfo.getString(gpiotext, "");
        Gpio.gpioInt(gpiotext);
        Gpio.set(gpiotext, 48);
        super.onResume();


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Logger.e("LockActivity" + "onDestroy");
        // TTSUtils.getInstance().release();
        if (usbDevConn == null) {

        } else {
            usbDevConn.close();
        }
        if (null != mTts) {
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
        realm.close();
        unregisterReceiver(mesReceiver);
        if (Camera.getNumberOfCameras() != 0) {
            mFRAbsLoop.shutdown();
            AFT_FSDKError err = engine.AFT_FSDK_UninitialFaceEngine();
            Log.d(TAG, "AFT_FSDK_UninitialFaceEngine =" + err.getCode());

            ASAE_FSDKError err1 = mAgeEngine.ASAE_FSDK_UninitAgeEngine();
            Log.d(TAG, "ASAE_FSDK_UninitAgeEngine =" + err1.getCode());
            ASGE_FSDKError err2 = mGenderEngine.ASGE_FSDK_UninitGenderEngine();
            Log.d(TAG, "ASGE_FSDK_UninitGenderEngine =" + err2.getCode());

        }
        super.onDestroy();

    }

    @Override
    public Camera setupCamera() {
        // TODO Auto-generated method stub
        if (Camera.getNumberOfCameras() != 0) {
            mCamera = Camera.open(mCameraID);
            try {
                Camera.Parameters parameters = mCamera.getParameters();

                parameters.setPreviewSize(mWidth, mHeight);
                parameters.setPreviewFormat(mFormat);
                mCamera.setDisplayOrientation(90);
                for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
                    Log.d(TAG, "SIZE:" + size.width + "x" + size.height);
                }
                for (Integer format : parameters.getSupportedPreviewFormats()) {
                    Log.d(TAG, "FORMAT:" + format);
                }

                List<int[]> fps = parameters.getSupportedPreviewFpsRange();
                for (int[] count : fps) {
                    Log.d(TAG, "T:");
                    for (int data : count) {
                        Log.d(TAG, "V=" + data);
                    }
                }
                mCamera.setParameters(parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mCamera != null) {
                mWidth = mCamera.getParameters().getPreviewSize().width;
                mHeight = mCamera.getParameters().getPreviewSize().height;
            }
            return mCamera;
        }
        return null;
    }

    @Override
    public void setupChanged(int format, int width, int height) {

    }

    public void saveJpg() {
        ExtByteArrayOutputStream ops = new ExtByteArrayOutputStream();
        YuvImage yuv = new YuvImage(clone, ImageFormat.NV21, 640, 480, null);
        yuv.compressToJpeg(new Rect(0, 0, 640, 480), 85, ops);
        final Bitmap bitmap = BitmapFactory.decodeByteArray(ops.getByteArray(), 0, ops.getByteArray().length);
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/faceIn.jpg");
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsolutePath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileOutputStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(LockActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public boolean startPreviewImmediately() {
        return true;
    }

    @Override
    public Object onPreview(byte[] data, int width, int height, int format, long timestamp) {

        AFT_FSDKError err = engine.AFT_FSDK_FaceFeatureDetect(data, width, height, AFT_FSDKEngine.CP_PAF_NV21, result);
        Log.d(TAG, "AFT_FSDK_FaceFeatureDetect =" + err.getCode());
        Log.d(TAG, "Face=" + result.size());
        for (AFT_FSDKFace face : result) {
            Log.d(TAG, "Face:" + face.toString());
        }
        if (mImageNV21 == null) {
            if (!result.isEmpty()) {
                mAFT_FSDKFace = result.get(0).clone();
                mImageNV21 = data.clone();
                clone = data.clone();
            } else {

            }
        }

        Rect[] rects = new Rect[result.size()];
        for (int i = 0; i < result.size(); i++) {
            rects[i] = new Rect(result.get(i).getRect());
        }
        result.clear();
        return rects;
    }
    @OnClick(R.id.bindface)
    public void onClick(View view){
        startActivity(new Intent(this,BindFaceActivity.class));
    }
    @Override
    public void onBeforeRender(CameraFrameData data) {

    }

    @Override
    public void onAfterRender(CameraFrameData data) {
        mGLSurfaceView.getGLES2Render().draw_rect((Rect[]) data.getParams(), Color.GREEN, 2);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (Camera.getNumberOfCameras() != 0) {
            CameraHelper.touchFocus(mCamera, event, v, this);

        }
        return false;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (success) {
            Log.d(TAG, "Camera Focus SUCCESS!");
        }
    }

    class MyMessageRevicer extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MyMessageReceiver.TAG)) {
                String faceUrl = intent.getStringExtra("FaceUrl");
                String idcard = intent.getStringExtra("idCard");
                faceInController.downloadFile(idcard, faceUrl);
                faceInController.getRfid(idcard);
            }


        }
    }

    /**
     * 广播接收器
     *
     * @author kevin
     */
    public class MesReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BaseApplication.ACTION_UPDATEUI)) {
                head_text_03.setText(intent.getStringExtra("timeStr"));
                head_text_01.setText(intent.getStringExtra("timeData"));
                if (context == null) {
                    context.unregisterReceiver(this);
                }

            }
        }
    }

    class FRAbsLoop extends AbsLoop {
        AFR_FSDKVersion version = new AFR_FSDKVersion();
        AFR_FSDKEngine engine = new AFR_FSDKEngine();
        AFR_FSDKFace result = new AFR_FSDKFace();

        @Override
        public void setup() {
            AFR_FSDKError error = engine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
            Log.d(TAG, "AFR_FSDK_InitialEngine = " + error.getCode());
            error = engine.AFR_FSDK_GetVersion(version);
            Log.d(TAG, "FR=" + version.toString() + "," + error.getCode()); //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
        }

        @Override
        public void loop() {
            if (mImageNV21 != null) {
                AFR_FSDKError error = engine.AFR_FSDK_ExtractFRFeature(mImageNV21, mWidth, mHeight, AFR_FSDKEngine.CP_PAF_NV21, mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree(), result);
                Log.d(TAG, "Face=" + result.getFeatureData()[0] + "," + result.getFeatureData()[1] + "," + result.getFeatureData()[2] + "," + error.getCode());
                AFR_FSDKMatching score = new AFR_FSDKMatching();
                float max = 0.0f;
                String name = null;
                ((BaseApplication) getApplicationContext().getApplicationContext()).mFaceDB.loadFaces();
                Log.e(TAG, "loop: " + ((BaseApplication) getApplicationContext().getApplicationContext()).mFaceDB.mFaceList.size());
                if (((BaseApplication) getApplicationContext().getApplicationContext()).mFaceDB.mFaceList.size() > 0) {
                    //是否识别成功(如果第一次没成功就再次循环验证一次)
                    for (Map.Entry<String, AFR_FSDKFace> entry : ((BaseApplication) getApplicationContext().getApplicationContext()).mFaceDB.mFaceList.entrySet()) {
                        error = engine.AFR_FSDK_FacePairMatching(result, entry.getValue(), score);
                        Log.d(TAG, "Score:" + score.getScore() + ", AFR_FSDK_FacePairMatching=" + error.getCode());
                        if (max < score.getScore()) {
                            max = score.getScore();
                            name = entry.getKey();

                        }
                    }
                    if (max > 0.65f) {
//                        Realm realm = Realm.getDefaultInstance();
//                        CardIDBean id_card = realm.where(CardIDBean.class).equalTo("id_card", name).findFirst();
//                        long secondTime = System.currentTimeMillis();
 //                       if (secondTime - firstTime > 2000) {
//                            if(id_card!=null&&!id_card.getRfid_number().equals(card)){
//                                mTts.startSpeaking("身份证与卡号不匹配", mTtsListener);
//                            }
//                            if(id_card==null){
//                                mTts.startSpeaking("未查询到该人信息", mTtsListener);
//                            }
//                            firstTime=secondTime;
 //                       }

//                        if(id_card!=null&&!id_card.getRfid_number().equals(card)){
//                            mImageNV21 = null;
//                            realm.close();
//                           return;
//                        }
//                        if(id_card==null){
//                            realm.close();
//                            mImageNV21 = null;
//                            return;
//                        }
//                        realm.close();
                        workHandler.sendEmptyMessage(101);
                        long secondTime = System.currentTimeMillis();
                        if (secondTime - firstTime > 2000) {
                            firstTime=secondTime;
                            saveJpg();
                            long createTime = System.currentTimeMillis()+8*60*60*1000;
                            SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Long time1=new Long(createTime);
                            String d = format.format(time1);
                            idCard=name;
                            timeCreate =d;
                            image = imageToBase64(Environment.getExternalStorageDirectory() + "/faceIn.jpg");
                            faceInController.report(name,image,timeCreate);

                        }

                        isReportNotSuccess=false;
                    } else {
                        recindex = recindex + 1;
                        if (recindex == 3) {
                            recindex = 0;
                        }
                    }
                } else {

                }
                mImageNV21 = null;
            }
        }

        @Override
        public void over() {
            AFR_FSDKError error = engine.AFR_FSDK_UninitialEngine();
            Log.d(TAG, "AFR_FSDK_UninitialEngine : " + error.getCode());
        }

    }
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            return true;
//        } else {
//            if(keyCode==20){
//                try {
//                    String code = code_mumber.getText().toString();
//                    long now = System.currentTimeMillis();
//                    code =code.replace("\n","");
//                    long passTime = now - Long.parseLong(code);
//                    Logger.e("1536490732355".equals (code)+"");
//                    Logger.e((passTime< time)+"");
//                    Logger.e(passTime+"");
//                    Logger.e(time+"");
//
//                    if ("1536490729828".equals(code) &&passTime< time) {
//                        Logger.e("LockAcitvity" + "===========" + (now - Long.parseLong(code)));
//                        SharedPreferences sharedPreferences = getSharedPreferences("user_info", 0);
//                        gpiostr = sharedPreferences.getString("gpiotext", "");
//                        Logger.e("LockAcitvity" + "===========" + gpiostr);
//                        try {
//                            Gpio.gpioInt(gpiostr);
//                            Thread.sleep(400);
//                            Gpio.set(gpiostr, 48);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        Gpio.set(gpiostr, 49);
//                        isopenCabinet.memberCode(deviceId, code);
//                        mTts.startSpeaking("验证成功",mTtsListener);
//                    } else if ("1536490732355".equals (code)&&passTime<time ){
//                        Logger.e("LockAcitvity" + "===========" + (now - Long.parseLong(code)));
//                        SharedPreferences sharedPreferences = getSharedPreferences("user_info", 0);
//                        gpiostr = sharedPreferences.getString("gpiotext", "");
//                        Logger.e("LockAcitvity" + "===========" + gpiostr);
//                        try {
//                            Gpio.gpioInt(gpiostr);
//                            Thread.sleep(400);
//                            Gpio.set(gpiostr, 48);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        Gpio.set(gpiostr, 49);
//                        mTts.startSpeaking("验证成功",mTtsListener);
//                    }else {
//                        mTts.startSpeaking("验证失败",mTtsListener);
//                    }
//                } catch (NumberFormatException e) {
//                    e.printStackTrace();
//                    mTts.startSpeaking("验证失败",mTtsListener);
//                }
//                code_mumber.setText("");
//                return true;
//            }
//
//        }
//        return super.onKeyDown(keyCode, event);
//    }


}
