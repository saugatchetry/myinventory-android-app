package zakoi.com.myinventory;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import zakoi.com.myinventory.Utility.Config;
import zakoi.com.myinventory.model.CustomerInfo;
import zakoi.com.myinventory.model.ItemReceipt;
import zakoi.com.myinventory.model.Items;

public class ReceiptEntry extends AppCompatActivity {

    Button btnSubmitReceipt,dateEditButton;
    EditText et_customerName,et_customerPhoneNumber,et_itemName,et_itemQuantity, et_itemAmount;
    TextView tv_date,tv_itemUOM;

    AutoCompleteTextView actv_itemName;
    List<Items> allItems = new ArrayList<>();
    String[] itemNames;
    List<CustomerInfo> allCustomer = new ArrayList<>();
    DatePickerDialog.OnDateSetListener mDateSetListener;

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";

    String storeName;

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            checkIfAllFieldAreFilled();

        }
    };

    private void checkIfAllFieldAreFilled() {

        btnSubmitReceipt = (Button) findViewById(R.id.submitReceiptButton);
        String date = tv_date.getText().toString();
        String customerName = et_customerName.getText().toString();
        String customerPhoneNumber = et_customerPhoneNumber.getText().toString();
        String itemName = actv_itemName.getText().toString();
        String quantity = et_itemQuantity.getText().toString();
        String amount = et_itemAmount.getText().toString();

        if(date.equals("")||customerName.equals("")||
                itemName.equals("")||quantity.equals("")||amount.equals("")){
            btnSubmitReceipt.setEnabled(false);
        }
        else{
            btnSubmitReceipt.setEnabled(true);
        }
    }


    double selectedItemAmount;

    String itemUOM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActiveAndroid.initialize(this);

        setContentView(R.layout.activity_receipt_entry);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        storeName = sharedpreferences.getString(Name,"default");


        //get Current date

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateandTime = sdf.format(new Date());
        Log.d("Date","date is "+currentDateandTime);

        boolean itemTableExists = new Select()
                        .from(Items.class)
                        .exists();

        boolean customerTableExists = new Select()
                .from(CustomerInfo.class)
                .exists();



        Log.d("Test","exists = "+itemTableExists);

        if(itemTableExists) {
            allItems = Items.getAllItems();
            itemNames = new String[allItems.size()];
            int index = 0;
            for(Items i : allItems){
               itemNames[index] = i.itemName;
               index++;
            }
        }

        if(customerTableExists) {
            allCustomer = CustomerInfo.getAllCustomerInfo();
            for(CustomerInfo i : allCustomer){
                Log.d("Saved Data","item name "+i.customerName+" phone number "+i.customerPhoneNumber);
            }
        }
        /*
            Get a reference to all the ui elementsl
         */

        dateEditButton = (Button) findViewById(R.id.dateEditButton);

        dateEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(ReceiptEntry.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        tv_date = (TextView) findViewById(R.id.dateText);
        tv_date.setText(currentDateandTime);

        tv_itemUOM = (TextView) findViewById(R.id.itemUOMText);

        /*tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(ReceiptEntry.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });*/

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day+"/"+month+"/"+year;
                tv_date.setText(date);
            }
        };

        et_customerName = (EditText) findViewById(R.id.customerNameText);
        et_customerPhoneNumber = (EditText) findViewById(R.id.customerPhoneNumberText);
        //et_itemName = (EditText) findViewById(R.id.itemNameText) ;

        actv_itemName = (AutoCompleteTextView) findViewById(R.id.itemNameText1);

        et_itemQuantity = (EditText) findViewById(R.id.quantityText);
        et_itemAmount = (EditText) findViewById(R.id.amountText);
        btnSubmitReceipt = (Button) findViewById(R.id.submitReceiptButton);

        //textwatcher to enable submit button
        et_customerName.addTextChangedListener(mTextWatcher);
        et_itemQuantity.addTextChangedListener(mTextWatcher);
        et_itemAmount.addTextChangedListener(mTextWatcher);
        //et_customerPhoneNumber.addTextChangedListener(mTextWatcher);
        tv_date.addTextChangedListener(mTextWatcher);



        //Adapter for autocompletetextview

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,itemNames);
        actv_itemName.setAdapter(adapter);

        actv_itemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String itemNameSelected = actv_itemName.getText().toString();

                Log.d("Selected","item selected - "+itemNameSelected);
                for(Items itm : allItems){
                    if(itm.itemName.equalsIgnoreCase(itemNameSelected)){
                        itemUOM = itm.uom;
                    }
                }

                tv_itemUOM.setText(itemUOM);
//                String quant = et_itemQuantity.getText().toString();
//
//                if(!quant.equalsIgnoreCase("")){
//                    et_itemAmount.setText(""+Double.parseDouble(et_itemQuantity.getText().toString())*selectedItemAmount);
//                }
                checkIfAllFieldAreFilled();
            }

            @Override
            public void afterTextChanged(Editable editable) {




            }
        });



//        et_itemQuantity.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                et_itemAmount.setText(""+Double.parseDouble(et_itemQuantity.getText().toString())*selectedItemAmount);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

        btnSubmitReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String date = tv_date.getText().toString();
                String itemName = actv_itemName.getText().toString();
                String itemQuantity = et_itemQuantity.getText().toString();
                String customerName = et_customerName.getText().toString();
                String customerPhoneNumber = et_customerPhoneNumber.getText().toString();
                String itemAmount = et_itemAmount.getText().toString();
                double quantity = Double.parseDouble(itemQuantity);
                double amount = Double.parseDouble(itemAmount);
                int canEdit = 1;
                int synced = 0;
                saveReceiptToDB(customerName,customerPhoneNumber,itemName,quantity,amount,canEdit,date,synced);
                Log.d("Date","Date is - "+tv_date.getText().toString());
                clearForm();

            }
        });

    }

    private void clearForm() {
        et_customerPhoneNumber.setText("");
        et_itemAmount.setText("");
        actv_itemName.setText("");
        et_itemQuantity.setText("");
        tv_itemUOM.setText("");
    }


    private void saveReceiptToDB(String customerName, String customerPhoneNumber, String itemName, double quantity, double amount,int canEdit,String date, int synced) {

        ItemReceipt itemReceipt = new ItemReceipt();
        itemReceipt.itemName = itemName;
        itemReceipt.quantity = quantity;
        itemReceipt.amount = amount;
        itemReceipt.customerName = customerName;
        itemReceipt.customerPhoneNumber = customerPhoneNumber;
        itemReceipt.editable = canEdit;
        itemReceipt.receiptDate = date;
        itemReceipt.sync_cloud = synced;
        itemReceipt.save();


    }


    private void sendDataToServer() {

//        List<ItemReceipt> savedReceipts = ItemReceipt.getAllUnsyncedReceipts();
//        List<Receipts> receiptList = new ArrayList<>();
//        for(ItemReceipt ir : savedReceipts) {
//            Receipts receipts = new Receipts();
//            receipts.setItemName(ir.itemName);
//            receipts.setCustomerName(ir.customerName);
//            receipts.setCustomerPhoneNumber(ir.customerPhoneNumber);
//            receipts.setQuantity(ir.quantity);
//            receipts.setAmount(ir.amount);
//            receipts.setReceiptDate(ir.receiptDate);
//            receipts.setReceiptOutletName(storeName);
//            receiptList.add(receipts);
//        }
//
//        Log.d("Editable","size of receipts - "+receiptList.size());
//
//        Retrofit.Builder builder = new Retrofit.Builder()
//                //.baseUrl("http://10.0.2.2:8080/")
//                .baseUrl(Config.SERVER_URL)
//                .addConverterFactory(GsonConverterFactory.create());
//        Retrofit retrofit = builder.build();
//
//        ReceiptClient receiptClient = retrofit.create(ReceiptClient.class);
//
//        Call<Void> call = receiptClient.submitReceipt(receiptList);
//
//        call.enqueue(new Callback() {
//            @Override
//            public void onResponse(Call call, Response response) {
//
//                Toast.makeText(ReceiptEntry.this,"Submitted",Toast.LENGTH_SHORT).show();
//                updateAllSubmittedReceipts();
//
//            }
//
//            @Override
//            public void onFailure(Call call, Throwable t) {
//                Log.d("Sync","Error :- "+t.getMessage());
//                Toast.makeText(ReceiptEntry.this,"Failed Reason :- "+t.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    private void updateAllSubmittedReceipts() {
        new Update(ItemReceipt.class).set("sync_cloud = 1, editable = 0").execute();

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
            Intent intent = new Intent(ReceiptEntry.this,AllReceiptEntry.class);
            startActivity(intent);
        }

        if(id == R.id.sync){
            sendDataToServer();
        }

        return super.onOptionsItemSelected(item);
    }






}
