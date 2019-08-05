/*
 * Project: Waves
 *
 * Purpose: Creates and sends an email to given recipient with a list of all the tasks
 * in a selected category.
 *
 * Reference(s): Aweys Abdullatif
 */

package com.example.waves_app.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.waves_app.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ShareFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private EditText etEmail;
    private EditText etSubject;
    private EditText etMessage;
    private TextView tvAttachment;
    private Button send;
    private String email;
    private String subject;
    private String message;
    private String attachmentFile;
    private Uri URI = null;
    private static final int PICK_FROM_GALLERY = 101;
    private List<String> categoryData;
    private List<String> taskData;
    private int columnIndex;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, container, false);
        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.sand_background));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etEmail = (EditText) view.findViewById(R.id.et_to);
        etSubject = (EditText) view.findViewById(R.id.et_subject);
        etMessage = (EditText) view.findViewById(R.id.et_message);
        tvAttachment = (TextView) view.findViewById(R.id.tv_attachment);
        tvAttachment.setText("Attached file");

        readCategoryItems();

        List<String> data = new ArrayList<>();
        for (String categoryName : categoryData) {
            data.add(categoryName);
        }

        Spinner spin = (Spinner) view.findViewById(R.id.drop_down);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);

        // Set send button listener
        send = (Button) view.findViewById(R.id.bt_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        etMessage.getText().clear();
        readCategoryItems();

        List<String> data = new ArrayList<>();
        for (String categoryName : categoryData) {
            data.add(categoryName);
        }

        readTaskItems(data.get(position));

        // Create and format the message
        String message = "Below are the items in my " + data.get(position) + " list: \n \n";
        for (String task : taskData) {
            int delimiter = task.indexOf(",");
            String taskDetail = task.substring(0, delimiter);
            String dueDate = task.substring(delimiter + 1);

            message += "> " + taskDetail + "\n";
            if (!dueDate.equals("set due date")) {
                message += "   " + dueDate + "\n";
            }
        }

        etMessage.setText(message);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        Toast.makeText(getContext(), "Please select a category to share" ,Toast.LENGTH_SHORT).show();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContext().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();

            columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            attachmentFile = cursor.getString(columnIndex);

            URI = Uri.parse("file://" + attachmentFile);
            cursor.close();
        }
    }

    public void sendEmail() {
        if (!isValidEmail(etEmail.getText().toString())) {
            Toast.makeText(getContext(), "Your email is not valid.", Toast.LENGTH_LONG).show();
        }
        else {
            try {
                email = etEmail.getText().toString();
                subject = etSubject.getText().toString();
                message = etMessage.getText().toString();

                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);

                if (URI != null) {
                    emailIntent.putExtra(Intent.EXTRA_STREAM, URI);
                }

                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
                this.startActivity(Intent.createChooser(emailIntent, "Send email with..."));
            } catch (Throwable t) {
                Toast.makeText(getContext(), "Request failed try again: " + t.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void readCategoryItems() {
        try {
            // Create the array of categories
            categoryData = new ArrayList<String>(FileUtils.readLines(getCategoriesFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            categoryData = new ArrayList<>();
            e.printStackTrace();
        }
    }

    private File getCategoriesFile() {
        return new File(getContext().getFilesDir(), "allCategories.txt");
    }

    private File getTaskFile(String cat) {
        return new File(getContext().getFilesDir(), cat + ".txt");
    }

    private void readTaskItems(String cat) {
        try {
            // create the array of tasks
            taskData = new ArrayList<String>(FileUtils.readLines(getTaskFile(cat), Charset.defaultCharset()));
        } catch (IOException e) {
            taskData = new ArrayList<>();
            e.printStackTrace();
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}