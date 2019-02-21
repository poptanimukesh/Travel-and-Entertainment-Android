package com.example.mukesh.myapplication.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.mukesh.myapplication.R;
import com.example.mukesh.myapplication.model.Reviews;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by mukesh on 4/14/18.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private List<Reviews> reviewsList;
    private FragmentActivity activity;

    public ReviewAdapter(FragmentActivity activity, List<Reviews> reviewsList) {
        this.activity = activity;
        this.reviewsList = reviewsList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public TextView name;
        public TextView comment;
        public TextView date;
        public ImageView authorImg;
        public RatingBar ratingBar;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            layout = v;
            name = (TextView) v.findViewById(R.id.authorName);
            comment = (TextView) v.findViewById(R.id.authorComment);
            date = (TextView) v.findViewById(R.id.authorDate);
            authorImg = (ImageView) v.findViewById(R.id.authorImage);
            ratingBar = (RatingBar) v.findViewById(R.id.authorRating);
        }

        @Override
        public void onClick(View view) {
            if(!reviewsList.get(getPosition()).getUrl().isEmpty()) {
                Uri uri = Uri.parse(reviewsList.get(getPosition()).getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                activity.startActivity(intent);
            }
        }
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.review_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder holder, int position) {
        final Reviews review = reviewsList.get(position);

        holder.name.setText(review.getName());
        holder.comment.setText(review.getComment());
        holder.date.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(review.getDate()));
        holder.ratingBar.setRating(review.getRating());

        if(review.getPhotoUrl().isEmpty()) {
            holder.authorImg.setImageResource(0);
        } else{
            Picasso.get().load(review.getPhotoUrl()).into(holder.authorImg);
        }

    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }
}
