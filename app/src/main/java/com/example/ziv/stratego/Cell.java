package com.example.ziv.stratego;

import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by ZIV on 11/01/2016.
 */
public class Cell extends Button
{
    private boolean inGame;
    private int row;
    private int col;
    private int numOfCell;
    private boolean isReal;
    private Piece p;

    public Cell (Context context)
    {
        super(context);
    }

    public Cell(Context context, boolean inGame, int row, int col, int numOfCell, boolean isReal) {
        super(context);
        this.inGame = inGame;
        this.row = row;
        this.col = col;
        this.numOfCell = numOfCell;
        this.isReal = isReal;
    }
    public Cell(Context context, boolean inGame, int row, int col, int numOfCell) {
        super(context);
        this.inGame = inGame;
        this.row = row;
        this.col = col;
        this.numOfCell = numOfCell;
    }

    public Cell(Context context, boolean inGame, int numOfCell) {
        super(context);
        this.inGame = inGame;
        this.numOfCell = numOfCell;
    }

    public boolean getInGame() {
        return inGame;
    }
    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }
    public int getRow() {
        return row;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public int getCol() {
        return col;
    }
    public void setCol(int col) {
        this.col = col;
    }
    public boolean isReal() {
        return isReal;
    }
    public void setReal(boolean isReal) {
        this.isReal = isReal;
    }
    public Piece getP() {
        return p;
    }
    public void setP(Piece p) {
        this.p = p;
    }
    public int getNumOfCell() {
        return numOfCell;
    }
    public void setNumOfCell(int numOfCell) {
        this.numOfCell = numOfCell;
    }





}