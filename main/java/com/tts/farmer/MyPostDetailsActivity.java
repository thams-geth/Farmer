package com.tts.farmer;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostDetailsActivity extends AppCompatActivity {
    private ImageView iv_image;
    private TextView tv_product,tv_price,tv_quantity,tv_detail,tv_location,tv_boost;
    private Button b_pay;
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private String docid;
    private FirebaseAuth mAuth;
    private String s_name,s_phoneno;
    private Mypost product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post_details);

        mAuth=FirebaseAuth.getInstance();


        docid=getIntent().getStringExtra("docid");

        iv_image=(ImageView)findViewById(R.id.iv_image);
        tv_product=(TextView)findViewById(R.id.tv_product);
        tv_price=(TextView)findViewById(R.id.tv_price);
        tv_quantity=(TextView)findViewById(R.id.tv_quantity);
        tv_detail=(TextView)findViewById(R.id.tv_detail);
        tv_location=(TextView)findViewById(R.id.tv_location);
        tv_boost=(TextView)findViewById(R.id.tv_boost_text);
        b_pay=(Button) findViewById(R.id.b_pay);

        fetch_product_data();
        fetch_user_data(mAuth.getUid());
        b_pay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final CollectionReference collectionReference=firebaseFirestore.collection("featured");
                product.setBoost("boosted");
                collectionReference.add(product).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        DocumentReference ref=firebaseFirestore.document("users/"+mAuth.getUid()+"/posts/"+docid);
                        ref.set(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                fetch_product_data();
                                showAlert("Your product boosted successfully.");

                            }
                        });
                    }
                });
                return false;
            }
        });

    }

    private void fetch_product_data() {
        DocumentReference ref=firebaseFirestore.document("users/"+mAuth.getUid()+"/posts/"+docid);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                product=documentSnapshot.toObject(Mypost.class);
                Picasso.with(MyPostDetailsActivity.this).load(product.getImage()).into(iv_image);
                tv_product.setText(product.getProduct());
                tv_price.setText("₹"+product.getPrice());
                tv_quantity.setText(product.getQuantity()+"/"+product.getQuantity_type());
                tv_detail.setText(product.getDetails());
                tv_location.setText(product.getLocation_name()+"\n"+product.getLocation_address());
                if (!product.getBoost().equals("null")){
                    tv_boost.setText("Your product is already boosted");
                    tv_boost.setTextColor(getResources().getColor(R.color.colorAccent));
                    b_pay.setVisibility(View.GONE);
                }

            }
        });

    }
    private void fetch_user_data(String userid) {

        DocumentReference ref=firebaseFirestore.document("users/"+userid);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                s_name=documentSnapshot.getString("name");
                s_phoneno=mAuth.getCurrentUser().getPhoneNumber();


            }
        });

    }
    public void onClick(View view) {
        if (view.getId()==R.id.iv_image){

        }
        if(view.getId()==R.id.b_pay){
            launchPaymentFlow();

        }

    }
    private void launchPaymentFlow() {
        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();
        payUmoneyConfig.setPayUmoneyActivityTitle("Boost product");
        payUmoneyConfig.setDoneButtonText("Pay " + getResources().getString(R.string.Rupees) + getResources().getString(R.string.txt_product_price));
        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
        builder.setAmount(convertStringToDouble(getResources().getString(R.string.txt_product_price)))
                .setTxnId(System.currentTimeMillis() + "")
                .setPhone(s_phoneno)
                .setProductName("Boost product")
                .setFirstName(s_name)
                .setEmail(Constants.EMAIL)
                .setsUrl(Constants.SURL)
                .setfUrl(Constants.FURL)
                .setUdf1("")
                .setUdf2("")
                .setUdf3("")
                .setUdf4("")
                .setUdf5("")
                .setUdf6("")
                .setUdf7("")
                .setUdf8("")
                .setUdf9("")
                .setUdf10("")
                .setIsDebug(Constants.DEBUG)
                .setKey(Constants.MERCHANT_KEY)
                .setMerchantId(Constants.MERCHANT_ID);

        try {
            PayUmoneySdkInitializer.PaymentParam mPaymentParams = builder.build();
            calculateHashInServer(mPaymentParams);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void calculateHashInServer(final PayUmoneySdkInitializer.PaymentParam mPaymentParams) {
        ProgressUtils.showLoadingDialog(this);
        String url = Constants.MONEY_HASH;
        StringRequest request = new StringRequest(Request.Method.POST, url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String merchantHash = "";

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            merchantHash = jsonObject.getString("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ProgressUtils.cancelLoading();

                        if (merchantHash.isEmpty() || merchantHash.equals("")) {
                            Toast.makeText(MyPostDetailsActivity.this, "Could not generate hash", Toast.LENGTH_SHORT).show();
                        } else {
//                            Toast.makeText(MyPostDetailsActivity.this, "hash generated", Toast.LENGTH_SHORT).show();

                            mPaymentParams.setMerchantHash(merchantHash);
                            PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, MyPostDetailsActivity.this, R.style.PayUMoney, true);
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(MyPostDetailsActivity.this, "Connect to internet Volley", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MyPostDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        ProgressUtils.cancelLoading();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return mPaymentParams.getParams();
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {

            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
//                    showAlert("Payment Successful");
                    final CollectionReference collectionReference=firebaseFirestore.collection("featured");
                    product.setBoost("boosted");
                    collectionReference.add(product).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            DocumentReference ref=firebaseFirestore.document("users/"+mAuth.getUid()+"/posts/"+docid);
                            ref.set(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    fetch_product_data();
                                    showAlert("Your product boosted successfully.");

                                }
                            });
                        }
                    });


                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.CANCELLED)) {
                    showAlert("Payment Cancelled");
                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.FAILED)) {
                    showAlert("Payment Failed");
                }

            } else if (resultModel != null && resultModel.getError() != null) {
                Toast.makeText(this, "Error check log", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Both objects are null", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_CANCELED) {
            showAlert("Payment Cancelled");
        }
    }

    private Double convertStringToDouble(String str) {
        return Double.parseDouble(str);
    }

    private void showAlert(String msg){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }
}
