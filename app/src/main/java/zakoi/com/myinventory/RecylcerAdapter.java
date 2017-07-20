package zakoi.com.myinventory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import zakoi.com.myinventory.model.EditReceiptModel;

/**
 * Created by saugatchetry on 04/06/17.
 */

public class RecylcerAdapter extends RecyclerView.Adapter<RecylcerAdapter.MyViewHolder> {

    ArrayList<EditReceiptModel> arrayList = new ArrayList<>();
    Context ctx;

    public RecylcerAdapter(ArrayList<EditReceiptModel> arrayList, Context ctx) {
        this.arrayList = arrayList;
        this.ctx = ctx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row1_layout,parent,false);
        return new MyViewHolder(view,ctx,arrayList);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tv_customerName.setText(arrayList.get(position).getCustomerName());
        holder.tv_itemAmount.setText("" +arrayList.get(position).getAmount());
        holder.tv_itemName.setText(arrayList.get(position).getItemName());
        String quantity = ""+arrayList.get(position).getItemQuantity();
        holder.tv_itemQuantity.setText(quantity);

    }

    @Override
    public int getItemCount() {

        return arrayList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_customerName, tv_itemAmount,tv_itemName,tv_itemQuantity;
        Button tv_editButton;

        Context ctx;
        ArrayList<EditReceiptModel> data = new ArrayList<>();

        public MyViewHolder(View itemView,Context ctx,ArrayList<EditReceiptModel> data) {
            super(itemView);

            tv_customerName = (TextView) itemView.findViewById(R.id.customerName);
            tv_itemAmount = (TextView) itemView.findViewById(R.id.itemAmount);
            tv_itemName = (TextView) itemView.findViewById(R.id.itemName);
            tv_itemQuantity = (TextView) itemView.findViewById(R.id.itemQuantity);
            tv_editButton = (Button) itemView.findViewById(R.id.editReceiptButton);

            tv_editButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    performEdit(v);
                }
            });
            this.ctx = ctx;
            this.data = data;

        }

        public void performEdit(View view) {
            Log.d("Click","clicked");
            int position = getAdapterPosition();
            EditReceiptModel model = this.data.get(position);
            Log.d("Ids","rate - "+model.getAmount());
            Intent intent = new Intent(this.ctx,EditActivity.class);

            intent.putExtra("id",model.getId());
            intent.putExtra("customerName",model.getCustomerName());
            intent.putExtra("customerPhoneNumber",model.getCustomerPhoneNumber());
            intent.putExtra("itemName",model.getItemName());
            intent.putExtra("quantity",""+model.getItemQuantity());
            intent.putExtra("date",model.getDate());
            intent.putExtra("amount",""+model.getAmount());
            //this.ctx.startActivity(intent);

            Activity origin = (Activity)ctx;
            origin.startActivityForResult(intent,1000);

        }
    }


    /*public void onActivityResult(ArrayList<EditReceiptModel> editData){
        Log.d("OnActivityResult","Success");

        this.arrayList = editData;
        notifyDataSetChanged();
    }*/


    public void setFilter(ArrayList<EditReceiptModel> newList){
        arrayList = new ArrayList<>();
        arrayList.addAll(newList);
        notifyDataSetChanged();
    }
}
