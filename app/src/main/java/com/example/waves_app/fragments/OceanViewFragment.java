package com.example.waves_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
        panoWidgetView = (VrPanoramaView) view.findViewById(R.id.pano_view);
        plasticOceans = (ImageView) view.findViewById(R.id.ivPlasticOceans);
        oceanCleanup = (ImageView) view.findViewById(R.id.ivOceanCleanup);

        // redirects to those organizations and their websites
        plasticOceans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //https://plasticoceans.org
            }
        });

        oceanCleanup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //http://theoceancleanup.com/north-pacific-foundation/
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
            // Cancel any task from a previous loading.
            task.cancel(true);
        }

        // pass in the name of the image to load from assets.
        VrPanoramaView.Options viewOptions = new VrPanoramaView.Options();
        viewOptions.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER;

        // use the name of the image in the assets/directory.
        String panoImageName = getRandomFishScene();

        // create the task passing the widget view and call execute to start.
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
