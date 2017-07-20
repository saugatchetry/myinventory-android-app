package zakoi.com.myinventory.Utility;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.Date;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import zakoi.com.myinventory.ReceiptClient;

/**
 * Created by zakoi on 7/18/17.
 */

public class NetworkManager {

    private static String Tag = "Network Manager";
    private static NetworkManager instance = null;
    private static Date TIME_GET_ITEMS = null;
    private static Date TIME_GET_TRANSFERS = null;
    public ReceiptClient client;
    public enum API {
        GET_ALL_VENDORS,
        GET_ALL_ITEMS,
    };

    private NetworkManager() {
        GsonBuilder builder = new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
        Gson gson = builder.create();
        Retrofit.Builder builder1 = new Retrofit.Builder()
                //.baseUrl("http://10.0.2.2:8080/")
                .baseUrl(Config.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(gson));
        Retrofit retrofit = builder1.build();
        client = retrofit.create(ReceiptClient.class);
    }

    // Singleton
    public static NetworkManager getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new NetworkManager();
        return instance;
    }

    public void sendRequest(NetworkInterface instance, API api) {
        Log.d(Tag, "Sending request");
    }

    public static Boolean CallGetItems() {
        Date current = new Date();
        if(TIME_GET_ITEMS == null) {
            TIME_GET_ITEMS = current;
            return true;
        }

        long minutes = Util.getElapsedMins(current, TIME_GET_ITEMS);

        if (minutes >= Config.TIME_INTERVAL) {
            TIME_GET_ITEMS = current;
            return true;
        }

        return false;
    }

    public static Boolean CallRefreshStock() {
        Date current = new Date();
        if(TIME_GET_TRANSFERS == null) {
            TIME_GET_TRANSFERS = current;
            return true;
        }

        long minutes = Util.getElapsedMins(current, TIME_GET_TRANSFERS);

        if (minutes >= Config.TIME_STOCK_INTERVAL) {
            TIME_GET_TRANSFERS = current;
            return true;
        }

        return false;
    }

}
