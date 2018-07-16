package biggieconsulting.cake;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import services.HMACSha1SignatureService;
import services.TimestampServiceImpl;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final String BASE_SITE = "7eleventest1.co.in/chocofantasy";
        final String BASE_URL = "http://" + BASE_SITE + "/wp-json/wc/v2/coupons";
        //final String BASE_URL = "http://" + BASE_SITE + "/wp-json/wc/v2/products/categories";
        final String COSTUMER_KEY = "ck_4e22f7bac8196e0195a19171aeff301c30ce6522";
        String COSTUMER_SECRET = "cs_ff34f9998e6c2e05b405790ce061fc74248d8107";

        /*final String BASE_SITE = "androidmill.com";
        final String BASE_URL = "http://" + BASE_SITE + "/wp-json/wc/v1/products";
        final String COSTUMER_KEY = "ck_7081efc222e724c72aee854b56de443a4dae9251";
        String COSTUMER_SECRET = "cs_e35e69b5c43ba794e756507557f6f16ba6c7801e";*/
        String METHORD = "GET";
        //change API method eg POST,PUT, DELETE etc (ONLY FOR THIS EXAMPLE FOR LIB LIKE RETROFIT,OKHTTP, The Are Dynamic Way)
        final String nonce = new TimestampServiceImpl().getNonce();
        final String timestamp = new TimestampServiceImpl().getTimestampInSeconds();

        // GENERATED NONCE and TIME STAMP
        Log.d("cake_nonce", nonce);
        Log.d("cake_time", timestamp);
        String firstEncodedString = METHORD + "&" + encodeUrl(BASE_URL);
        Log.d("cake_firstEncodedString", firstEncodedString);
        String parameterString = "oauth_consumer_key=" + COSTUMER_KEY + "&oauth_nonce=" + nonce + "&oauth_signature_method=HMAC-SHA256&oauth_version=1.0";
        //String parameterString = "oauth_consumer_key=" + COSTUMER_KEY + "&oauth_nonce=" + nonce + "&oauth_signature_method=HMAC-SHA256&oauth_timestamp=" + timestamp + "&oauth_version=1.0";
        String secoundEncodedString = "&" + encodeUrl(parameterString);
        Log.d("cake_secoundEncoded", secoundEncodedString);
        String baseString = firstEncodedString + secoundEncodedString;
        //THE BASE STRING AND COSTUMER_SECRET KEY IS USED FOR GENERATING SIGNATURE
        Log.d("cake_baseString", baseString);
        String signature = new HMACSha1SignatureService().getSignature(baseString, COSTUMER_SECRET, "");
        Log.d("cake_SignatureBefore", signature);
        //Signature is encoded before parsing (ONLY FOR THIS EXAMPLE NOT NECESSARY FOR LIB LIKE RETROFIT,OKHTTP)
        signature = encodeUrl(signature);
        Log.d("cake_Signature ENCODING", signature);
        final String finalSignature = signature;//BECAUSE I PUT IN SIMPLE THREAD NOT NECESSARY

        new Thread() {
            @Override
            public void run() {
                //  THIS IS A VERY BASIC EXAMPLE OF PARSING USER CAN USE ANY LATEST METHORD RETROFIT,OKHTTP,VOLLEY ETC
                String filterid = "filter[categories]=gedgets";
                filterid = encodeUrl(filterid);
                //String parseUrl = BASE_URL ;
                //String parseUrl = BASE_URL + "&oauth_signature_method=HMAC-SHA256&oauth_consumer_key=" + COSTUMER_KEY + "&oauth_version=1.0&oauth_signature=" + finalSignature;
                //String parseUrl = BASE_URL + "?" + filterid + "&oauth_signature_method=HMAC-SHA256&oauth_consumer_key=" + COSTUMER_KEY + "&oauth_version=1.0&oauth_timestamp=" + timestamp + "&oauth_nonce=" + nonce + "&oauth_signature=" + finalSignature;
                String parseUrl = BASE_URL + "&oauth_signature_method=HMAC-SHA256&oauth_consumer_key=" + COSTUMER_KEY + "&oauth_version=1.0&oauth_timestamp=" + timestamp + "&oauth_nonce=" + nonce + "&oauth_signature=" + finalSignature;
                getJSON(parseUrl);
            }
        }.start();
    }

    public String encodeUrl(String url) {
        String encodedurl = "";
        try {
            encodedurl = URLEncoder.encode(url, "UTF-8");
            Log.d("Code_Encodeurl", encodedurl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodedurl;
    }


    public void getJSON(String url) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            Log.d("urlioz", "" + c.getURL());
//            c.setConnectTimeout(timeout);
//            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();
            Log.d("staus", "" + status);
            switch (status) {
                case 200:
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                    Log.i("code_success", "success");
                case 401:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    Log.d("RESonse here ", sb.toString());
                    sb.toString();
            }

        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
