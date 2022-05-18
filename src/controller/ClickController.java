package controller;

import model.*;
import view.Chessboard;
import view.ChessboardPoint;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ClickController {
    private final Chessboard chessboard;
    private ChessComponent first, chessPassingPawn = null;

    public ClickController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public List<ChessboardPoint> ChessboardPointsList = new ArrayList<>();

    private ChessboardPoint passPawnPoint = null;

    public void onClick(ChessComponent chessComponent) {
        if (first == null) {
            if (handleFirst(chessComponent)) {
                chessComponent.setSelected(true);
                first = chessComponent;
                first.repaint();
                ChessboardPointsList = first.canMoveTo(chessboard.getChessComponents());
                if (first instanceof PawnChessComponent) { // 是否过路兵
                    processPawn();
                }

                for (ChessboardPoint chessboardPoint : ChessboardPointsList) { // CanReached
                    int x = chessboardPoint.getX(), y = chessboardPoint.getY();
                    chessboard.getChessComponents()[x][y].setReached(true);
                    chessboard.getChessComponents()[x][y].repaint();
                }
            }
        } else {
            if (first == chessComponent) { // 再次点击取消选取
                chessComponent.setSelected(false);
                for (ChessboardPoint chessboardPoint : ChessboardPointsList) {
                    int x = chessboardPoint.getX(), y = chessboardPoint.getY();
                    chessboard.getChessComponents()[x][y].setReached(false);
                    chessboard.getChessComponents()[x][y].repaint();
                }
                ChessComponent recordFirst = first;
                first = null;
                recordFirst.repaint();
            } else if (handleSecond(chessComponent)) {
                // repaint in swap chess method.
                for (ChessboardPoint chessboardPoint : ChessboardPointsList) {
                    int x = chessboardPoint.getX(), y = chessboardPoint.getY();
                    chessboard.getChessComponents()[x][y].setReached(false);
                    chessboard.getChessComponents()[x][y].repaint();
                }
                if (isPassPawn(chessComponent)) {
                    if (chessPassingPawn instanceof PawnChessComponent) {
                        int row2 = chessPassingPawn.getChessboardPoint().getX(), col2 = chessPassingPawn.getChessboardPoint().getY();
                        chessboard.remove(chessPassingPawn);
                        chessboard.add(chessPassingPawn = new EmptySlotComponent(chessPassingPawn.getChessboardPoint(),
                                chessPassingPawn.getLocation(), this, chessboard.getCHESS_SIZE()));
                        chessboard.getChessComponents()[row2][col2] = chessPassingPawn;
                        chessPassingPawn.repaint();
                        passPawnPoint = null;
                        chessPassingPawn = null;
                    }
                }
                chessboard.swapChessComponents(first, chessComponent);
                chessboard.swapColor();
                if (isCheck())
                    JOptionPane.showMessageDialog(null, "将军！", "alert", JOptionPane.WARNING_MESSAGE);
                first.setSelected(false);
                first.repaint();
                first = null;
                chessboard.addHistory();
                ChessboardPointsList.clear();
            }

        }
        chessboard.setStatus();
    }

    private void processPawn() {
        ChessComponent[][] chessComponents = chessboard.getChessComponents();
        int[] dyArray = { -1, 1 };
        int x = first.getChessboardPoint().getX();
        ChessColor chessColor = first.getChessColor();

        for (int dy : dyArray) {
            if (x == 3 && chessColor == ChessColor.WHITE) {
                int y = first.getChessboardPoint().getY();
                if (first.getChessboardPoint().offset(0, dy) != null) {
                    ChessComponent c = chessComponents[3][y + dy];
                    if (c instanceof PawnChessComponent && c.getChessColor() == ChessColor.BLACK) {
                        if (chessComponents[1][y + dy] instanceof EmptySlotComponent) {
                            int historyIndex = chessboard.getChessHistory().size() - 2;
                            String cb = chessboard.getChessHistory().get(historyIndex);
                            if (cb.substring(9 + y + dy, 9 + y + dy + 1).equals("P")) {
                                ChessboardPointsList.add(first.getChessboardPoint().offset(-1, dy));
                                passPawnPoint = first.getChessboardPoint().offset(-1, dy);
                                chessPassingPawn = c;
                            }
                        }
                    }
                }
            }
            if (x == 4 && chessColor == ChessColor.BLACK) {
                int y = first.getChessboardPoint().getY();
                if (first.getChessboardPoint().offset(0, dy) != null) {
                    ChessComponent c = chessComponents[4][y + dy];
                    if (c instanceof PawnChessComponent && c.getChessColor() == ChessColor.WHITE) {
                        if (chessComponents[6][y + dy] instanceof EmptySlotComponent) {
                            int historyIndex = chessboard.getChessHistory().size() - 2;
                            String cb = chessboard.getChessHistory().get(historyIndex);
                            if (cb.substring(54 + y + dy, 54 + y + dy + 1).equals("p")) {
                                ChessboardPointsList.add(first.getChessboardPoint().offset(1, dy));
                                passPawnPoint = first.getChessboardPoint().offset(1, dy);
                                chessPassingPawn = c;
                            }
                        }
                    }
                }
            }
        }
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
                ((first.canMoveTo(chessboard.getChessComponents(), chessComponent.getChessboardPoint()) ||
                        isPassPawn(chessComponent)));
    }

    boolean isCheck() {
        ChessComponent[][] chessComponents = chessboard.getChessComponents();
        List<ChessboardPoint> ChessboardPointsList = new ArrayList<>();
        for (ChessComponent[] chessComponents2 : chessComponents)
            for (ChessComponent chess : chessComponents2)
                if (chess.getChessColor() == first.getChessColor()){
                    ChessboardPointsList.addAll(chess.canMoveTo(chessComponents));
                }

        for (ChessComponent[] chessComponents2 : chessComponents)
            for (ChessComponent chess : chessComponents2)
                if (chess instanceof KingChessComponent && chess.getChessColor() == chessboard.getCurrentColor()) {
                    ChessboardPoint destination = chess.getChessboardPoint();
                    return isContains(ChessboardPointsList, destination);
                }
        return false;
    }

    private boolean isContains(List<ChessboardPoint> chessboardPointList, ChessboardPoint destination) {
        for (ChessboardPoint chessboardPoint : chessboardPointList)
            if (chessboardPoint.toString().equals(destination.toString()))
                return true;
        return false;
    }

    private boolean isPassPawn(ChessComponent chessComponent) {
        if (passPawnPoint == null)
            return false;
        if (chessComponent.getChessboardPoint().toString().equals(passPawnPoint.toString()))
            return true;
        return false;
    }

}
