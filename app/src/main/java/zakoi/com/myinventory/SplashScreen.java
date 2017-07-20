package zakoi.com.myinventory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import zakoi.com.myinventory.Utility.Config;
import zakoi.com.myinventory.Utility.NetworkManager;
import zakoi.com.myinventory.model.Vendors;

public class SplashScreen extends AppCompatActivity {

    private static String TAG = "Splash Screen";
    private ReceiptClient _client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedpreferences = getSharedPreferences(Config.SHARED_PREF_STORE, Context.MODE_PRIVATE);
        final String storeName = sharedpreferences.getString(Config.P_STORE_KEY,"default");

        if(!storeName.equalsIgnoreCase("default")){
            Intent intent = new Intent(this, SelectTask.class);
            startActivity(intent);
            return;
        }

        _client = NetworkManager.getInstance().client;
        Call<List<Vendors>> call1 = _client.getAllVendors();
        call1.enqueue(new Callback<List<Vendors>>() {
            @Override
            public void onResponse(Call<List<Vendors>> call, Response<List<Vendors>> response) {
                Log.d(TAG,"Received vendor list");
                saveVendorsToDB(response.body());
            }

            @Override
            public void onFailure(Call<List<Vendors>> call, Throwable t) {
                ShowError();
                // Intent intent = new Intent(SignInActivity.class, SelectTask.class);
                //startActivity(intent);
                //finish();
            }
        });
    }

    private void saveVendorsToDB(List<Vendors> body) {
        if(body == null) {
            SplashScreen.this.ShowError();
            return;
        }

        ArrayList<String> vendor_names = new ArrayList<>();
        for (Vendors vendor : body){
            Vendors ven = new Vendors();
            ven.storeName = vendor.storeName;
            vendor_names.add(ven.storeName);
            ven.save();
        }

        Intent intent = new Intent(this, SignInActivity.class);
        intent.putStringArrayListExtra(Config.I_VENDOR_LIST, vendor_names);
        startActivity(intent);
    }

    private void ShowError() {
        Toast.makeText(SplashScreen.this,"Failed to Connect to Server",Toast.LENGTH_SHORT).show();
    }
}
