package com.androidui.georgew.androidui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class linear_layout_vertical extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_layout_vertical);

        Button btnClose = (Button) findViewById(R.id.btnClose);
        final TextView text = (TextView) findViewById(R.id.addText);
        Button btnChangeWord = (Button) findViewById(R.id.btnChangeWord);
        final EditText edit = (EditText) findViewById(R.id.addField);
        String[] array = getResources().getStringArray(R.array.linear_array);

        btnClose.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                //Closing linear_layout_vertical Activity
                finish();
            }
        });

        final List<String> inputsList = new ArrayList<String>(Arrays.asList(array));

        btnChangeWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = edit.getText().toString();
                inputsList.add(inputsList.size(), result);
                text.setText(result);

                String changedItemText = inputsList.get(inputsList.size() - 1);

                // Confirm the addition
                Toast.makeText(getApplicationContext(),
                        "Item Changed : " + changedItemText, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
