package minesweeper;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * This board uses two 2D arrays to create the game board. It is safe to have many threads
 * since it uses the synchronize method to protect it. It uses only one board for multiple
 * players to play it, which is an efficient way. The datatype is simply mutable 2D arrays.
 * It is also protected from deadlock since each thread is independent.
 *  
 */
public class Board{
	private String[][] board;
    private final int dim;
    private boolean[][] bomb;

    public Board() {
        this.dim = 10;
        board = new String[10][10];
        bomb = new boolean[this.dim][this.dim];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                board[i][j] = "-";
                if (Math.random() < 0.25)
                    bomb[i][j] = true;
                else
                    bomb[i][j] = false;
            }
        }
    }
    
    public Board(int dimensions) {
        this.dim = dimensions;
        board = new String[dimensions][dimensions];
        bomb = new boolean[this.dim][this.dim];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                board[i][j] = "-";
                if (Math.random() < 0.25)
                    bomb[i][j] = true;
                else
                    bomb[i][j] = false;
            }
        }
    }

    public Board(String filename) throws IOException {
        FileInputStream file = new FileInputStream(filename);
        DataInputStream dataSt = new DataInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(dataSt));
        String thisLine;
        ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<Boolean>> bombRows = new ArrayList<ArrayList<Boolean>>();
        //the file input format is tested to make sure it's not null
        while ((thisLine = br.readLine()) != null) {
            String[] tokens = thisLine.split(" ");
            ArrayList<String> row = new ArrayList<String>();
            ArrayList<Boolean> bombLine = new ArrayList<Boolean>();
            for (int j = 0; j < tokens.length; j++) {
                row.add("-");
                int val = Integer.parseInt(tokens[j]);
                if (val == 1)
                    bombLine.add(true);
                else
                    bombLine.add(false);
            }
            rows.add(row);
            bombRows.add(bombLine);
        }
        this.dim = rows.get(0).size();
        board = new String[this.dim][this.dim];
        bomb = new boolean[this.dim][this.dim];
        for (int i = 0; i < this.dim; i++) {
            for (int j = 0; j < this.dim; j++) {
                board[i][j] = rows.get(i).get(j);
                bomb[i][j] = bombRows.get(i).get(j);
            }
        }
    }

    public int getDim() {
        return this.dim;
    }
    
    public synchronized String getStr(int x, int y) {
        if (isValid(x, y))
            return board[x][y];
        return "";
    }
    
    public synchronized boolean hasBomb(int x, int y) {
        if (isValid(x, y))
            return bomb[x][y];
        return false;
    }

    public synchronized void removeBomb(int x, int y) {
        if (isValid(x, y))
            bomb[x][y] = false;
    }

    public synchronized void flag(int x, int y) {
        if (isValid(x, y) && board[x][y].equals("-"))
            board[x][y] = "F";
    }
    
    public synchronized boolean isValid(int x, int y) {
        if (x >= 0 && x < this.dim && y >= 0 && y < this.dim)
            return true;
        return false;
    }

    public synchronized void deflag(int x, int y) {
    //F (flagged) represents the spot bring flagged
        if (isValid(x, y) && board[x][y].equals("F"))
            board[x][y] = "-";
    }

    public synchronized void dig(int x, int y) {
        boolean dugBomb = false;
        if (isValid(x, y)) {
            if (board[x][y] == " " || board[x][y] == "F") {
                return;
            }
            board[x][y] = " ";
        }
        if (hasBomb(x, y)) {
            this.removeBomb(x,y);
            dugBomb = true;
        }
        int[][] adjacentVals = new int[][] {
                { x - 1, x - 1, x - 1, x, x, x + 1, x + 1, x + 1 },
                { y + 1, y, y - 1, y + 1, y - 1, y + 1, y, y - 1 } };
        int bombCount = 0;
        for (int i = 0; i < adjacentVals[0].length; i++) {
            int testx = adjacentVals[0][i];
            int testy = adjacentVals[1][i];
            if (isValid(testx, testy)) {
                if (hasBomb(testx, testy)) {
                    bombCount++;
                } else if (dugBomb) {
                    if (board[testx][testy].matches("\\d")) {
                        int sqCount = Integer.parseInt(board[testx][testy]);
                        sqCount--;
                        if (sqCount <= 0) {
                            dig(testx,testy);
                        }
                        else {
                            board[testx][testy] = sqCount+"";
                        }
                    }
                }
            }
        }
        if (bombCount == 0) {
            if (dugBomb)
                return;
            for (int i = 0; i < adjacentVals[0].length; i++) {
                int testx = adjacentVals[0][i];
                int testy = adjacentVals[1][i];
                if (isValid(testx, testy))
                    dig(testx, testy);
            }
        } else {
            board[x][y] = bombCount + "";
        }
    }
    
    public String getBombs() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < this.dim; i++) {
            for (int j = 0; j < this.dim; j++) {
                str.append(bomb[i][j]);
            }
            str.append("\r\n");
        }
        return str.toString();
    }

    public String toString() {
        StringBuilder strRep = new StringBuilder();
        for (int i = 0; i < this.dim; i++) {
            for (int j = 0; j < this.dim; j++) {
                if (j != 0) {
                    strRep.append(" ");
                }
                strRep.append(board[i][j]);
            }
            if (i < this.dim-1) { 
                strRep.append("\r\n");
            }
        }
        return strRep.toString();
    }
}

