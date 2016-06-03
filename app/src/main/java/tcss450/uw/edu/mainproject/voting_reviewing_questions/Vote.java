/*
 * Slick pick app
  * Mehgan Cook and Tony Zullo
  * Mobile apps TCSS450
 * */
package tcss450.uw.edu.mainproject.voting_reviewing_questions;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import tcss450.uw.edu.mainproject.Helper;
import tcss450.uw.edu.mainproject.R;
import tcss450.uw.edu.mainproject.data.UserDB;
import tcss450.uw.edu.mainproject.model.QuestionWithDetail;
import tcss450.uw.edu.mainproject.myApplication;


/**
 * The vote fragment allows the user to vote for one of the question options
 * A simple {@link Fragment} subclass.
 */
public class Vote extends Fragment {
   /** String url used to update the question member*/
    public static String UPDATE_QUESTIONMEMBER_URL = "http://cssgate.insttech.washington.edu/~_450atm4/" +
            "zombieturtles.php?totallyNotSecure=update+QuestionMember+set+questionanswered+" +
            "%3D+%27true%27%2C+optionpicked+%3D+";
    /**String url to update the question detail*/
    public static String UPDATE_QUESTIONDETAIL_URL = "http://cssgate.insttech.washington.edu/~_450atm4/" +
            "zombieturtles.php?totallyNotSecure=update+QuestionDetail+set+votecount+%3D+";
    /**The user id*/
    public int mUserID;
    /**Holder for text option 1*/
    public  TextView mOption1Text;
    /**Holder for text option 2*/
    public  TextView mOption2Text;
    /**Holder for image option 1*/
    public  ImageView mOption1Image;
    /**Holder for image option 2*/
    public  ImageView mOption2Image;
    /**Holder for comment option 1*/
    public  TextView mOption1Comment;
    /**Holder for comment option 2*/
    public  TextView mOption2Comment;
    /**Button to vote for option 1*/
    public Button mOption1Vote;
    /**Button to vote for option 2*/
    public Button mOption2Vote;
    /**vote id*/
    public int mVoteId;
    /**option 1 id*/
    public int mOption1ID;
    /**option 2 id*/
    public int mOption2ID;
    /**list of questions with details*/
    private List<QuestionWithDetail> mQuestionWithDetail;

    /**
     * Required empty constructor
     * */
    public Vote() {
        // Required empty public constructor
    }

     /**
      * string to bitmap
     * @param encodedString is the string representation of an image
     * @return bitmap (from given string)
     */
    public Bitmap StringToBitMap(String encodedString){
        try{
           // Log.i("StringToBitMap", encodedString);
            byte [] encodeByte=Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
    /**
     * On create view
     * @param savedInstanceState the saved instance state
     * @param inflater the inflater
     * @param container the container
     * @return view
     * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_vote, container, false);

        Helper helper = new Helper(getActivity().getAssets());

        mQuestionWithDetail = ((myApplication) getActivity().getApplication()).getCurrentQuestion();
        mOption1Text = (TextView) view.findViewById(R.id.option1Text);
        mOption2Text = (TextView) view.findViewById(R.id.option2Text);
        mOption1Comment = (TextView) view.findViewById(R.id.option1Comment);
        mOption2Comment = (TextView) view.findViewById(R.id.option2Comment);
        mOption1Image = (ImageView) view.findViewById(R.id.option1Image);
        mOption2Image = (ImageView) view.findViewById(R.id.option2Image);
        mOption1Vote =  (Button) view.findViewById(R.id.voteOption1);
        mOption2Vote =  (Button) view.findViewById(R.id.voteOption2);

        helper.setFontStyle((TextView) view.findViewById(R.id.option1));
        helper.setFontStyle((TextView) view.findViewById(R.id.option2));
        helper.setFontStyle(mOption1Text);
        helper.setFontStyle(mOption2Text);
        helper.setFontStyle(mOption1Comment);
        helper.setFontStyle(mOption2Comment);
        helper.setFontStyle(mOption1Vote);
        helper.setFontStyle(mOption2Vote);

        mUserID = ((myApplication) getActivity().getApplication()).getUserID();
        if (mQuestionWithDetail != null) {
            mOption1ID = mQuestionWithDetail.get(0).getQuestionDetailID();
            mOption2ID = mQuestionWithDetail.get(1).getQuestionDetailID();
            mOption1Text.setText(mQuestionWithDetail.get(0).getQuestionText());
            mOption2Text.setText(mQuestionWithDetail.get(1).getQuestionText());
            String image1 = mQuestionWithDetail.get(0).getQuestionImage();
            String image2 = mQuestionWithDetail.get(1).getQuestionImage();
            Log.i("image1", image1);
            Log.i("image2", image2);
            if (!image1.equals(null)) {
                mOption1Image.setImageBitmap(StringToBitMap(image1));

            }
            if (!image2.equals(null)) {
                mOption2Image.setImageBitmap(StringToBitMap(image2));
            }

            mOption1Comment.setText(mQuestionWithDetail.get(0).getQuestionComment());
            mOption2Comment.setText(mQuestionWithDetail.get(1).getQuestionComment());
        }

        mOption1Vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVoteId = mQuestionWithDetail.get(0).getQuestionId();
                new AlertDialog.Builder(getContext())
                        .setTitle("Vote option 1")
                        .setMessage("Are you sure you want to vote for option 1?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String url = buildURL(1);
                                VoteQuestionTask task = new VoteQuestionTask();
                                task.execute(url);
                                String url1 = buildDetailURL(0);
                                VoteQuestionTask task1 = new VoteQuestionTask();
                                task1.execute(url1);
                                Toast.makeText(getContext(), "Option 1 picked!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), VotingActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), "Select another Option!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

        });

        mOption2Vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVoteId = mQuestionWithDetail.get(1).getQuestionId();
                new AlertDialog.Builder(getContext())
                        .setTitle("Vote option 2")
                        .setMessage("Are you sure you want to vote for option 2?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String url = buildURL(2);
                                VoteQuestionTask task = new VoteQuestionTask();
                                task.execute(url);
                                String url1 = buildDetailURL(1);
                                VoteQuestionTask task1 = new VoteQuestionTask();
                                task1.execute(url1);
                                Toast.makeText(getContext(), "Option 2 picked!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), VotingActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), "Select another Option!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

        });
        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Build the url string to update the question member table
     * @return the url
     * */
    public String buildURL(int option) {
        StringBuilder sb = new StringBuilder(UPDATE_QUESTIONMEMBER_URL);
        try {
            String needsEncode = option + " where questionmemberid = " + mUserID + " and questionid = " + mVoteId;
            sb.append(URLEncoder.encode(needsEncode, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sb.append("%3B");
        return sb.toString();
    }

    /**
     * Build the url string to update the question with details table
     * @return the url
     * */
    public String buildDetailURL(int option) {
        StringBuilder sb = new StringBuilder(UPDATE_QUESTIONDETAIL_URL);
        int votecount = mQuestionWithDetail.get(option).getVoteCount();
        votecount++;
        String needsEncode;
        try {
            if (option == 0) {
                needsEncode = votecount + " where questiondetailid = " + mOption1ID;
            } else {
                needsEncode = votecount + " where questiondetailid = " + mOption2ID;
            }
            sb.append(URLEncoder.encode(needsEncode, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sb.append("%3B");
        return sb.toString();
    }

    /**
     * VoteQuestionTask is an async class that will acccess the database to update tables
     * @author Meneka Abraham and Mehgan Cook
     * */
    private class VoteQuestionTask extends AsyncTask<String, Void, String> {

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

                } else {

                }
            } catch (JSONException e) {
                Toast.makeText(getActivity(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }



}
