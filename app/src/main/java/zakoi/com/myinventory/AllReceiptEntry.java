package zakoi.com.myinventory;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import zakoi.com.myinventory.model.EditReceiptModel;
import zakoi.com.myinventory.model.ItemReceipt;

public class AllReceiptEntry extends AppCompatActivity implements OnQueryTextListener{

    RecyclerView recyclerView;
    RecylcerAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    ArrayList<EditReceiptModel> editData = new ArrayList<>();
    List<ItemReceipt> allEditableReceipts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_receipt_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        int count = 0;

        allEditableReceipts = ItemReceipt.getAllEditableReceipts();

        for(ItemReceipt i : allEditableReceipts){
            Log.d("Id","ids = "+i.getId());

            editData.add(new EditReceiptModel(i.getId(),i.customerName,i.customerPhoneNumber,i.itemName,i.receiptDate,i.quantity,i.rate));
        }

        /*editData.add(new EditReceiptModel("Saugat","12345","Potato",12.2));
        editData.add(new EditReceiptModel("Shantanu","12345","Potato",12.2));
        editData.add(new EditReceiptModel("Shweta","12345","Potato",12.2));
        editData.add(new EditReceiptModel("Praveen","12345","Potato",12.2));*/


        /*
        * Write method to read entry from DB Table and populate the "editData"
        */

        adapter = new RecylcerAdapter(editData,this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_edit,menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        //newText = newText.toLowerCase();
        ArrayList<EditReceiptModel> newList = new ArrayList<>();

        for(EditReceiptModel model : editData){
            String customerName = model.getCustomerName();

            if(customerName.contains(newText)){
                newList.add(model);
            }
        }

        adapter.setFilter(newList);
        return true;
    }
}
