package com.example.waves_app;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.funfactsfragment.model.FunFacts;
import com.example.waves_app.model.FunFacts;

import java.util.List;


public class FunFactsAdapter extends RecyclerView.Adapter<FunFactsAdapter.ViewHolder> {


    List<FunFacts> TvShowList;
    Context context;

    public FunFactsAdapter(List<FunFacts>TvShowList)
    {
        this.TvShowList = TvShowList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.facts_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final FunFacts tvShow = TvShowList.get(position);

        holder.textTvShow.setText(TvShowList.get(position).getTvshow());
        holder.textTvShow1.setText(TvShowList.get(position).getTvshowAnswer());

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                flipCard();


                final View v = holder.cv;

                // first quarter turn
                v.animate().withLayer()
                        .rotationY(90)
                        .setDuration(300)
                        .withEndAction(
                                new Runnable() {
                                    @Override public void run() {

                                        // second quarter turn
                                        v.setRotationY(-90);
                                        v.animate().withLayer()
                                                .rotationY(0)
                                                .setDuration(300)
                                                .start();
                                        holder.cv.setVisibility(View.GONE);
                                        holder.cv1.setVisibility(View.VISIBLE);
                                    }
                                }
                        ).start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return TvShowList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textTvShow;
        CardView cv;
        TextView textTvShow1;
        CardView cv1;

        public ViewHolder(View itemView)
        {
            super(itemView);
            textTvShow = (TextView)itemView.findViewById(R.id.textTvshow);
            cv = (CardView)itemView.findViewById(R.id.cv);
            textTvShow1 = (TextView)itemView.findViewById(R.id.textTvshow1);
            cv1 = (CardView)itemView.findViewById(R.id.cv1);
        }
    }
}