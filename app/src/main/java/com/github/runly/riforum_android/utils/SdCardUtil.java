package com.github.runly.riforum_android.utils;

import android.content.Context;
import android.os.Environment;

import com.github.runly.riforum_android.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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

}
