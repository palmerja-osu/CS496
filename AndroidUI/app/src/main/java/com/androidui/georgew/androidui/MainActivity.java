package com.androidui.georgew.androidui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLinearHoriz = (Button) findViewById(R.id.btnLinearHoriz);
        Button btnLinearVert = (Button) findViewById(R.id.btnLinearVert);
        Button btnGrid = (Button) findViewById(R.id.btnGrid);
        Button btnRelative = (Button) findViewById(R.id.btnRelative);

        btnLinearHoriz.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0){
                Intent linearHorizontal = new Intent(getApplicationContext(), linear_layout_horizontal.class);
                startActivity(linearHorizontal);
            }
        });

        btnLinearVert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0){
                Intent linearVertical = new Intent(getApplicationContext(), linear_layout_vertical.class);
                startActivity(linearVertical);
            }
        });

        btnGrid.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0){
                Intent grid = new Intent(getApplicationContext(), grid_layout.class);
                startActivity(grid);
            }
        });

        btnRelative.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0){
                Intent relative = new Intent(getApplicationContext(), relative_layout.class);
                startActivity(relative);
            }
        });
    };
}
