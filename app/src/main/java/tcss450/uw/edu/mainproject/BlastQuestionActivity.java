package tcss450.uw.edu.mainproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import tcss450.uw.edu.mainproject.authenticate.MainLoginActivity;
import tcss450.uw.edu.mainproject.data.UserDB;
import tcss450.uw.edu.mainproject.model.QuestionDetail;
import tcss450.uw.edu.mainproject.model.User;

public class BlastQuestionActivity extends AppCompatActivity implements FollowListFragment.OnListFragmentInteractionListener{
 private List<User> mSendToUsers;
    private Button mSendButton;
    private int mQuestionID;
    public List<QuestionDetail> mQuestionDetails;
    private String mQuestionText;
    private String mQuestionComment;
    private String mQuestionImage;
    private int mFollowerID;
    /**static variable of the first part of the URL for adding a user to the databse*/
    private final static String BLAST_QUESTION
            = "http://cssgate.insttech.washington.edu/~_450atm4/zombieturtles.php?totallyNotSecure=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blast_question);

        mSendButton = (Button) findViewById(R.id.send_button);
        mSendButton.setVisibility(View.INVISIBLE);
        EnterQuestionFragment enterQuestionFragment = new EnterQuestionFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.blast_question_container, enterQuestionFragment)
                .addToBackStack(null)
                .commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    /**
     * on options item selected
     * @param item menu item
     * @return boolean
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //will log the user out
        if (id == R.id.action_logout) {
            SharedPreferences sharedPreferences =
                    getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean(getString(R.string.LOGGEDIN), false)
                    .commit();
            //will return to the main login activity
            Intent i = new Intent(this, MainLoginActivity.class);
            startActivity(i);
            finish();
            return true;

        }
        return  super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(User user) {
        Log.i("question options", ((myApplication) this.getApplication()).getDetailList().size() +"");
        mSendButton.setVisibility(View.VISIBLE);
        if (mSendToUsers == null) {
            mSendToUsers = new ArrayList<>();
            mSendToUsers.add(user);
        } else {
            boolean flag = false;
            for(int i = 0; i < mSendToUsers.size(); i++) {
                if (mSendToUsers.get(i).getUsername().equals(user.getUsername())) {
                    flag = true;
                    Toast.makeText(this,user.getUsername() + " remmoved from list!", Toast.LENGTH_SHORT).show();
                    mSendToUsers.remove(i);
                }
            }
            if (!flag) {
                mSendToUsers.add(user);
                Toast.makeText(this, user.getUsername() + " added to list!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void blastQuestion(View v) {
        mQuestionID = ((myApplication) getApplication()).getQuestionID();
        String names = mSendToUsers.get(0).getUsername();
        for (int i = 1; i < mSendToUsers.size(); i++) {
            names += ", " +mSendToUsers.get(i).getUsername();
        }
        new AlertDialog.Builder(this)
                .setTitle("Send to Users")
                .setMessage("Are you sure you want to Send to " + names + "?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mQuestionDetails =  ((myApplication) getApplication()).getDetailList();
                        for (int i = 0; i < mQuestionDetails.size(); i++) {
                            mQuestionText = mQuestionDetails.get(i).getQuestionText();
                            mQuestionComment = mQuestionDetails.get(i).getQuestionComment();
                            mQuestionImage = mQuestionDetails.get(i).getmQuestionImage();
                            String url = insertDetailsURL();
                            BlastQuestionTask task = new BlastQuestionTask();
                            task.execute(url);
                        }
                        for (int i = 0; i < mSendToUsers.size(); i++) {
                            mFollowerID = mSendToUsers.get(i).getUserID();
                            String url = insertFollowerURL();
                            BlastQuestionTask task = new BlastQuestionTask();
                            task.execute(url);
                        }
                        Toast.makeText(getBaseContext(), "Question sent!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getBaseContext(), "Reselect followers!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    private String insertDetailsURL() {
        StringBuilder sb = new StringBuilder();
        sb.append(BLAST_QUESTION);
        String url = "insert into QuestionDetail values (" + mQuestionID + ",'" + mQuestionText + "','"
                + mQuestionComment + "','" + mQuestionImage +"');";
        try {
            url = URLEncoder.encode(url, "UTF-8");
        } catch (Exception exception) {
            Log.e("exception", exception.toString());
        }
        sb.append(url);
        return sb.toString();
    }
    private String insertFollowerURL() {
        StringBuilder sb = new StringBuilder();
        sb.append(BLAST_QUESTION);
        String url = "insert into QuestionMember values (" + mQuestionID + "," + mFollowerID+ ");";
        try {
            url = URLEncoder.encode(url, "UTF-8");
        } catch (Exception exception) {
            Log.e("exception", exception.toString());
        }
        sb.append(url);
        return sb.toString();
    }

    /**
     * DownloadUsersTask is an async class that will acccess the database and retreive the current list of users
     * @author Meneka Abraham and Mehgan Cook
     * */
    private class BlastQuestionTask extends AsyncTask<String, Void, String> {

        /**
         * onPreExecute
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * doInBackground
         * @param urls is the string url
         * @return String returns the response from the server
         * */
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s;
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to blast question! Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }


        /**
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         * If not, it displays the exception.
         *
         * @param result is the result
         */
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("errors");
                if (status.equals("none")) {
                    Intent i = new Intent(getBaseContext(), MainViewUsersActivity.class);
                    startActivity(i);

                } else {

                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }


}
