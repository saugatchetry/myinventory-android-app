package zakoi.com.myinventory;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.activeandroid.query.Update;

import java.util.Calendar;

import zakoi.com.myinventory.model.ItemReceipt;

public class EditActivity extends AppCompatActivity {

    EditText et_dateText,et_customerName,et_customerPhoneNumber,et_itemName,et_quantity, et_amount;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    Button btn_save;

    Long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        et_dateText = (EditText) findViewById(R.id.dateText);

        et_customerName = (EditText) findViewById(R.id.customerNameText);
        et_customerPhoneNumber = (EditText) findViewById(R.id.customerPhoneNumberText);

        et_itemName = (EditText) findViewById(R.id.itemNameText);
        et_quantity = (EditText) findViewById(R.id.quantityText);
        et_amount = (EditText) findViewById(R.id.amountText);


        id = getIntent().getLongExtra("id",0);

        et_dateText.setText(getIntent().getStringExtra("date"));
        et_customerName.setText(getIntent().getStringExtra("customerName"));
        et_customerPhoneNumber.setText(getIntent().getStringExtra("customerPhoneNumber"));
        et_itemName.setText(getIntent().getStringExtra("itemName"));
        et_quantity.setText(getIntent().getStringExtra("quantity"));
        et_amount.setText(getIntent().getStringExtra("amount"));

        btn_save = (Button) findViewById(R.id.submitReceiptButton);

        et_dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(EditActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });



        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day+"/"+month+"/"+year;
                et_dateText.setText(date);
            }
        };


        btn_save.setOnClickListener(new View.OnClickListener() {




            String updateSet = " receipt_date = ? ," +
                               " customer_name = ? ," +
                               " customer_phone_number = ? ," +
                               " item_name = ? ," +
                               " quantity = ? ,"+
                               " amount = ? ";
            @Override
            public void onClick(View view) {
                /*new Update(ItemReceipt.class).set(updateSet,date,customerName,customerPhone,itemName,quantity,amount)
                        .where("Id = ?",id)
                        .execute();*/

                String date = et_dateText.getText().toString();
                String customerName = et_customerName.getText().toString();
                String itemName = et_itemName.getText().toString();
                String customerPhone = et_customerPhoneNumber.getText().toString();
                double quantity = Double.parseDouble(et_quantity.getText().toString());
                double amount = Double.parseDouble(et_amount.getText().toString());

                Log.d("Update","id = "+id+" customer number = "+customerPhone);


                new Update(ItemReceipt.class).set(updateSet,date,customerName,customerPhone,itemName,quantity, amount)
                        .where("Id = ?",id)
                        .execute();

                /*new Update(ItemReceipt.class).set("customer_phone_number = ? ",customerPhone)
                        .where("Id = ?",id)
                        .execute();*/

                Toast.makeText(EditActivity.this,"Done",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
