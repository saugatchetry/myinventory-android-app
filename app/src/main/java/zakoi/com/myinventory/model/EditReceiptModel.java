package zakoi.com.myinventory.model;

/**
 * Created by saugatchetry on 03/06/17.
 */

public class EditReceiptModel {

    Long id;
    String customerName;
    String customerPhoneNumber;
    String itemName;
    String date;
    double itemQuantity;
    double amount;




    public EditReceiptModel(Long id,String customerName, String customerPhoneNumber, String itemName, String date, double itemQuantity, double amount) {

        this.id = id;
        this.customerName = customerName;
        this.customerPhoneNumber = customerPhoneNumber;
        this.itemName = itemName;
        this.date = date;
        this.itemQuantity = itemQuantity;
        this.amount = amount;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCustomerName() {

        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(double itemQuantity) {
        this.itemQuantity = itemQuantity;
    }
}
