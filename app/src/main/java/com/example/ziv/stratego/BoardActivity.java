package com.example.ziv.stratego;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BoardActivity extends SuperActivity implements View.OnClickListener{

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
    Cell clickedButton, lastCell, movedCell, otherMovedCell;
    int [] mySelectIm;
    int [] myIm, myMoved;
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
    int backColor, fightOptionBackColor, theOption, theFightOption, backLast;
    boolean isSelect, myTurn, stopGame, isKilled;
    Piece selectedPiece;
    ArrayList<Cell> options = new ArrayList<Cell>();
    ArrayList<Cell> fightOptions = new ArrayList<Cell>();
    ParseQuery<User> q;
    GLSurfaceView mGLView;
    TimerTask timerTask;
    final Handler handler = new Handler();
    Timer timer;
    Piece winner;
    Piece loser;
    View view, outsView, winView, backView;
    Button threeDots, ok;
    Dialog alert;
    TextView tv;
    RelativeLayout relativeLayout;
    TextView turnColor, messege;
    int color;
    EditText myMessege;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        threeDots = (Button) findViewById(R.id.threeDots);
        threeDots.setBackgroundResource(R.drawable.outpieces1);
        ok = (Button) findViewById(R.id.send);
        relativeLayout = (RelativeLayout) findViewById(R.id.board);
        relativeLayout.setBackgroundResource(R.drawable.brown1);
        turnColor = (TextView) findViewById(R.id.turn);
        messege = (TextView) findViewById(R.id.messege);
        myMessege = (EditText) findViewById(R.id.enterMessege);
        myId = getIntent().getStringExtra(getString(R.string.myId));
        otherId = getIntent().getStringExtra(getString(R.string.otherId));
        //myPiecesLocation = getIntent().getIntArrayExtra("piecesLocation");
        myColor = getIntent().getCharExtra(getString(R.string.myColor), 'r');
        turn = 0;
        stopGame = false;
        q = new ParseQuery<User>(getString(R.string.strategoUser));
        myColor(myColor);
        doFirstQuery();

        threeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alert = onCreateOutsDialog();
                alert.show();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messege = myMessege.getText().toString();
                if(!messege.isEmpty()) {
                    other.setMessege(messege);
                    myMessege.setText("");
                    CharSequence text = getString(R.string.messegeToast);
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                    toast.show();
                }

            }
        });
    }

    public void doFirstQuery()
    {
        query = new ParseQuery<User>(getString(R.string.strategoUser));
        q = new ParseQuery<User>(getString(R.string.strategoUser));

        query.whereEqualTo("objectId", myId);
        query.findInBackground(new FindCallback<User>() {

            @Override
            public void done(List<User> objects, ParseException e) {
                me = objects.get(0);
                q.whereEqualTo("objectId", otherId);
                q.findInBackground(new FindCallback<User>() {

                    @Override
                    public void done(List<User> objects, ParseException e) {
                        other = objects.get(0);
                        buildBoard();
                        setMe(me);
                    }
                });
            }
        });
    }

    public void doNewQuery()
    {
        query = new ParseQuery<User>(getString(R.string.strategoUser));
        try {
            me = query.get(myId);
            other = query.get(otherId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void createPieces()
    {
        final int TYPE_PIECES = 11;
        int id = 0;
        int []times = {1,1,8,5,4,4,4,3,2,1,1,6};

        for(int i = 0; i <= TYPE_PIECES; i++)
            for(int j = 1; j <= times[i]; j++)
                redPieces[id] = new Piece(id++,i,'r',rIm[i]);
        id = 0;
        for(int i = 0; i <= TYPE_PIECES; i++)
            for(int j = 1; j <= times[i]; j++)
                bluePieces[id] = new Piece(id++,i,'b',bIm[i]);
    }

    public void myColor(char myColor)
    {
        if(myColor == 'r')
        {
            myMoved = movedRIm;
            mySelectIm = selectRIm;
            myIm = rIm;
            myPieces = redPieces;
            hisIm = bIm;
            hisSelectIm = selectBIm;
            otherPieces = bluePieces;
            hisColor = 'b';
            backColor = R.drawable.bbackn;
            backLast = R.drawable.bbacklast;
            fightOptionBackColor = R.drawable.bbs;
        }
        else
        {
            myMoved = movedBIm;
            mySelectIm = selectBIm;
            myIm = bIm;
            hisIm = rIm;
            hisSelectIm = selectRIm;
            myPieces = bluePieces;
            otherPieces = redPieces;
            hisColor = 'r';
            backColor = R.drawable.rbackn;
            backLast = R.drawable.rbacklast;
            fightOptionBackColor = R.drawable.rbs;
        }
    }

    public void buildTheMainBoard() {
        locateOtherPieces();
        int n = -1;
        for (int row = 0; row < mainRowsNum; row++) {
            // The main layout (vertical)
            LinearLayout rowLayout = new LinearLayout(this);
            for (int column = 0; column < colsNum; column++) {
                Cell c = new Cell(this, true, row, column, ++n);

                c.setWidth(mainCellW);
                c.setHeight(mainCellH);
                if ((row == 4 || row == 5) && (column == 2 || column == 3 || column == 6 || column == 7)) {
                    c.setBackgroundResource(R.drawable.w2);
                    c.setReal(false);
                    c.setClickable(false);
                } else if (myPiecesLocationList.get(c.getNumOfCell()) != -1 ) {
                    c.setP(myPieces[myPiecesLocationList.get(c.getNumOfCell())]);
                    myPieces[myPiecesLocationList.get(c.getNumOfCell())].setInTheGame(true);
                    myPieces[myPiecesLocationList.get(c.getNumOfCell())].setGameCell(c);
                    c.setBackgroundResource(myIm[c.getP().getNumber()]);
                    c.setReal(true);
                }
                else if (hisPiecesLocationList.get(c.getNumOfCell()) != -1 ) {
                    c.setP(otherPieces[hisPiecesLocationList.get(c.getNumOfCell())]);
                    otherPieces[hisPiecesLocationList.get(c.getNumOfCell())].setInTheGame(true);
                    otherPieces[hisPiecesLocationList.get(c.getNumOfCell())].setGameCell(c);
                    c.setBackgroundResource(backColor);
                    c.setReal(true);
                }
                else
                {
                    c.setBackgroundResource(R.drawable.gg2);
                    c.setReal(true);
                }
                c.setOnClickListener(this);
                rowLayout.addView(c, mainImageLayoutParams);
                mainBoard[row][column] = c;
            }
            mainRowsLayout.addView(rowLayout);
        }
    }

    public void locateOtherPieces()
    {

        myPiecesLocationList = me.getList(getString(R.string.piecesLocationList));
        hisPiecesLocationList = other.getList(getString(R.string.piecesLocationList));
        for(int i = 60; i < hisPiecesLocationList.size(); i++)
        {
            if(hisPiecesLocationList.get(i) != -1)
            {
                hisPiecesLocationList.set(getNewLocation(i), hisPiecesLocationList.get(i));
                hisPiecesLocationList.set(i, -1);
            }
        }
    }


    public void firstTurn()
    {
        long otherTime = (long)other.get(getString(R.string.time));
        long myTime = (long)me.get(getString(R.string.time));
        myTurn = otherTime > myTime;
        me.setMyTurn(myTurn);
        if(myTurn)
            turnColor.setText(getString(R.string.turnTitle, me.getString("Name")));
        else
            turnColor.setText(getString(R.string.turnTitle, other.getString("Name")));
    }


    public Dialog onCreateFightDialog() {

        view = (LayoutInflater.from(BoardActivity.this)).inflate(R.layout.fight_dialog, null);
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

    public Dialog onCreateOutsDialog() {

        outsView = (LayoutInflater.from(BoardActivity.this)).inflate(R.layout.out_pieces, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        outsLayout = (LinearLayout) outsView.findViewById(R.id.outs);
        builder.setView(outsView);
        buildOuts();
        builder.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    public Dialog onCreateWinDialog(User winner) {

        String winnerName = winner.getString(getString(R.string.name));
        winView = (LayoutInflater.from(BoardActivity.this)).inflate(R.layout.winner_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        tv = (TextView) winView.findViewById(R.id.winText);
        tv.setText(getString(R.string.winnerTitle, winnerName));
        builder.setView(winView);
        builder.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                isKilled = false;
                Intent startAct = new Intent(BoardActivity.this, StartActivity.class);
                startActivity(startAct);
            }
        });
        return builder.create();
    }

    public Dialog onCreateBackDialog() {
        backView = (LayoutInflater.from(BoardActivity.this)).inflate(R.layout.back_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(backView);
        builder.setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                me.setIsReady(false);
                isKilled = false;
                Intent startAct = new Intent(BoardActivity.this, StartActivity.class);
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

    public void onClick(View v) {

        if(myTurn) {
            if (v instanceof Cell) {
                clickedButton = (Cell) v;
                if (lastCell != null && lastCell.getP() != null && clickedButton.getP() != null && lastCell.getP().getColor() == myColor)
                    lastCell.setBackgroundResource(myIm[lastCell.getP().getNumber()]);
                if (!isSelect)//select piece
                    choosePiece(clickedButton);
                else
                    chooseDest(clickedButton);
            }
        }
    }

    public void choosePiece(Cell theCell)
    {
        if(theCell.getP() != null) {
            selectedPiece = theCell.getP();
            selectedPiece.setGameCell(theCell);
            if (selectedPiece.getColor() == myColor) {
                searchOptions(selectedPiece, theCell);
                showOptions();
                isSelect = true;
                theCell.setBackgroundResource(mySelectIm[selectedPiece.getNumber()]);
                lastCell = theCell;
            }
        }
    }

    public void chooseDest(Cell theCell) {
        if(options.contains(theCell))
        {
            theOption = options.indexOf(theCell);
            theFightOption = -1;
            if(movedCell != null && movedCell.getP() != null && movedCell.getP().getColor() == myColor)
                movedCell.setBackgroundResource(myIm[movedCell.getP().getNumber()]);
            movedCell = theCell;

            theCell.setP(selectedPiece);
            selectedPiece.setGameCell(theCell);
            theCell.setBackgroundResource(myMoved[selectedPiece.getNumber()]);
            lastCell.setBackgroundResource(R.drawable.gg2);
            lastCell.setP(null);

            mainBoard[theCell.getRow()][theCell.getCol()].setP(selectedPiece);
            mainBoard[lastCell.getRow()][lastCell.getCol()].setP(null);
            myPiecesLocationList.set(lastCell.getNumOfCell(), -1);
            myPiecesLocationList.set(theCell.getNumOfCell(), theCell.getP().getNumber());

            doNewQuery();
            me.setPiecesLocation(myPiecesLocationList);
            other.setPiecesLocation(hisPiecesLocationList);
            me.setPieceId(selectedPiece.getId());
            me.setSourceCellNum(lastCell.getNumOfCell());
            me.setDestCellNum(theCell.getNumOfCell());
            removeOptions();
            try {
                endMyTurn();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else if(fightOptions.contains(theCell))
        {
            if(movedCell != null && movedCell.getP() != null && movedCell.getP().getColor() == myColor)
                movedCell.setBackgroundResource(myIm[movedCell.getP().getNumber()]);
            movedCell = theCell;
            theFightOption = fightOptions.indexOf(theCell);
            theOption = -1;
            attack(selectedPiece,theCell.getP());
            removeOptions();
            try {
                endMyTurn();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else if(theCell.getP() != null && theCell.getP().getColor() == myColor) {
            if(theCell.getNumOfCell() != lastCell.getNumOfCell())
                removeOptions();
            choosePiece(theCell);
        }
    }

    public void fightDialog(Piece a, Piece d)
    {
        Dialog alert11 = onCreateFightDialog();
        ImageView image1 = (ImageView) view.findViewById(R.id.image1);
        ImageView image2 = (ImageView) view.findViewById(R.id.image2);

        if(a.getColor() == 'b')
        {
            image1.setBackgroundResource(rIm[d.getNumber()]);
            image2.setBackgroundResource(bIm[a.getNumber()]);
            alert11.show();
        }
        else
        {
            image1.setBackgroundResource(bIm[d.getNumber()]);
            image2.setBackgroundResource(rIm[a.getNumber()]);
            alert11.show();
        }
    }

    public void endMyTurn() throws ParseException {
        isSelect = false;
        myTurn = false;
        me.save();
        other.save();
        me.setMyTurn(false);
        other.setMyTurn(true);
        me.save();
        other.save();
        turnColor.setText(getString(R.string.turnTitle, other.getString(getString(R.string.name))));
        startTimer();
    }

    public void endOtherTurn() throws ParseException {
        me.setPieceId(-1);
        me.setDestCellNum(-1);
        me.setSourceCellNum(-1);
        me.setLosers(-1);
        me.save();
        myTurn = true;
        me.save();
        turnColor.setText(getString(R.string.turnTitle, me.getString(getString(R.string.name))));

    }

    public void attack(Piece a,Piece d)
    {
        me.setIsFight(true);
        fight(a, d);
        doNewQuery();
        //me.setPiecesLocation(myPiecesLocationList);
        //other.setPiecesLocation(hisPiecesLocationList);
        fightDialog(a, d);
        if(winner == null && loser == null)
        {
            fightResult = "e";
            myPieces[a.getGameCell().getP().getId()].setInTheGame(false);
            a.getGameCell().setBackgroundResource(R.drawable.gg2);
            a.getGameCell().setP(null);
            mainBoard[a.getGameCell().getRow()][a.getGameCell().getCol()].setP(null);
            //myPiecesLocationList.set(a.getGameCell().getNumOfCell(), -1);

            me.setFightResult(fightResult);
            me.setLosers(getNewLocation(a.getGameCell().getNumOfCell()));

            other.setLosers(getNewLocation(d.getGameCell().getNumOfCell()));
            //hisPiecesLocationList.set(d.getGameCell().getNumOfCell(), -1);
            d.getGameCell().setBackgroundResource(R.drawable.gg2);
            d.getGameCell().setP(null);
            mainBoard[d.getGameCell().getRow()][d.getGameCell().getCol()].setP(null);
            d.setGameCell(null);
            a.setGameCell(null);
        }
        else if(winner != null && loser != null) {
            Cell wCell = winner.getGameCell();
            Cell lCell = loser.getGameCell();
            if (a.getGameCell().getNumOfCell() == wCell.getNumOfCell()) {
                fightResult = "a";
                mainBoard[wCell.getRow()][wCell.getCol()].getP().setGameCell(null);
                mainBoard[wCell.getRow()][wCell.getCol()].setBackgroundResource(R.drawable.gg2);
                mainBoard[wCell.getRow()][wCell.getCol()].setP(null);
                mainBoard[lCell.getRow()][lCell.getCol()].setP(winner);
                winner.setGameCell(mainBoard[lCell.getRow()][lCell.getCol()]);
                mainBoard[lCell.getRow()][lCell.getCol()].setBackgroundResource(myIm[winner.getNumber()]);


                //myPiecesLocationList.set(wCell.getNumOfCell(), -1);
                //myPiecesLocationList.set(lCell.getNumOfCell(), winner.getNumber());
                me.setFightResult(fightResult);
                other.setLosers(getNewLocation(d.getGameCell().getNumOfCell()));
                loser.setGameCell(null);
                me.setPieceId(a.getId());
                me.setSourceCellNum(wCell.getNumOfCell());
                me.setDestCellNum(lCell.getNumOfCell());

            } else {
                fightResult = "d";
                myPieces[a.getGameCell().getP().getId()].setInTheGame(false);
                mainBoard[lCell.getRow()][lCell.getCol()].setBackgroundResource(R.drawable.gg2);
                mainBoard[lCell.getRow()][lCell.getCol()].getP().setGameCell(null);
                mainBoard[lCell.getRow()][lCell.getCol()].setP(null);
                //myPiecesLocationList.set(lCell.getNumOfCell(), -1);

                me.setFightResult(fightResult);
                me.setLosers(getNewLocation(mainBoard[lCell.getRow()][lCell.getCol()].getNumOfCell()));
                other.setPieceId(d.getId());
            }
            removeOptions();
            if (loser.getNumber() == 0) {
                win(me);
            }
        }

    }

    public void fight(Piece a, Piece d)
    {
        int aNum = a.getNumber();
        int dNum = d.getNumber();
        if (aNum == dNum)
        {
           winner = null;
           loser = null;

        } else if(aNum == 1 && dNum == 10)
        {
            winner = a;
            loser = d;
        }
        else if(aNum == 3 && dNum == 11)
        {
            winner = a;
            loser = d;
        }
        else if(aNum > dNum)
        {
            winner = a;
            loser = d;
        }
        else
        {
            winner = d;
            loser = a;
        }
    }

    public void win(User winner)
    {
        Dialog winAlert = onCreateWinDialog(winner);
        winAlert.show();
    }

    public void buildOuts() {
        int n = -1;
        for (int row = 0; row < rowsNum; row++) {
            // The main layout (vertical)
            LinearLayout rowLayout = new LinearLayout(this);
            for (int column = 0; column < colsNum; column++) {
                Cell c = new Cell(this,false,++n);
                c.setWidth(cellWH);
                c.setHeight(cellWH);
                c.setP(myPieces[n]);
                c.setOnClickListener(this);
                if(myPieces[n].isInTheGame())
                    c.setBackgroundResource(R.drawable.gg2);
                else
                    c.setBackgroundResource(myPieces[n].getImg());
                myPieces[n].setPreGameCell(c);
                rowLayout.addView(c, outsLayoutParams);
                outsBoard[row][column] = c;
            }
            outsLayout.addView(rowLayout);
        }
    }

    public void startMoveOther()
    {
        otherPid = (int)other.get(getString(R.string.pieceId));
        otherSCellNum = (int)other.get(getString(R.string.sourceCellNum));
        newOtherSCellNum = getNewLocation(otherSCellNum);
        otherDCellNum = (int) other.get(getString(R.string.destCellNum));
        newOtherDCellNum = getNewLocation(otherDCellNum);
    }

    public void moveOtherPieces()
    {
        int lastRow;
        int lastCol;
        int newRow;
        int newCol;
        doNewQuery();
        startMoveOther();
        if(otherPid == -1 || otherSCellNum == -1 || otherDCellNum == -1)
            super.toasts(getString(R.string.wrongLocationToast), Toast.LENGTH_LONG);
        else {
            lastRow = newOtherSCellNum / mainRowsNum;
            lastCol = newOtherSCellNum % colsNum;
            newRow = newOtherDCellNum / mainRowsNum;
            newCol = newOtherDCellNum % colsNum;
            if(otherMovedCell != null && otherMovedCell.getP() != null && otherMovedCell.getP().getColor() != myColor)
                otherMovedCell.setBackgroundResource(backColor);
            otherMovedCell = mainBoard[newRow][newCol];
            //hisPiecesLocationList.set(newOtherSCellNum, -1);
            //hisPiecesLocationList.set(newOtherDCellNum, otherPid);
            mainBoard[newRow][newCol].setP(otherPieces[otherPid]);
            mainBoard[lastRow][lastCol].setP(null);
            otherPieces[otherPid].setGameCell(mainBoard[newRow][newCol]);
            //mainBoard[newRow][newCol].setBackgroundResource(hisIm[otherPieces[otherPid].getNumber()]);
            mainBoard[newRow][newCol].setBackgroundResource(backLast);
            mainBoard[lastRow][lastCol].setBackgroundResource(R.drawable.gg2);
            try {
                endOtherTurn();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeOutOtherPieces()
    {
        int lastRow;
        int lastCol;
        int newRow;
        int newCol;
        Piece loser;
        doNewQuery();
        String result = (String)other.get(getString(R.string.fightResult));
        startMoveOther();
        Piece a = null, d = null;

        switch(result)
        {
            case "e":
                myLosersCell = (int)me.get(getString(R.string.losersNum));
                hisLosersCell = (int)other.get(getString(R.string.losersNum));
                if(myLosersCell == -1 || hisLosersCell == -1)
                    super.toasts(getString(R.string.wrongLocationToast), Toast.LENGTH_LONG);
                else
                {
                    newRow = myLosersCell / mainRowsNum;
                    newCol = myLosersCell % colsNum;
                    d = mainBoard[newRow][newCol].getP();
                    mainBoard[newRow][newCol].getP().setGameCell(null);
                    mainBoard[newRow][newCol].setP(null);
                    mainBoard[newRow][newCol].setBackgroundResource(R.drawable.gg2);
                    myPieces[d.getId()].setInTheGame(false);
                    newRow = hisLosersCell / mainRowsNum;
                    newCol = hisLosersCell % colsNum;
                    a = mainBoard[newRow][newCol].getP();
                    mainBoard[newRow][newCol].getP().setGameCell(null);
                    mainBoard[newRow][newCol].setP(null);
                    mainBoard[newRow][newCol].setBackgroundResource(R.drawable.gg2);
                }
                break;
            case "a":
                    myLosersCell = (int) me.get(getString(R.string.losersNum));
                if(otherPid == -1 || otherSCellNum == -1 || otherDCellNum == -1 || myLosersCell == -1)
                    super.toasts(getString(R.string.wrongLocationToast), Toast.LENGTH_LONG);
                else
                {
                    newRow = myLosersCell / mainRowsNum;
                    newCol = myLosersCell % colsNum;
                    loser = mainBoard[newRow][newCol].getP();
                    d = loser;
                    loser.setGameCell(null);
                    mainBoard[newRow][newCol].setP(null);
                    mainBoard[newRow][newCol].setBackgroundResource(R.drawable.gg2);
                    myPieces[d.getId()].setInTheGame(false);
                    lastRow = newOtherSCellNum / mainRowsNum;
                    lastCol = newOtherSCellNum % colsNum;
                    newRow = newOtherDCellNum / mainRowsNum;
                    newCol = newOtherDCellNum % colsNum;
                    //hisPiecesLocationList.set(newOtherSCellNum, -1);
                    //hisPiecesLocationList.set(newOtherDCellNum, otherPid);
                    mainBoard[newRow][newCol].setP(otherPieces[otherPid]);
                    a = mainBoard[newRow][newCol].getP();
                    mainBoard[lastRow][lastCol].setP(null);
                    otherPieces[otherPid].setGameCell(mainBoard[newRow][newCol]);
                    //mainBoard[newRow][newCol].setBackgroundResource(hisIm[otherPieces[otherPid].getNumber()]);
                    if(otherMovedCell != null && otherMovedCell.getP() != null && otherMovedCell.getP().getColor() != myColor)
                        otherMovedCell.setBackgroundResource(backColor);
                    otherMovedCell = mainBoard[newRow][newCol];
                    mainBoard[newRow][newCol].setBackgroundResource(backLast);
                    mainBoard[lastRow][lastCol].setBackgroundResource(R.drawable.gg2);
                    if (loser.getNumber() == 0)
                        win(other);
                }
                break;

            case "d":
                hisLosersCell = (int)other.get(getString(R.string.losersNum));
                if(hisLosersCell == -1)
                    super.toasts(getString(R.string.wrongLocationToast), Toast.LENGTH_LONG);
                else
                {
                    newRow = hisLosersCell / mainRowsNum;
                    newCol = hisLosersCell % colsNum;
                    a = mainBoard[newRow][newCol].getP();
                    mainBoard[newRow][newCol].getP().setGameCell(null);
                    mainBoard[newRow][newCol].setP(null);
                    mainBoard[newRow][newCol].setBackgroundResource(R.drawable.gg2);
                    d = myPieces[me.getInt(getString(R.string.pieceId))];
                }
                break;
        }
        fightDialog(d, a);
        other.setIsFight(false);
        try {
            endOtherTurn();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public int getNewLocation(int i)
    {
        int lastRow;
        int lastCol;
        int newRow;
        int newCol;
        int j;

        lastRow = i/mainRowsNum;
        lastCol = i%colsNum;
        newRow = mainRowsNum-lastRow-1;
        newCol = colsNum-lastCol-1;
        j = (newRow*mainRowsNum+newCol);
        return j;
    }


    public void showOptions()
    {
        Cell temp;
        for(int i = 0; i < options.size(); i++) {
            temp = options.get(i);
            temp.setBackgroundResource(R.drawable.gs1);
        }
        for(int i = 0; i < fightOptions.size(); i++) {
            temp = fightOptions.get(i);
            temp.setBackgroundResource(fightOptionBackColor);
        }
    }

    public void removeOptions()
    {
        Cell temp;
        for(int i = 0; i < options.size(); i++) {
            temp = options.get(i);
            if(temp.getP() != null && temp.getP().getColor() != myColor)
                temp.setBackgroundResource(backColor);
            else if(temp.getP() != null && temp.getP().getColor() == myColor)
                temp.setBackgroundResource(myMoved[temp.getP().getNumber()]);
            else
                temp.setBackgroundResource(R.drawable.gg2);
        }
        for(int i = 0; i < fightOptions.size(); i++) {
            temp = fightOptions.get(i);
            if(temp.getP() != null && temp.getP().getColor() != myColor)
                temp.setBackgroundResource(backColor);
            else if(temp.getP() != null && temp.getP().getColor() == myColor)
                temp.setBackgroundResource(myMoved[temp.getP().getNumber()]);
            else
                temp.setBackgroundResource(R.drawable.gg2);
        }
    }
    public void searchOptions(Piece myPiece, Cell current)
    {
        options.clear();
        fightOptions.clear();
        int num = myPiece.getNumber();
        switch(num){
            case 0:
                break;
            case 11:
                break;
            case 2:
                checkLongAround(current);
                break;
            default:
                checkAround(current);

        }
    }

    public void checkLongAround(Cell cur)
    {
        int startRow = cur.getRow();
        int startCol = cur.getCol();

        //on the row- down
        while(startRow < mainRowsNum-1 && mainBoard[++startRow][startCol].getP() == null && mainBoard[startRow][startCol].isReal())
            options.add(mainBoard[startRow][startCol]);
        if(mainBoard[startRow][startCol].getP() != null && mainBoard[startRow][startCol].getP().getColor() != myColor)
            fightOptions.add(mainBoard[startRow][startCol]);
        startRow = cur.getRow();
        startCol = cur.getCol();
        //on the row- up
        while(startRow > 0 &&mainBoard[--startRow][startCol].getP() == null && mainBoard[startRow][startCol].isReal())
            options.add(mainBoard[startRow][startCol]);
        if(mainBoard[startRow][startCol].getP() != null && mainBoard[startRow][startCol].getP().getColor() != myColor)
            fightOptions.add(mainBoard[startRow][startCol]);
        startRow = cur.getRow();
        startCol = cur.getCol();
        //on the col- right
        while(startCol < colsNum-1 && mainBoard[startRow][++startCol].getP() == null && mainBoard[startRow][startCol].isReal())
            options.add(mainBoard[startRow][startCol]);
        if(mainBoard[startRow][startCol].getP() != null && mainBoard[startRow][startCol].getP().getColor() != myColor)
            fightOptions.add(mainBoard[startRow][startCol]);
        startRow = cur.getRow();
        startCol = cur.getCol();
        //on the col- left
        while(startCol > 0 && mainBoard[startRow][--startCol].getP() == null && mainBoard[startRow][startCol].isReal())
            options.add(mainBoard[startRow][startCol]);
        if(mainBoard[startRow][startCol].getP() != null && mainBoard[startRow][startCol].getP().getColor() != myColor)
            fightOptions.add(mainBoard[startRow][startCol]);
    }

    public void checkAround(Cell cur)
    {
        int startRow = cur.getRow();
        int startCol = cur.getCol();

        if(startRow < mainRowsNum-1) {
            if (mainBoard[++startRow][startCol].getP() == null && mainBoard[startRow][startCol].isReal())
                options.add(mainBoard[startRow][startCol]);
            else if (mainBoard[startRow][startCol].getP() != null && mainBoard[startRow][startCol].getP().getColor() != myColor)
                fightOptions.add(mainBoard[startRow][startCol]);
        }
        startRow = cur.getRow();
        startCol = cur.getCol();
        if(startRow > 0) {
            if (mainBoard[--startRow][startCol].getP() == null && mainBoard[startRow][startCol].isReal())
                options.add(mainBoard[startRow][startCol]);
            else if (mainBoard[startRow][startCol].getP() != null && mainBoard[startRow][startCol].getP().getColor() != myColor)
                fightOptions.add(mainBoard[startRow][startCol]);
        }
        startRow = cur.getRow();
        startCol = cur.getCol();
        if(startCol < colsNum-1) {
            if (mainBoard[startRow][++startCol].getP() == null && mainBoard[startRow][startCol].isReal())
                options.add(mainBoard[startRow][startCol]);
            else if (mainBoard[startRow][startCol].getP() != null && mainBoard[startRow][startCol].getP().getColor() != myColor)
                fightOptions.add(mainBoard[startRow][startCol]);
        }
        startRow = cur.getRow();
        startCol = cur.getCol();
        if(startCol > 0) {
            if (mainBoard[startRow][--startCol].getP() == null && mainBoard[startRow][startCol].isReal())
                options.add(mainBoard[startRow][startCol]);
            else if (mainBoard[startRow][startCol].getP() != null && mainBoard[startRow][startCol].getP().getColor() != myColor)
                fightOptions.add(mainBoard[startRow][startCol]);
        }
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
        Dialog out = onCreateBackDialog();
        out.show();
    }

    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 0, 1000); //
    }

    public void stopTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            if(other.getBoolean(getString(R.string.isFight)))
                removeOutOtherPieces();
            else
                moveOtherPieces();
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                handler.post(new Runnable() {
                    public void run() {
                        if(me.getString(getString(R.string.userMessege)) != null) {
                            messege.setText(me.getString(getString(R.string.userMessege)));
                            me.getString("");
                        }
                        doNewQuery();
                        if(me.getBoolean(getString(R.string.myTurn)))
                            stopTimerTask();
                    }
                });
            }
        };
    }

    public void buildBoard()
    {
        firstTurn();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        mainCellWH = Math.min(width / colsNum, height / mainRowsNum) * 9 / 10;
        outCellWH = Math.min(width / colsNum, height / mainRowsNum) * 9 / 12;
        mainImageLayoutParams = new ViewGroup.LayoutParams(mainCellWH, mainCellWH);
        outsLayoutParams = new ViewGroup.LayoutParams(outCellWH, outCellWH);
        createPieces();
        mainRowsLayout = (LinearLayout) findViewById(R.id.mainBoard);
        buildTheMainBoard();
    }

    public void setMe(User me) {
        this.me = me;
        isKilled = true;
        me.setIsFight(false);
        me.setPieceId(-1);
        me.setDestCellNum(-1);
        me.setSourceCellNum(-1);
        me.setLosers(-1);
        me.setMessege("");
        if(!me.getBoolean(getString(R.string.myTurn))) {
            startTimer();
        }
    }
}

