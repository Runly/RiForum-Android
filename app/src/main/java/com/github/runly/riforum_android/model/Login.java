package com.github.runly.riforum_android.model;

/**
 * Created by ranly on 17-2-13.
 */

public class Login {

    /**
     * message : login successfully
     * code : 1
     * data : {"token":"c1f42d89a4c3ab634f62d715665b5114","uid":10000003}
     * dateline : 1486979211
     */

    public String message;
    public String code;
    public Data data;
    public int dateline;

    public static class Data {
        /**
         * token : c1f42d89a4c3ab634f62d715665b5114
         * uid : 10000003
         */

        public String token;
        public int uid;
    }
}
