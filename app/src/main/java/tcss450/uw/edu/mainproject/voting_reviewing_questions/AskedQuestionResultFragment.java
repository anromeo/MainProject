/*
 * Slick pick app
  * Mehgan Cook and Tony Zullo
  * Mobile apps TCSS450
 * */
package tcss450.uw.edu.mainproject.voting_reviewing_questions;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import tcss450.uw.edu.mainproject.Helper;
import tcss450.uw.edu.mainproject.R;
import tcss450.uw.edu.mainproject.data.UserDB;
import tcss450.uw.edu.mainproject.followers_askers_groups.FollowListFragment;
import tcss450.uw.edu.mainproject.model.QuestionWithDetail;
import tcss450.uw.edu.mainproject.myApplication;


/**
 * A fragment representing a list of asked questions.
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class AskedQuestionResultFragment extends Fragment {

    /**string column count*/
    private static final String ARG_COLUMN_COUNT = "column-count";
    /**int column count*/
    private int mColumnCount = 1;
    /**List of questions with details*/
    private List<QuestionWithDetail> mQuestionWithDetail;
    /**Listener*/
    private OnListFragmentInteractionListener mListener;
    /**Recycle view*/
    private RecyclerView mRecyclerView;
    /**String for the url to access the users from the database*/
    private static final String QUESTIONS_I_ASKED_URL
            = "http://cssgate.insttech.washington.edu/~_450atm4/zombieturtles.php?totallyNotSecure=select+questionanswered%2C+questionname%2C+questionid%2C+questiontext%2C+" +
            "questioncomment%2C+questionimage%2C+votecount%2C+questiondetailid%0D%0Afrom+" +
            "QuestionDetail+natural+join+QuestionMember+natural+join+Question+where+" +
            "Question.useremail+%3D+%27";

    /** Helper for fonts */
    private Helper mHelper;



    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AskedQuestionResultFragment() {
    }

    /**
     * onCreate
     * @param savedInstanceState the saved instance state
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
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
        View view = inflater.inflate(R.layout.fragment_followlist_list, container, false);
//        mHelper = new Helper(getActivity().getAssets());
//        mHelper.setFontStyle((TextView) view.findViewById(R.id.content));
        String url = buildURL();
        Log.i("URL FOR ASKED Q!", url);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            DownloadAskedQuestionsTask task = new DownloadAskedQuestionsTask();
            task.execute(url);
        }

        return view;
    }

    /**
     * on attack
     * @param context the context
     * */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    /**
     * on detach
     * */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Build the url string based on the currently logged in user email address
     * @return the url
     * */
    public String buildURL() {
        StringBuilder sb = new StringBuilder(QUESTIONS_I_ASKED_URL);
        UserDB userDB = new UserDB(getActivity());
        String email = userDB.getUsers().get(0).getEmail();
        try {
            sb.append(URLEncoder.encode(email, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sb.append("%27%3B");
        return sb.toString();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        /**list fragement interaction
         */
        void onListFragmentInteraction(QuestionWithDetail questionWithDetail);
    }
    /**
     * Class to download the followeres list in the backgroud using an async task
     * */
    private class DownloadAskedQuestionsTask extends AsyncTask<String, Void, String> {
        /**
         * call the server in the background
         * @param urls the url
         * @return the response from the servier
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
                    response = "Unable to download the list of followers, Reason: "
                            + e.getMessage();
                }
                finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }
        /**
         * on post execute
         * @param result the result from the server
         * */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            mQuestionWithDetail = new ArrayList<>();
            result = QuestionWithDetail.parseQuestionWithDetailJSON(result, mQuestionWithDetail);
            // Something wrong with the JSON returned.
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            // Everything is good, show the list of courses.
            List<QuestionWithDetail> distinct = new ArrayList<>();
            ((myApplication) getActivity().getApplication()).setQuestionLst(mQuestionWithDetail);
            for (int i = 0; i < mQuestionWithDetail.size(); i++) {
                if (i == 0) {//this is if we only have 2 options will need to change if we want to add more options
                    distinct.add(mQuestionWithDetail.get(i));
                } else {
                    if (mQuestionWithDetail.get(i).getQuestionId() !=
                            mQuestionWithDetail.get(i - 1).getQuestionId()) {
                        distinct.add(mQuestionWithDetail.get(i));
                    }
                }

            }
            Log.i("distinct list numbers", distinct.size() + "");
            if (!distinct.isEmpty()) {
                mRecyclerView.setAdapter(new MyAskedQuestionResultRecyclerViewAdapter(distinct, mListener,
                        Typeface.createFromAsset(getActivity().getAssets(), "fonts/Oswald-Regular.ttf")));
            }

        }


    }
}
