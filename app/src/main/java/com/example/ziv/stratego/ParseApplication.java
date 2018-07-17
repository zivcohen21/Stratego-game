package com.example.ziv.stratego;

import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by ZIV on 11/02/2016.
 */
public class ParseApplication extends android.app.Application{

    SharedPreferences sharedPref;
    String currentUserID;
    ParseQuery<User> query;
    User me;

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("myAppId")
                .server("https://stratego-game-android.herokuapp.com/parse/")   // '/' important after 'parse'
                .build());

        ParseObject.registerSubclass(User.class);


        //ParseInstallation.getCurrentInstallation().saveInBackground();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        currentUserID = sharedPref.getString(getString(R.string.currentID), "");
        query = new ParseQuery<User>(getString(R.string.strategoUser));



   /*   ParseObject.registerSubclass(User.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        currentUserID = sharedPref.getString(getString(R.string.currentID), "");
        query = new ParseQuery<User>(getString(R.string.strategoUser));*/
    }
}
