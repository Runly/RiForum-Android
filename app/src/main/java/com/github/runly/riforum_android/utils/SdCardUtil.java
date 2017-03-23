package com.github.runly.riforum_android.utils;

import android.content.Context;
import android.os.Environment;

import com.github.runly.riforum_android.model.ModelBase;
import com.github.runly.riforum_android.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import rx.schedulers.Schedulers;


/**
 * Created by ranly on 17-2-14.
 */

public class SdCardUtil {

	public static void saveUserToSdCard(Context context, User user) {
		String user_path = File.separator + "data"
			+ Environment.getDataDirectory().getAbsolutePath() + File.separator
			+ context.getPackageName() + File.separator + "user" + File.separator;
		String fileName = "user.ser";

		// 在io线程中写文件
		Schedulers.io().createWorker()
			.schedule(() -> {
				File dir = new File(user_path);
				if (!dir.exists()) {
					dir.mkdir();
				}
				File userFile = new File(user_path + fileName);
				FileOutputStream fs;
				try {
					fs = new FileOutputStream(userFile);
					ObjectOutputStream os = new ObjectOutputStream(fs);
					os.writeObject(user);
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			})
			.unsubscribe();
	}

	public static User loadUserFromSdCard(Context context) {
		User user = null;
		String user_path = File.separator + "data"
			+ Environment.getDataDirectory().getAbsolutePath() + File.separator
			+ context.getPackageName() + File.separator + "user" + File.separator;
		String fileName = "user.ser";

		File userFile = new File(user_path + fileName);

		if (userFile.exists()) {
			FileInputStream fs;
			try {
				fs = new FileInputStream(userFile);
				ObjectInputStream is = new ObjectInputStream(fs);
				user = (User) is.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return user;
	}

	public static void removeUserFromSdCard(Context context) {
		String user_path = File.separator + "data"
			+ Environment.getDataDirectory().getAbsolutePath() + File.separator
			+ context.getPackageName() + File.separator + "user" + File.separator;
		String fileName = "user.ser";

		// 在io线程中删文件
		Schedulers.io().createWorker()
			.schedule(() -> {
				File userFile = new File(user_path + fileName);

				if (userFile.exists()) {
					try {
						userFile.delete();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			})
			.unsubscribe();
	}

	public static void saveHistory(Context context, List<ModelBase> list) {
		if (list.size() > 15) {
			list.remove(list.size() - 1);
		}

		String history_path = File.separator + "data"
			+ Environment.getDataDirectory().getAbsolutePath() + File.separator
			+ context.getPackageName() + File.separator + "history" + File.separator;
		String fileName = "history.ser";

		// 在io线程中写文件
		Schedulers.io().createWorker()
			.schedule(() -> {
				File dir = new File(history_path);
				if (!dir.exists()) {
					dir.mkdir();
				}
				File file = new File(history_path + fileName);
				FileOutputStream fs;
				try {
					fs = new FileOutputStream(file);
					ObjectOutputStream os = new ObjectOutputStream(fs);
					os.writeObject(list.toArray());
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			})
			.unsubscribe();
	}

	public static List<ModelBase> loadHistory(Context context) {
		List<ModelBase> list;
		String history_path = File.separator + "data"
			+ Environment.getDataDirectory().getAbsolutePath() + File.separator
			+ context.getPackageName() + File.separator + "history" + File.separator;
		String fileName = "history.ser";

		File file = new File(history_path + fileName);

		if (file.exists()) {
			FileInputStream fs;
			try {
				fs = new FileInputStream(file);
				ObjectInputStream is = new ObjectInputStream(fs);
				list = new ArrayList<>();
				for (Object o : (Object[]) is.readObject()) {
					list.add((ModelBase) o);
				}
				return list;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static void removeHistory(Context context) {
		String history_path = File.separator + "data"
			+ Environment.getDataDirectory().getAbsolutePath() + File.separator
			+ context.getPackageName() + File.separator + "history" + File.separator;
		String fileName = "history.ser";

		// 在io线程中删文件
		Schedulers.io().createWorker()
			.schedule(() -> {
				File file = new File(history_path + fileName);

				if (file.exists()) {
					try {
						file.delete();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			})
			.unsubscribe();
	}

}
