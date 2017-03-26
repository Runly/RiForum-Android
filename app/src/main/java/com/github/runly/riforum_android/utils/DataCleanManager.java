package com.github.runly.riforum_android.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.math.BigDecimal;

/**
 * Created by ranly on 17-3-26.
 */

public class DataCleanManager {
	/**
	 * * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * *
	 *
	 * @param context
	 */
	public static void cleanInternalCache(Context context) {
		deleteFolderFile(context.getCacheDir(), true);
	}

	/**
	 * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) *
	 *
	 * @param context
	 */
	public static void cleanSharedPreference(Context context) {
		deleteFolderFile(new File(File.separator + "data"
			+ Environment.getDataDirectory().getAbsolutePath() + File.separator
			+ context.getPackageName() + File.separator + "shared_prefs"), true);
	}

	/**
	 * * 清除/data/data/com.xxx.xxx/files下的内容 * *
	 *
	 * @param context
	 */
	public static void cleanFiles(Context context) {
		deleteFolderFile(context.getFilesDir(), true);
	}

	/**
	 * * 清除本应用所有的数据 * *
	 *
	 * @param context
	 */
	public static void cleanApplicationData(Context context) {
		cleanInternalCache(context);
		cleanSharedPreference(context);
		cleanFiles(context);
	}

	/**
	 * 删除指定目录下文件及目录
	 *
	 * @param file
	 * @param deleteThisPath
	 * @return
	 */
	public static void deleteFolderFile(File file, boolean deleteThisPath) {
		try {
			if (file.isDirectory()) {// 如果下面还有文件
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFolderFile(files[i], true);
				}
			}
			if (deleteThisPath) {
				if (!file.isDirectory()) {// 如果是文件，删除
					file.delete();
				} else {// 目录
					if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
						file.delete();
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 获取文件
	//Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
	//Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
	public static long getFolderSize(File file) throws Exception {
		long size = 0;
		try {
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				// 如果下面还有文件
				if (fileList[i].isDirectory()) {
					size = size + getFolderSize(fileList[i]);
				} else {
					size = size + fileList[i].length();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}

	public static String getAllFolderSize(Context context) {
		double sum = 0;
		try {
			sum += getFolderSize(context.getFilesDir());
			sum += getFolderSize(context.getCacheDir());
			sum += getFolderSize(new File(File.separator + "data"
				+ Environment.getDataDirectory().getAbsolutePath() + File.separator
				+ context.getPackageName() + File.separator + "shared_prefs"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return getFormatSize(sum);
	}

	/**
	 * 格式化单位
	 *
	 * @param size
	 * @return
	 */
	public static String getFormatSize(double size) {
		double kiloByte = size / 1024;
		if (kiloByte < 1) {
			return size + "Byte";
		}

		double megaByte = kiloByte / 1024;
		if (megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
		}

		double gigaByte = megaByte / 1024;
		if (gigaByte < 1) {
			BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
		}

		double teraBytes = gigaByte / 1024;
		if (teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
		}
		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
	}

}
