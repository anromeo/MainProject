package tcss450.uw.edu.mainproject.authenticate;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import tcss450.uw.edu.mainproject.Helper;
import tcss450.uw.edu.mainproject.R;

public class LoginUserActivity extends AppCompatActivity {

    private Helper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        mHelper = new Helper(getAssets());


        Typeface oswald = Typeface.createFromAsset(getAssets(), "fonts/Oswald-Regular.ttf");

        TextView titleText = (TextView) findViewById(R.id.login_title);
        titleText.setTypeface(oswald);


        EditText user = (EditText) findViewById(R.id.user);
        user.setTypeface(oswald);

        EditText password = (EditText) findViewById(R.id.password);
        password.setTypeface(oswald);

        Button signInButton = (Button) findViewById(R.id.login_user_button);
        signInButton.setTypeface(oswald);
    }

}
