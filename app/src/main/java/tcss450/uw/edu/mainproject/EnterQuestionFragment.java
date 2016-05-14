package tcss450.uw.edu.mainproject;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import tcss450.uw.edu.mainproject.data.UserDB;


/**
 * A simple {@link Fragment} subclass.
 */
public class EnterQuestionFragment extends Fragment {
    private final static String ADD_QUESTION
            = "http://cssgate.insttech.washington.edu/~_450atm4/zombieturtles.php?totallyNotSecure=";
        private String mQuestion;
    private UserDB mUserDB;
    private String mEmail;


    public EnterQuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_enter_question, container, false);
        // Inflate the layout for this fragment
        final EditText editText = (EditText) view.findViewById(R.id.enter_question);
        mQuestion = editText.getText().toString();
        mUserDB = new UserDB(getActivity());
        mEmail = mUserDB.getUsers().get(0).getEmail();
        final String url = buildCourseURL();

        Button options = (Button) view.findViewById(R.id.select_options);
      //  options.setOnClickListener(this);
        options.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mQuestion = editText.getText().toString();
                if (TextUtils.isEmpty(mQuestion)) {
                    Toast.makeText(getActivity(), "Enter Question"
                            , Toast.LENGTH_SHORT)
                            .show();
                    editText.requestFocus();
                    return;
                }
                String url = buildCourseURL();
                Log.i("url", url);
                Log.i("mquestion", mQuestion);
                AddQuestionTask task = new AddQuestionTask();
                task.execute(url);
                Fragment options = new EnterQuestionOptionFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.blast_question_container,options)
                        .commit();
                View view = getActivity().getCurrentFocus();
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
              //  editText.clearFocus();

            }
        }) ;
        return view;//inflater.inflate(R.layout.fragment_enter_question, container, false);
    }




    private String buildCourseURL() {
        StringBuilder sb = new StringBuilder();
        sb.append(ADD_QUESTION);
    //    String selectStatement = "(select userid from User where email = '"  + mEmail + "')";
        String url = "insert into Question values ('','" + mQuestion + "','" + mEmail + "');";
        try {
            url = URLEncoder.encode(url, "UTF-8");
        } catch (Exception exception) {
            Log.e("exception", exception.toString());
        }
        sb.append(url);
        Log.i("ecnoded url", sb.toString());
        return sb.toString();
    }


    /**
     * DownloadUsersTask is an async class that will acccess the database and retreive the current list of users
     * @author Meneka Abraham and Mehgan Cook
     * */
    private class AddQuestionTask extends AsyncTask<String, Void, String> {

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
                    response = "Unable to add " + mQuestion + ", Reason: "
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
//                    Intent i = new Intent(mMe, MainViewUsersActivity.class);
//                    startActivity(i);  START THE NEW FRAGMENT

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Email is already registered! Please log in"

                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getActivity().getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }


}
