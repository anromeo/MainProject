/*
 * Slick pick app
  * Mehgan Cook and Tony Zullo
  * Mobile apps TCSS450
 * */
package tcss450.uw.edu.mainproject;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import tcss450.uw.edu.mainproject.data.UserDB;
import tcss450.uw.edu.mainproject.model.User;

/**
 * A fragment representing a list of Askers.
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class AskerListFragment extends Fragment {

    /** string Column count*/
    private static final String ARG_COLUMN_COUNT = "column-count";
    /**int column count*/
    private int mColumnCount = 1;
    /**List of users*/
    private List<User> mAskers;
    /**Listener*/
    private OnListFragmentInteractionListener mListener;
    /**Recycle view*/
    private RecyclerView mRecyclerView;
    /**URL to retrieve user information*/
    private static final String ASKER_LIST_URL
            = "http://cssgate.insttech.washington.edu/~_450atm4/zombieturtles.php?totallyNotSecure=" +
            "select+username%2CUser.email%2CUser.password%2CUser.userid+from+Askers%2CUser+where+" +
            "User.userid+%3D+Askers.askerid+and+Askers.userid+%3D+%0D%0A%28select+userid+" +
            "from+User+where+email+%3D+%27";



    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AskerListFragment() {
    }

    /**
     * newIstance of the askerlist
     * @param columnCount the column count
     * */
    @SuppressWarnings("unused")
    public static AskerListFragment newInstance(int columnCount) {
        AskerListFragment fragment = new AskerListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * oncreate
     * @param savedInstanceState the saved instance
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }
    /**
     * on create view
     * @param savedInstanceState the saved instance
     * @param container of viewGroup
     * @param inflater inflates the fragment
     * @return the view
     * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_askerlist_list, container, false);
        String url = buildURL();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            DownloadAskersTask task = new DownloadAskersTask();
            task.execute(url);

        }

        return view;
    }

    /**
     * onAttach
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
     *
     * */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    /**
     * builds the url to retreive asker lists using the email of the current logged in user
     * @return the url string
     * */
    public String buildURL() {
        StringBuilder sb = new StringBuilder(ASKER_LIST_URL);
        UserDB userDB = new UserDB(getActivity());
        String email = userDB.getUsers().get(0).getEmail();
        try {
            sb.append(URLEncoder.encode(email, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sb.append("%27%29%3B");
        return sb.toString();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        /**
         * The list fragment interaction
         * The uesr
         * */
        void onListFragmentInteraction(User user);
    }

    /**
     * Class to download the askers list in the backgroud using an async task
     * */
    private class DownloadAskersTask extends AsyncTask<String, Void, String> {
        /**
         * tasks to be done in the background
         * @param urls the string of url
         * @return the response from the server
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
                    response = "Unable to download the list of askers, Reason: "
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
         * On Post execute of the task
         * @param result the result from the databsae
         * */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            mAskers = new ArrayList<>();
            result = User.parseUserJSON(result, mAskers);
            // Something wrong with the JSON returned.
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            // Everything is good, show the list of courses.
            if (!mAskers.isEmpty()) {

                mRecyclerView.setAdapter(new MyAskerListRecyclerViewAdapter(mAskers, mListener,
                        Typeface.createFromAsset(getActivity().getAssets(), "fonts/Oswald-Regular.ttf")));
            }
        }


    }
}
