package com.aojiexun.smartbuilding.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import com.aojiexun.smartbuilding.BaseApplication;
import com.aojiexun.smartbuilding.R;
import com.aojiexun.smartbuilding.constant.Constant;
import com.aojiexun.smartbuilding.gpiotest.Gpio;
import com.aojiexun.smartbuilding.network.BaseEntity;
import com.aojiexun.smartbuilding.network.BaseObserver;
import com.aojiexun.smartbuilding.network.HttpConfig;
import com.aojiexun.smartbuilding.network.IOMainThread;
import com.aojiexun.smartbuilding.network.RetrofitFactory;
import com.aojiexun.smartbuilding.request.RequsetProjectId;
import com.aojiexun.smartbuilding.response.ProjectID;
import com.aojiexun.smartbuilding.utils.ReservoirUtils;
import com.aojiexun.smartbuilding.utils.Utils;
import com.aojiexun.smartbuilding.view.ExitAlertDialog;


import com.orhanobut.logger.Logger;

/**
 * Created by Administrator on 2017/8/16.
 */
public class WelcomeActivity extends Activity {
    ExitAlertDialog exitAlertDialog;
    BaseApplication baseApplication;
    ConnectivityManager connectivityManager;
    SharedPreferences userInfo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏以及状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效,需要去掉标题**/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        baseApplication=(BaseApplication)getApplication();
        exitAlertDialog=new ExitAlertDialog(this);
        exitAlertDialog.setCanceledOnTouchOutside(false);
        exitAlertDialog.setCancelable(false);
        TextView textView2 = (TextView) findViewById(R.id.textView2);
        connectivityManager =(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);//获取当前网络的连接服务
        RequsetProjectId requsetProjectId = new RequsetProjectId();
        requsetProjectId.setMac_address(Utils.getMac().trim());
        textView2.setText("设备ID："+Utils.getMac().trim());
        Log.e("onCreate: ", Utils.getMac().trim());
        HttpConfig.MAC = Utils.getMac().trim();
        userInfo = getSharedPreferences("user_info", 0);
        String password = userInfo.getString("password", "");
        if(TextUtils.isEmpty(password)){
            userInfo.edit().putString("password", "666666").commit();
        }


//        try {
//
//            Gpio.gpioInt("1067");
//
//            Thread.sleep(400);
//
//            Gpio.set("1067", 48);
//
//        } catch (InterruptedException e) {
//
//            e.printStackTrace();
//
//        }
//
//        Gpio.set("1067", 49);
        if(userInfo.getBoolean("isFirst",false)){
            HttpConfig.id= userInfo.getInt("projectId",0);
            HttpConfig.derication= userInfo.getString("derication","");
            getHome();
        }else {
            RetrofitFactory.getInstence().API().getProjectId(requsetProjectId).compose(IOMainThread.composeIO2main()).subscribe(new BaseObserver<ProjectID>() {

                @Override
                protected void onSuccees(BaseEntity<ProjectID> t) {
                    ProjectID data = t.getData();
                    userInfo.edit().putBoolean("isFirst",true).commit();
                    userInfo.edit().putInt("projectId",data.getId()).commit();
                    userInfo.edit().putString("derication",data.getLocation()).commit();
                    HttpConfig.id= data.getId();
                    HttpConfig.derication= data.getLocation();
                    getHome();
                }

                @Override
                protected void onCodeError(String msg, int codeErrorr) {
                    Log.e("onCodeError: ", msg);
                    textView2.setVisibility(View.VISIBLE);

                }

                @Override
                protected void onFailure(Throwable e, boolean isNetWorkError) {
                    Log.e("onCodeError: ", e.getMessage());
                    textView2.setVisibility(View.VISIBLE);
                }
            });
        }



    }
    public void getHome(){
        Logger.e("WelcomeActivity"+"=====getHome====");
        Intent intent = new Intent(WelcomeActivity.this, LockActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }



}
