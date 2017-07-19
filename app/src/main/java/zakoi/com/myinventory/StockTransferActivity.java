package zakoi.com.myinventory;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import zakoi.com.myinventory.model.Items;
import zakoi.com.myinventory.model.OutgoingStockTransfer;
import zakoi.com.myinventory.model.StockTransfer;
import zakoi.com.myinventory.model.Vendors;

import static android.R.attr.vendor;

public class StockTransferActivity extends AppCompatActivity {

    List<Vendors> allVendors = new ArrayList<>();
    List<Items> allItems = new ArrayList<>();
    ArrayAdapter<String> adapterVendor, adapterItem;
    Spinner sp_vendorName;
    AutoCompleteTextView sp_itemName;
    Button btn_save;
    TextView tv_date;
    EditText et_quantity;

    String storeName;

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_transfer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        storeName = sharedpreferences.getString(Name,"default");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateandTime = sdf.format(new Date());

        //read the vendor table
        allVendors = readTheVendorTable();


        List<String> vendorNames = new ArrayList<>();

        for(Vendors v : allVendors){
            vendorNames.add(v.storeName);
        }


        //read the item table
        allItems = readTheItemTable();

        List<String> itemNames = new ArrayList<>();

        for(Items i : allItems){
            itemNames.add(i.itemName);
        }


        tv_date = (TextView) findViewById(R.id.setDateLabel);

        tv_date.setText(currentDateandTime);

        btn_save = (Button) findViewById(R.id.saveTransferButton);
        sp_vendorName = (Spinner) findViewById(R.id.vendorSpinner);
        sp_itemName = (AutoCompleteTextView) findViewById(R.id.itemSpinner);
        et_quantity = (EditText) findViewById(R.id.quantityEntry);

        adapterVendor = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,vendorNames);
        adapterVendor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_vendorName.setAdapter(adapterVendor);

        adapterItem = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,itemNames);

        sp_itemName.setAdapter(adapterItem);


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OutgoingStockTransfer ost = new OutgoingStockTransfer();
                ost.transferDate = tv_date.getText().toString();
                ost.sourceVendor = storeName;
                ost.targetVendor = sp_vendorName.getSelectedItem().toString();
                ost.itemName = sp_itemName.getText().toString();
                ost.quantity = Integer.parseInt(et_quantity.getText().toString());
                ost.status = "Initiated";
                ost.sync_cloud = 0;

                ost.save();

                Toast.makeText(StockTransferActivity.this,"Saved",Toast.LENGTH_SHORT).show();
                clearForm();

            }
        });
    }

    private void clearForm() {

        et_quantity.setText("");
        sp_itemName.setText("");

    }

    private List<Vendors> readTheVendorTable() {
        return Vendors.getAllVendors();
    }

    private List<Items> readTheItemTable() {
        return Items.getAllItems();
    }
}
