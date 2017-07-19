package zakoi.com.myinventory.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

import static android.R.attr.name;

/**
 * Created by saugatchetry on 03/06/17.
 */

@Table(name="item_receipt")
public class ItemReceipt extends Model {

    public int id;

    @Column(name="item_name")
    public String itemName;

    @Column(name="quantity")
    public double quantity;

    @Column(name="rate")
    public double rate;

    @Column(name="customer_name")
    public String customerName;

    @Column(name="customer_phone_number")
    public String customerPhoneNumber;

    @Column(name="editable")
    public int editable;

    @Column(name="sync_cloud")
    public int sync_cloud;

    @Column(name="receipt_date")
    public String receiptDate;

    public static List<ItemReceipt> getAllItemReceipts(){
        return new Select().from(ItemReceipt.class).execute();
    }

    public static List<ItemReceipt> getAllEditableReceipts(){
        return new Select().from(ItemReceipt.class).where("editable = ?",1).execute();
    }

    public static List<ItemReceipt> getAllUnsyncedReceipts(){
        return new Select().from(ItemReceipt.class).where("sync_cloud = ?",0).execute();
    }


    public static List<ItemReceipt> getAllCommitedButUnsyncedReceipts(){
        return new Select().from(ItemReceipt.class)
                           .where("sync_cloud = ?",0)
                           .where("editable = ?",0).execute();
    }

}
