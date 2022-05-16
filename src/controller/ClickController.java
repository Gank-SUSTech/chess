package controller;


import model.ChessComponent;
import model.KingChessComponent;
import view.Chessboard;
import view.ChessboardPoint;

import javax.swing.*;
import java.util.List;

public class ClickController {
    private final Chessboard chessboard;
    private ChessComponent first;

    public ClickController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public void onClick(ChessComponent chessComponent) {
        if (first == null) {
            if (handleFirst(chessComponent)) {
                chessComponent.setSelected(true);
                first = chessComponent;
                first.repaint();
                List<ChessboardPoint> lChessboardPoints = first.canMoveTo(chessboard.getChessComponents());
                for (ChessboardPoint chessboardPoint : lChessboardPoints) { // CanReached
                    int x = chessboardPoint.getX(), y = chessboardPoint.getY();
                    chessboard.getChessComponents()[x][y].setReached(true);
                    chessboard.getChessComponents()[x][y].repaint();
                }
            }
        } else {
            if (first == chessComponent) { // 再次点击取消选取
                chessComponent.setSelected(false);
                List<ChessboardPoint> lChessboardPoints = first.canMoveTo(chessboard.getChessComponents());
                for (ChessboardPoint chessboardPoint : lChessboardPoints) {
                    int x = chessboardPoint.getX(), y = chessboardPoint.getY();
                    chessboard.getChessComponents()[x][y].setReached(false);
                    chessboard.getChessComponents()[x][y].repaint();
                }
                ChessComponent recordFirst = first;
                first = null;
                recordFirst.repaint();
            } else if (handleSecond(chessComponent)) {
                //repaint in swap chess method.
                List<ChessboardPoint> lChessboardPoints = first.canMoveTo(chessboard.getChessComponents());
                for (ChessboardPoint chessboardPoint : lChessboardPoints) {
                    int x = chessboardPoint.getX(), y = chessboardPoint.getY();
                    chessboard.getChessComponents()[x][y].setReached(false);
                    chessboard.getChessComponents()[x][y].repaint();
                }
                chessboard.swapChessComponents(first, chessComponent);
                chessboard.swapColor();
                if (isCheck())
                    JOptionPane.showMessageDialog(null, "将军！", "alert", JOptionPane.WARNING_MESSAGE);
                first.setSelected(false);
                first.repaint();
                first = null;

                chessboard.addHistory();
            }
        }
        chessboard.setStatus();
    }

    /**
     * @param chessComponent 目标选取的棋子
     * @return 目标选取的棋子是否与棋盘记录的当前行棋方颜色相同
     */


    private boolean handleFirst(ChessComponent chessComponent) {
        return chessComponent.getChessColor() == chessboard.getCurrentColor();
    }

    /**
     * @param chessComponent first棋子目标移动到的棋子second
     * @return first棋子是否能够移动到second棋子位置
     */

    private boolean handleSecond(ChessComponent chessComponent) {
        return chessComponent.getChessColor() != chessboard.getCurrentColor() &&
                first.canMoveTo(chessboard.getChessComponents(), chessComponent.getChessboardPoint());
    }

    boolean isCheck() {
        ChessComponent[][] chessComponents = chessboard.getChessComponents();
        List<ChessboardPoint> lChessboardPoints = first.canMoveTo(chessComponents);
        for (ChessComponent[] chessComponents2 : chessComponents)
            for (ChessComponent chess : chessComponents2)
                if (chess instanceof KingChessComponent && chess.getChessColor() == chessboard.getCurrentColor()) {
                    ChessboardPoint destination = chess.getChessboardPoint();
                    for (ChessboardPoint chessboardPoint : lChessboardPoints)
                        if (chessboardPoint.toString().equals(destination.toString()))
                            return true;
                }
        return false;
    }

}
