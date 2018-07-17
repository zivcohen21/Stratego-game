package com.example.ziv.stratego;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WaitingActivity extends SuperActivity {

    final int mainRowsNum = 10;
    final int colsNum = 10;
    ParseQuery<User> query,q;
    User me, other;
    TextView title;
    char myColor;
    String myId, otherId, text;
    boolean isPre, isKilled, isReq;
    int [] piecesLocation = new int[mainRowsNum*colsNum];
    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();
    RelativeLayout relativeLayout;
    List<User> ob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        relativeLayout = (RelativeLayout) findViewById(R.id.wait);
        relativeLayout.setBackgroundResource(R.drawable.brown1);
        myId = getIntent().getStringExtra(getString(R.string.myId));
        otherId = getIntent().getStringExtra(getString(R.string.otherId));
        myColor = getIntent().getCharExtra(getString(R.string.myColor), 'r');
        text = getIntent().getStringExtra(getString(R.string.waitingTextKey));
        isPre = getIntent().getBooleanExtra(getString(R.string.isPreKey), false);
        isReq = getIntent().getBooleanExtra("isRequested", false);
        piecesLocation = getIntent().getIntArrayExtra(getString(R.string.piecesLocation));
        title = (TextView) findViewById(R.id.waiting);
        title.setText(text);
        query = new ParseQuery<User>(getString(R.string.strategoUser));
        q = new ParseQuery<User>(getString(R.string.strategoUser));
        query.whereEqualTo("objectId", myId);
        query.findInBackground(new FindCallback<User>() {

            @Override
            public void done(List<User> objects, ParseException e) {
                setMe(objects.get(0));
            }
        });
        /*if(isReq) {
            q.whereEqualTo("objectId", otherId);
            q.findInBackground(new FindCallback<User>() {

                @Override
                public void done(List<User> objects, ParseException e) {
                    other = objects.get(0);
                }
            });
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isKilled) {
            super.exit();
        }
    }

    @Override
    public void onBackPressed() {
        isKilled = false;
        super.onBackPressed();
        Intent startAct = new Intent(WaitingActivity.this, StartActivity.class);
        startActivity(startAct);
    }

    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 500, 600); //
    }

    public void stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                handler.post(new Runnable() {
                    public void run() {
                        if(isPre)
                        {
                            try {
                                other = query.get(otherId);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (other.getBoolean(getString(R.string.isReady)))
                            {
                                isKilled = false;
                                Intent boardAct = new Intent(WaitingActivity.this, BoardActivity.class);
                                boardAct.putExtra(getString(R.string.myId), myId);
                                boardAct.putExtra(getString(R.string.otherId), otherId);
                                boardAct.putExtra(getString(R.string.myColor), myColor);
                                boardAct.putExtra(getString(R.string.piecesLocation), piecesLocation);
                                startActivity(boardAct);
                                stopTimerTask();
                            }
                        }
                        else
                        {
                            query.whereEqualTo(getString(R.string.isOnlineWait), true).whereNotEqualTo(getString(R.string.objectId), myId);
                            if(query != null) {
                                try {
                                    for (ParseObject strategoUser : query.find()) {
                                        if (strategoUser.getString(getString(R.string.otherPlayerID)).equals(me.getObjectId()) ||
                                                !strategoUser.getBoolean(getString(R.string.isPlaying))) {
                                            otherId = strategoUser.getObjectId();
                                            isKilled = false;
                                            Intent setAct = new Intent(WaitingActivity.this, SettingsActivity.class);
                                            setAct.putExtra(getString(R.string.myId), myId);
                                            setAct.putExtra(getString(R.string.otherId), otherId);
                                            startActivity(setAct);
                                            stopTimerTask();
                                            break;
                                        }
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }
        };
    }

    public void setMe(User me) {
        this.me = me;
        if(!isPre) {
            me.setIsOnlineAndWait(true);
        }
        else {
            me.setIsReady(true);
            me.setTimeReady(System.currentTimeMillis());
        }
        isKilled = true;
        startTimer();
    }
}

