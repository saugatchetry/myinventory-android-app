package zakoi.com.myinventory;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import zakoi.com.myinventory.Utility.Config;
import zakoi.com.myinventory.model.ItemReceipt;
import zakoi.com.myinventory.model.OutgoingStockTransfer;
import zakoi.com.myinventory.model.StockTransfer;

import static zakoi.com.myinventory.R.id.stockTransfer;

public class SelectTask extends AppCompatActivity implements View.OnClickListener{

    int MY_PREMISSON_REQUEST_SEND_SMS = 1;
    Button btn_saveReceipts,btn_stockTransfer,btn_checkOut,btn_sync,btn_confirmStockTransfer;
    List<StockTransfer> list = new ArrayList<>();
    List<Integer> stockIds = new ArrayList<>();
    AlertDialog alertDialog,downloadingDialog, syncingDataDialog;

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    String storeName;

    String sent= "sms sent";
    String delivered = "sms delivered";

    PendingIntent sentPI,deliveredPI;

    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        storeName = sharedpreferences.getString(Name,"default");

        btn_checkOut = (Button) findViewById(R.id.checkOut);
        btn_saveReceipts = (Button) findViewById(R.id.saveEntry);
        btn_stockTransfer = (Button) findViewById(stockTransfer);
        btn_sync = (Button)findViewById(R.id.sync);
        btn_confirmStockTransfer = (Button) findViewById(R.id.confirmStock);

        btn_checkOut.setOnClickListener(this);
        btn_saveReceipts.setOnClickListener(this);
        btn_stockTransfer.setOnClickListener(this);
        btn_sync.setOnClickListener(this);
        btn_confirmStockTransfer.setOnClickListener(this);

        sentPI = PendingIntent.getBroadcast(this,0,new Intent(sent),0);
        deliveredPI = PendingIntent.getBroadcast(this,0,new Intent(delivered), 0);

        boolean stockTransferTableExists = new Select()
                .from(StockTransfer.class)
                .exists();
        if(stockTransferTableExists){
            readAllStockTransfers();
        }

        getAllTransferStocks();

        AlertDialog.Builder downloadingAlertDialogBuilder = new AlertDialog.Builder(this);
        downloadingAlertDialogBuilder.setMessage("Syncing with Server .... ");


        downloadingDialog = downloadingAlertDialogBuilder.create();
        downloadingDialog.show();

    }



    @Override
    protected void onResume(){
        super.onResume();

        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(SelectTask.this,"SMS Sent Successfully",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(SelectTask.this,"SMS Couldn't be Sent",Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        };

        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(SelectTask.this,"SMS Delivered Successfully",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(SelectTask.this,"SMS Couldn't be Delivered",Toast.LENGTH_SHORT).show();
                        break;
                }


            }
        };

        registerReceiver(smsSentReceiver, new IntentFilter(sent));
        registerReceiver(smsDeliveredReceiver, new IntentFilter(delivered));
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
    }

    private void readAllStockTransfers() {

         list = StockTransfer.getAllUnconfirmedTransfers();
         if(list.size() > 0){

             AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
             alertDialogBuilder.setMessage("You have pending transfer Request. Would you like to confirm it ?");
                     alertDialogBuilder.setPositiveButton("yes",
                             new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface arg0, int arg1) {
                                     Intent intent = new Intent(SelectTask.this,ConfirmStockTransferActivity.class);
                                     startActivity(intent);
                                 }
                             });

             alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     alertDialog.dismiss();
                 }
             });

             alertDialog = alertDialogBuilder.create();
             alertDialog.show();





         }
        for(StockTransfer st : list){
            stockIds.add(st.transferId);
        }
    }

    private void getAllTransferStocks() {

        GsonBuilder builder = new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
        Gson gson = builder.create();
        Retrofit.Builder builder1 = new Retrofit.Builder()
                //.baseUrl("http://10.0.2.2:8080/")
                .baseUrl(Config.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(gson));
        Retrofit retrofit = builder1.build();

        ReceiptClient receiptClient = retrofit.create(ReceiptClient.class);
        Call<List<StockTransfer>> call = receiptClient.getAllStockTransfers("shantanu");
        call.enqueue(new Callback<List<StockTransfer>>() {
            @Override
            public void onResponse(Call<List<StockTransfer>> call, Response<List<StockTransfer>> response) {

                downloadingDialog.dismiss();
                saveStockTransferDataToTable(response.body());

            }

            @Override
            public void onFailure(Call<List<StockTransfer>> call, Throwable t) {
                Log.e("Error","Failed baba Network call failed because - "+t.getLocalizedMessage());
                downloadingDialog.dismiss();

            }
        });
    }

    private void saveStockTransferDataToTable(List<StockTransfer> body) {

        for(StockTransfer st : body){
            if(!stockIds.contains(st.transferId)) {
                StockTransfer stockTransfer = new StockTransfer();
                stockTransfer.date = st.date;
                stockTransfer.itemName = st.itemName;
                stockTransfer.sourceVendor = st.sourceVendor;
                stockTransfer.targerVendor = st.targerVendor;
                stockTransfer.transferId = st.transferId;
                stockTransfer.quantity = st.quantity;
                stockTransfer.status = st.status;

                stockTransfer.save();
            }
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.checkOut:
                getTotalCashAmount();
                makeAllReceiptsUneditable();

                /*
                    Now sync all the confirmed transfers requests
                 */

                //syncAllConfirmedTransfers();


                break;
            case stockTransfer:
                Intent stockIntent = new Intent(SelectTask.this,StockTransferActivity.class);
                startActivity(stockIntent);
                break;
            case R.id.saveEntry:
                Intent intent = new Intent(SelectTask.this,ReceiptEntry.class);
                startActivity(intent);
                break;

            case R.id.confirmStock:
                Intent confirmStockIntent = new Intent(SelectTask.this,ConfirmStockTransferActivity.class);
                startActivity(confirmStockIntent);
                break;

            case R.id.sync:

                syncAllConfirmedTransfers();


                break;
        }
    }

    private void syncAllConfirmedTransfers() {

        downloadingDialog.show();

        List<StockTransfer> confirmedStocks = StockTransfer.getaAllConfirmedTransfers();
        final ArrayList<Integer> ids = new ArrayList<>();
        for(StockTransfer st : confirmedStocks){
            ids.add(st.transferId);
        }

        Retrofit.Builder builder = new Retrofit.Builder()
                //.baseUrl("http://10.0.2.2:8080/")
                .baseUrl(Config.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        ReceiptClient receiptClient = retrofit.create(ReceiptClient.class);

        Call<Void> call = receiptClient.submitConfirmedTransfers(ids);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                Toast.makeText(SelectTask.this,"Submitted",Toast.LENGTH_SHORT).show();
                //now mark them as synced in db
                for(Integer id : ids) {
                    new Update(StockTransfer.class).set("sync_cloud = 1").where("transferId = ?",id).execute();
                }
                syncAllOutgoingTransfer();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                downloadingDialog.dismiss();
                Log.d("Sync","Error :- "+t.getMessage());
                Toast.makeText(SelectTask.this,"Failed Reason :- "+t.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void syncAllOutgoingTransfer(){
        List<OutgoingStockTransfer> outgoingStocks = OutgoingStockTransfer.getaAllOutgoingTransfers();




        GsonBuilder builder = new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
        Gson gson = builder.create();
        Retrofit.Builder builder1 = new Retrofit.Builder()
                //.baseUrl("http://10.0.2.2:8080/")
                .baseUrl(Config.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(gson));
        Retrofit retrofit = builder1.build();

        ReceiptClient receiptClient = retrofit.create(ReceiptClient.class);

        Call<Void> call = receiptClient.submitAllOutgoingStockTransfers(outgoingStocks);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                Toast.makeText(SelectTask.this,"Submitted",Toast.LENGTH_SHORT).show();
                new Update(OutgoingStockTransfer.class).set("sync_cloud = 1").execute();
                syncAllUnsyncedReceipts();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                downloadingDialog.dismiss();
                Log.d("Sync","Error :- "+t.getMessage());
                Toast.makeText(SelectTask.this,"Failed Reason :- "+t.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void syncAllUnsyncedReceipts(){


            List<ItemReceipt> savedReceipts = ItemReceipt.getAllCommitedButUnsyncedReceipts();
            List<Receipts> receiptList = new ArrayList<>();
            final List<Long> ids = new ArrayList<>();
            for(ItemReceipt ir : savedReceipts) {
                Receipts receipts = new Receipts();
                receipts.setItemName(ir.itemName);
                receipts.setCustomerName(ir.customerName);
                receipts.setCustomerPhoneNumber(ir.customerPhoneNumber);
                receipts.setQuantity(ir.quantity);
                receipts.setAmount(ir.amount);
                receipts.setReceiptDate(ir.receiptDate);
                receipts.setReceiptOutletName(storeName);
                receiptList.add(receipts);

                ids.add(ir.getId());
            }

            Log.d("Editable","size of receipts - "+receiptList.size());

            Retrofit.Builder builder = new Retrofit.Builder()
                    //.baseUrl("http://10.0.2.2:8080/")
                    .baseUrl(Config.SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();

            ReceiptClient receiptClient = retrofit.create(ReceiptClient.class);

            Call<Void> call = receiptClient.submitReceipt(receiptList);

            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {

                    Toast.makeText(SelectTask.this,"Submitted",Toast.LENGTH_SHORT).show();
                    downloadingDialog.dismiss();
                    for(Long id: ids) {
                        new Update(ItemReceipt.class).set("sync_cloud = 1").where("Id = ?",id).execute();
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    downloadingDialog.dismiss();
                    Log.d("Sync","Error :- "+t.getMessage());
                    Toast.makeText(SelectTask.this,"Failed Reason :- "+t.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            });


    }

    private void makeAllReceiptsUneditable() {
        new Update(ItemReceipt.class).set("editable = 0").execute();

    }

    private void getTotalCashAmount(){
        List<ItemReceipt> allReceipts = ItemReceipt.getAllEditableReceipts();
        double totalCash = 0.0;
        for(ItemReceipt ir : allReceipts){
            totalCash = totalCash + ir.amount;
        }

        sendSMS(totalCash);

        Log.d("TotalCash","Cash = "+totalCash);
    }

    private void sendSMS(double totalCash) {

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());

        Log.d("date","date = "+date);

        String message = "Time Staamp - "+date
                        +"\nStore Name - "+storeName
                        +"\nTotal cash - "+totalCash;

        if(ContextCompat.checkSelfPermission(SelectTask.this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SelectTask.this,new String[]{android.Manifest.permission.SEND_SMS},MY_PREMISSON_REQUEST_SEND_SMS);
        }
        else{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("3528881024",null,message,sentPI,deliveredPI);
        }
    }

    private void updateAllSubmittedReceipts() {
        new Update(ItemReceipt.class).set("sync_cloud = 1").execute();

    }

}
