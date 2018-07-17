package com.example.ziv.stratego;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UsersListActivity extends SuperActivity
{
    ListView listview;
    List<ParseObject> ob;
    ImageView statusImage;
    TextView txtTitle;
    ListView list;
    CustomAdapter adapter;
    UsersListActivity CustomListView = null;
    ArrayList<ListModel> CustomListViewValuesArr = new ArrayList<ListModel>();
    User me;
    User other;
    SharedPreferences sharedPref;
    String currentUserID;
    String otherId;
    ParseQuery<User> q;
    boolean isnewAct, isKilled;
    SwipeRefreshLayout swipeRefreshLayout;
    ParseQuery<ParseObject> query;
    JSONObject jo;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        //tv = (TextView)findViewById(R.id.title);
        //tv.setBackgroundResource(R.drawable.selectcompetitor);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setBackgroundResource(R.drawable.brown1);
        jo = new JSONObject();
        isnewAct = getIntent().getBooleanExtra(getString(R.string.isNewActKey), true);
        q = new ParseQuery<User>(getString(R.string.strategoUser));
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        currentUserID = sharedPref.getString(getString(R.string.currentID), "");
        q.whereEqualTo("objectId", currentUserID);
        q.findInBackground(new FindCallback<User>() {

            @Override
            public void done(List<User> objects, ParseException e) {
                setMe(objects.get(0));
            }
        });
        makeList();
        CustomListView = this;
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        myUpdateOperation();
                    }
                }
        );
    }

    public void makeList()
    {
        CustomListViewValuesArr.clear();
        query = new ParseQuery<ParseObject>(getString(R.string.strategoUser));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                ob = objects;
                setListData();
                Resources res = getResources();
                list = (ListView) findViewById(R.id.list);
                adapter = new CustomAdapter(CustomListView, CustomListViewValuesArr, res);
                list.setAdapter(adapter);
            }
        });


    }

    public void myUpdateOperation()
    {
        makeList();
        swipeRefreshLayout.setRefreshing(false);
    }

    public void setListData()
    {
        for (ParseObject strategoUser : ob)
        {
            Log.d("list", "currentUserID " + currentUserID);
            Log.d("list", "strategoUser.getObjectId() " + strategoUser.getObjectId());
            if(!strategoUser.getObjectId().equals(currentUserID))
            {
                final ListModel sched = new ListModel();
                sched.setName((String) strategoUser.get(getString(R.string.name)));
                sched.setId(strategoUser.getObjectId());
                if(!strategoUser.getBoolean(getString(R.string.isOnlineSelect)) &&
                        !strategoUser.getBoolean(getString(R.string.isOnlineWait)))
                    sched.setImage(R.drawable.gray_status);
                else if ((strategoUser.getBoolean(getString(R.string.isOnlineSelect)) ||
                        strategoUser.getBoolean(getString(R.string.isOnlineWait))) &&
                        !strategoUser.getBoolean(getString(R.string.isPlaying)))
                    sched.setImage(R.drawable.green_status);
                else
                    sched.setImage(R.drawable.red_status);
                CustomListViewValuesArr.add(sched);
            }
        }
    }

    public void onItemClick(int mPosition)
    {
        ListModel tempValues = (ListModel) CustomListViewValuesArr.get(mPosition);
        otherId = tempValues.getId();
        try {
            other = q.get(otherId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(!other.getBoolean(getString(R.string.isPlaying)))
        {
            jo = new JSONObject();
            try {
                jo.put(getString(R.string.senderId),currentUserID);
                jo.put(getString(R.string.receiverId),otherId);
                jo.put(getString(R.string.notificationKey),
                        getString(R.string.notificationValue, me.getString(getString(R.string.name))));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.saveInBackground();
            ParsePush push = new ParsePush();
            push.setData(jo);
            ParseQuery<ParseInstallation> installationQuery = ParseQuery.getQuery(ParseInstallation.class);// <-- Installation query
            ParseQuery<User>  userQuery =  new ParseQuery<User>(getString(R.string.strategoUser));
            userQuery.whereEqualTo(getString(R.string.name), other.get(getString(R.string.name)));
            installationQuery.whereMatchesQuery(getString(R.string.strategoUser), userQuery);
            push.setQuery(installationQuery);
            push.sendInBackground();
            isKilled = false;
            Intent waitAct = new Intent(UsersListActivity.this, WaitingActivity.class);
            waitAct.putExtra(getString(R.string.myId), currentUserID);
            waitAct.putExtra(getString(R.string.otherId), otherId);
            waitAct.putExtra(getString(R.string.waitingTextKey),
                    getString(R.string.waitingTextValueInvite,other.getString(getString(R.string.name))));
            waitAct.putExtra(getString(R.string.isPreKey), false);
            startActivity(waitAct);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isKilled)
            super.exit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        me.setIsOnlineAndSelect(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        isKilled = false;
        super.onBackPressed();
    }

    public void setMe(User me) {
        this.me = me;
        isKilled = true;
        me.setIsOnlineAndSelect(true);
        me.setIsPlaying(false);
    }
}


