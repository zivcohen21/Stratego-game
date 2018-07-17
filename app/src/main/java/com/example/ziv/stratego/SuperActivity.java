package com.example.ziv.stratego;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class SuperActivity extends AppCompatActivity {

    SharedPreferences sharedPref;
    String currentUserID;
    ParseQuery<User> query;
    int [] hisIm;
    int [] hisSelectIm;
    int [] rIm = {R.drawable.r0, R.drawable.r1, R.drawable.r2, R.drawable.r3, R.drawable.r4,
            R.drawable.r5, R.drawable.r6, R.drawable.r7,R.drawable.r8, R.drawable.r9,
            R.drawable.r10, R.drawable.r11};
    int [] bIm = {R.drawable.b0, R.drawable.b1, R.drawable.b2, R.drawable.b3, R.drawable.b4,
            R.drawable.b5, R.drawable.b6, R.drawable.b7,R.drawable.b8, R.drawable.b9,
            R.drawable.b10, R.drawable.b11};

    int [] selectRIm = {R.drawable.rs0, R.drawable.rs1, R.drawable.rs2, R.drawable.rs3, R.drawable.rs4,
            R.drawable.rs5, R.drawable.rs6, R.drawable.rs7,R.drawable.rs8, R.drawable.rs9,
            R.drawable.rs10, R.drawable.rs11};
    int [] selectBIm = {R.drawable.bs0, R.drawable.bs1, R.drawable.bs2, R.drawable.bs3, R.drawable.bs4,
            R.drawable.bs5, R.drawable.bs6, R.drawable.bs7,R.drawable.bs8, R.drawable.bs9,
            R.drawable.bs10, R.drawable.bs11};

    int [] movedRIm = {0, R.drawable.rm1, R.drawable.rm2, R.drawable.rm3, R.drawable.rm4,
            R.drawable.rm5, R.drawable.rm6, R.drawable.rm7, R.drawable.rm8, R.drawable.rm9,
            R.drawable.rm10, 0};
    int [] movedBIm = {0, R.drawable.bm1, R.drawable.bm2, R.drawable.bm3, R.drawable.bm4,
            R.drawable.bm5, R.drawable.bm6, R.drawable.bm7,R.drawable.bm8, R.drawable.bm9,
            R.drawable.bm10, 0};
    final int rowsNum = 4;
    final int mainRowsNum = 10;
    final int colsNum = 10;
    char myColor, hisColor;
    User me, other;
    String myId, otherId, fightResult;
    int mainCellWH, mainCellW,mainCellH, cellWH, numOfPieces = 40 , otherPid, otherSCellNum,
            newOtherSCellNum, otherDCellNum, newOtherDCellNum, turn, outCellWH;
    ViewGroup.LayoutParams mainImageLayoutParams, imageLayoutParams, outsLayoutParams;
    LinearLayout mainRowsLayout, outsLayout, bottomRowsLayout;
    Cell[][] mainBoard = new Cell[mainRowsNum][colsNum];
    Cell[][] topBoard = new Cell[rowsNum][colsNum];
    Cell[][] bottomBoard = new Cell[rowsNum][colsNum];
    Cell[][] outsBoard = new Cell[rowsNum][colsNum];
    Piece[] redPieces = new Piece[numOfPieces];
    Piece[] bluePieces = new Piece[numOfPieces];
    Piece[] myPieces = new Piece[numOfPieces];
    Piece[] otherPieces = new Piece[numOfPieces];
    //int [] myPiecesLocation = new int[mainRowsNum*colsNum];
    //int [] hisPiecesLocation = new int[mainRowsNum*colsNum];
    List<Integer> myPiecesLocationList;
    List<Integer> hisPiecesLocationList;
    int myLosersCell;
    int hisLosersCell;
    Integer[] losers = new Integer[2];
    Cell clickedButton, lastCell, movedCell;
    int [] mySelectIm;
    int [] myIm, myMoved;
    int backColor, fightOptionBackColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super);
        //Intent intent = new Intent(this, OnClearFromRecentService.class);
        //startService(intent);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        currentUserID = sharedPref.getString(getString(R.string.currentID), "");
        doNewQuery(currentUserID);
    }

    public void doNewQuery(String myId)
    {
        query = new ParseQuery<User>(getString(R.string.strategoUser));
        try {
            me = query.get(myId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        me.setIsFight(false);
        me.setIsOnlineAndSelect(false);
        me.setIsOnlineAndWait(false);
        me.setIsPlaying(false);
        me.setIsReady(false);
        me.setOtherPlayerId("");
    }

    public void toasts(CharSequence theText,int length)
    {
        Toast toast = Toast.makeText(getApplicationContext(), theText, length);
        toast.show();
    }


}
