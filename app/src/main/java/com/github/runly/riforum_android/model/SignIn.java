package com.github.runly.riforum_android.model;

/**
 * Created by ranly on 17-2-13.
 */

public class SignIn {

    /**
     * message : sign in successfully
     * code : 1
     * data : {"grade":1,"phone":"18861822161","birth":"","password":"123456","id":10000004,"permissions":1,"name":"ranly","gender":0,"experience":0,"location":"","time":1486980108,"email":""}
     * dateline : 1486980108
     */

    public String message;
    public String code;
    public Data data;
    public int dateline;

    public static class Data {
        /**
         * grade : 1
         * phone : 18861822161
         * birth :
         * password : 123456
         * id : 10000004
         * permissions : 1
         * name : ranly
         * gender : 0
         * experience : 0
         * location :
         * time : 1486980108
         * email :
         */

        public int grade;
        public String phone;
        public String birth;
        public String password;
        public int id;
        public int permissions;
        public String name;
        public int gender;
        public int experience;
        public String location;
        public int time;
        public String email;
    }
}
