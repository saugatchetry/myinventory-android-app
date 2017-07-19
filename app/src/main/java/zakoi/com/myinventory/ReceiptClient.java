package zakoi.com.myinventory;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import zakoi.com.myinventory.model.Items;
import zakoi.com.myinventory.model.OutgoingStockTransfer;
import zakoi.com.myinventory.model.StockTransfer;
import zakoi.com.myinventory.model.Vendors;

/**
 * Created by saugatchetry on 31/05/17.
 */

public interface ReceiptClient {

    @POST("/api/receipts")
    Call<Void> submitReceipt(@Body List<Receipts> receipts);

    @GET("/api/getItemsByVendorName")
    Call<HashMap<String,Float>> getAllItems(@Query("storeName") String storeName);

    @GET("/api/getItemsByVendorName1")
    Call<List<Items>> getAllStoreItems(@Query("storeName") String storeName);

    @GET("/api/getAllVendors")
    Call<List<Vendors>> getAllVendors();

    @GET("/api/getAllStockTransfers")
    Call<List<StockTransfer>> getAllStockTransfers(@Query("storeName") String storeName);

    @POST("/api/confirmedIds")
    Call<Void> submitConfirmedTransfers(@Body List<Integer> ids);

    @POST("/api/outgoingStockTransfers")
    Call<Void> submitAllOutgoingStockTransfers(@Body List<OutgoingStockTransfer> list);



}
