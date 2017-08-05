package zakoi.com.myinventory.Utility;

import android.util.Log;

import com.activeandroid.query.Delete;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import zakoi.com.myinventory.model.CustomerInfo;
import zakoi.com.myinventory.model.ItemReceipt;
import zakoi.com.myinventory.model.Items;
import zakoi.com.myinventory.model.OutgoingStockTransfer;
import zakoi.com.myinventory.model.StockTransfer;
import zakoi.com.myinventory.model.Vendors;

/**
 * Created by zakoi on 7/19/17.
 */

public class Util {
    public static String ChangeDateFormat(String date) {
        DateFormat df = new SimpleDateFormat("dd/mm/yyyy");
        DateFormat df_new = new SimpleDateFormat("yyyy-mm-dd");
        try {
            Date return_date = df.parse(date);
            date = df_new.format(return_date);
        } catch (ParseException e) {
            Log.e("Util","Could not parse date" + df);
        }
        return date;
    }

    public static void saveItemsToDB(List<Items> body) {
        for(Items items : body){
            Items i = new Items();
            i.itemName = items.itemName;
            i.uom = items.uom;
            i.save();
        }
    }

    public static void ClearAllDBs() {
        try {
            new Delete().from(CustomerInfo.class).execute();
            new Delete().from(ItemReceipt.class).execute();
            new Delete().from(Items.class).execute();
            new Delete().from(OutgoingStockTransfer.class).execute();
            new Delete().from(StockTransfer.class).execute();
            new Delete().from(Vendors.class).execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long getElapsedMins(Date now, Date before) {
        return TimeUnit.MILLISECONDS.toMinutes(now.getTime() - before.getTime());
    }

    public static  String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new Date());
    }

}
