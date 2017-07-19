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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import zakoi.com.myinventory.model.Items;
import zakoi.com.myinventory.model.Vendors;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class SignInActivity extends AppCompatActivity {



    EditText vendorName;
    Button btnSave;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActiveAndroid.initialize(this);


        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        final String storeName = sharedpreferences.getString(Name,"default");

        if(!storeName.equalsIgnoreCase("default")){
            Intent intent = new Intent(this, SelectTask.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vendorName = (EditText) findViewById(R.id.vendorNameText);
        btnSave = (Button) findViewById(R.id.saveButton);



        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString(Name, vendorName.getText().toString());

                String myvendorName = vendorName.getText().toString();

                editor.commit();

                Toast.makeText(SignInActivity.this,"Thanks",Toast.LENGTH_LONG).show();
                GsonBuilder builder = new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
                Gson gson = builder.create();
                Retrofit.Builder builder1 = new Retrofit.Builder()
                        //.baseUrl("http://10.0.2.2:8080/")
                        .baseUrl("https://myinventory-test.herokuapp.com/")
                        .addConverterFactory(GsonConverterFactory.create(gson));
                Retrofit retrofit = builder1.build();

                ReceiptClient receiptClient = retrofit.create(ReceiptClient.class);
                Call<List<Items>> call = receiptClient.getAllStoreItems(myvendorName);
                call.enqueue(new Callback<List<Items>>() {
                    @Override
                    public void onResponse(Call<List<Items>> call, Response<List<Items>> response) {


                            saveItemsToDB(response.body());

                    }

                    @Override
                    public void onFailure(Call<List<Items>> call, Throwable t) {
                        Log.e("Error","Network call failed because - "+t.getLocalizedMessage());
                    }
                });

                Call<List<Vendors>> call1 = receiptClient.getAllVendors();
                call1.enqueue(new Callback<List<Vendors>>() {
                    @Override
                    public void onResponse(Call<List<Vendors>> call, Response<List<Vendors>> response) {
                        saveVendorsToDB(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<Vendors>> call, Throwable t) {
                        Toast.makeText(SignInActivity.this,"Failed to Connect to Server",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignInActivity.this, SelectTask.class);
                        startActivity(intent);
                        finish();

                    }
                });



            }
        });

    }

    private void saveVendorsToDB(List<Vendors> body) {

        for (Vendors vendor : body){
            Vendors ven = new Vendors();
            ven.storeName = vendor.storeName;
            ven.save();
        }

    }

    private void saveItemsToDB(List<Items> body) {


        for(Items items : body){
            Items i = new Items();
            i.itemName = items.itemName;
            i.rate = items.rate;
            i.uom = items.uom;
            i.save();
        }

        /*for (Map.Entry<String, Float> entry : body.entrySet()) {
            Items items = new Items();
            items.itemName = entry.getKey();
            items.rate = entry.getValue();
            Log.d("Response","itemname = "+items.itemName);
            items.save();
        }
        Log.d("Test","DB create .. Data inserted");*/
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
