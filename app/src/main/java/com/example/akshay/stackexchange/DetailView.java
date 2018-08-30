package com.example.akshay.stackexchange;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class DetailView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        String body = getIntent().getExtras().getString("body");
        TextView detailView = (TextView)findViewById(R.id.detail_view);
        detailView.setText(Html.fromHtml(body));
    }
}
