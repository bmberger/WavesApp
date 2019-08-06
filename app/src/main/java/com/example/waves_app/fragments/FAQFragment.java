/*
 * Project: Waves
 *
 * Purpose: Displays the FAQ page
 *
 * Reference(s): Briana Berger
 */

package com.example.waves_app.fragments;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.waves_app.R;

public class FAQFragment extends Fragment {
    private TextView tvPlasticOceans;
    private TextView tvOceanCleanup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq, container, false);
        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.sand_background));
        getActivity().setTitle(""); // Required for setting action bar title
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvPlasticOceans = ((TextView) getActivity().findViewById(R.id.tvPlasticOceans));
        tvOceanCleanup = ((TextView) getActivity().findViewById(R.id.tvOceanCleanup));

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("FAQ");

        // Sets up blue HTML link for Plastic Oceans and Ocean Cleanup
        if (tvPlasticOceans != null && tvOceanCleanup != null) {
            tvPlasticOceans.setMovementMethod(LinkMovementMethod.getInstance());
            tvOceanCleanup.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}