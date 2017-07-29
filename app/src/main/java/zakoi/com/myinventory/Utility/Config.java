package zakoi.com.myinventory.Utility;

import java.util.Date;

/**
 * Created by zakoi on 7/18/17.
 */

public class Config {
    //Server
    public static final String SERVER_URL = "http://server.cemnohouse.biz:8443";
//    public static final String SERVER_URL = "https://myinventory-test.herokuapp.com";


    //Shared Pref
    public static final String SHARED_PREF_STORE = "MyPrefs" ;
    public static final String P_STORE_KEY = "store_name";
    public static final String P_TIME_STAMP = "time_stamp";

    //Intent String Constants
    public static final String I_VENDOR_LIST = "vendor_list";

    //Date time in minutes
    public static final int TIME_INTERVAL = 1;
    public static final int TIME_STOCK_INTERVAL = 1;
    public static final int SPLASH_DISPLAY_LENGTH = 2000;

    //Config
    public static int UNCONFIRMED_TRANSFERS = 0;


}
