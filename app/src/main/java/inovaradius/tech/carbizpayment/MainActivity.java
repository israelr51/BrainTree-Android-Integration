package inovaradius.tech.carbizpayment;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    EditText amount;
    Button makepay;
    String token_Url = "http://immohouse.maplechap.com";   // Here is the payment Url
    String clientTken;
    String TAG = "mybrain";
    int REQUEST_CODE = 8452;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        amount = findViewById(R.id.amount);
        makepay = findViewById(R.id.payment);
        getClientToken();

        makepay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DropInRequest dropInRequest = new DropInRequest().clientToken(clientTken);
                startActivityForResult(dropInRequest.getIntent(MainActivity.this), REQUEST_CODE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);

                String paymentNonce = result.getPaymentMethodNonce().getNonce(); //Getting the Payment Nonce from User

                sendPaymentNonceToServer(paymentNonce); //Snding payment Nonce to Server

                // use the result to update your UI and send the payment method nonce to your server
            } else if (resultCode == RESULT_CANCELED) {
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            }
        }
    }


    // FUNCTION UPDATING THE PAYMENT NONCE FROM THE SERVER...
    private void sendPaymentNonceToServer(String paymentNonce) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("payment_method_nonce", paymentNonce);
        params.put("amount", amount.getText().toString().trim());
        params.put("username", "wilsondelsol");

        client.post(token_Url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e(TAG, "Payment Made Successuffly: " + statusCode + "headers" + headers + "response body" + responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, "onFailure:  failed to make payment");
            }
        });
    }

    // FUNCTION TO GENERATE USER TOKEN
    void getClientToken() {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(token_Url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {


            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                clientTken = responseString; // Assigning the generaed token to ClientToken
                Log.e(TAG, "onTokenSuccess: " + clientTken);
            }
        });
    }


}
