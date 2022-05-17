package model;

import controller.ClickController;
import view.ChessboardPoint;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 这个类表示棋盘上的空位置
 */
public class EmptySlotComponent extends ChessComponent {

    public EmptySlotComponent(ChessboardPoint chessboardPoint, Point location, ClickController listener, int size) {
        super(chessboardPoint, location, ChessColor.NONE, listener, size);
    }

    @Override
    public boolean canMoveTo(ChessComponent[][] chessboard, ChessboardPoint destination) {
        return false;
    }

    @Override
    public List<ChessboardPoint> canMoveTo(ChessComponent[][] chessComponents) {
        return new ArrayList<>();
    }


    @Override
    public void loadResource() throws IOException {
        //No resource!
    }

    @Override
    public String toString() {
        return "_";
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
