package com.example.wehere;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WaitforRequest extends AppCompatActivity implements View.OnClickListener
{
    private Button btnReqChange;
    private TextView textViewExplain;
    private TextView textViewWaitforReq;
    private Button btnEditWait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitfor_request);
        btnReqChange = findViewById(R.id.btn_reqchange);
        btnEditWait = findViewById(R.id.btn_editwait);
        textViewWaitforReq = findViewById(R.id.textView_waitforreq);
        textViewExplain = findViewById(R.id.textView_explain);
        btnEditWait.setOnClickListener(this);
        btnReqChange.setOnClickListener(this);
    }

    @Override
    public void onClick(View V)
    {
        if (btnEditWait == V)
        {
            Intent intent_editProfile = new Intent(this, EditProfile.class);
            startActivity(intent_editProfile);
        }

        if (btnReqChange == V)
        {
            Intent intent_chooseIcons = new Intent(this, ChooseiconsActivity.class);
            startActivity(intent_chooseIcons);
        }
    }
}