package com.aojiexun.smartbuilding.serialport_util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.aojiexun.smartbuilding.R;
import com.hurray.plugins.serial;



import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;

/**
 * @author fanming
 * 串口工具类
 */
public class NewLockerSerialportUtil {

	private static NewLockerSerialportUtil INSTANCE;

	private static final String TAG = "SerialPort";
    protected OutputStream mOutputStreamBox;


    private onReadCardListener sportInterface;
    protected SerialPort boxPort;
    private static Context mContext;
    private String path;
    private  int baudrate;
    private SerialBroadcastReceiverBox m_Receiver2;


    private serial pSerialport = new serial();
//    private String   arg = "/dev/ttyS1,9600,N,1,8";
    private String   arg = "/dev/ttyS1,115200,N,1,8";
    private int  iRead = 0;
    private Thread pthread =null;

    private NewLockerSerialportUtil(String path, int baudrate, onReadCardListener lockerPortInterface){
    	this.path = path;
        this.baudrate = baudrate;
        this.sportInterface = lockerPortInterface;
    	initSerialPort();
    }

    public static void init(Context context, String path, int baudrate, onReadCardListener lockerPortInterface){
    	mContext = context;
    	INSTANCE = new NewLockerSerialportUtil(path, baudrate, lockerPortInterface);
    }

    public static NewLockerSerialportUtil getInstance(){
		return INSTANCE;
    }

    public SerialPort getboxPort() throws SecurityException, IOException, InvalidParameterException {
        return boxPort;
    }

    public class SerialBroadcastReceiverBox extends BroadcastReceiver {
        Context ct = null;
        public SerialBroadcastReceiverBox(Context c) {
            Log.i(TAG, "enter  SerialBroadcastReceiverBox ");
            ct = c;
            m_Receiver2 = this;
        }

        //注册  锁屏广播
        public void registerAction() {
            Log.i(TAG, "enter  registerAction ");
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            if(ct == null){
            	Log.e(TAG, "ct nulll");
            }
            if(m_Receiver2 == null){
                Log.e(TAG, "m_Receiver2 nulll");
            }
            ct.registerReceiver(m_Receiver2, filter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "enter  onReceive");
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_ON)) { // 屏幕开启后打开串口
                Log.i(TAG, "recevied  ACTION_SCREEN_ON ");
                if (boxPort == null) {
                    try {
                        boxPort = new SerialPort(new File(path), baudrate, 0);
                    } catch (SecurityException e) {
                        DisplayError(context, R.string.error_security);
                    } catch (IOException e) {
                        DisplayError(context,R.string.error_unknown);
                    } catch (InvalidParameterException e) {
                        DisplayError(context,R.string.error_configuration);
                    }
                }
            }

            if (action.equals(Intent.ACTION_SCREEN_OFF)) {  // 锁屏后关闭串口
                Log.i(TAG, "recevied  ACTION_SCREEN_OFF ");
                if (boxPort != null) {
                    closeSerialPort();
                    pthread.interrupt();
                }
            }
        }
    }

    /**
     * 关闭串口
     */
    private void closeSerialPort() {
        int iret=pSerialport.open(arg);
        if(pSerialport != null)
            pSerialport.close(iret);
    }


    /**
     * 串口初始化出错的提示
     * @param context
     * @param resourceId
     */
    private void DisplayError(Context context,int resourceId) {
    	Toast.makeText(context,path+"\n"+context.getString(resourceId),Toast.LENGTH_SHORT).show();
    }



    /**
     * byte[]转换成字符串
     * @param b
     * @return
     */
    public static String byte2HexString(byte[] b)
    {
        StringBuffer sb = new StringBuffer();
        int length = b.length;
        for (int i = 0; i < b.length; i++) {
            String stmp = Integer.toHexString(b[i]&0xff);
            if(stmp.length() == 1)
                sb.append("0"+stmp);
            else
                sb.append(stmp);
        }
        return sb.toString();
    }


    /**
     * 初始化串口
     */
    private void initSerialPort(){
        try {
            int iret=pSerialport.open(arg);
            if(iret>0){
                iRead=iret;
                log(String.format("打开串口成功 (port = %s,fd=%d)", arg,iret));

                runReadSerial(iRead);
            }else{
                log(String.format("打开串口失败 (fd=%d)", iret));
            }
//            sportInterface.onLockerOutputStream(mOutputStreamBox);
        } catch (SecurityException e) {
            e.printStackTrace();
            DisplayError(mContext, R.string.error_security);
        } catch (InvalidParameterException e) {
            e.printStackTrace();
            DisplayError(mContext,R.string.error_configuration);
        }
    }

    // 读取串口数据线程
    public void runReadSerial(final int fd){
        Runnable run=new Runnable() {
            public void run() {
                StringBuilder builder = new StringBuilder();
                while(true){
                    int r = pSerialport.select(fd, 1, 0);
                    if(r == 1)
                    {
                        //测试 普通读串口数据
                        byte[] buf = new byte[64];
                        buf = pSerialport.read(fd,100);
                        String str = "";
                        if(buf.length > 0){
                            str = byte2HexString(buf);
                            builder.append(str);
                        }
                        if("23".equals(str)){
                            if (sportInterface != null) {
                                String str2 = builder.toString();
                                Log.d("TAg","卡号"+str2);
//                                sportInterface.onLockerDataReceived(tem, tem.length,path);
                                sportInterface.dealSwingCard(str2);
                                builder.delete(0,builder.length());
                            }
                        }
                    }
                }
            }
        };
        pthread=new Thread(run);
        pthread.start();
    }

    public interface onReadCardListener{
        void dealSwingCard(String cardNum);
    }


    public void log(String str){
        System.out.println("[output] "+str);
        Log.v("info", str);
    }

}

