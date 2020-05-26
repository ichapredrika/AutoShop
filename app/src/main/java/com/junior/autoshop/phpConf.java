package com.junior.autoshop;

public class phpConf {
    private final static String ip = "192.168.1.8";

    private final static String prefix = "https://" + ip + "/autoshop/";
    public final static String URL_CONNECTION = "connection.php";
    public final static String URL_LOGIN = prefix + "login.php";
    public final static String URL_REGISTER_CUSTOMER = prefix + "register_customer.php";
    public final static String URL_REGISTER_AUTOSHOP = prefix + "register_autoshop.php";
    public final static String URL_GET_PROFILE_AUTOSHOP = prefix + "get_profile_autoshop.php";
    public final static String URL_GET_PROFILE_CUSTOMER = prefix + "get_profile_customer.php";
    
}
