package zakoi.com.myinventory.Utility;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import zakoi.com.myinventory.ReceiptClient;

/**
 * Created by zakoi on 7/18/17.
 */

public class NetworkManager {

    private static String Tag = "Network Manager";
    private static NetworkManager instance = null;
    private ReceiptClient _client;
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
        _client = retrofit.create(ReceiptClient.class);
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


}
