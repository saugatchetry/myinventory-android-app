package zakoi.com.myinventory;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.activeandroid.query.Update;

import java.util.ArrayList;
import java.util.List;

import zakoi.com.myinventory.Utility.Config;
import zakoi.com.myinventory.model.EditReceiptModel;
import zakoi.com.myinventory.model.ItemReceipt;
import zakoi.com.myinventory.model.StockTransfer;

import static android.media.CamcorderProfile.get;

/**
 * Created by saugatchetry on 15/06/17.
 */

public class StockTransferRecyclerAdapter extends RecyclerView.Adapter<StockTransferRecyclerAdapter.MyViewHolder> {

    static List<StockTransfer> data = new ArrayList<>();
    Context ctx;
    public StockTransferRecyclerAdapter(List<StockTransfer> data, Context context) {
        this.data = data;
        this.ctx = context;

    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_layout,parent,false);
        return new MyViewHolder(view,ctx,data);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.tv_vendorName.setText(data.get(position).sourceVendor);
        holder.tv_transferDate.setText(data.get(position).date);
        holder.tv_itemName.setText(data.get(position).itemName);
        String quantity = ""+data.get(position).quantity;
        holder.tv_itemQuantity.setText(quantity);

        int currentPosition = position;
        final StockTransfer stock = data.get(position);

        holder.btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmItem(stock);
            }
        });

    }

    private void confirmItem(StockTransfer stock) {
        Config.UNCONFIRMED_TRANSFERS -= 1;
        int position = data.indexOf(stock);

        data.remove(position);
        new Update(StockTransfer.class).set("status = ?","Confirmed").where("transferId = ?",stock.transferId).execute();
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_vendorName,tv_transferDate,tv_itemName,tv_itemQuantity;
        Button btn_confirm;

        Context ctx;
        List<StockTransfer> data = new ArrayList<>();

        public MyViewHolder(View itemView,Context ctx,List<StockTransfer> data) {
            super(itemView);



            tv_vendorName = (TextView) itemView.findViewById(R.id.fromVendor);
            tv_transferDate = (TextView) itemView.findViewById(R.id.transferDate);
            tv_itemName = (TextView) itemView.findViewById(R.id.itemName);
            tv_itemQuantity = (TextView) itemView.findViewById(R.id.itemQuantity);
            btn_confirm = (Button) itemView.findViewById(R.id.confirmButton);

            this.ctx = ctx;
            this.data = data;

        }




    }



}
