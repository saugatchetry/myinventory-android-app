package zakoi.com.myinventory.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by saugatchetry on 13/06/17.
 */
@Table(name = "StockTransfer")
public class StockTransfer extends Model {

    @Column(name = "transferId")
    public int transferId;

    @Column(name = "source_vendor")
    public String sourceVendor;

    @Column(name = "target_vendor")
    public String targerVendor;

    @Column(name = "item_name")
    public String itemName;

    @Column(name = "date")
    public String date;

    @Column(name = "quantity")
    public int quantity;

    @Column(name = "status")
    public String status;

    @Column(name="sync_cloud")
    public int sync_cloud;

    public static List<StockTransfer> getAllUnconfirmedTransfers(){
        return new Select().all().from(StockTransfer.class).where("status = ?","Initiated").execute();
    }

    public static List<StockTransfer> getAllConfirmedTransfers(){
        return new Select().all().from(StockTransfer.class).where("status = ?","Confirmed")
                                                           .where("sync_cloud = ?",0).execute();

    }


}
