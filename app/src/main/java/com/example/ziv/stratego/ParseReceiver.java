package com.example.ziv.stratego;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ZIV on 09/02/2016.
 */
public class ParseReceiver extends BroadcastReceiver {

    ParseQuery<User> query;
    User obMe;
    @Override
    public void onReceive(Context context, Intent intent) {

        JSONObject jsonObject;
        String senderId = "";
        String receiverId = "";
        query = new ParseQuery<User>(context.getString(R.string.strategoUser));

        try {
            jsonObject = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            senderId = jsonObject.getString(context.getString(R.string.senderId));
            receiverId = jsonObject.getString(context.getString(R.string.receiverId));

            Log.d("LOG", senderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            obMe = query.get(receiverId);
            obMe.setOtherPlayerId(senderId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        obMe.setRequested(true);
    }
}
