package com.example.ziv.stratego;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;


public class PreGameActivity extends SuperActivity implements View.OnClickListener {

    final int rowsNum = 4;
    final int mainRowsNum = 10;
    final int colsNum = 10;
    char myColor;
    User me, other;
    String myId, otherId;
    int mainCellWH, cellWH, numOfPieces = 40, startRowPreBoard = 6, startTopBoard = 60;
    ViewGroup.LayoutParams mainImageLayoutParams, imageLayoutParams;
    LinearLayout mainRowsLayout, topRowsLayout, bottomRowsLayout;
    Cell[][] mainBoard = new Cell[mainRowsNum][colsNum];
    Cell[][] topBoard = new Cell[mainRowsNum][colsNum];
    Cell[][] bottomBoard = new Cell[rowsNum][colsNum];
    Piece[] redPieces = new Piece[numOfPieces];
    Piece[] bluePieces = new Piece[numOfPieces];
    Piece[] myPieces = new Piece[numOfPieces];
    Piece[] otherPieces = new Piece[numOfPieces];
    Cell clickedButton, lastCell;
    int [] mySelectIm;
    int [] myIm;
    Integer [] piecesLocation = new Integer[mainRowsNum*colsNum];
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

    boolean isSelect, fullBoard, isKilled;
    Piece selectedPiece;
    RadioGroup radioColorGroup;
    ParseQuery<User> query, q;
    Integer [] temp = new Integer[mainRowsNum*colsNum];
    int choiceNum, numofChoices;
    RelativeLayout relativeLayout;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_geame);
        isKilled = true;
        relativeLayout = (RelativeLayout) findViewById(R.id.preGame);
        relativeLayout.setBackgroundResource(R.drawable.brown1);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();
        editor.apply();
        myId = getIntent().getStringExtra(getString(R.string.myId));
        otherId = getIntent().getStringExtra(getString(R.string.otherId));
        myColor = getIntent().getCharExtra(getString(R.string.myColor), 'r');
        tv = (TextView) findViewById(R.id.explanation);
        tv.setBackgroundResource(R.drawable.explanation);
        query = new ParseQuery<User>(getString(R.string.strategoUser));
        q = new ParseQuery<User>(getString(R.string.strategoUser));
        query.whereEqualTo("objectId", myId);
        query.findInBackground(new FindCallback<User>() {

            @Override
            public void done(List<User> objects, ParseException e) {
                me = objects.get(0);
                isKilled = true;
                me.setIsPlaying(true);
            }
        });

        q.whereEqualTo("objectId", otherId);
        q.findInBackground(new FindCallback<User>() {

            @Override
            public void done(List<User> objects, ParseException e) {
                other = objects.get(0);
            }
        });
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        cellWH = Math.min(width / colsNum, height / rowsNum) * 9 / 10;
        imageLayoutParams = new ViewGroup.LayoutParams(cellWH, cellWH);
        createPieces();
        myColor(myColor);
        topRowsLayout = (LinearLayout) findViewById(R.id.topBoard);
        buildTheTopBoard();
        bottomRowsLayout = (LinearLayout) findViewById(R.id.bottomBoard);
        buildTheBottomBoard();

        Button coButton = (Button) findViewById(R.id.startGame);
        coButton.setBackgroundResource(R.drawable.cont1);
        coButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(checkBoard()) {
                me.setPiecesLocation(piecesLocation);
                isKilled = false;
                Intent waitAct = new Intent(PreGameActivity.this, WaitingActivity.class);
                waitAct.putExtra(getString(R.string.myId), myId);
                waitAct.putExtra(getString(R.string.otherId), otherId);
                waitAct.putExtra(getString(R.string.piecesLocation), piecesLocation);
                waitAct.putExtra(getString(R.string.myColor), myColor);
                waitAct.putExtra(getString(R.string.waitingTextKey),
                        getString(R.string.waitingTextValuePre,
                                other.getString(getString(R.string.name))));
                waitAct.putExtra(getString(R.string.isPreKey), true);
                startActivity(waitAct);
                // }
            }
        });

        Button showButton = (Button) findViewById(R.id.showTemplet);
        showButton.setBackgroundResource(R.drawable.mytemplet);
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyFromFileToBoard();

            }
        });

        Button clearButton = (Button) findViewById(R.id.clearAll);
        clearButton.setBackgroundResource(R.drawable.clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               clearBoard();
            }
        });

        Button saveButton = (Button) findViewById(R.id.saveTemplet);
        saveButton.setBackgroundResource(R.drawable.savetemplet);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    copyFromBoardToFile();
                    toasts(getString(R.string.templateSavedToast), Toast.LENGTH_SHORT);
            }
        });

    }

    public void buildTheTopBoard() {
        int n = 59;
        for (int row = startRowPreBoard; row < startRowPreBoard+rowsNum; row++) {
            // The main layout (vertical)
            LinearLayout rowLayout = new LinearLayout(this);
            for (int column = 0; column < colsNum; column++) {
                Cell c = new Cell(this,true,row,column,++n);
                c.setWidth(cellWH);
                c.setHeight(cellWH);
                c.setReal(true);
                c.setOnClickListener(this);
                c.setBackgroundResource(R.drawable.gg2);
                rowLayout.addView(c, imageLayoutParams);
                topBoard[row][column] = c;
            }
            topRowsLayout.addView(rowLayout);
        }

        for(int i = 0; i < piecesLocation.length; i++)
            piecesLocation[i] = -1;
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
            fightOptionBackColor = R.drawable.rbs;
        }
    }

    public void buildTheBottomBoard() {
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
                c.setBackgroundResource(myPieces[n].getImg());
                myPieces[n].setPreGameCell(c);
                rowLayout.addView(c, imageLayoutParams);
                bottomBoard[row][column] = c;
            }
            bottomRowsLayout.addView(rowLayout);
        }
    }

    public void onClick(View v) {
        if (v instanceof Cell) {
            clickedButton = (Cell) v;
            if(lastCell != null && lastCell.getP() != null)
                lastCell.setBackgroundResource(myIm[lastCell.getP().getNumber()]);
            if(!isSelect)//select piece
                choosePiece(clickedButton);
            else
                choosePreDest(clickedButton);
        }
    }

    public void choosePiece(Cell theCell)
    {
        if(theCell.getP() != null) {
            selectedPiece = theCell.getP();
            if (selectedPiece.getColor() == myColor) {
                isSelect = true;
                theCell.setBackgroundResource(mySelectIm[selectedPiece.getNumber()]);
                lastCell = theCell;
            }
        }
    }

    public void choosePreDest(Cell theCell)
    {
        if(theCell.getP() == null && lastCell != null)
        {
            isSelect = false;
            theCell.setP(selectedPiece);
            theCell.setBackgroundResource(myIm[selectedPiece.getNumber()]);
            if(lastCell.getInGame())
            {
                lastCell.setBackgroundResource(R.drawable.gg2);
                piecesLocation[lastCell.getNumOfCell()] = -1;
                lastCell.getP().setGameCell(null);
            }
            else
                lastCell.setBackgroundResource(R.drawable.w1);
            lastCell.setP(null);
            if(theCell.getInGame())
            {
                selectedPiece.setGameCell(theCell);
                topBoard[theCell.getRow()][theCell.getCol()].setP(selectedPiece);
                piecesLocation[theCell.getNumOfCell()] = selectedPiece.getId();
            }
            else
            {
                choosePiece(theCell);
                choosePreDest(selectedPiece.getPreGameCell());
            }
        }
        else if(theCell.getP() != null)
            if(theCell.getP().getColor() == myColor)
                choosePiece(theCell);
            else {
                selectedPiece = null;
                isSelect = false;
            }
    }

    public boolean checkBoard()
    {
        fullBoard = true;
        for(int i = 60; i < mainRowsNum*colsNum; i++)
        {
            if(piecesLocation[i] == -1)
            {
                fullBoard = false;
                break;
            }
        }
        return fullBoard;
    }

    public void copyFromBoardToFile()
    {
        for (int i = startRowPreBoard; i < startRowPreBoard+rowsNum; i++)
            for(int j = 0; j < colsNum; j++) {
                int n = topBoard[i][j].getNumOfCell();
                if(topBoard[i][j].getP() != null) {
                    editor.putInt(Integer.toString(n), topBoard[i][j].getP().getId());
                } else {
                    editor.remove(Integer.toString(n));
                    editor.putInt(Integer.toString(n), -1);
                }
                editor.apply();
            }
    }

    public void copyFromFileToBoard()
    {
        clearBoard();
        for (int i = startRowPreBoard; i < startRowPreBoard+rowsNum; i++)
            for(int j = 0; j < colsNum; j++) {
                int n = topBoard[i][j].getNumOfCell();
                int temp = sharedPref.getInt(Integer.toString(n), -1);
                if(temp != -1) {
                    choosePiece(myPieces[temp].getPreGameCell());
                    choosePreDest(topBoard[i][j]);
                }
                else
                    topBoard[i][j].setBackgroundResource(R.drawable.gg2);
            }
    }

    public void clearBoard()
    {
        for (int i = startRowPreBoard; i < startRowPreBoard+rowsNum; i++)
            for(int j = 0; j < colsNum; j++) {
                if (topBoard[i][j].getP() != null) {
                    choosePiece(topBoard[i][j]);
                    choosePreDest(topBoard[i][j].getP().getPreGameCell());
                }
            }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isKilled)
            super.exit();
    }

    @Override
    public void onBackPressed() {
        isKilled = false;
        super.onBackPressed();
    }

}
