package com.example.waves_app.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.waves_app.R;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static com.parse.Parse.getApplicationContext;

public class SettingsFragment extends Fragment {
    // Declarations
    ArrayList<String> settings; // items data in strings (model)
    ArrayAdapter<String> settingsAdapter; // items that moves the model to the view (controller)
    ListView settingsList;
    Document doc;
    //String filepath = "/Users/brianamb/AndroidStudioProjects/WavesApp/app/src/main/res/values/styles.xml";
    String filepath = "styles.xml";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.sand_background));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up architecture of list and list adapter
        settingsList = (ListView) view.findViewById(R.id.listItems);
        settings = new ArrayList<String>();

        String[] settingsPageOptions = new String[] { "Change Font Style", "Change Font Size"};
        settings.addAll(Arrays.asList(settingsPageOptions));

        settingsAdapter = new ArrayAdapter<String>(getContext(), R.layout.simple_row_layout, settings) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the current item from ListView and sets colors for gradient
                View view = super.getView(position, convertView, parent);
                view.setBackgroundColor(getResources().getColor(R.color.blue_6));
                return view;
            }
        };

        settingsList.setAdapter(settingsAdapter);
        listViewListener();

        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filepath));
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    // Listens for when someone clicks on an item in list
    private void listViewListener() {
        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Get the item at position in the items ArrayList<String>
                String clickedOption = settings.get(position).toString();

                final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment fragment;

                // Switches to a different category dependent on user choice
                if (clickedOption.equals("Change Font Style")) {
                    try {
                        popup("font style");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }
                } else if (clickedOption.equals("Change Font Size")) {
                    try {
                        popup("font size");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void popup(String popupType) throws IOException, SAXException, ParserConfigurationException {
        Dialog dialog = new Dialog(this.getContext());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.ic_popup_select);
        TextView tvSelectStatement;
        
        tvSelectStatement = (TextView) dialog.findViewById(R.id.tvSelectStatement);
        tvSelectStatement.setText("Select " + popupType);

        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter;

        adapter = (ArrayAdapter<CharSequence>) ArrayAdapter.createFromResource(dialog.getContext(), R.array.preloaded_fonts, android.R.layout.simple_spinner_dropdown_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        dialog.show();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View view,
                                       int pos, long id) {
            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }
}
