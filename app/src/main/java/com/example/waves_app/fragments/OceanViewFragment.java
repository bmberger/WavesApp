/*
 * Project: Waves
 *
 * Purpose: To display a VR view of the ocean
 * and informs the user on plastic pollution resources
 *
 * Reference(s): Briana Berger
 */

package com.example.waves_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.waves_app.R;
import com.google.devrel.vrviewapp.ImageLoaderTask;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.util.Random;

public class OceanViewFragment extends Fragment {

    private VrPanoramaView panoWidgetView;
    private ImageLoaderTask backgroundImageLoaderTask;
    private ImageView plasticOceans;
    private ImageView oceanCleanup;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ocean_view, container,false);
        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.sand_background));

        // Intializes views for screen
        panoWidgetView = (VrPanoramaView) view.findViewById(R.id.pano_view);
        plasticOceans = (ImageView) view.findViewById(R.id.ivPlasticOceans);
        oceanCleanup = (ImageView) view.findViewById(R.id.ivOceanCleanup);
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // Redirects to specific organizations and their websites
        plasticOceans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new WebViewFragment();
                Bundle information = new Bundle();

                information.putString("url", "https://plasticoceans.org");
                fragment.setArguments(information);
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment, "WebView").addToBackStack(null).commit();
            }
        });

        oceanCleanup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new WebViewFragment();
                Bundle information = new Bundle();

                information.putString("url", "http://theoceancleanup.com/north-pacific-foundation/");
                fragment.setArguments(information);
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment, "WebView").addToBackStack(null).commit();
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        panoWidgetView.pauseRendering();
        super.onPause();
    }

    @Override
    public void onResume() {
        panoWidgetView.resumeRendering();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        // Destroy the widget and free memory.
        panoWidgetView.shutdown();
        super.onDestroy();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadPanoImage();
    }

    private synchronized void loadPanoImage() {
        ImageLoaderTask task = backgroundImageLoaderTask;
        if (task != null && !task.isCancelled()) {
            // Cancel any task from a previous loading
            task.cancel(true);
        }

        // Pass in the name of the image to load from assets
        VrPanoramaView.Options viewOptions = new VrPanoramaView.Options();
        viewOptions.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER;

        // Use the name of the image in the assets/directory
        String panoImageName = getRandomFishScene();

        // Create the task passing the widget view and call execute to start
        task = new ImageLoaderTask(panoWidgetView, viewOptions, panoImageName);
        task.execute(getActivity().getAssets());
        backgroundImageLoaderTask = task;
    }

    public String getRandomFishScene() {
        // Generates random integer between 0 and 4 inclusive
        int random = new Random().nextInt(5);
        return "vr_" + random + ".jpg";
    }
}