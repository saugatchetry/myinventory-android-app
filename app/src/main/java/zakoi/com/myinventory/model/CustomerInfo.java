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
@Table(name="Customer_Info")
public class CustomerInfo extends Model {

    @Column(name="customer_name")
    public String customerName;

    @Column(name="customer_phone")
    public String customerPhoneNumber;

    public static List<CustomerInfo> getAllCustomerInfo(){
        return new Select().from(CustomerInfo.class).execute();
    }


}
