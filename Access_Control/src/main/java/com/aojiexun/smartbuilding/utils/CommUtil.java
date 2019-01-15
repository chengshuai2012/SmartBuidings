package com.aojiexun.smartbuilding.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.nio.charset.Charset;

public class CommUtil {
	public static void initTitle(Activity activity) {
		// 隐藏标题栏
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 隐藏状态栏
		// 定义全屏参数
		int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		// 获得当前窗体对象
		Window window = activity.getWindow();
		// 设置当前窗体为全屏显示
		window.setFlags(flag, flag);

		WindowManager.LayoutParams params = window.getAttributes();
		params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
		window.setAttributes(params);
	}
	//判断网络是否连接
	public static boolean checkNet(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {

					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	/**
	 * 获取软件版本
	 */
	public static double getVersion(Context context)// 获取版本号
	{
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 获取软件版本
	 */
	public static String getVersionName(Context context)// 获取版本号
	{
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "0";
		}
	}

	public static int convertKeyCode(String c) {
		Log.i("aabbb", c);
		c = c.substring(6, 10);
		int value = -1;
		if(!c.equals("0000")){
			if ((c.equals("0200"))) {
				value = 0;
			} else if ((c.equals("1000"))) {
				value = 1;
			} else if ((c.equals("0004"))) {
				value = 2;
			} else if ((c.equals("0008"))) {
				value = 3;
			} else if ((c.equals("2000"))) {
				value = 4;
			} else if ((c.equals("0002"))) {
				value = 5;
			} else if ((c.equals("0001"))) {
				value = 6;
			} else if ((c.equals("4000"))) {
				value = 7;
			} else if ((c.equals("0100"))) {
				value = 8;
			} else if ((c.equals("0400"))) {
				value = 9;
			} else if ((c.equals("0800"))) {// #
				value = 10;
			} else if ((c.equals("8000"))) {// *
				value = 11;
			}
			return value;
		}else{
			return value;
		}
	}

	public static String convertToCardNo(byte[] b, int length) {
		String value = "";
		for (int i = length - 1; i >= 0; i--) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			value = value + hex;
		}
		return value.toUpperCase();
	}

	public static void hideStatusBar(Activity activity) {
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		activity.getWindow().getDecorView().setSystemUiVisibility(8);
		// activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
		// WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//
		// 隐藏
	}

	public static byte[] getBytes(short data) {
		byte[] bytes = new byte[2];
		bytes[1] = (byte) (data & 0xff);
		bytes[0] = (byte) ((data & 0xff00) >> 8);
		return bytes;
	}

	public static byte[] getBytes(char data) {
		byte[] bytes = new byte[2];
		bytes[1] = (byte) (data);
		bytes[0] = (byte) (data >> 8);
		return bytes;
	}

	public static byte[] getBytes(int data) {
		byte[] bytes = new byte[4];
		bytes[3] = (byte) (data & 0xff);
		bytes[2] = (byte) ((data & 0xff00) >> 8);
		bytes[1] = (byte) ((data & 0xff0000) >> 16);
		bytes[0] = (byte) ((data & 0xff000000) >> 24);
		return bytes;
	}

	public static byte[] getBytes(long data) {
		byte[] bytes = new byte[8];
		bytes[7] = (byte) (data & 0xff);
		bytes[6] = (byte) ((data >> 8) & 0xff);
		bytes[5] = (byte) ((data >> 16) & 0xff);
		bytes[4] = (byte) ((data >> 24) & 0xff);
		bytes[3] = (byte) ((data >> 32) & 0xff);
		bytes[2] = (byte) ((data >> 40) & 0xff);
		bytes[1] = (byte) ((data >> 48) & 0xff);
		bytes[0] = (byte) ((data >> 56) & 0xff);
		return bytes;
	}

	public static byte[] getBytes(float data) {
		int intBits = Float.floatToIntBits(data);
		return getBytes(intBits);
	}

	public static byte[] getBytes(double data) {
		long intBits = Double.doubleToLongBits(data);
		return getBytes(intBits);
	}

	public static byte[] getBytes(String data, String charsetName) {
		Charset charset = Charset.forName(charsetName);
		return data.getBytes(charset);
	}

	public static byte[] getBytes(String data) {
		return getBytes(data, "GBK");
	}

	public static short getShort(byte[] bytes) {
		return (short) ((0xff & bytes[1]) | (0xff00 & (bytes[0] << 8)));
	}

	public static char getChar(byte[] bytes) {
		return (char) ((0xff & bytes[1]) | (0xff00 & (bytes[0] << 8)));
	}

	public static int getInt(byte[] bytes) {
		return (0xff & bytes[3]) | (0xff00 & (bytes[2] << 8)) | (0xff0000 & (bytes[1] << 16))
				| (0xff000000 & (bytes[0] << 24));
	}

	public static long getLong(byte[] bytes) {
		return (0xffL & (long) bytes[7]) | (0xff00L & ((long) bytes[6] << 8)) | (0xff0000L & ((long) bytes[5] << 16))
				| (0xff000000L & ((long) bytes[4] << 24)) | (0xff00000000L & ((long) bytes[3] << 32))
				| (0xff0000000000L & ((long) bytes[2] << 40)) | (0xff000000000000L & ((long) bytes[1] << 48))
				| (0xff00000000000000L & ((long) bytes[0] << 56));
	}

	public static float getFloat(byte[] bytes) {
		return Float.intBitsToFloat(getInt(bytes));
	}

	public static double getDouble(byte[] bytes) {
		long l = getLong(bytes);
		return Double.longBitsToDouble(l);
	}

	public static String getString(byte[] bytes, String charsetName) {
		return new String(bytes, Charset.forName(charsetName));
	}

	public static String getString(byte[] bytes) {
		return getString(bytes, "GBK");
	}

	/**
	 * 截取IC卡的序列号并转变成10进制的数字
	 * 卡号：5743444100000000dff1bd0a0023
	 * 序列：dff1bd0a
	 * 转换：3757161738
	 * @param hexCardNum
	 * @return
	 */
	public static String convertICCardNum(String hexCardNum) {
		if(hexCardNum == null || (hexCardNum.length() < 24)){
			return "";
		}
		long carNum = 0;
		try {
			//10eee2d9序列号按16进制有高低之分，得重组d9e2ee10
			String xulieNum = hexCardNum.substring(16,24);
			Log.d("TAg","序列号:"+xulieNum);

			//拆分重组
			String di_l = xulieNum.substring(0,2);
			String di_h = xulieNum.substring(2,4);
			String gao_l = xulieNum.substring(4,6);
			String gao_h = xulieNum.substring(6,8);

			String newXulieNum = gao_h + gao_l + di_h + di_l;
			Log.d("TAg","新序列号:"+newXulieNum);
			carNum = Long.parseLong(newXulieNum,16);
			Log.d("TAg","转换后的卡号:"+carNum);
			return carNum+"";
		}catch (Exception e){
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 直接转换IC卡序列号
	 * 序列：dff1bd0a
	 * 转换：3757161738
	 * @param xulieNum
	 * @return
	 */
	public static String convertICCardNum2(String xulieNum) {

		long carNum = 0;
		try {
			//10eee2d9序列号按16进制有高低之分，得重组d9e2ee10

//			Log.d("TAg","序列号:"+xulieNum);

			//拆分重组
			String di_l = xulieNum.substring(0,2);
			String di_h = xulieNum.substring(2,4);
			String gao_l = xulieNum.substring(4,6);
			String gao_h = xulieNum.substring(6,8);

			String newXulieNum = gao_h + gao_l + di_h + di_l;
//			Log.d("TAg","新序列号:"+newXulieNum);
			carNum = Long.parseLong(newXulieNum,16);
			newXulieNum =  carNum+"";
			String zero = "";
			//转换后的卡长度固定为10位
			for(int i = 0; i < (10-newXulieNum.length()); i++){
				zero+="0";
			}
			String newCarNum = zero+newXulieNum;
			Log.d("TAg","转换后的卡号:"+newCarNum);
			return newCarNum;
		}catch (Exception e){
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 校验ic卡号
	 * 序列：dff1bd0a
	 * 转换：3757161738
	 * @param xulieNum
	 * @return
	 */
	public static boolean checkIC(String xulieNum) {

		try {
			//10eee2d9序列号按16进制有高低之分，得重组d9e2ee10

			Log.d("TAg","序列号:"+xulieNum);

			//拆分重组
			String di_l = xulieNum.substring(0,2);
			String di_h = xulieNum.substring(2,4);
			String gao_l = xulieNum.substring(4,6);
			String gao_h = xulieNum.substring(6,8);
			String checNum = xulieNum.substring(8,10);

			int result = Integer.parseInt(di_l,16) ^ Integer.parseInt(di_h,16) ^Integer.parseInt(gao_l,16) ^Integer.parseInt(gao_h,16) ^Integer.parseInt(checNum,16);
			Log.d("TAg", di_l+" "+di_h+" "+gao_l+" "+gao_h+" "+checNum);
			if(result == 0){
				return true;
			}else {
				return false;
			}




		}catch (Exception e){
			e.printStackTrace();
		}

		return false;
	}

}
