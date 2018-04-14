package com.bitbay.bitbay;

import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class RateActivity extends Activity{
    double currentRate;
    int numVoted;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(height*.6));

        Button star1 = (Button)findViewById(R.id.star1);
        Button star2 = (Button)findViewById(R.id.star2);
        Button star3 = (Button)findViewById(R.id.star3);
        Button star4 = (Button)findViewById(R.id.star4);
        Button star5 = (Button)findViewById(R.id.star5);

        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {rate(1.0);}});
        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {rate(2.0);}});
        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {rate(3.0);}});
        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {rate(4.0);}});
        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {rate(5.0);}});

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        currentRate = (double) bundle.get("currentRate");
        numVoted = (int) bundle.get("numVoted");
    }

    public  void rate(double rateValue){
        double newRate = 0;
        newRate = (currentRate*(double)numVoted+rateValue)/(double) (numVoted+1);
        Toast.makeText(getApplication(), "We received your vote", Toast.LENGTH_LONG).show();
        Intent resultData = new Intent();
        resultData.putExtra("newRate", newRate);
        resultData.putExtra("numVoters", numVoted+1);
        setResult(Activity.RESULT_OK, resultData);
        finish();

    }
}
