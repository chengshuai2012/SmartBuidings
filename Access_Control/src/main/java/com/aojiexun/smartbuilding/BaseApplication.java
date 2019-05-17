
package com.aojiexun.smartbuilding;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.aojiexun.smartbuilding.activity.WelcomeActivity;
import com.aojiexun.smartbuilding.component.TimeService;
import com.aojiexun.smartbuilding.utils.FaceDB;
import com.aojiexun.smartbuilding.utils.Utils;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;


import com.orhanobut.logger.Logger;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.TimeZone;
import io.realm.Realm;
import io.realm.RealmConfiguration;
/**
 * Description：BaseApplication
 * Created by Shaozy on 2016/8/10.
 */
public class BaseApplication extends MultiDexApplication  {
    private static BaseApplication ourInstance = new BaseApplication();
    public boolean log = true;
    private static Context context;
    private static final String TAG = "BaseApplication";
    public static BaseApplication getInstance() {
        return ourInstance;
    }
    String deviceTargetValue;
    public static BaseApplication instances;
    public FaceDB mFaceDB;
    public static final String COUNT_CHANGE = "change_count";
    ConnectivityManager connectivityManager;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static BaseApplication getInstances() {
        return instances;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instances = this;
        ourInstance = this;
        mFaceDB = new FaceDB(Environment.getExternalStorageDirectory().getAbsolutePath() + "/faceFile");
         context=getApplicationContext();
        Thread.setDefaultUncaughtExceptionHandler(restartHandler);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
            }
            @Override
            public void onActivityStarted(Activity activity) {
                count++;
                Log.e("onActivityStarted: ",count+"" );
                Intent countIntent = new Intent(COUNT_CHANGE);
                countIntent.putExtra("count",count);
                sendBroadcast(countIntent);
            }
            @Override
            public void onActivityResumed(Activity activity) {
                TestActivityManager.getInstance().setCurrentActivity(activity);
            }
            @Override
            public void onActivityPaused(Activity activity) {
            }
            @Override
            public void onActivityStopped(Activity activity) {
                count--;
                Intent countIntent = new Intent(COUNT_CHANGE);
                countIntent.putExtra("count",count);
                sendBroadcast(countIntent);
            }
            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            }
            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
        Realm.init(this);
        //自定义配置
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("myRealm.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);
        Intent intent = new Intent(getApplicationContext(), TimeService.class);
        startService(intent);
        ifspeaking();
        initCloudChannel(this);
        handler.sendEmptyMessage(1);
    }
    public static final String ACTION_UPDATEUI = "com.link.cloud.updateTiem";
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler.removeCallbacksAndMessages(null);
            switch (msg.what){
                case 1:
                    handler.removeMessages(1);
                    if(time==null){
                        time=new Intent();
                    }
                    time.setAction(ACTION_UPDATEUI);
                    time.putExtra("timethisStr",getthisTime());
                    time.putExtra("timeStr",getTime());
                    time.putExtra("timeData",getData());
                    sendBroadcast(time);
                    handler.sendEmptyMessageDelayed(1,1000);
                    break;
            }

        }
    };
    public String getthisTime(){
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int mtime=c.get(Calendar.HOUR_OF_DAY);
        int mHour = c.get(Calendar.HOUR);//时
        int mMinute = c.get(Calendar.MINUTE);//分
        int seconds=c.get(Calendar.SECOND);
        return checknum(mtime)+":"+checknum(mMinute)+":"+checknum(seconds);
    }
    Intent time;
    //获得当前年月日时分秒星期
    public String getData(){
        String timeStr=null;
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        int mtime=c.get(Calendar.HOUR_OF_DAY);
        int mHour = c.get(Calendar.HOUR);//时
        int mMinute = c.get(Calendar.MINUTE);//分
        int seconds=c.get(Calendar.SECOND);
        if (mtime>=0&&mtime<=5){
            timeStr="凌晨";
        }else if (mtime>5&&mtime<8){
            timeStr="早晨";
        }else if(mtime>8&&mtime<12){
            timeStr="上午";
        }else if(mtime>=12&&mtime<14){
            timeStr="中午";
        }else if(mtime>=14&&mtime<18){
            timeStr="下午";
        }else if(mtime>=18&&mtime<19){
            timeStr="傍晚";
        }else if(mtime>=19&&mtime<=22){
            timeStr="晚上";
        }else if(mtime>22){
            timeStr="深夜";
        }
        if("1".equals(mWay)){
            mWay ="天";
        }else if("2".equals(mWay)){
            mWay ="一";
        }else if("3".equals(mWay)){
            mWay ="二";
        }else if("4".equals(mWay)){
            mWay ="三";
        }else if("5".equals(mWay)){
            mWay ="四";
        }else if("6".equals(mWay)){
            mWay ="五";
        }else if("7".equals(mWay)){
            mWay ="六";
        }
        return mMonth + "月" + mDay+"日"+"|"+"星期"+mWay;
    }
    public String getTime(){
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int mtime=c.get(Calendar.HOUR_OF_DAY);
        int mHour = c.get(Calendar.HOUR);//时
        int mMinute = c.get(Calendar.MINUTE);//分
        int seconds=c.get(Calendar.SECOND);
        return checknum(mHour)+":"+checknum(mMinute);
    }
    private String checknum(int num){
        String strnum=null;
        if (num<10){
            strnum="0"+num;
        }else {
            strnum=num+"";
        }
        return strnum;
    }
    public int count =0;
    private Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            Throwable cause = ex.getCause();
            StringBuilder builder = new StringBuilder();
            builder.append(ex.getCause().toString()+"\r\n");
            for(int x=0;x<cause.getStackTrace().length;x++){
                builder.append("FileName:"+cause.getStackTrace()[x].getFileName()+">>>>Method:"+cause.getStackTrace()[x].getMethodName()+">>>>FileLine:"+cause.getStackTrace()[x].getLineNumber()+"\r\n");
            }

            Logger.e(builder.toString());
            restartApp();
        }
    };
    public void restartApp() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
    }

    void ifspeaking(){
        StringBuffer param = new StringBuffer();
        param.append("appid="+getString(R.string.app_id));
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE+"="+ SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(BaseApplication.this, param.toString());
    }

    public static Context getContext() {
        return context;
    }


    /**
     * 初始化云推送通道
     * @param applicationContext
     */
    private void initCloudChannel(final Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        String id =pushService.getDeviceId();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        deviceTargetValue = Utils.getMac();
                        if(deviceTargetValue!=null){
                            deviceTargetValue= deviceTargetValue.trim();
                        }
                        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
                        pushService.bindAccount(deviceTargetValue, new CommonCallback() {
                            @Override
                            public void onSuccess(String s) {
                                connectivityManager =(ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);//获取当前网络的连接服务
                                NetworkInfo info =connectivityManager.getActiveNetworkInfo(); //获取活动的网络连接信息
                                if (info != null) {   //当前没有已激活的网络连接（表示用户关闭了数据流量服务，也没有开启WiFi等别的数据服务）
                                }else {
                                    Toast.makeText(getContext(), getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
                                }
                            }
                            @Override
                            public void onFailed(String s, String s1) {
                            }
                        });
                    }
                }).start();
            }
            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.e(TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });
    }



}
