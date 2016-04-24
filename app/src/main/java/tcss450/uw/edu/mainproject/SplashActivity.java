package tcss450.uw.edu.mainproject;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.LoginButton;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        TextView joinLink = (TextView) findViewById(R.id.join);
        TextView loginLink = (TextView) findViewById(R.id.login);

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        Typeface oswald = Typeface.createFromAsset(getAssets(), "fonts/Oswald-Regular.ttf");
        joinLink.setTypeface(oswald);
        loginLink.setTypeface(oswald);
    }

    public void toLoginPage(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void toFacebook(View view) {

        // Intent intentFacebook = new Intent(this, .class);
        // startActivity(intentImage);
    }

    public void toJoin(View view) {
        Intent intentJoin = new Intent(this, AddUserActivity.class);
        startActivity(intentJoin);

    }
}
