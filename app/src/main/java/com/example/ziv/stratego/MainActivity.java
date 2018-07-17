package com.example.ziv.stratego;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends SuperActivity {

    TextView changeView;
    User newUser, user;
    String newUserID;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String currentUserID, myName;
    Intent startAct;
    ParseQuery<User> query;
    ParseQuery<User> q;
    View view;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        relativeLayout = (RelativeLayout) findViewById(R.id.main);
        relativeLayout.setBackgroundResource(R.drawable.brown1);
        query = new ParseQuery<User>(getString(R.string.strategoUser));
        q = new ParseQuery<User>(getString(R.string.strategoUser));
        startAct = new Intent(MainActivity.this, StartActivity.class);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        currentUserID = sharedPref.getString(getString(R.string.currentID), "");
        editor = sharedPref.edit();
        editor.apply();
        changeView = (TextView) findViewById(R.id.enterName);
        query.whereEqualTo(getString(R.string.objectId), currentUserID);
        try {
            query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            if (currentUserID == null || query.count() == 0)  // new user
            {
                Log.d("main", "new user");
                final EditText myText = (EditText) findViewById(R.id.myUserName);
                Button okButton = (Button) findViewById(R.id.okB);
                okButton.setBackgroundResource(R.drawable.ok1);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myName = myText.getText().toString();
                        Log.d("main", "myName: " + myName);
                        q.whereEqualTo(getString(R.string.name), myName);
                        q.findInBackground(new FindCallback<User>() {
                            @Override
                            public void done(List<User> objects, ParseException e) {
                                if (objects.size() == 0) {
                                    enterNewUser(myName);
                                } else {
                                    Dialog alert11 = onCreateNotUniqueName();
                                    alert11.show();
                                }
                            }
                        });
                    }
                });


            } else {//old user
                startActivity(startAct);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /*query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {

            }

        });*/
    }

    public void enterNewUser(String myName) {
        newUser = new User(myName);
        newUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                currentUserID = newUser.getObjectId();
                newUser.setRequested(false);
                editor.putString(getString(R.string.currentID), currentUserID);
                editor.apply();
                startActivity(startAct);
            }
        });
    }


    public Dialog onCreateNotUniqueName() {

        view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.not_unique_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
