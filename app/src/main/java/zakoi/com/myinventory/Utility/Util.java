package zakoi.com.myinventory.Utility;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
}
