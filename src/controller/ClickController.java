package controller;

import model.*;
import view.Chessboard;
import view.ChessboardPoint;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ClickController {
    private final Chessboard chessboard;
    private ChessComponent first,chess2=null;

    public ClickController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public List<ChessboardPoint> lChessboardPoints = new ArrayList<>();

    private ChessboardPoint passPawnPoint = null;

    public void onClick(ChessComponent chessComponent) {
        if (first == null) {
            if (handleFirst(chessComponent)) {
                chessComponent.setSelected(true);
                first = chessComponent;
                first.repaint();
                lChessboardPoints = first.canMoveTo(chessboard.getChessComponents());
                if (first instanceof PawnChessComponent) { // 是否过路兵
                    processPawn();
                }

                for (ChessboardPoint chessboardPoint : lChessboardPoints) { // CanReached
                    int x = chessboardPoint.getX(), y = chessboardPoint.getY();
                    chessboard.getChessComponents()[x][y].setReached(true);
                    chessboard.getChessComponents()[x][y].repaint();
                }
            }
        } else {
            if (first == chessComponent) { // 再次点击取消选取
                chessComponent.setSelected(false);
                // System.out.println(lChessboardPoints.size());
                for (ChessboardPoint chessboardPoint : lChessboardPoints) {
                    int x = chessboardPoint.getX(), y = chessboardPoint.getY();
                    chessboard.getChessComponents()[x][y].setReached(false);
                    chessboard.getChessComponents()[x][y].repaint();
                }
                ChessComponent recordFirst = first;
                first = null;
                recordFirst.repaint();
            } else if (handleSecond(chessComponent)) {
                // repaint in swap chess method.
                for (ChessboardPoint chessboardPoint : lChessboardPoints) {
                    int x = chessboardPoint.getX(), y = chessboardPoint.getY();
                    chessboard.getChessComponents()[x][y].setReached(false);
                    chessboard.getChessComponents()[x][y].repaint();
                }
                if (isPassPawn(chessComponent)){
                    if(chess2 instanceof PawnChessComponent){
                        int row2 = chess2.getChessboardPoint().getX(), col2 = chess2.getChessboardPoint().getY();
                        chessboard.remove(chess2);
                        chessboard.add(chess2 = new EmptySlotComponent(chess2.getChessboardPoint(), chess2.getLocation(), this, chessboard.getCHESS_SIZE()));
                        chessboard.getChessComponents()[row2][col2] = chess2;
                        chess2.repaint();
                        passPawnPoint = null;
                        chess2 = null;
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
                lChessboardPoints.clear();
            }
            
        }
        chessboard.setStatus();
    }

    private void processPawn() {
        // if (first instanceof PawnChessComponent) { // 兵
        ChessComponent[][] chessComponents = chessboard.getChessComponents();

        int x = first.getChessboardPoint().getX();
        ChessColor chessColor = first.getChessColor();

        if (x == 3 && chessColor == ChessColor.WHITE) {
            int y = first.getChessboardPoint().getY();
            if (first.getChessboardPoint().offset(0, -1) != null) {
                ChessComponent c = chessComponents[3][y - 1];
                if (c instanceof PawnChessComponent && c.getChessColor() == ChessColor.BLACK) {
                    if (chessComponents[1][y - 1] instanceof EmptySlotComponent) {
                        int historyIndex = chessboard.getChessHistory().size() - 2;
                        String cb = chessboard.getChessHistory().get(historyIndex);
                        if (cb.substring(9 + y - 1, 9 + y).equals("P")) {
                            lChessboardPoints.add(first.getChessboardPoint().offset(-1, -1));
                            passPawnPoint = first.getChessboardPoint().offset(-1, -1);
                            chess2= c;
                        }
                    }
                }
            }
            if (first.getChessboardPoint().offset(0, 1) != null) {
                ChessComponent c = chessComponents[3][y + 1];
                if (c instanceof PawnChessComponent && c.getChessColor() == ChessColor.BLACK) {
                    if (chessComponents[1][y + 1] instanceof EmptySlotComponent) {
                        if (chessboard.getChessHistory().size() > 1) {
                            int historyIndex = chessboard.getChessHistory().size() - 2;
                            String cb = chessboard.getChessHistory().get(historyIndex);
                            if (cb.substring(9 + y + 1, 9 + y + 1 + 1).equals("P")) {
                                lChessboardPoints.add(first.getChessboardPoint().offset(-1, 1));
                                passPawnPoint = first.getChessboardPoint().offset(-1, 1);
                                chess2= c;
                            }
                        }
                    }
                }
            }

        }
        if (x == 4 && first.getChessColor() == ChessColor.BLACK) { // 黑兵
            int y = first.getChessboardPoint().getY();
            if (first.getChessboardPoint().offset(0, -1) != null) {
                ChessComponent c = chessComponents[4][y - 1];
                if (c instanceof PawnChessComponent && c.getChessColor() == ChessColor.WHITE) {
                    if (chessComponents[6][y - 1] instanceof EmptySlotComponent) {
                        int historyIndex = chessboard.getChessHistory().size() - 2;
                        String cb = chessboard.getChessHistory().get(historyIndex);
                        if (cb.substring(54 + y - 1, 54 + y).equals("p")) {
                            lChessboardPoints.add(first.getChessboardPoint().offset(1, -1));
                            passPawnPoint = first.getChessboardPoint().offset(1, -1);
                            chess2= c;
                        }
                    }
                }
            }
            if (first.getChessboardPoint().offset(0, 1) != null) {
                ChessComponent c = chessComponents[4][y + 1];
                if (c instanceof PawnChessComponent && c.getChessColor() == ChessColor.WHITE) {
                    if (chessComponents[6][y + 1] instanceof EmptySlotComponent) {
                        if (chessboard.getChessHistory().size() > 1) {
                            int historyIndex = chessboard.getChessHistory().size() - 2;
                            String cb = chessboard.getChessHistory().get(historyIndex);
                            if (cb.substring(54 + y + 1, 54 + y + 1 + 1).equals("p")) {
                                lChessboardPoints.add(first.getChessboardPoint().offset(1, 1));
                                passPawnPoint = first.getChessboardPoint().offset(1, 1);
                                chess2= c;
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
        List<ChessboardPoint> lChessboardPoints = first.canMoveTo(chessComponents);
        for (ChessComponent[] chessComponents2 : chessComponents)
            for (ChessComponent chess : chessComponents2)
                if (chess instanceof KingChessComponent && chess.getChessColor() == chessboard.getCurrentColor()) {
                    ChessboardPoint destination = chess.getChessboardPoint();
                    return isContains(lChessboardPoints, destination);
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
