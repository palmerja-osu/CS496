package com.androidui.georgew.androidui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class linear_layout_horizontal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_layout_horizontal);

        Button btnClose = (Button) findViewById(R.id.btnClose);
        final TextView text = (TextView) findViewById(R.id.addTextHoriz);
        Button btnChangeWord = (Button) findViewById(R.id.btnChangeNumber);
        final EditText edit = (EditText) findViewById(R.id.addField);


        final Integer [] numbers = new Integer[]{1};

        final List<Integer> numberList = new ArrayList<Integer>(Arrays.asList(numbers));

        btnClose.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                //Closing linear_layout_horizontal Activity
                finish();
            }
        });


        btnChangeWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = edit.getText().toString();
                Integer intResult = Integer.parseInt(result);
                numberList.add(numberList.size(), intResult);
                text.setText(result);

                Integer changedItemNumber = numberList.get(numberList.size() - 1);

                // Confirm the addition
                Toast.makeText(getApplicationContext(),
                        "Item Changed : " + changedItemNumber, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
