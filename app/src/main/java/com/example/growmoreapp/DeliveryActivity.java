package com.example.growmoreapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.growmoreapp.DBqueries.firebaseFirestore;

public class DeliveryActivity extends AppCompatActivity {

    private RecyclerView deliveryRecyclerView;
    private Button changeORaddNewAddressBtn, continueBtn;
    public static final int SELECT_ADDRESS = 0;
    private TextView totalAmount, fullname, fullAddress, pincode, orderId;
    private String name, mobileNo;
    public static Dialog loadingDialog;
    private Dialog paymentMethodDialog;
    private ImageButton paytm, payumoney, continueShoppingBtn;
    public static List<CartItemModel> cartItemModelList;
    private String paymentMethod;
    public static boolean fromCart;
    private ConstraintLayout orderConfirmationLayout;
    private boolean successResponse = false;
    private String order_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Delivery");

        deliveryRecyclerView = findViewById(R.id.delivery_recyclerview);
        changeORaddNewAddressBtn = findViewById(R.id.change_or_add_address_button);
        totalAmount = findViewById(R.id.total_cart_amount);
        fullname = findViewById(R.id.fullname);
        fullAddress = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);
        continueBtn = findViewById(R.id.cart_continue_btn);
        orderConfirmationLayout = findViewById(R.id.order_confirmation_layout);
        orderId = findViewById(R.id.order_id);
        continueShoppingBtn = findViewById(R.id.continue_shopping_btn);

        //////////loading dialog

        loadingDialog = new Dialog(DeliveryActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //////////loading dialog

        //////////payment method dialog

        paymentMethodDialog = new Dialog(DeliveryActivity.this);
        paymentMethodDialog.setContentView(R.layout.payment_method);
        paymentMethodDialog.setCancelable(true);
        paymentMethodDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        paymentMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paytm = paymentMethodDialog.findViewById(R.id.paytm);
        payumoney = paymentMethodDialog.findViewById(R.id.payumoney);

        //////////payment method dialog

        LinearLayoutManager layoutManager = new LinearLayoutManager((this));
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        deliveryRecyclerView.setLayoutManager(layoutManager);

        CartAdapter cartAdapter = new CartAdapter(cartItemModelList, totalAmount, false);
        deliveryRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        changeORaddNewAddressBtn.setVisibility(View.VISIBLE);
        changeORaddNewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DeliveryActivity.this, MyAddressesActivity.class).putExtra("MODE", SELECT_ADDRESS));
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentMethodDialog.show();
            }
        });

        payumoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DeliveryActivity.this, "Payment Successful.", Toast.LENGTH_SHORT).show();
                paymentMethodDialog.dismiss();
                SystemClock.sleep(3000);

                showConfirmationLayout();

//                Intent i = new Intent(DeliveryActivity.this, PaymentSuccessful.class);
//                startActivity(i);
                //orderConfirmationLayout.setVisibility(View.VISIBLE);
            }
        });


        paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DeliveryActivity.this, "Payment Successful.", Toast.LENGTH_SHORT).show();
                paymentMethodDialog.dismiss();
                SystemClock.sleep(3000);

                    showConfirmationLayout();


                // orderConfirmationLayout.setVisibility(View.VISIBLE);
            }
        });


    }

    private void showConfirmationLayout()  {
        successResponse = true;

        String SMS_API = "https://www.fast2sms.com/dev/bulkV2";
        order_id = UUID.randomUUID().toString().substring(0, 28);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SMS_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("authorization", "Ah7VmEOGgnXFLzeNj31pYd4x6sMKkialc8HIStCJr2DPZyf9oqTiKj19LoGuvSQ3W4lws8h67qpPnyNI");

                return headers;

            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> body = new HashMap<String, String>();
                body.put("sender_id", "TXTIND");
                body.put("language", "english");
                body.put("route", "v3");
                body.put("numbers", mobileNo);
                body.put("message", "Thanks for purchasing with GrowMore. Your order will be shipped shortly and you'll be notified." +
                        "Your Order ID is " + order_id);
                return body;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue requestQueue = Volley.newRequestQueue(DeliveryActivity.this);
        requestQueue.add(stringRequest);

        /////sms for confirmation of order


        if (fromCart) {
            loadingDialog.show();
            Map<String, Object> updateCart = new HashMap<>();
            long cartListSize = 0;
            final List<Integer> indexList = new ArrayList<>();
            for (int x = 0; x < DBqueries.cartList.size(); x++) {
                if (!cartItemModelList.get(x).isInStock()) {
                    updateCart.put("product_ID_" + cartListSize, cartItemModelList.get(x).getProductID());
                    cartListSize++;
                } else {
                    indexList.add(x);
                }

            }
            updateCart.put("list_size", cartListSize);

            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                    .collection("USER_DATA").document("MY_CART").set(updateCart)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                for (int x = 0; x < indexList.size(); x++) {
                                    DBqueries.cartList.remove(indexList.get(x).intValue());
                                    DBqueries.cartItemModelList.remove(indexList.get(x).intValue());
                                    DBqueries.cartItemModelList.remove(DBqueries.cartItemModelList.size() - 1);
                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });
        }

        Intent i = new Intent(DeliveryActivity.this, PaymentSuccessful.class);
        i.putExtra("ORDER_ID",order_id);
        startActivity(i);
//        continueShoppingBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
    }

//    private String generateString() {
//        String uuid = UUID.randomUUID().toString();
//        return uuid.replaceAll("-", "");
//    }

    @Override
    public void onBackPressed() {
        if (successResponse) {
            finish();
            return;
        }
        super.onBackPressed();
    }
    ////                paymentMethodDialog.dismiss();
////                loadingDialog.show();
////                if (ContextCompat.checkSelfPermission(DeliveryActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
////                    ActivityCompat.requestPermissions(DeliveryActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
////                }
////                final String M_id = "JXlvFG60779433869819";
////                final String customer_id = FirebaseAuth.getInstance().getUid();
////                final String order_id = UUID.randomUUID().toString().substring(0, 28);
////                final String url = "https://growmoreapplication.000webhostapp.com/paytm/generateChecksum.php";
////                final String callBackUrl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
////
////                final RequestQueue requestQueue = Volley.newRequestQueue(DeliveryActivity.this);
////                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
////                {
////                    @Override
////                    public void onResponse(String response) {
////
////                        try {
////                            JSONObject jsonObject = new JSONObject(response);
////                            if (jsonObject.has("CHECKSUMHASH")) {
////                                String CHECKSUMHASH = jsonObject.getString("CHECKSUMHASH");
////
////                                PaytmPGService paytmPGService = PaytmPGService.getStagingService();
////                                HashMap<String, String> paramMap = new HashMap<>();
////                                paramMap.put("MID", M_id);
////                                paramMap.put("ORDER_ID", order_id);
////                                paramMap.put("CUST_ID", customer_id);
////                                paramMap.put("CHANNEL_ID", "WAP");
////                                paramMap.put("TXN_AMOUNT", totalAmount.getText().toString().substring(3, totalAmount.getText().length() - 2));
////                                paramMap.put("WEBSITE", "WEBSTAGING");
////                                paramMap.put("INDUSTRY_TYPE_ID", "Retail");
////                                paramMap.put("CALLBACK_URL", callBackUrl);
////                                paramMap.put("CHECKSUMHASH", CHECKSUMHASH);
////
////                                PaytmOrder order = new PaytmOrder(paramMap);
////                                paytmPGService.initialize(order, null);
////                                paytmPGService.startPaymentTransaction(DeliveryActivity.this, true, true, new PaytmPaymentTransactionCallback() {
////
////
////                                    @Override
////                                    public void onTransactionResponse(@Nullable Bundle bundle) {
////                                        Toast.makeText(getApplicationContext(), "Payment Transaction response " + bundle.toString(), Toast.LENGTH_LONG).show();
////                                    }
////
////
////                                    @Override
////                                    public void networkNotAvailable() {
////                                        Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();
////                                    }
////
////                                    @Override
////                                    public void clientAuthenticationFailed(String s) {
////                                        Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + s, Toast.LENGTH_LONG).show();
////                                    }
////
////                                    @Override
////                                    public void someUIErrorOccurred(String s) {
////                                        Toast.makeText(getApplicationContext(), "UI Error " + s, Toast.LENGTH_LONG).show();
////                                    }
////
////                                    @Override
////                                    public void onErrorLoadingWebPage(int i, String s, String s1) {
////                                        Toast.makeText(getApplicationContext(), "Unable to load webpage " + s, Toast.LENGTH_LONG).show();
////                                    }
////
////                                    @Override
////                                    public void onBackPressedCancelTransaction() {
////                                        Toast.makeText(getApplicationContext(), "Transaction cancelled", Toast.LENGTH_LONG).show();
////                                    }
////
////                                    @Override
////                                    public void onTransactionCancel(String s, Bundle bundle) {
////                                        Toast.makeText(getApplicationContext(), "Transaction cancelled " + bundle.toString(), Toast.LENGTH_LONG).show();
////                                    }
////                                });
////                            }
////                        } catch (JSONException e) {
////                            e.printStackTrace();
////                        }
////                    }
////                }, (VolleyError error) -> {
////                    loadingDialog.dismiss();
////                    Toast.makeText(DeliveryActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
////                }) {
////                    @Override
////                    protected Map<String, String> getParams() throws AuthFailureError {
////                        Map<String, String> paramMap = new HashMap<String, String>();
////                        paramMap.put("MID", M_id);
////                        paramMap.put("ORDER_ID", order_id);
////                        paramMap.put("CUST_ID", customer_id);
////                        paramMap.put("CHANNEL_ID", "WAP");
////                        paramMap.put("TXN_AMOUNT", totalAmount.getText().toString().substring(3, totalAmount.getText().length() - 2));
////                        paramMap.put("WEBSITE", "WEBSTAGING");
////                        paramMap.put("INDUSTRY_TYPE_ID", "Retail");
////                        paramMap.put("CALLBACK_URL", callBackUrl);
////                        return paramMap;
////                    }
////                };
////                requestQueue.add(stringRequest);
////            }
////        });
//
//                generateCheckSum();
//
//            }
//        });
//    }
//
//        private void generateCheckSum() {
//
//        //getting the tax amount first.
//        String txnAmount = totalAmount.getText().toString().trim();
//
//        //creating a retrofit object.
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Api.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        //creating the retrofit api service
//        Api apiService = retrofit.create(Api.class);
//
//        //creating paytm object
//        //containing all the values required
//        final Paytm paytm = new Paytm(
//                Constants.M_ID,
//                Constants.CHANNEL_ID,
//                txnAmount,
//                Constants.WEBSITE,
//                Constants.CALLBACK_URL,
//                Constants.INDUSTRY_TYPE_ID
//        );
//
//        //creating a call object from the apiService
//        Call<Checksum> call = apiService.getChecksum(
//                paytm.getmId(),
//                paytm.getOrderId(),
//                paytm.getCustId(),
//                paytm.getChannelId(),
//                paytm.getTxnAmount(),
//                paytm.getWebsite(),
//                paytm.getCallBackUrl(),
//                paytm.getIndustryTypeId()
//        );
//
//        //making the call to generate checksum
//        call.enqueue(new Callback<Checksum>() {
//
//
//            @Override
//            public void onResponse(Call<Checksum> call, retrofit2.Response<Checksum> response) {
//                initializePaytmPayment(response.body().getChecksumHash(), paytm);
//            }
//
//            @Override
//            public void onFailure(Call<Checksum> call, Throwable t) {
//
//            }
//        });
//    }
//
//    private void initializePaytmPayment(String checksumHash, Paytm paytm) {
//
//        //getting paytm service
//        PaytmPGService Service = PaytmPGService.getStagingService();
//
//        //use this when using for production
//        //PaytmPGService Service = PaytmPGService.getProductionService();
//
//        //creating a hashmap and adding all the values required
//        Map<String, String> paramMap = new HashMap<>();
//        paramMap.put("MID", Constants.M_ID);
//        paramMap.put("ORDER_ID", paytm.getOrderId());
//        paramMap.put("CUST_ID", paytm.getCustId());
//        paramMap.put("CHANNEL_ID", paytm.getChannelId());
//        paramMap.put("TXN_AMOUNT", paytm.getTxnAmount());
//        paramMap.put("WEBSITE", paytm.getWebsite());
//        paramMap.put("CALLBACK_URL", paytm.getCallBackUrl());
//        paramMap.put("CHECKSUMHASH", checksumHash);
//        paramMap.put("INDUSTRY_TYPE_ID", paytm.getIndustryTypeId());
//
//
//        //creating a paytm order object using the hashmap
//        PaytmOrder order = new PaytmOrder((HashMap<String, String>) paramMap);
//        //intializing the paytm service
//        Service.initialize(order, null);
//
//        //finally starting the payment transaction
//        Service.startPaymentTransaction(DeliveryActivity.this, true, true, new PaytmPaymentTransactionCallback() {
//            //all these overriden method is to detect the payment result accordingly
//            public void onTransactionResponse(Bundle bundle) {
//
//                Toast.makeText(DeliveryActivity.this, bundle.toString(), Toast.LENGTH_LONG).show();
//            }
//
//            // @Override
//            public void networkNotAvailable() {
//                Toast.makeText(DeliveryActivity.this, "Network error", Toast.LENGTH_LONG).show();
//            }
//
//            // @Override
//            public void clientAuthenticationFailed(String s) {
//                Toast.makeText(DeliveryActivity.this, s, Toast.LENGTH_LONG).show();
//            }
//
//            // @Override
//            public void someUIErrorOccurred(String s) {
//                Toast.makeText(DeliveryActivity.this, s, Toast.LENGTH_LONG).show();
//            }
//
//            // @Override
//            public void onErrorLoadingWebPage(int i, String s, String s1) {
//                Toast.makeText(DeliveryActivity.this, s, Toast.LENGTH_LONG).show();
//            }
//
//            // @Override
//            public void onBackPressedCancelTransaction() {
//                Toast.makeText(DeliveryActivity.this, "Back Pressed", Toast.LENGTH_LONG).show();
//            }
//
//            // @Override
//            public void onTransactionCancel(String s, Bundle bundle) {
//                Toast.makeText(DeliveryActivity.this, s + bundle.toString(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingDialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        name = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getName();
        mobileNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getMobileNo();

        if (DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo().equals("")) {
            fullname.setText(name + " - " + mobileNo);
        } else {
            fullname.setText(name + " - " + mobileNo + " or " + DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo());
        }

        String flatNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getFlatNo();
        String city = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getCity();
        String locality = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLocality();
        String landmark = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLandmark();
        String state = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getState();

        if (landmark.equals("")) {
            fullAddress.setText(flatNo + "," + locality + "," + city + "," + state);
        } else {
            fullAddress.setText(flatNo + "," + locality + "," + landmark + "," + city + "," + state);
        }
        pincode.setText(DBqueries.addressesModelList.get(DBqueries.selectedAddress).getPincode());

    }
}
