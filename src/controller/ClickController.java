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

    public List<ChessboardPoint> chessboardPointsList = new ArrayList<>();

    private ChessboardPoint passPawnPoint = null;

    public void onClick(ChessComponent chessComponent) {
        boolean gameOver = chessboard.getGameOver();
        if (gameOver)
            return;
        if (first == null) {
            if (handleFirst(chessComponent)) {
                chessComponent.setSelected(true);
                first = chessComponent;
                first.repaint();
                chessboardPointsList = first.canMoveTo(chessboard.getChessComponents());
                if (first instanceof PawnChessComponent) {
                    // 若是兵,则找到是否有过路兵并标记出来
                    processPawn();
                }
                // 标记棋子可到达位置
                setCanReachPoint();
            }
        } else {
            if (first == chessComponent) { // 再次点击取消选取
                chessComponent.setSelected(false);
                clearCanReachPoint();
                ChessComponent recordFirst = first;
                first = null;
                recordFirst.repaint();
            } else if (handleSecond(chessComponent)) {
                // repaint in swap chess method.
                // 清楚掉可到达坐标
                clearCanReachPoint();

                // 吃过路兵
                if (isPassPawn(chessComponent)) {
                    eatPassPawn();
                }

                chessboard.swapChessComponents(first, chessComponent);
                chessboard.swapColor();

                // 若是兵则看能否兵升变
                if (first instanceof PawnChessComponent) {
                    pawnPromote();
                }
                first.setSelected(false);
                first.repaint();

                chessboard.addHistory();
                chessboardPointsList.clear();

                // 判断是否将军及将军结果
                check();
                first = null;
            }
        }
        if (!chessboard.getGameOver()) {
            chessboard.setStatus();
        }
    }

    /**
     * clear first chess canReached point
     */
    private void clearCanReachPoint() {
        for (ChessboardPoint chessboardPoint : chessboardPointsList) {
            int x = chessboardPoint.getX(), y = chessboardPoint.getY();
            chessboard.getChessComponents()[x][y].setReached(false);
            chessboard.getChessComponents()[x][y].repaint();
        }
    }

    /**
     * set first chess canReached point
     */
    private void setCanReachPoint() {
        for (ChessboardPoint chessboardPoint : chessboardPointsList) {
            int x = chessboardPoint.getX(), y = chessboardPoint.getY();
            chessboard.getChessComponents()[x][y].setReached(true);
            chessboard.getChessComponents()[x][y].repaint();
        }
    }

    /**
     * 将军判断
     */
    private void check() {
        if (isCheck()) {
            // 0:没有将 ; 1:有将但未将死 2:王被将死
            int checkResult = getCheckResult();
            switch (checkResult) {
                case 1:
                    JOptionPane.showMessageDialog(null, "将军！", "alert", JOptionPane.WARNING_MESSAGE);
                    break;
                case 2:
                    String msg = first.getChessColor().getName() + " 方取得胜利!";
                    JOptionPane.showMessageDialog(null, msg, "alert", JOptionPane.WARNING_MESSAGE);
                    gameOver();
                    break;
                case 0:
                default:
                    break;
            }
        }
    }

    /**
     * 吃过路兵
     */
    private void eatPassPawn() {
        if (chessPassingPawn instanceof PawnChessComponent) {
            int row2 = chessPassingPawn.getChessboardPoint().getX(),
                    col2 = chessPassingPawn.getChessboardPoint().getY();
            chessboard.remove(chessPassingPawn);
            chessboard.add(chessPassingPawn = new EmptySlotComponent(chessPassingPawn.getChessboardPoint(),
                    chessPassingPawn.getLocation(), this, chessboard.getCHESS_SIZE()));
            chessboard.getChessComponents()[row2][col2] = chessPassingPawn;
            chessPassingPawn.repaint();
            passPawnPoint = null;
            chessPassingPawn = null;
        }
    }

    /**
     * 兵升变
     */
    private void pawnPromote() {
        if (first instanceof PawnChessComponent) {
            int row1 = first.getChessboardPoint().getX(), col1 = first.getChessboardPoint().getY();
            if (row1 == 0 || row1 == 7) {
                ChessComponent[][] chessComponents = chessboard.getChessComponents();
                int sl = JOptionPane.showOptionDialog(null, "promote to ", "Congratulations",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Queen", "Rook", "Knight", "Bishop"},
                        "Queen");
                switch (sl) {
                    case 1:
                        chessboard.remove(first);
                        chessboard.add(first = new RookChessComponent(first.getChessboardPoint(), first.getLocation(),
                                first.getChessColor(), this, chessboard.getCHESS_SIZE()));
                        chessComponents[row1][col1] = first;
                        break;
                    case 2:
                        chessboard.remove(first);
                        chessboard.add(first = new KnightChessComponent(first.getChessboardPoint(), first.getLocation(),
                                first.getChessColor(), this, chessboard.getCHESS_SIZE()));
                        chessComponents[row1][col1] = first;
                        break;
                    case 3:
                        chessboard.remove(first);
                        chessboard.add(first = new BishopChessComponent(first.getChessboardPoint(), first.getLocation(),
                                first.getChessColor(), this, chessboard.getCHESS_SIZE()));
                        chessComponents[row1][col1] = first;
                        break;
                    case 0:
                    default:
                        chessboard.remove(first);
                        chessboard.add(first = new QueenChessComponent(first.getChessboardPoint(), first.getLocation(),
                                first.getChessColor(), this, chessboard.getCHESS_SIZE()));
                        chessComponents[row1][col1] = first;
                        break;
                }
            }
        }
    }

    private void gameOver() {
        chessboard.setGameOver(true);
    }

    /**
     * 若first是兵,则找到是否有过路兵并标记出来
     */
    private void processPawn() {
        ChessComponent[][] chessComponents = chessboard.getChessComponents();
        int[] dyArray = {-1, 1};
        int x = first.getChessboardPoint().getX();
        int y = first.getChessboardPoint().getY();
        ChessColor chessColor = first.getChessColor();
        for (int dy : dyArray) {
            if (x == 3 && chessColor == ChessColor.WHITE) {
                if (first.getChessboardPoint().offset(0, dy) != null) {
                    ChessComponent c = chessComponents[3][y + dy];
                    if (c instanceof PawnChessComponent && c.getChessColor() == ChessColor.BLACK) {
                        if (chessComponents[1][y + dy] instanceof EmptySlotComponent) {
                            int historyIndex = chessboard.getChessHistory().size() - 2;
                            String cb = chessboard.getChessHistory().get(historyIndex);
                            if (cb.substring(9 + y + dy, 9 + y + dy + 1).equals("P")) {
                                chessboardPointsList.add(first.getChessboardPoint().offset(-1, dy));
                                passPawnPoint = first.getChessboardPoint().offset(-1, dy);
                                chessPassingPawn = c;
                            }
                        }
                    }
                }
            }
            if (x == 4 && chessColor == ChessColor.BLACK) {
                if (first.getChessboardPoint().offset(0, dy) != null) {
                    ChessComponent c = chessComponents[4][y + dy];
                    if (c instanceof PawnChessComponent && c.getChessColor() == ChessColor.WHITE) {
                        if (chessComponents[6][y + dy] instanceof EmptySlotComponent) {
                            int historyIndex = chessboard.getChessHistory().size() - 2;
                            String cb = chessboard.getChessHistory().get(historyIndex);
                            if (cb.substring(54 + y + dy, 54 + y + dy + 1).equals("p")) {
                                chessboardPointsList.add(first.getChessboardPoint().offset(1, dy));
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

    /**
     * @return 判断是否被将军
     */
    private boolean isCheck() {
        ChessColor chessColor = first.getChessColor() == ChessColor.WHITE ? ChessColor.BLACK : ChessColor.WHITE;
        ChessComponent[][] chessComponents = chessboard.getChessComponents();
        List<ChessComponent> checkChessList = new ArrayList<>(); // 可以将军的棋子列表
        ChessComponent King = null;

        // 获得 King 王
        for (ChessComponent[] chessComponents2 : chessComponents)
            for (ChessComponent chess : chessComponents2)
                if (chess instanceof KingChessComponent && chess.getChessColor() == chessColor) {
                    King = chess;
                    break;
                }
        if (King == null) return true;
        ChessboardPoint kingPoint = King.getChessboardPoint();

        for (ChessComponent[] chessComponents2 : chessComponents)
            for (ChessComponent chess : chessComponents2)
                if (chess.getChessColor() == first.getChessColor()) {
                    if (isContains(chess.canMoveTo(chessComponents), kingPoint)) {
                        checkChessList.add(chess);
                    }
                }
        return checkChessList.size() > 0;
    }

    /**
     * @return 获得将军结果 2：将死   1：可活  0：未将
     */
    private int getCheckResult() {
        ChessColor chessColor = first.getChessColor() == ChessColor.WHITE ? ChessColor.BLACK : ChessColor.WHITE;
        List<ChessboardPoint> canEatPointsList = new ArrayList<>();
        ChessComponent King = null;
        ChessComponent[][] chessComponents = chessboard.getChessComponents();

        //找到 王 这个棋子
        for (ChessComponent[] chessComponents2 : chessComponents)
            for (ChessComponent chess : chessComponents2)
                if (chess instanceof KingChessComponent && chess.getChessColor() == chessColor) {
                    King = chess;
                    break;
                }
        if (King == null) return 2;

        List<ChessComponent> checkChessList = new ArrayList<>();

        //获得可将军的所有棋子 及 所有将军棋子能到达的位置
        for (ChessComponent[] chessComponents2 : chessComponents)
            for (ChessComponent chess : chessComponents2)
                if (chess.getChessColor() == first.getChessColor()) {
                    if (isContains(chess.canMoveTo(chessComponents), King.getChessboardPoint())) {
                        checkChessList.add(chess);
                        canEatPointsList.addAll(chess.canMoveTo(chessComponents));
                    }
                }

        if (checkChessList.size() == 0) // 没有将军
            return 0;

        if (checkChessList.size() > 1) { // 双将
            for (ChessboardPoint chessboardPoint : King.canMoveTo(chessComponents)) {
                // King can move
                if (!isContains(canEatPointsList, chessboardPoint))
                    return 1;
            }
            return 2;
        }

        // (checkChessList.size() == 1) 1个子将
        ChessComponent checkChess = checkChessList.get(0);

        // King can move
        for (ChessboardPoint chessboardPoint : King.canMoveTo(chessComponents)) {
            if (!isContains(canEatPointsList, chessboardPoint)) {
                return 1;
            }
        }
        // 判断能否吃子或垫子
        // can eat checkChess
        if (canEatCheckPiece(checkChess) ||canPlace(checkChess, King) ) {
            return 1;
        }
        return 2;
    }

    /**
     * @param chess2 可垫子位置的空白棋子  或是对方将军的棋子
     * @return 能否垫子 或 吃掉对方将军的棋子
     */
    private boolean canEatCheckPiece(ChessComponent chess2) {
        ChessColor chessColor = first.getChessColor() == ChessColor.WHITE ? ChessColor.BLACK : ChessColor.WHITE;
        ChessboardPoint checkPiecePoint = chess2.getChessboardPoint();
        ChessComponent[][] chessComponents = chessboard.getChessComponents();
        for (ChessComponent[] chessComponents2 : chessComponents)
            for (ChessComponent chess1 : chessComponents2) {
                if (chess1.getChessColor() == chessColor) {
                    if (isContains(chess1.canMoveTo(chessComponents), checkPiecePoint)) {
                        if (chess2 instanceof EmptySlotComponent) {
                            chessboard.swapChessComponents(chess1, chess2);
                            if (!isCheck()) {
                                chessboard.swapChessComponents(chess1, chess2);
                                return true;
                            }
                            chessboard.swapChessComponents(chess1, chess2);
                        } else {
                            chessboard.swapChessComponents(chess1, chess2);
                            if (!isCheck()) {
                                chessboard.loadGame(chessboard.getChessHistory());
                                return true;
                            }
                            chessboard.loadGame(chessboard.getChessHistory());
                        }
                    }
                }
            }
        return false;
    }


    /**
     * @param checkChess 将军的棋子
     * @param King       被将的王
     * @return 是否能够垫子
     */
    private boolean canPlace(ChessComponent checkChess, ChessComponent King) {
        ChessComponent[][] chessComponents = chessboard.getChessComponents();
        // can place 垫子  以下是获得可垫子的位置坐标列表
        if (checkChess instanceof RookChessComponent || checkChess instanceof QueenChessComponent
                || checkChess instanceof BishopChessComponent) {
            int x1 = checkChess.getChessboardPoint().getX(), y1 = checkChess.getChessboardPoint().getY();
            int x2 = King.getChessboardPoint().getX(), y2 = King.getChessboardPoint().getY();
            int xMin = Math.min(x1, x2), xMax = Math.max(x1, x2);
            int yMin = Math.min(y1, y2), yMax = Math.max(y1, y2);
            List<ChessboardPoint> canPlacePointsList = new ArrayList<>();
            //两字间若是横线，可垫子位置
            if (x1 == x2 && y1 != y2) {
                for (int i = yMin + 1; i < yMax; i++) {
                    canPlacePointsList.add(new ChessboardPoint(x1, i));
                }
            }
            //两字间若是竖线，可垫子位置
            if (y1 == y2 && x1 != x2) {
                for (int i = xMin + 1; i < xMax; i++) {
                    canPlacePointsList.add(new ChessboardPoint(i, y1));
                }
            }
            //两字间若是斜线，可垫子位置
            if (x1 != x2 && y1 != y2) {
                int dx = x1 < x2 ? 1 : -1;
                int dy = y1 < y2 ? 1 : -1;
                for (int i = x1 + dx, j = y1 + dy; i != x2 && j != y2; i = i + dx, j = j + dy) {
                    canPlacePointsList.add(new ChessboardPoint(i, j));
                }
            }
            //判断是否可将子移到垫子位置（即吃掉空棋子）
            for (ChessboardPoint chess2Point : canPlacePointsList) {
                int row = chess2Point.getX(), col = chess2Point.getY();
                ChessComponent chess2 = chessComponents[row][col];
                if (canEatCheckPiece(chess2)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param chessboardPointList 位置坐标列表
     * @param another             某一棋子坐标
     * @return 位置坐标列表是否包含某一棋子坐标
     */
    private boolean isContains(List<ChessboardPoint> chessboardPointList, ChessboardPoint another) {
        for (ChessboardPoint chessboardPoint : chessboardPointList)
            if (chessboardPoint.toString().equals(another.toString()))
                return true;
        return false;
    }

    /**
     * @param chessComponent 过路兵棋子
     * @return 是否是过路兵
     */
    private boolean isPassPawn(ChessComponent chessComponent) {
        if (passPawnPoint == null)
            return false;
        return chessComponent.getChessboardPoint().toString().equals(passPawnPoint.toString());
    }

}
