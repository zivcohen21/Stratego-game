package com.example.ziv.stratego;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class SettingsActivity extends SuperActivity {

    RadioGroup radioColorGroup;
    RadioButton radioColorButton;
    char myColor = 'b';
    String myId, otherId;
    User me;
    ParseQuery<User> query;
    RelativeLayout relativeLayout;
    TextView tv;
    View backView;
    boolean isKilled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        relativeLayout = (RelativeLayout) findViewById(R.id.settings);
        relativeLayout.setBackgroundResource(R.drawable.brown1);
        radioColorGroup=(RadioGroup)findViewById(R.id.colorRadioGroup);
        tv = (TextView)findViewById(R.id.colorTitle);
        tv.setBackgroundResource(R.drawable.choosecolor);
        myId = getIntent().getStringExtra(getString(R.string.myId));
        otherId = getIntent().getStringExtra(getString(R.string.otherId));
        query = new ParseQuery<User>(getString(R.string.strategoUser));
        query.whereEqualTo("objectId", myId);
        query.findInBackground(new FindCallback<User>() {

            @Override
            public void done(List<User> objects, ParseException e) {
                me = objects.get(0);
                setMe();
            }
        });
        Button coButton = (Button) findViewById(R.id.startGame);
        coButton.setBackgroundResource(R.drawable.cont1);
        coButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isKilled = false;
                Intent preAct = new Intent(SettingsActivity.this, PreGameActivity.class);
                preAct.putExtra(getString(R.string.myId), myId);
                preAct.putExtra(getString(R.string.otherId), otherId);
                preAct.putExtra(getString(R.string.myColor), myColor);
                startActivity(preAct);
            }
        });

        radioColorGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioColorButton = (RadioButton) findViewById(checkedId);
                switch (checkedId) {
                    case R.id.radioRed:
                        myColor = 'r';
                        break;
                    case R.id.radioBlue:
                        myColor = 'b';
                        break;
                }
            }
        });

    }
    public Dialog onCreateBackDialog() {

        backView = (LayoutInflater.from(SettingsActivity.this)).inflate(R.layout.back_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(backView);
        builder.setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                me.setIsReady(false);
                isKilled = false;
                Intent startAct = new Intent(SettingsActivity.this, StartActivity.class);
                startActivity(startAct);
            }
        });
        builder.setCancelable(true).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isKilled)
            super.exit();
    }

    @Override
    public void onBackPressed() {
        me.setIsReady(false);
        isKilled = false;
        Dialog out = onCreateBackDialog();
        out.show();
    }

    public void setMe() {
        isKilled = true;
        me.setOtherPlayerId(otherId);
        me.setIsPlaying(true);
    }
}
