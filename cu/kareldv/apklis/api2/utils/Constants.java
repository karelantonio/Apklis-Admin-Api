package cu.kareldv.apklis.api2.utils;

import cu.kareldv.apklis.api2.model.Application;

public final class Constants {
    Constants(){}

    public static final String AUTHORIZATION = "7UMqh5p9CPPPEPi0s4Ksrv2vR5tBjBJH8XlaNMMB";
    public static final String URL_API_HOST = "https://api.apklis.cu";
    public static final String URL_API_CATEGORIES = "https://api.apklis.cu/v1/category/";

    public static final class V2{

        V2(){}


        public static final String URL_API_MONTH_DOWNLOADS = URL_API_HOST+"/v2/download/by_months/";

        public static final String URL_API_APP_FORMAT = URL_API_HOST+"/v2/application/%s";
        public static final String URL_API_APP_FORMAT(String arg){
            return String.format(URL_API_APP_FORMAT, arg);
        }

        public static final String URL_API_DEV_FORMAT = URL_API_HOST+"/v2/application/developer/?developer=%s";
        public static final String URL_API_DEV_FORMAT(String arg){
            return String.format(URL_API_DEV_FORMAT, arg);
        }

        public static final String URL_API_GLOBALS_DASHBOARD_STATS = "https://api.apklis.cu/v2/download/dashboard_stats/";
        public static final String URL_API_GLOBALS_DASHBOARD_DOWNLOADS = "https://api.apklis.cu/v2/download/dashboard_download/";

        public static final String URL_API_RELEASES = "https://api.apklis.cu/v2/download/by_releases/";
    }

    public static final class V1{
        public static final String URL_API_TOKEN_DEV = "https://api.apklis.cu/o/token";
        public static final String URL_API_REGISTER = "https://api.apklis.cu/v1/developer/";

        public static final String URL_API_USER_INFO_FORMAT = "https://api.apklis.cu/v1/user/%s/";
        public static final String URL_API_USER_INFO(String val){
            return String.format(URL_API_USER_INFO_FORMAT, val);
        }
        public static final String URL_API_APPS_BY_OWNER = "https://api.apklis.cu/v1/application/?owner=%s";
        public static final String URL_API_APPS_BY_OWNER(String s){
            return String.format(URL_API_APPS_BY_OWNER, s);
        }

        public static final String URL_API_CHANGE_USER_INFO_FORMAT = "https://api.apklis.cu/v1/user/%s/";

        public static final String URL_API_CHANGE_USER_INFO(String user){
            return String.format(URL_API_CHANGE_USER_INFO_FORMAT, user);
        }

        public static final String URL_API_CHANGE_APP_DATA_FORMAT = "https://api.apklis.cu/v1/application/%s/";
        public static final String URL_API_CHANGE_APP_DATA(String arg){
            return String.format(URL_API_CHANGE_APP_DATA_FORMAT, arg);
        }

        public static final String URL_API_SEE_REVIEWS = "https://api.apklis.cu/v1/review/?search=&limit=10&offset=%s&application=%s&ordering=-published";
        public static final String URL_API_SEE_REVIEWS(int last, Application app){
            return String.format(URL_API_SEE_REVIEWS, last*10+"", app.getId()+"");
        }

        public static final String URL_API_REVIEW = "https://api.apklis.cu/v1/review/%s/";
        public static final String URL_API_REVIEW(int id){
            return String.format(URL_API_REVIEW, id+"");
        }

        public static final String URL_API_REPLY = "https://api.apklis.cu/v2/application/send_reply/";

        public static final String URL_API_PUSH_APP = "https://api.apklis.cu/v1/release";
    }
}
