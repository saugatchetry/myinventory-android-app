package zakoi.com.myinventory.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static android.R.attr.name;

/**
 * Created by saugatchetry on 03/06/17.
 */

@Table(name = "Items")
public class Items extends Model {

    @Column(name = "uom")
    public String uom;

    @Column(name = "item_name")
    public String itemName;

    public static List<Items> getAllItems(){
        return new Select().all().from(Items.class).execute();
    }

    public static void deleteItems(HashMap<String,Items> delete_list) {
        for(String itemName : delete_list.keySet()){
            new Delete().from(Items.class).where("item_name = ?",itemName).execute();
        }
    }
}
