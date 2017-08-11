package com.androidui.georgew.androidui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ListMenuItemView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class grid_layout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_layout);

        GridView gridView;
        Button btnAddNumber = (Button) findViewById(R.id.add_number_grid);
        Button btnClose = (Button) findViewById(R.id.btnClose);

        btnClose.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                //Closing linear_layout_horizontal Activity
                finish();
            }
        });

        final Integer[] numbers = new Integer[]{
                1,2,3,4,5,6,7
        };

        final List<Integer> numberList = new ArrayList<Integer>(Arrays.asList(numbers));

        gridView = (GridView) findViewById(R.id.gridview1);

        final ArrayAdapter<Integer> gridViewAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, numberList);

        gridView.setAdapter(gridViewAdapter);

        btnAddNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer highestNum = numberList.get(numberList.size() - 1) + 1;
                numberList.add(numberList.size(), highestNum);
                gridViewAdapter.notifyDataSetChanged();


                // Confirm the addition
                Toast.makeText(getApplicationContext(),
                        "Item added : " + highestNum, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
