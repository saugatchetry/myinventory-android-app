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
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import zakoi.com.myinventory.Utility.Config;
import zakoi.com.myinventory.Utility.NetworkManager;
import zakoi.com.myinventory.Utility.ToastManager;
import zakoi.com.myinventory.Utility.Util;
import zakoi.com.myinventory.model.ItemReceipt;
import zakoi.com.myinventory.model.Items;
import zakoi.com.myinventory.model.OutgoingStockTransfer;
import zakoi.com.myinventory.model.StockTransfer;

import static zakoi.com.myinventory.R.id.id_tv_cash;
import static zakoi.com.myinventory.R.id.id_tv_date;
import static zakoi.com.myinventory.R.id.id_tv_stockTransfer;
import static zakoi.com.myinventory.R.id.id_tv_storeName;
import static zakoi.com.myinventory.R.id.stockTransfer;

public class SelectTask extends AppCompatActivity implements View.OnClickListener{

    private final static String TAG = "Select Task";

    int MY_PREMISSON_REQUEST_SEND_SMS = 1;
    int call_count = 0;
    Button btn_saveReceipts,btn_stockTransfer,btn_checkOut,btn_sync,btn_confirmStockTransfer;
    List<StockTransfer> list = new ArrayList<>();
    HashSet<Integer> stockIds = new HashSet<>();
    AlertDialog alertDialog,downloadingDialog,confirmSendSMSDialog,resetDialog,cantResetDialog, syncingDataDialog;

    SharedPreferences sharedpreferences;
    String storeName;
    String sent= "sms sent";
    String delivered = "sms delivered";
    String timeStamp = "";
    PendingIntent sentPI,deliveredPI;
    double totalCash = 0;


    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
    TextView tv_storeName,tv_stockTransfer,tv_date,tv_totalCash;
    ToastManager _toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedpreferences = getSharedPreferences(Config.SHARED_PREF_STORE, Context.MODE_PRIVATE);
        storeName = sharedpreferences.getString(Config.P_STORE_KEY,"default");

        btn_checkOut = (Button) findViewById(R.id.checkOut);
        btn_saveReceipts = (Button) findViewById(R.id.saveEntry);
        btn_stockTransfer = (Button) findViewById(stockTransfer);
        btn_sync = (Button)findViewById(R.id.sync);
        btn_confirmStockTransfer = (Button) findViewById(R.id.confirmStock);

        tv_date = (TextView) findViewById(id_tv_date);
        tv_totalCash = (TextView) findViewById(id_tv_cash);
        tv_stockTransfer = (TextView) findViewById(id_tv_stockTransfer);
        tv_storeName = (TextView) findViewById(id_tv_storeName);

        btn_checkOut.setOnClickListener(this);
        btn_saveReceipts.setOnClickListener(this);
        btn_stockTransfer.setOnClickListener(this);
        btn_sync.setOnClickListener(this);
        btn_confirmStockTransfer.setOnClickListener(this);

        sentPI = PendingIntent.getBroadcast(this,0,new Intent(sent),0);
        deliveredPI = PendingIntent.getBroadcast(this,0,new Intent(delivered), 0);

        AlertDialog.Builder downloadingAlertDialogBuilder = new AlertDialog.Builder(this);
        downloadingAlertDialogBuilder.setMessage("Syncing with Server .... ");
        downloadingDialog = downloadingAlertDialogBuilder.create();

        AlertDialog.Builder confirmSendSMSAlertDialogBuilder = new AlertDialog.Builder(this);
        confirmSendSMSDialog = confirmSendSMSAlertDialogBuilder.create();
        confirmSendSMSDialog.setTitle("Send SMS");
        String sourceString = "Total Cash Collected : " + "<b>" + tv_totalCash.getText() + "</b> ";
        confirmSendSMSDialog.setMessage(Html.fromHtml(sourceString));
        confirmSendSMSDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Send",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.dismiss();
                        sendSMS();
                    }
                });
        confirmSendSMSDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        AlertDialog.Builder resetBuilder = new AlertDialog.Builder(this);
        resetDialog = resetBuilder.create();
        resetDialog.setTitle("Reset");
        resetDialog.setMessage("Do you want to change your store?");
        resetDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Reset();
                    }
                });
        resetDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"No",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog.Builder cantResetBuilder = new AlertDialog.Builder(this);
        cantResetDialog = cantResetBuilder.create();
        cantResetDialog.setTitle("Reset Not Allowed");
        cantResetDialog.setMessage("Do you want to change your store?");
        cantResetDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        _toast = ToastManager.getInstance();
        tv_storeName.setText(storeName);
        tv_date.setText(Util.getDate());
    }

    private void Reset() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Config.P_STORE_KEY, "default");
        editor.putString(Config.P_TIME_STAMP, "");
        editor.commit();

        //clear all DBs
        Util.ClearAllDBs();

        //Reset Network
        NetworkManager.getInstance().Reset();

        Intent intent = new Intent(this, SplashScreen.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onResume(){

        super.onResume();
        SyncAll();
        totalCash = getTotalCashAmount();
        String msg = "" + totalCash;
        tv_totalCash.setText(msg);

        msg = "" + Config.UNCONFIRMED_TRANSFERS;
        tv_stockTransfer.setText(msg);

        ToastManager.getInstance().Reset();
        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        _toast.SendSMS(SelectTask.this,"SMS Sent, Syncing Now");
                        totalCash = 0;
                        String msg = "" + totalCash;
                        tv_totalCash.setText(msg);

                        SyncAll();
                        //Toast.makeText(SelectTask.this,"SMS Sent Successfully",Toast.LENGTH_SHORT).show();
                        break;

                    /*
                        why sync with default result code ??
                     */

                    default:
                        Toast.makeText(SelectTask.this,"SMS Couldn't be Sent",Toast.LENGTH_SHORT).show();
                        SyncAll(); //this might cause a problem ....
                        break;
                }
            }
        };

        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        // ToastManager.getInstance().SendSMS(SelectTask.this,"SMS Delivered");
                        // Toast.makeText(SelectTask.this,"SMS Delivered Successfully",Toast.LENGTH_SHORT).show();
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
        ToastManager.getInstance().DismissToast();
        try {
            unregisterReceiver(smsSentReceiver);
            unregisterReceiver(smsDeliveredReceiver);
        }
        catch(IllegalArgumentException e) {
            Log.e("Select Task", "SMS not registered");
        }
    }

    private void readAllStockTransfers() {

         list = StockTransfer.getAllUnconfirmedTransfers();
          Config.UNCONFIRMED_TRANSFERS = list.size();

         if(list.size() > 0){

//             if (!NetworkManager.ShowAlertDialogBox())
//                 return;

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

    }

    private void getAllTransferStocks() {

//        GsonBuilder builder = new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
//        Gson gson = builder.create();
//        Retrofit.Builder builder1 = new Retrofit.Builder()
//                .baseUrl(Config.SERVER_URL)
//                .addConverterFactory(GsonConverterFactory.create(gson));
//        Retrofit retrofit = builder1.build();
//
//        ReceiptClient receiptClient = retrofit.create(ReceiptClient.class);
//        _toast.ShowSyncToast(this);
        Call<List<StockTransfer>> call = NetworkManager.getInstance().client.getAllStockTransfers(storeName);
        call.enqueue(new Callback<List<StockTransfer>>() {
            @Override
            public void onResponse(Call<List<StockTransfer>> call, Response<List<StockTransfer>> response) {

                downloadingDialog.dismiss();
                _toast.DismissToast();
                saveStockTransferDataToTable(response.body());

            }

            @Override
            public void onFailure(Call<List<StockTransfer>> call, Throwable t) {
                Log.e("Error","Failed baba Network call failed because - "+t.getLocalizedMessage());
                downloadingDialog.dismiss();
                _toast.DismissToast();
                OnError(t);
            }
        });
    }

    private void saveStockTransferDataToTable(List<StockTransfer> body) {
        if(body == null) {
            Log.d("SelectTask", "Save stock trasfer table returned null");
            return;
        }
        list = StockTransfer.getAllUnconfirmedTransfers();
        for(StockTransfer st : list){
            stockIds.add(st.transferId);
        }
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
                String sourceString = "Total Cash Collected : " + "<b>" + tv_totalCash.getText() + "</b> ";
                confirmSendSMSDialog.setMessage(Html.fromHtml(sourceString));
                confirmSendSMSDialog.show();
                makeAllReceiptsUneditable();
                Config.SYNC = true;
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
                SyncAll(true);
                break;
        }
    }

    private void SyncAll() {
        SyncAll(false);
    }

    private void SyncAll(boolean force) {

        if(Config.SYNC) {
            Log.i(TAG, "Sync called");
            Config.SYNC = false;
            syncAllConfirmedTransfers();
        }

        if(force || NetworkManager.CallGetItems()) { {
            Log.i(TAG, "Get items called");
//            if (force)
                _toast.ShowSyncToast(this);
            getAllTransferStocks();
            CheckForNewItems();
        }}

        boolean stockTransferTableExists = new Select()
                .from(StockTransfer.class)
                .exists();

        if(force || (stockTransferTableExists && NetworkManager.CallRefreshStock())){
            readAllStockTransfers();
        }

        Config.SYNC = false;
    }


    private void syncAllConfirmedTransfers() {

        // downloadingDialog.show();
        Log.i(TAG, "Syncing confirmed transfers");

        List<StockTransfer> confirmedStocks = StockTransfer.getAllConfirmedTransfers();
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

                //Toast.makeText(SelectTask.this,"Submitted",Toast.LENGTH_SHORT).show();
                //now mark them as synced in db
                for(Integer id : ids) {
                    new Update(StockTransfer.class).set("sync_cloud = 1").where("transferId = ?",id).execute();
                }
                _toast.ShowSyncToast(SelectTask.this);
                syncAllOutgoingTransfer();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Config.SYNC = true;
                downloadingDialog.dismiss();
                _toast.DismissToast();
                OnError(t);
            }
        });

    }


    private void syncAllOutgoingTransfer(){

        Log.i(TAG, "Syncing outgoing transfers");
        List<OutgoingStockTransfer> outgoingStocks = OutgoingStockTransfer.getaAllOutgoingTransfers();
        GsonBuilder builder = new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
        Gson gson = builder.create();
        Retrofit.Builder builder1 = new Retrofit.Builder()
                .baseUrl(Config.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(gson));
        Retrofit retrofit = builder1.build();

        ReceiptClient receiptClient = retrofit.create(ReceiptClient.class);
        Call<Void> call = receiptClient.submitAllOutgoingStockTransfers(outgoingStocks);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                //Toast.makeText(SelectTask.this,"Submitted",Toast.LENGTH_SHORT).show();
                new Update(OutgoingStockTransfer.class).set("sync_cloud = 1").execute();
                syncAllUnsyncedReceipts();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Config.SYNC = true;
                downloadingDialog.dismiss();
                _toast.DismissToast();
                OnError(t);
            }
        });
    }


    public void syncAllUnsyncedReceipts(){
        Log.i(TAG, "Syncing receipts");
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
            receipts.setReceiptDate(Util.ChangeDateFormat(ir.receiptDate));
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

                // Toast.makeText(SelectTask.this,"Submitted",Toast.LENGTH_SHORT).show();
                 downloadingDialog.dismiss();
                //_toast.DismissToast();
                for(Long id: ids) {
                    new Update(ItemReceipt.class).set("sync_cloud = 1").where("Id = ?",id).execute();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Config.SYNC = true;
                 downloadingDialog.dismiss();
                //_toast.DismissToast();
                OnError(t);
            }
        });
    }

    private void makeAllReceiptsUneditable() {
        new Update(ItemReceipt.class).set("editable = 0").execute();

    }

    private double getTotalCashAmount(){
        List<ItemReceipt> allReceipts = ItemReceipt.getAllEditableReceipts();
        totalCash = 0.0;
        for(ItemReceipt ir : allReceipts){
            if (ir.customerName.equalsIgnoreCase("Cash")) {
                totalCash = totalCash + ir.amount;
            }
        }
        Log.d("TotalCash","Cash = "+totalCash);
        return totalCash;
    }

    private void sendSMS() {

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());

        Log.d("date","date = "+date);

        String message = "Date - " + date
                        +"\nStore Name - " + storeName
                        +"\nTotal cash - " + totalCash;

        if(ContextCompat.checkSelfPermission(SelectTask.this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SelectTask.this,new String[]{android.Manifest.permission.SEND_SMS},MY_PREMISSON_REQUEST_SEND_SMS);
        }
        else{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(Config.PHONE_NUMBER,null,message,sentPI,deliveredPI);
            makeAllReceiptsUneditable(); // added by Saugat to fix error reported on 11th-August-2017
        }
    }

    private void updateAllSubmittedReceipts() {
        new Update(ItemReceipt.class).set("sync_cloud = 1").execute();
    }

    private void CheckForNewItems() {
        //_toast.ShowSyncToast(this);
        Call<ResponseBody> call = NetworkManager.getInstance().client.getLastUpdatedTime();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String currentTimeStamp = "";
                _toast.DismissToast();
                try {
                    if(response != null && response.body() != null)
                        currentTimeStamp = response.body().string();
                    else {
                        OnError("Could not connect ");
                        return;
                    }
                } catch (IOException e) {
                    Log.e("SelectTask","Error on processing timestamp");
                    return;
                }

                CheckTimeStamp(currentTimeStamp);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                _toast.DismissToast();
                OnError(t);
            }
        });
    }

    void OnError(Throwable t) {
        Log.d("Sync","Error :- "+t.getMessage());
        _toast.ShowError(SelectTask.this, "Offline, check Internet");
//        Toast.makeText(SelectTask.this,"Failed Reason :- "+t.getLocalizedMessage(),Toast.LENGTH_LONG).show();

    }

    void OnError(String message) {
        _toast.ShowError(SelectTask.this, message);
    }

    void CheckTimeStamp(final String currentTimeStamp) {

        String timeStamp = sharedpreferences.getString(Config.P_TIME_STAMP,"default");

        if (timeStamp.equals(currentTimeStamp)) {
            return;
        }

        timeStamp = currentTimeStamp;

//        boolean itemTableExists = new Select()
//                .from(Items.class)
//                .exists();

        Call<List<Items>> call = NetworkManager.getInstance().client.getAllStoreItems(storeName);
        call.enqueue(new Callback<List<Items>>() {
            @Override
            public void onResponse(Call<List<Items>> call, Response<List<Items>> response) {
                List<Items> new_items_list = response.body();
                AddnDeleteItemsInDB(new_items_list);
            }

            @Override
            public void onFailure(Call<List<Items>> call, Throwable t) {
                OnError(t);
            }
        });
    }

    private void AddnDeleteItemsInDB(List<Items> new_items_list) {
        HashMap<String, Items> items_to_be_deleted = GetItemMap(Items.getAllItems());;
        HashMap<String, Items> items_in_db = UpdateDeletedItemsAndReturnItemsInDB(new_items_list,items_to_be_deleted);
        Items.deleteItems(items_to_be_deleted);

        List<Items> new_items = FilterExistingItems(new_items_list, items_in_db);
        Util.saveItemsToDB(new_items);

        // Update Timestamp
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Config.P_TIME_STAMP, timeStamp);
        editor.commit();
    }

    private HashMap<String, Items> GetItemMap(List<Items> item_list) {
        HashMap<String, Items> return_array =  new HashMap<String, Items>();

        for(Items item : item_list) {
            return_array.put(item.itemName, item);
        }
        return return_array;
    }

    private List<Items> FilterExistingItems(List<Items> item_list, HashMap<String,Items> exists_set) {
        List<Items> return_list = new ArrayList<>();
        for(Items item : item_list) {
            if(!exists_set.containsKey(item.itemName))
                return_list.add(item);
        }
        return return_list;
    }

    private HashMap<String, Items> UpdateDeletedItemsAndReturnItemsInDB(List<Items> server_items_list, HashMap<String, Items> local_items_set) {
        HashMap<String, Items> updated_items = new HashMap<>();
        for(Items item : server_items_list) {
            if(local_items_set.containsKey(item.itemName)) {
                Items temp = local_items_set.get(item.itemName);
                if (temp.uom.equals(item.uom)) {
                    updated_items.put(item.itemName, item);
                    local_items_set.remove(item.itemName);
                }
            }
        }
        return updated_items;
    }

    private String CheckIfResetPossible() {
        if (ItemReceipt.getAllEditableReceipts().size() > 0) {
            return "There are editable receipts, checkout and try again";
        }

        if (ItemReceipt.getAllCommitedButUnsyncedReceipts().size() > 0) {
            return "There are unsynced receipts, Press Sync and try again";
        }

        if (StockTransfer.getAllUnconfirmedTransfers().size() > 0) {
            return "There are unconfirmed stock transfers, Confirm and try again";
        }

        if (OutgoingStockTransfer.getaAllOutgoingTransfers().size() > 0 ||
                StockTransfer.getAllConfirmedTransfers().size() > 0) {
            return "There are unsynced stock transfers, Press Sync and try again";
        }
        return "Yes";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_task, menu);
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
            Intent intent = new Intent(SelectTask.this,AllReceiptEntry.class);
            startActivity(intent);
        }
        if(id == R.id.reset){
            String message = CheckIfResetPossible();
            if (message.equalsIgnoreCase("Yes")) {
                resetDialog.show();
            }
            else {
                cantResetDialog.setMessage(message);
                cantResetDialog.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
