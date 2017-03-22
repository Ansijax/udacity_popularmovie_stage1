package com.ansijax.udacity.popularmovies.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ansijax.udacity.popularmovies.popularmovies.pojo.Reviews;

/**
 * Created by massimo on 22/03/17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsListViewHolder> {

    Reviews mReviews;


    @Override
    public ReviewsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item,parent,false);
        ReviewsListViewHolder holder = new ReviewsListViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(ReviewsListViewHolder holder, int position) {

        holder.mTvAuthor.setText(mReviews.getResults().get(position).getAuthor());
        holder.mTvReview.setText(mReviews.getResults().get(position).getContent());

    }



    @Override
    public int getItemCount() {
        if(mReviews==null)
            return 0;
        else
            return mReviews.getResults().size();
    }

    public class ReviewsListViewHolder extends RecyclerView.ViewHolder {

        TextView mTvAuthor;
        TextView mTvReview;

        public ReviewsListViewHolder(View itemView) {

            super(itemView);

            mTvReview = (TextView) itemView.findViewById(R.id.tv_review);
            mTvAuthor = (TextView) itemView.findViewById(R.id.tv_author);



        }


    }


    public void setAdapter(Reviews reviews){
        mReviews=reviews;
        notifyDataSetChanged();
    }
}
