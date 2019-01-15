package com.aojiexun.smartbuilding.serialport_util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.hurray.plugins.serial;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Timer;
import java.util.TimerTask;

import android_serialport_api.SerialPort;

/**
 * @author fanming
 * 串口工具类
 */
public class NewLockerSerialportUtil_2 {

	private static NewLockerSerialportUtil_2 INSTANCE;

	private static final String TAG = "SerialPort";
    protected OutputStream mOutputStreamBox;


    private NewLockerSerialportUtil.onReadCardListener sportInterface;
    protected SerialPort boxPort;
    private static Context mContext;
    private String path;
    private  int baudrate;

    protected SerialPort mSerialPort = null;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    public final  static int Msg_ReadData = 0x01;
    String RcvDisplayBuff = new String("");
    int buffLen = 0;

    private final  static int CLEAN_FLAG = 1;

    private serial pSerialport = new serial();
//    private String   arg = "/dev/ttyS1,9600,N,1,8";
//    private String   arg = "/dev/ttyS4,115200,N,1,8";
    private int  iRead = 0;
    private Thread pthread =null;
    static String strTemp = "";
    private Timer timer;
    private TimerTask timerTask;

    private NewLockerSerialportUtil_2(String path, int baudrate, NewLockerSerialportUtil.onReadCardListener lockerPortInterface){
    	this.path = path;
        this.baudrate = baudrate;
        this.sportInterface = lockerPortInterface;
    	initSerialPort();
    }

    public static void init(Context context, String path, int baudrate, NewLockerSerialportUtil.onReadCardListener lockerPortInterface){
    	mContext = context.getApplicationContext();
    	INSTANCE = new NewLockerSerialportUtil_2(path, baudrate, lockerPortInterface);
    }

    public static NewLockerSerialportUtil_2 getInstance(){
		return INSTANCE;
    }

    public SerialPort getboxPort() throws SecurityException, IOException, InvalidParameterException {
        return boxPort;
    }

    /**
     * 初始化串口
     */
    private void initSerialPort(){
        try {
//			mSerialPort = new SerialPort(new File("/dev/gm8123_serial1"),9600,0);
            mSerialPort = new SerialPort(new File(path),baudrate,0);
            Log.d("TAg","serialpors:初始化成功");
        } catch (SecurityException e) {
            Log.d("TAg","serialpors:"+e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("TAg","serialpors:"+e.getMessage());
            e.printStackTrace();
        }
        mOutputStream =  mSerialPort.getOutputStream();
        mInputStream = mSerialPort.getInputStream();
        mReadThread = new ReadThread();
        mReadThread.start();

//        handler.sendEmptyMessageDelayed(CLEAN_FLAG,2000);

    }




    public void log(String str){
        System.out.println("[output] "+str);
        Log.v("info", str);
    }


    private class ReadThread extends Thread {
        byte[] buf = new byte[36];
        Message msg;
        private int sum = 0;
        private boolean isClean = true;

        @Override
        public void run() {
            super.run();
            int len = 0;
            String  tem, temp2;
            while (true) {
                if (mInputStream == null)
                    return;
                try {
                    len = mInputStream.read(buf);
                    if(len > 0){
//                        Log.i("TAg", "len" + len);
                        sum += len;
                        tem = byte2HexString(buf, len);
                        strTemp+=tem;
                        Log.d("TAgSerial",strTemp+"---"+sum);
                        if(sum == 18 ||strTemp.length() == 36){
                               msg = handler.obtainMessage();
                               msg.what = Msg_ReadData;
                               msg.obj = strTemp;
                               handler.sendMessage(msg);
                               if(sportInterface != null ){
                                       sportInterface.dealSwingCard(strTemp);
                               }
                            strTemp = "";
                            sum = 0;
                        }else if(sum >18){
                            strTemp = "";
                            sum = 0;
                        }
                    }
                    //定时清理串口数据
                    if(sum > 0 &&  sum< 18 && !TextUtils.isEmpty(strTemp) && isClean){
                        isClean = false;
                        if (timer==null){
                            timer = new Timer();
                            timerTask = new TimerTask() {
                                @Override
                                public void run() {
                                    Log.d("TAgSerial","清掉数据");
                                    strTemp = "";
                                    sum = 0;
                                    isClean = true;
                                }
                            };
                        }

                        timer.schedule(timerTask,1000);
                    }
                    //+
                } catch (Exception e) {
                    e.printStackTrace();
                    strTemp = "";
                    sum = 0;
                }
            }
        }
    }

    /** byte[]转换成字符串
     */
    public static String byte2HexString(byte[] b, int len)
    {
        StringBuffer sb = new StringBuffer();
        int length = b.length;
        for (int i = 0; i < len; i++) {
            String stmp = Integer.toHexString(b[i]&0xff);
            if(stmp.length() == 1)
                sb.append("0"+stmp);
            else
                sb.append(stmp);
        }
        return sb.toString();
    }


    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Msg_ReadData) {
                String info = (String) msg.obj;
                if(!"".equals(info))
                    Log.d("TAg","serialpors:"+info);
            }
            if(msg.what == CLEAN_FLAG){
                handler.removeMessages(CLEAN_FLAG);
                strTemp = "";
                handler.sendEmptyMessageDelayed(CLEAN_FLAG,2000);
            }
        }
    };

}

