package biggieconsulting.cake;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.IOException;

import biggieconsulting.cake.utility.AccessToken;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    public String clientId = "ck_84cb5c38a8e5e4d426490c7905cf077cd74860c5";
    public String clientSecret = "cs_46d5f0c40af2f64f08142abee8b36b6e84b27175";
    /*public String clientId = "ck_4e22f7bac8196e0195a19171aeff301c30ce6522";
    public String clientSecret = "cs_ff34f9998e6c2e05b405790ce061fc74248d8107";*/
    public final String signature_method = "HMAC-SHA256";
    public String redirectUri = "http://supermarket1.co.in/mogappairshoppee/index.php?route=common/home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ServiceGenerator.API_BASE_URL
                + "/login" + "?client_id="
                + clientId + "&redirect_uri="
                + redirectUri));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // the intent filter defined in AndroidManifest will handle the return from ACTION_VIEW intent
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(redirectUri)) {
            // use the parameter your API exposes for the code (mostly it's "code")
            String code = uri.getQueryParameter("code");
            if (code != null) {
                // get access token
                LoginService loginService =
                        ServiceGenerator.createService(LoginService.class, clientId, clientSecret);
                Call<AccessToken> call = loginService.getAccessToken(code, "authorization_code");
                try {
                    AccessToken accessToken = call.execute().body();
                    assert accessToken != null;
                    String ac = accessToken.getAccessToken();
                    Toast.makeText(this, ac, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (uri.getQueryParameter("error") != null) {
                // show an error message here
                Toast.makeText(this, "Error Message Displaying.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*@Override
    protected void onResume() {
        super.onResume();

        // the intent filter defined in AndroidManifest will handle the return from ACTION_VIEW intent
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(redirectUri)) {
            // use the parameter your API exposes for the code (mostly it's "code")
            String code = uri.getQueryParameter("code");
            if (code != null) {
                // get access token
                // we'll do that in a minute
            } else if (uri.getQueryParameter("error") != null) {
                // show an error message here

                Toast.makeText(this, "Error Message Displaying.", Toast.LENGTH_SHORT).show();
            }
        }
    }*/
}
