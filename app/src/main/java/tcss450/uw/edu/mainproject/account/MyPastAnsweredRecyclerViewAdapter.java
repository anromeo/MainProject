/*
 * Slick pick app
  * Mehgan Cook and Tony Zullo
  * Mobile apps TCSS450
 * */
package tcss450.uw.edu.mainproject.account;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tcss450.uw.edu.mainproject.R;
import tcss450.uw.edu.mainproject.model.QuestionWithDetail;
import tcss450.uw.edu.mainproject.voting_reviewing_questions.AskedQuestionResultFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link QuestionWithDetail} and makes a call to the
 * specified {@link PastAnsweredFragment.OnPastAnsweredListFragmentInteractionListener}.
 *
 */
public class MyPastAnsweredRecyclerViewAdapter extends RecyclerView.Adapter<MyPastAnsweredRecyclerViewAdapter.ViewHolder> {

    /**List of questions with details*/
    private final List<QuestionWithDetail> mValues;
    /**Listener*/
    private final PastAnsweredFragment.OnPastAnsweredListFragmentInteractionListener mListener;
    /**font*/
    private Typeface mFont;

    /**Constructor for recycler view
     * @param items the users
     * @param listener the listener*/
    public MyPastAnsweredRecyclerViewAdapter(List<QuestionWithDetail> items, PastAnsweredFragment.OnPastAnsweredListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }
    /**Constructor for recylcer view
     * @param listener the listener
     * @param items the users
     * @param font the font*/
    public MyPastAnsweredRecyclerViewAdapter(List<QuestionWithDetail> items, PastAnsweredFragment.OnPastAnsweredListFragmentInteractionListener listener,
                                                Typeface font) {
        this(items, listener);
        mFont = font;
    }
    /**
     * on create view holder
     * @param parent the view group
     * @param viewType the view type
     * */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pastansweredfragment, parent, false);
        return new ViewHolder(view);
    }
    /**
     * on bind view holder
     * @param holder the view holder
     * @param position the position
     * */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getQuestionName());
        // holder.mContentView.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onPastAnsweredListFragmentInteraction(holder.mItem);
                }
            }
        });
    }
    /**
     * get item count
     * @return amount of users
     * */
    @Override
    public int getItemCount() {
        return mValues.size();
    }
    /**
     * View holder class to hold the recylcer view
     * */
    public class ViewHolder extends RecyclerView.ViewHolder {
        /**the view*/
        public final View mView;
        /** the text view of id*/
        public final TextView mIdView;
        /**the text view of content*/
        public final TextView mContentView;
        /**The current Question with details*/
        public QuestionWithDetail mItem;
        /**Constructor of the viewholder
         * @param view the view*/
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            if (mFont != null) {
                mIdView.setTypeface(mFont);
                mContentView.setTypeface(mFont);
            }
        }
        /**
         * tostring
         * @return string
         * */
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
