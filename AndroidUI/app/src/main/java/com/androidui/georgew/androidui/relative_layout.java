package com.androidui.georgew.androidui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class relative_layout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relative_layout);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        Button btnAdd = (Button) findViewById(R.id.btnAddWord);
        Button btnClose = (Button) findViewById(R.id.btnClose);
        final EditText edit = (EditText) findViewById(R.id.inputs);
        String[] array = getResources().getStringArray(R.array.spinner_array);


        final List<String> inputsList = new ArrayList<String>(Arrays.asList(array));

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, inputsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        btnClose.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                //Closing relative_layout activity
                finish();
            }
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = edit.getText().toString();
                inputsList.add(inputsList.size(), result);
                adapter.notifyDataSetChanged();

                String addedItemText = inputsList.get(inputsList.size() - 1);

                // Confirm the addition
                Toast.makeText(getApplicationContext(),
                        "Item added : " + addedItemText, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

