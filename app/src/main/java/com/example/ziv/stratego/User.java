package com.example.ziv.stratego;

import android.content.Context;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ZIV on 11/01/2016.
 */
@ParseClassName("StrategoUsers")
public class User extends ParseObject {

    public User()
    {
        super();
    }

    public User(String userName) {

        setUserName(userName);
        setIsOnlineAndWait(false);
        setIsOnlineAndSelect(false);
        setIsPlaying(false);
        setIsReady(false);
    }

    public void setUserName(String userName)
    {
        put("Name", userName);
        try {
            save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public void setPieceId(int PieceId)
    {
        put("PieceId", PieceId);
        saveInBackground();
    }

    public void setSourceCellNum(int sourceCellNum)
    {
        put("sourceCellNum", sourceCellNum);
        saveInBackground();
    }

    public void setDestCellNum(int destCellNum)
    {
        put("destCellNum", destCellNum);
        saveInBackground();
    }

    public void setLosers(int losersNum)
    {
        put("losersNum", losersNum);
        saveInBackground();
    }

    public void setFightResult(String fightResult)
    {
        put("fightResult", fightResult);
        saveInBackground();
    }

    public void setIsFight(boolean isFight)
    {
        put("isFight", isFight);
        try {
            save();
            saveEventually();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setPiecesLocation(Integer [] piecesLocation)
    {
        List<Integer> piecesLocationList = Arrays.asList(piecesLocation);
        put("piecesLocationList", piecesLocationList);
        try {
            save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setPiecesLocation(List<Integer> piecesLocation)
    {
        put("piecesLocationList", piecesLocation);
        try {
            save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setIsOnlineAndWait(boolean isOnlineWait)
    {
        put("isOnlineWait", isOnlineWait);
        try {
            save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setIsOnlineAndSelect(boolean isOnlineSelect)
    {
        put("isOnlineSelect", isOnlineSelect);
        try {
            save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setIsPlaying(boolean isPlaying)
    {
        put("isPlaying", isPlaying);
        try {
            save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setOtherPlayerId(String OtherPlayerID)
    {
        put("OtherPlayerID", OtherPlayerID);
        try {
            save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setIsReady(boolean isReady)
    {
        put("isReady", isReady);
        try {
            save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setMyTurn(boolean myTurn)
    {
        put("myTurn", myTurn);
        try {
            save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setTimeReady(long time)
    {
        put("time", time);
        try {
            save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setRequested(boolean isRequested)
    {
        put("isRequested", isRequested);
        try {
            save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setMessege(String messege)
    {
        put("messege", messege);
        saveInBackground();
    }

}
