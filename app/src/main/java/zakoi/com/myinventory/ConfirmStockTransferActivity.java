package zakoi.com.myinventory;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


import zakoi.com.myinventory.model.StockTransfer;

import static android.R.id.list;

public class ConfirmStockTransferActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    StockTransferRecyclerAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    List<StockTransfer> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_stock_transfer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        recyclerView = (RecyclerView) findViewById(R.id.stockTransferRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


        list = StockTransfer.getAllUnconfirmedTransfers();

        adapter = new StockTransferRecyclerAdapter(list,this);
        recyclerView.setAdapter(adapter);

    }

}
