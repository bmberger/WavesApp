package com.example.waves_app.fragments;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.FunFactsAdapter;
import com.example.waves_app.R;
import com.example.waves_app.model.FunFacts;

import org.w3c.dom.Text;
import java.util.Random;

import java.util.ArrayList;
import java.util.List;

public class FunFactsFragment extends Fragment {

    RecyclerView recyclerView;
    FunFactsAdapter tvShowAdapter;
    ArrayList<FunFacts> tvShows = new ArrayList<FunFacts>();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_funfacts, container, false);
        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.sand_background));
        view.bringToFront();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        for(int i=0;i<TvShows.length;i++)
        {
            FunFacts tvShow = new FunFacts();

            tvShow.setTvshow(TvShows[i]);
            tvShow.setTvshowAnswer(TvShowsAnswers[i]);
            tvShows.add(tvShow);
        }

        tvShowAdapter = new FunFactsAdapter(tvShows);
        recyclerView = (RecyclerView)view.findViewById(R.id.TvShows);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(tvShowAdapter);
    }

    public static final String[] TvShows= {
            "The ocean constitutes over __ of the habitable space on the planet.",
            "By the year ___, without significant changes, more than half of the world’s marine species may stand on the brink of extinction.",
            "Today, fisheries provide over __ percent of the dietary intake of animal protein.",
            "Approximately 12% of the land area is protected, compared to roughly __ of the world ocean and adjacent seas.",
            "Tiny phytoplancton provide __ of the oxygen on earth and form the basis of the ocean food chain up to fish and marine mammals, and ultimately human consumption.",
            "Today __ of the world’s major marine ecosystems that underpin livelihoods have been degraded or are being used unsustainably.",
            "Commercial overexploitation of the world’s fish stocks is so severe that it has been estimated that up to __ percent of global fisheries have ‘collapsed.",
            "Coastal systems such as such as mangroves, salt marshes and seagrass meadows have the ability to absorb, or sequester, carbon at rates up to __ times those of the same area of tropical forest.",
            "Total carbon deposits in these coastal systems may be up to __ times the carbon stored in tropical forests."};

    public static final String[] TvShowsAnswers= {
            "90%",
            "2100",
            "15",
            "1%",
            "50%",
            "60%",
            "13",
            "50",
            "5"};
}

