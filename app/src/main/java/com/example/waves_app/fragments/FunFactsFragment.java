/*
 * Project: Waves
 *
 * Purpose: Displays the view for the fun facts page
 *
 * Reference(s): Aweys Abdullatif
 */

package com.example.waves_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.FunFactsAdapter;
import com.example.waves_app.R;
import com.example.waves_app.model.FunFacts;

import java.util.ArrayList;

public class FunFactsFragment extends Fragment {

    private RecyclerView rvQuestions;
    private FunFactsAdapter tvShowAdapter;
    private ArrayList<FunFacts> tvShows = new ArrayList<FunFacts>();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_funfacts, container, false);
        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.sand_background));
        view.bringToFront();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        for(int i = 0; i < questions.length; i++)
        {
            FunFacts tvShow = new FunFacts();

            tvShow.setTvshow(questions[i]);
            tvShow.setTvshowAnswer(answers[i]);
            tvShows.add(tvShow);
        }

        tvShowAdapter = new FunFactsAdapter(tvShows);
        rvQuestions = (RecyclerView) view.findViewById(R.id.rvQuestions);
        rvQuestions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvQuestions.setItemAnimator(new DefaultItemAnimator());
        rvQuestions.setAdapter(tvShowAdapter);
    }

    public static final String[] questions = {
            "Over __% of the Earth's surface is covered by salt water.",
            "The Earth's Oceans are home to ___,___ known species. And that's with only 5% of the Earth's oceans explored!",
            "Back in 2014, it was estimated that there were ___ trillion pieces of plastic debris in the ocean.",
            "The International Union for Conservation of Nature (IUCN) currently lists more than ___ marine species as already endangered or vulnerable of becoming so. Some of the best-known of endangered marine life includes the angel shark, and the blue whale.",
            "The ocean constitutes over __ of the habitable space on the planet.",
            "By the year ____, without significant changes, more than half of the world’s marine species may stand on the brink of extinction.",
            "Today, fisheries provide over __ percent of the dietary intake of animal protein.",
            "Approximately 12% of the land area is protected, compared to roughly _ percent of the world's oceans and adjacent seas.",
            "Tiny phytoplankton provide __ percent of the oxygen on the earth and form the basis of the ocean's food chain from fish up to marine mammals, and ultimately for human consumption.",
            "Today, __ percent of the world’s major marine ecosystems that underpin livelihoods have been degraded or are being used unsustainably.",
            "Commercial over-exploitation of the world’s fish stocks is so severe that it has been estimated that up to __ percent of global fisheries have collapsed.",
            "Coastal systems such as such as mangroves, salt marshes and seagrass meadows have the ability to absorb, or sequester, carbon at rates up to __ times those of the same area of tropical forests.",
            "Total carbon deposits in these coastal systems may be up to _ times the carbon stored in tropical forests."};

    public static final String[] answers = {
            "72",
            "230,000",
            "5.25",
            "360",
            "90%",
            "2100",
            "15%",
            "1%",
            "50%",
            "60%",
            "13",
            "50",
            "5"};
}