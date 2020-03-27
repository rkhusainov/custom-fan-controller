package com.khusainov.rinat.customfancontroller;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.khusainov.rinat.customfancontroller.view.ChangeableDialView;

public class ChangeableDialActivity extends AppCompatActivity {

    private ChangeableDialView mChangeableDialView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeable_dial);

        mChangeableDialView = findViewById(R.id.changeable_dial_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int n = item.getOrder();
        mChangeableDialView.setSelectionCount(n);
        return super.onOptionsItemSelected(item);
    }
}
