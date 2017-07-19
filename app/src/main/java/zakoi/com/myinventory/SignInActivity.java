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
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString(Config.P_STORE_KEY, vendorName.getSelectedItem().toString());
                String myvendorName = vendorName.getSelectedItem().toString();
                editor.commit();
                Toast.makeText(SignInActivity.this,"Successful",Toast.LENGTH_LONG).show();

                GsonBuilder builder = new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
                Gson gson = builder.create();
                Retrofit.Builder builder1 = new Retrofit.Builder()
                        .baseUrl(Config.SERVER_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson));
                Retrofit retrofit = builder1.build();

                ReceiptClient receiptClient = retrofit.create(ReceiptClient.class);
                Call<List<Items>> call = receiptClient.getAllStoreItems(myvendorName);
                call.enqueue(new Callback<List<Items>>() {
                    @Override
                    public void onResponse(Call<List<Items>> call, Response<List<Items>> response) {

                            Log.d("Tag", "Response " + response.body().toString());
                            saveItemsToDB(response.body());

                    }

                    @Override
                    public void onFailure(Call<List<Items>> call, Throwable t) {
                        Log.e("Error","Network call failed because - "+t.getLocalizedMessage());
                    }
                });
            }
        });

    }

    private void saveItemsToDB(List<Items> body) {

        for(Items items : body){
            Items i = new Items();
            i.itemName = items.itemName;
            i.uom = items.uom;
            i.save();
        }

        Intent intent = new Intent(SignInActivity.this, SelectTask.class);
        startActivity(intent);
        finish();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.edit) {
            return true;
        }

        if(id == R.id.sync){

        }

        return super.onOptionsItemSelected(item);
    }
}
