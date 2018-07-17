package com.example.ziv.stratego;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

public class StartActivity extends SuperActivity {

    SharedPreferences sharedPref;
    String currentUserID;
    ParseQuery<User> query;
    User me;
    RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        relativeLayout = (RelativeLayout) findViewById(R.id.start);
        relativeLayout.setBackgroundResource(R.drawable.brown1);
        query = new ParseQuery<User>(getString(R.string.strategoUser));
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        currentUserID = sharedPref.getString(getString(R.string.currentID), "");
        query.whereEqualTo("objectId", currentUserID);
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                setMe(objects.get(0));
            }
        });


        Button pNButton = (Button) findViewById(R.id.playNow);
        pNButton.setBackgroundResource(R.drawable.randomopp);
        pNButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent waitAct = new Intent(StartActivity.this, WaitingActivity.class);
                waitAct.putExtra(getString(R.string.waitingTextKey),getString(R.string.waitingTextValueStart));
                waitAct.putExtra(getString(R.string.isPreKey), false);
                waitAct.putExtra(getString(R.string.myId),currentUserID);
                waitAct.putExtra("isRequested", false);
                startActivity(waitAct);
            }
        });

        Button askFButton = (Button) findViewById(R.id.askUser);
        askFButton.setBackgroundResource(R.drawable.inviteopp);
        askFButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listAct = new Intent(StartActivity.this, UsersListActivity.class);
                listAct.putExtra(getString(R.string.isNewActKey), true);
                startActivity(listAct);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void setMe(User me) {
        this.me = me;
        me.setIsOnlineAndWait(false);
        me.setIsOnlineAndSelect(false);
        me.setIsPlaying(false);
        me.setIsReady(false);
        me.setIsFight(false);
        me.setOtherPlayerId("");

        if(me.getBoolean(getString(R.string.isRequested)))
        {
            me.setRequested(false);
            String otherId = me.getString(getString(R.string.otherPlayerID));
            Intent waitAct = new Intent(StartActivity.this, WaitingActivity.class);
            waitAct.putExtra(getString(R.string.myId), currentUserID);
            waitAct.putExtra(getString(R.string.otherId), otherId);
            waitAct.putExtra(getString(R.string.isPreKey), false);
            waitAct.putExtra("isRequested", true);
            startActivity(waitAct);
        }

        if(ParseInstallation.getCurrentInstallation().get(getString(R.string.strategoUser)) == null) {
            ParseInstallation.getCurrentInstallation().put(getString(R.string.strategoUser), me);
            ParseInstallation.getCurrentInstallation().saveInBackground();
        }
    }
}
