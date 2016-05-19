package tcss450.uw.edu.mainproject;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tcss450.uw.edu.mainproject.AskedQuestionResultFragment.OnListFragmentInteractionListener;

import tcss450.uw.edu.mainproject.model.QuestionWithDetail;

import java.util.List;

/**
 * specified {@link AskedQuestionResultFragment.OnListFragmentInteractionListener}.
 */
public class MyAskedQuestionResultRecyclerViewAdapter extends RecyclerView.Adapter<MyAskedQuestionResultRecyclerViewAdapter.ViewHolder> {

    private final List<QuestionWithDetail> mValues;
    private final AskedQuestionResultFragment.OnListFragmentInteractionListener mListener;
    /**font*/
    private Typeface mFont;

    public MyAskedQuestionResultRecyclerViewAdapter(List<QuestionWithDetail> items, AskedQuestionResultFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }
    /**Constructor for recylcer view
     * @param listener the listener
     * @param items the users
     * @param font the font*/
    public MyAskedQuestionResultRecyclerViewAdapter(List<QuestionWithDetail> items, AskedQuestionResultFragment.OnListFragmentInteractionListener listener,
                                          Typeface font) {
        this(items, listener);
        mFont = font;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_askedquestionresult, parent, false);
        return new ViewHolder(view);
    }

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
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public QuestionWithDetail mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
