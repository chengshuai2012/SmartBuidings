package com.aojiexun.smartbuilding.utils;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.hurray.plugins.rkctrl;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Runtime.getRuntime;

public class UtilFace {

	//等待呼叫的号码集合
	public static ArrayList<String> callInfos=new ArrayList<>();
	public static String PHONENUMBER="";
	public static int currhour=0;
   //当前下载位置(人脸数据全部下载完毕后进行读取)
	public static int currdownindex=0;
	public static int currdownlength=0;
	public static int currminute=0;
	public static String getErrorInfoFromException(Exception e) {

		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return "\r\n" + sw.toString() + "\r\n";
		} catch (Exception e2) {
			return "bad getErrorInfoFromException";
		}
	}



	@SuppressLint("SimpleDateFormat")
	public static String getDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		return str;
	}
	public static File getDir() {
 		// 得到SD卡根目录
 		File dir = Environment.getExternalStorageDirectory();

 		if (dir.exists()) {
 			return dir;
 		} else {
 			dir.mkdirs();
 			return dir;
 		}
 	}
	public static void installCalmlly(String path) {
		try {
			String command = "chmod 777" + " " + path;
			getRuntime().exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("cachePath = " + path);
		String result = execCmd("pm", "install", "-f", path);
		System.out.println("result = " + result);
		// Toast.makeText(MainActivity.this, "安装结果:" + result,
		// Toast.LENGTH_LONG).show();18565713232
	}


	public static String execCmd(String... command) {
		Process process = null;
		InputStream errIs = null, inIs = null;
		String result = "";
		try {
			process = new ProcessBuilder().command(command).start();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = -1;
			errIs = process.getErrorStream();
			while ((read = errIs.read()) != -1) {
				baos.write(read);
			}

			inIs = process.getInputStream();
			while ((read = inIs.read()) != -1) {
				baos.write(read);
			}

			result = new String(baos.toByteArray());

			if (errIs != null) {
				errIs.close();
			}
			if (inIs != null) {
				inIs.close();
			}
		} catch (Exception e) {
			result = e.toString();
		}
		return result;
	}

	/**
	 * 执行具体的静默安装逻辑，需要手机ROOT。
	 * 
	 * @param apkPath
	 *            要安装的apk文件的路径
	 * @return 安装成功返回true，安装失败返回false。
	 */
	public static void install1(final String apkPath) {
		new Thread() {
			@Override
			public void run()
			{
				super.run();
				Process systemProcess;
				DataOutputStream systemProcessDataOutputStream = null;
				try {
					systemProcess = getRuntime().exec("su");
					systemProcessDataOutputStream = new DataOutputStream(systemProcess.getOutputStream());
					systemProcessDataOutputStream
							.writeBytes("cp -a " + " " + apkPath + " /system/app/temp_name" + "\n");
					try {
						sleep(5000); 
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					systemProcessDataOutputStream.writeBytes("chown " + "root:root" + " /system/app/*" + "\n");
					systemProcessDataOutputStream.writeBytes("chmod " + "644" + " /system/app/*" + "\n");
					systemProcessDataOutputStream
							.writeBytes("cp -a " + " /system/app/temp_name" + " /system/app/HSHS_Intercom.apk" + "\n");

					Log.i("Util --->install1", "chown执行");
					try {
						sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Log.i("Util --->install1", "reboot执行")
					;
					systemProcessDataOutputStream.writeBytes("rm " + " /system/app/temp_name" + "\n");
					systemProcessDataOutputStream.writeBytes("reboot \n");

				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (systemProcessDataOutputStream != null) {
							systemProcessDataOutputStream.close();
						}
					} catch (IOException e) {
						Log.e("TAG", e.getMessage(), e);
					}
				}
			}
		}.start();
	}
	// /**
	// * 执行具体的静默安装逻辑，需要手机ROOT。
	// *
	// * @param apkPath
	// * 要安装的apk文件的路径
	// * @return 安装成功返回true，安装失败返回false。
	// */
	// public static void install1(final String apkPath) {
	// new Thread() {
	// @Override
	// public void run() {
	// super.run();
	// Process systemProcess;
	// DataOutputStream systemProcessDataOutputStream = null;
	// try {
	// systemProcess = Runtime.getRuntime().exec("su");
	// systemProcessDataOutputStream = new
	// DataOutputStream(systemProcess.getOutputStream());
	// systemProcessDataOutputStream.writeBytes("cp -a " + " " + apkPath + "
	// /system/app" + "\n");
	// try {
	// sleep(5000);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// systemProcessDataOutputStream.writeBytes("chown " + "root:root" + "
	// /system/app/*" + "\n");
	// systemProcessDataOutputStream.writeBytes("chmod " + "644" + "
	// /system/app/*" + "\n");
	// Log.i("Util --->install1", "chown执行");
	// try {
	// sleep(1000);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// Log.i("Util --->install1", "reboot执行");
	// systemProcessDataOutputStream.writeBytes("reboot \n");
	// } catch (IOException e) {
	// e.printStackTrace();
	// } finally {
	// try {
	// if (systemProcessDataOutputStream != null) {
	// systemProcessDataOutputStream.close();
	// }
	// } catch (IOException e) {
	// Log.e("TAG", e.getMessage(), e);
	// }
	// }
	// }
	// }.start();
	// }

	/**
	 * 执行具体的静默安装逻辑，需要手机ROOT。
	 * 
	 * @param要安装的apk文件的路径
	 * @return 安装成功返回true，安装失败返回false。
	 */
	public static void reboot() {
		Process systemProcess;
		DataOutputStream systemProcessDataOutputStream = null;
		try {
			systemProcess = getRuntime().exec("su");
			systemProcessDataOutputStream = new DataOutputStream(systemProcess.getOutputStream());
			systemProcessDataOutputStream.writeBytes("reboot \n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (systemProcessDataOutputStream != null) {
  					systemProcessDataOutputStream.close();
				}
			} catch (IOException e) {
				Log.e("TAG", e.getMessage(), e);
			}
		}
	}
	/**
	 * 执行具体的静默安装逻辑，需要手机ROOT。
	 * 
	 * @param
	 *
	 * @return 安装成功返回true，安装失败返回false。
	 */
	public static void deleteFile(String pash) {
		Process systemProcess;
		DataOutputStream systemProcessDataOutputStream = null;
		try {
			systemProcess = getRuntime().exec("su");
			systemProcessDataOutputStream = new DataOutputStream(systemProcess.getOutputStream());
			systemProcessDataOutputStream.writeBytes("rm -rf  "+pash+" \n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (systemProcessDataOutputStream != null) {
					systemProcessDataOutputStream.close();
				}
			} catch (IOException e) {
				Log.e("TAG", e.getMessage(), e);
			}
		}
	}
	//文件读写_开关门
	public static void do_exec(String cmd) {
        try {
            /* Missing read/write permission, trying to chmod the file */
            Process su;
            su = getRuntime().exec("su");
            String str = cmd + "\n" + "exit\n";
            su.getOutputStream().write(str.getBytes());
            Log.d("do_exec", "James DBG:>>>> do_exec cmd");
            if ((su.waitFor() != 0)) {
                System.out.println("cmd=" + cmd + " error!");
                throw new SecurityException();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	public static void writeFile(String str) throws IOException, InterruptedException {

        File file = new File("/sys/devices/misc_power_en.22/keyout");
        file.setExecutable(true);
        file.setReadable(true);
        file.setWritable(true);
        if (str.equals("0")) {
                        //do_exec("busybox echo 0 > /sys/class/disp/disp/attr/sys");
        	do_exec("busybox echo 0 > /sys/devices/misc_power_en.22/keyout");

        }  else {
                        //do_exec("busybox echo 1 > /sys/class/disp/disp/attr/sys");
        	do_exec("busybox echo 1 > /sys/devices/misc_power_en.22/keyout");

        }
	}
	public static void updatedown(Context mContext,String downPath){
		 //使用系统下载类
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(mContext.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(downPath);
		Log.e("updatedown: ", downPath);
		DownloadManager.Request request = new DownloadManager.Request(uri);
        // 设置自定义下载路径和文件名
//                    String apkName =  "yourName" + DateUtils.getCurrentMillis() + ".apk";
//                    request.setDestinationInExternalPublicDir(yourPath, apkName);
//                    MyApplication.getInstance().setApkName(apkName);
        //设置允许使用的网络类型，这里是移动网络和wifi都可以
       // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
        //禁止发出通知，既后台下载，如果要使用这一句必须声明一个权限：android.permission.DOWNLOAD_WITHOUT_NOTIFICATION
        //request.setShowRunningNotification(false);
        //不显示下载界面
        request.setVisibleInDownloadsUi(false);
        // 设置为可被媒体扫描器找到
        request.allowScanningByMediaScanner();
        // 设置为可见和可管理
        request.setVisibleInDownloadsUi(true);
        request.setMimeType("application/cn.trinea.download.file");
        /*设置下载后文件存放的位置,如果sdcard不可用，那么设置这个将报错，因此最好不设置如果sdcard可用，下载后的文件
                       在/mnt/sdcard/Android/data/packageName/files目录下面，如果sdcard不可用,设置了下面这个将报错，不设置，下载后的文件在/cache这个  目录下面*/
        //request.setDestinationInExternalFilesDir(mContext, getdownurl(mContext), "TvdLauncher.apk");
        //request.setDestinationInExternalFilesDir(mContext, getdownurl(mContext), "intercom.apk");
        File oriFile = new File(Environment.getExternalStorageDirectory()+"/system/app/new/", "new.apk");
        if(oriFile.exists()){
        	oriFile.delete();
        }
        request.setDestinationInExternalPublicDir("/system/app/new/","new.apk" ) ;
        //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //设置文件存放路径
       // request.setDestinationInExternalPublicDir(  Environment.DIRECTORY_DOWNLOADS  , "weixin.apk" ) ;
        //request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, "mydown");  
        long id = downloadManager.enqueue(request);//TODO 把id保存好，在接收者里面要用，最好保存在Preferences里面
        //MyApplication.getInstance().setApkId(Long.toString(id));//TODO 把id存储在Preferences里面
	}
	public static void updatefirmware(Context mContext,String downPath){
		//使用系统下载类
		DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(mContext.DOWNLOAD_SERVICE);
		Uri uri = Uri.parse(downPath);
		DownloadManager.Request request = new DownloadManager.Request(uri);
		// 设置自定义下载路径和文件名
//                    String apkName =  "yourName" + DateUtils.getCurrentMillis() + ".apk";
//                    request.setDestinationInExternalPublicDir(yourPath, apkName);
//                    MyApplication.getInstance().setApkName(apkName);
		//设置允许使用的网络类型，这里是移动网络和wifi都可以
		// request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
		//禁止发出通知，既后台下载，如果要使用这一句必须声明一个权限：android.permission.DOWNLOAD_WITHOUT_NOTIFICATION
		//request.setShowRunningNotification(false);
		//不显示下载界面
		request.setVisibleInDownloadsUi(false);
		// 设置为可被媒体扫描器找到
		request.allowScanningByMediaScanner();
		// 设置为可见和可管理
		request.setVisibleInDownloadsUi(true);
		request.setMimeType("application/cn.trinea.download.file");
        /*设置下载后文件存放的位置,如果sdcard不可用，那么设置这个将报错，因此最好不设置如果sdcard可用，下载后的文件
                       在/mnt/sdcard/Android/data/packageName/files目录下面，如果sdcard不可用,设置了下面这个将报错，不设置，下载后的文件在/cache这个  目录下面*/
		//request.setDestinationInExternalFilesDir(mContext, getdownurl(mContext), "TvdLauncher.apk");
		//request.setDestinationInExternalFilesDir(mContext, getdownurl(mContext), "intercom.apk");
		File oriFile = new File(Environment.getExternalStorageDirectory()+"/firmware/", "update.img");
		if(oriFile.exists()){
			oriFile.delete();
		}
		request.setDestinationInExternalPublicDir("/firmware/","update.img" ) ;
		//request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
		//设置文件存放路径
		// request.setDestinationInExternalPublicDir(  Environment.DIRECTORY_DOWNLOADS  , "weixin.apk" ) ;
		//request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, "mydown");
		long id = downloadManager.enqueue(request);//TODO 把id保存好，在接收者里面要用，最好保存在Preferences里面
		//MyApplication.getInstance().setApkId(Long.toString(id));//TODO 把id存储在Preferences里面
	}
	public static void removeDuplicate(List<String> list)
	{
		Set set = new LinkedHashSet<String>();
		set.addAll(list);
		list.clear();
		list.addAll(set);
	}

   public static String gettuis(String message){
	   String str="测试推送";
	   switch (Integer.parseInt(message)){
		   case 1:
			   str="设备升级";
		   	break;
		   case 3:
			   str="门卡更新";
			   break;
		   case 4:
			   str="楼栋信息更新";
			   break;
		   case 5:
			   str="广告推送";
			   break;
		   case 10:
			   str="设备重启";
			   break;
		   case 11:
			   str="音量调节";
			   break;
		   case 12:
			   str="开门";
			   break;
		   case 13:
			   str="公告更新";
			   break;
		   case 14:
			   str="固件更新";
			   break;
		   case 15:
			   str="人脸数据下发";
			   break;
		   case 16:
			   str="删除人脸";
			   break;
	   }
	   return str;
   }
	// 保存照片
	public static boolean savePic(byte[] data, File savefile) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(savefile);
			fos.write(data);
			fos.flush();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	 /*private static File updateFile = null;
	 // 下载文件的存放路径
	static File  updateDir;
	public static String getdownurl(Context mContext) {
		// 创建文件
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
			// 组合下载地址
			//updateDir = new File(Environment.getExternalStorageDirectory(), "/system/app/");
			updateDir = new File(Environment.getExternalStorageDirectory(), "/rmgs/");
		} else {
			updateDir = mContext.getFilesDir();
		}
		// 拼凑下载文件文件名称
		updateFile = new File(updateDir.getPath(), "intercom.apk");
		if(updateFile.exists()){
			updateFile.delete();
		}
		return updateDir.getPath();
	}*/

	 public static void downfacedata(Context mContext,String downPath,String filename){
		 //使用系统下载类
		 DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(mContext.DOWNLOAD_SERVICE);
		 Uri uri = Uri.parse(downPath);
		 DownloadManager.Request request = new DownloadManager.Request(uri);
		 // 设置自定义下载路径和文件名
//                    String apkName =  "yourName" + DateUtils.getCurrentMillis() + ".apk";
//                    request.setDestinationInExternalPublicDir(yourPath, apkName);
//                    MyApplication.getInstance().setApkName(apkName);
		 //设置允许使用的网络类型，这里是移动网络和wifi都可以
		 // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
		 //禁止发出通知，既后台下载，如果要使用这一句必须声明一个权限：android.permission.DOWNLOAD_WITHOUT_NOTIFICATION
		 //request.setShowRunningNotification(false);
		 //不显示下载界面
		 request.setVisibleInDownloadsUi(false);
		 // 设置为可被媒体扫描器找到
		 request.allowScanningByMediaScanner();
		 // 设置为可见和可管理
		 request.setMimeType("application/cn.trinea.download.file");
        /*设置下载后文件存放的位置,如果sdcard不可用，那么设置这个将报错，因此最好不设置如果sdcard可用，下载后的文件
                       在/mnt/sdcard/Android/data/packageName/files目录下面，如果sdcard不可用,设置了下面这个将报错，不设置，下载后的文件在/cache这个  目录下面*/
		 //request.setDestinationInExternalFilesDir(mContext, getdownurl(mContext), "TvdLauncher.apk");
		 //request.setDestinationInExternalFilesDir(mContext, getdownurl(mContext), "intercom.apk");
		 File oriFile = new File(Environment.getExternalStorageDirectory()+"/faceFile/", filename);
		 if(oriFile.exists()){
			 oriFile.delete();
		 }
		 request.setDestinationInExternalPublicDir("/faceFile/",filename ) ;
		 //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
		 //设置文件存放路径
		 // request.setDestinationInExternalPublicDir(  Environment.DIRECTORY_DOWNLOADS  , "weixin.apk" ) ;
		 //request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, "mydown");
		 long id = downloadManager.enqueue(request);//TODO 把id保存好，在接收者里面要用，最好保存在Preferences里面
		 //MyApplication.getInstance().setApkId(Long.toString(id));//TODO 把id存储在Preferences里面
	 }


	 //------------添加开们控制-------------------//
	 //12V电压控制电磁锁	输出	GPIO4	高有效
	 // 继电器控制电磁锁	输出	GPIO6	高有效
	 private static rkctrl m_rkctrl = new rkctrl();

	/**
	 * 12V电压打开电磁锁
	 */
	public static void openDoorBy12V(){
	 	 m_rkctrl.exec_io_cmd(4,1);
	 }
	/**
	 * 12V电压关闭电磁锁
	 */
	public static void closeDoorBy12V(){
		m_rkctrl.exec_io_cmd(4,0);
	}




}
