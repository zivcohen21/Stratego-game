package com.example.ziv.stratego;

import android.graphics.drawable.Drawable;

/**
 * Created by ZIV on 11/01/2016.
 */

public class Piece {
    private int id;
    private int number;
    private char color;
    private Cell gameCell;
    private Cell preGameCell;
    private int Img;
    private boolean inTheGame;

    public Piece(int id, int number, char color, Cell gameCell, Cell preGameCell, int Img)
    {
        this.id = id;
        this.number = number;
        this.color = color;
        this.gameCell = gameCell;
        this.preGameCell = preGameCell;
        this.Img = Img;
    }

    public Piece(int id, int number, char color, Cell preGameCell, int Img)
    {
        this.id = id;
        this.number = number;
        this.color = color;
        this.preGameCell = preGameCell;
        this.Img = Img;
    }
    public Piece(int id, int number, char color, int Img)
    {
        this.id = id;
        this.number = number;
        this.color = color;
        this.Img = Img;
    }



    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public char getColor() {
        return color;
    }
    public void setColor(char color) {
        this.color = color;
    }
    public Cell getGameCell() {
        return gameCell;
    }
    public void setGameCell(Cell gameCell) {
        this.gameCell= gameCell;
    }

    public Cell getPreGameCell() {
        return preGameCell;
    }
    public void setPreGameCell(Cell preGameCell) {
        this.preGameCell = preGameCell;
    }

    public int getImg() {
        return Img;
    }
    public void setRImg(int Img) {
        this.Img = Img;
    }

    public boolean isInTheGame() {
        return inTheGame;
    }

    public void setInTheGame(boolean inTheGame) {
        this.inTheGame = inTheGame;
    }


}
