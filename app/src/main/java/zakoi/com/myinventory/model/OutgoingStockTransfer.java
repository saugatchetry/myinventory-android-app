package zakoi.com.myinventory.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by saugatchetry on 16/06/17.
 */

@Table(name = "OutgoingStockTransfer")
public class OutgoingStockTransfer extends Model {


    @Column(name = "source_vendor")
    public String sourceVendor;

    @Column(name = "target_vendor")
    public String targetVendor;

    @Column(name = "item_name")
    public String itemName;

    @Column(name = "transferDate")
    public String transferDate;

    @Column(name = "quantity")
    public int quantity;

    @Column(name = "status")
    public String status;

    @Column(name="sync_cloud")
    public int sync_cloud;


    public static List<OutgoingStockTransfer> getaAllOutgoingTransfers(){
        return new Select().all().from(OutgoingStockTransfer.class).where("status = ?","Initiated")
                .where("sync_cloud = ?",0).execute();

    }

}
