package com.example.waves_app.fragments;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.waves_app.R;

public class FAQFragment extends Fragment {
    TextView tvPlasticOceans;
    TextView tvOceanCleanup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_faq, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvPlasticOceans = ((TextView) getActivity().findViewById(R.id.tvPlasticOceans));
        tvOceanCleanup = ((TextView) getActivity().findViewById(R.id.tvOceanCleanup));

        // Sets up blue HTML link for Plastic Oceans and Ocean Cleanup
        if (tvPlasticOceans != null && tvOceanCleanup != null) {
            tvPlasticOceans.setMovementMethod(LinkMovementMethod.getInstance());
            tvOceanCleanup.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
