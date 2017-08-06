package zakoi.com.myinventory.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by saugatchetry on 07/06/17.
 */

@Table(name = "Vendors")
public class Vendors extends Model{

    @Column(name = "store_name")
    public String storeName;

    public static List<Vendors> getAllVendors(){
        return new Select().all().from(Vendors.class).execute();
    }

    public static void DeleteAll() {
        new Delete().from(Vendors.class).execute();
    }

}
