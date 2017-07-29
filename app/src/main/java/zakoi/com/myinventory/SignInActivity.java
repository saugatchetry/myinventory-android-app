package zakoi.com.myinventory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import zakoi.com.myinventory.Utility.Config;
import zakoi.com.myinventory.Utility.NetworkManager;
import zakoi.com.myinventory.Utility.Util;
import zakoi.com.myinventory.model.Items;
import zakoi.com.myinventory.model.Vendors;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class SignInActivity extends AppCompatActivity {



    Spinner vendorName;
    Button btnSave;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActiveAndroid.initialize(this);

        sharedpreferences = getSharedPreferences(Config.SHARED_PREF_STORE, Context.MODE_PRIVATE);

        final String storeName = sharedpreferences.getString(Config.P_STORE_KEY,"default");

        if(!storeName.equalsIgnoreCase("default")){
            Intent intent = new Intent(this, SelectTask.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ArrayList<String> vendorNames = getIntent().getStringArrayListExtra(Config.I_VENDOR_LIST);

        vendorName = (Spinner) findViewById(R.id.vendorNameSpinner);
        btnSave = (Button) findViewById(R.id.saveButton);
        ArrayAdapter<String> adapterVendor = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,vendorNames);
        adapterVendor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vendorName.setAdapter(adapterVendor);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String storeName = vendorName.getSelectedItem().toString();

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(Config.P_STORE_KEY, storeName);
                editor.putString(Config.P_TIME_STAMP, "");
                editor.commit();
                Intent intent = new Intent(SignInActivity.this, SelectTask.class);
                startActivity(intent);
                finish();

//                Toast.makeText(SignInActivity.this,"Successful",Toast.LENGTH_LONG).show();
//                ReceiptClient receiptClient = NetworkManager.getInstance().client;
//                Call<List<Items>> call = receiptClient.getAllStoreItems(storeName);
//                call.enqueue(new Callback<List<Items>>() {
//                    @Override
//                    public void onResponse(Call<List<Items>> call, Response<List<Items>> response) {
//                        Log.d("Tag", "Response " + response.body().toString());
//                        saveItemsToDB(response.body());
//                    }
//
//                    @Override
//                    public void onFailure(Call<List<Items>> call, Throwable t) {
//                        Log.e("Error","Network call failed because - "+t.getLocalizedMessage());
//                    }
//                });
            }
        });

    }

    private void saveItemsToDB(List<Items> body) {
        Util.saveItemsToDB(body);
        Intent intent = new Intent(SignInActivity.this, SelectTask.class);
        startActivity(intent);
        finish();
    }

    
}
