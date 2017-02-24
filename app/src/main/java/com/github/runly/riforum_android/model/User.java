package com.github.runly.riforum_android.model;

import java.io.Serializable;

/**
 * Created by ranly on 17-2-13.
 */

public class User implements Serializable {
        private static final long serialVersionUID = 1L;
        public int grade;
        public String phone;
        public String birth;
        public String password;
        public int id;
        public int permissions;
        public String name;
        public int gender;
        public int experience;
        public String token;
        public String location;
        public long time;
        public String email;
        public String avatar;
}
