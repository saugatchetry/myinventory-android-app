package zakoi.com.myinventory.Utility;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import zakoi.com.myinventory.model.Items;

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

    public static long getElapsedMins(Date now, Date before) {
        return TimeUnit.MILLISECONDS.toMinutes(now.getTime() - before.getTime());
    }

    public static  String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new Date());
    }

}
