package zakoi.com.myinventory;

/**
 * Created by saugatchetry on 31/05/17.
 */

public class Receipts {

    private String receiptOutletName;
    private String customerName;
    private String customerPhoneNumber;
    private String itemName;
    private double quantity;
    private double rate;
    private String receiptDate;

    public String getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(String receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getReceiptOutletName() {
        return receiptOutletName;
    }

    public void setReceiptOutletName(String receiptOutletName) {
        this.receiptOutletName = receiptOutletName;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}
