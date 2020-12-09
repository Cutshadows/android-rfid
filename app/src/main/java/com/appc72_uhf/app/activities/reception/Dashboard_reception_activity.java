package com.appc72_uhf.app.activities.reception;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.appc72_uhf.app.R;

public class Dashboard_reception_activity extends AppCompatActivity {
    private ImageButton ibtn_reception_document, ibtn_automatic_reception;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_reception_activity);
    }
}
