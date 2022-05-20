package view;

import controller.ClickController;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 这个类表示面板上的棋盘组件对象
 */
public class Chessboard extends JComponent {
    /**
     * CHESSBOARD_SIZE： 棋盘是8 * 8的
     * <br>
     * BACKGROUND_COLORS: 棋盘的两种背景颜色
     * <br>
     * chessListener：棋盘监听棋子的行动
     * <br>
     * chessboard: 表示8 * 8的棋盘
     * <br>
     * currentColor: 当前行棋方
     */
    private static final int CHESSBOARD_SIZE = 8;

    private final ChessComponent[][] chessComponents = new ChessComponent[CHESSBOARD_SIZE][CHESSBOARD_SIZE];
    // private ChessColor currentColor = ChessColor.BLACK;
    private ChessColor currentColor = ChessColor.WHITE;
    // all chessComponents in this chessboard are shared only one model controller
    private final ClickController clickController = new ClickController(this);
    private final int CHESS_SIZE;

    private List<String> chessHistory = new ArrayList<>();
    private boolean gameOver = false;


    // private Thread worker;
    public Chessboard(int width, int height) {
        setLayout(null); // Use absolute layout.
        setSize(width, height);
        CHESS_SIZE = width / 8;
        initBoard();
        addHistory();
    }
    public void setGameOver(boolean gameOver){
        this.gameOver = gameOver;
    }
    public boolean getGameOver(){
        return gameOver;
    }


    public ChessComponent[][] getChessComponents() {
        return chessComponents;
    }

    public int getCHESS_SIZE() {
        return CHESS_SIZE;
    }

    public ChessColor getCurrentColor() {
        return currentColor;
    }

    public void putChessOnBoard(ChessComponent chessComponent) {
        int row = chessComponent.getChessboardPoint().getX(), col = chessComponent.getChessboardPoint().getY();

        if (chessComponents[row][col] != null) {
            remove(chessComponents[row][col]);
        }
        add(chessComponents[row][col] = chessComponent);
    }

    public void swapChessComponents(ChessComponent chess1, ChessComponent chess2) {
        // Note that chess1 has higher priority, 'destroys' chess2 if exists.
        if (!(chess2 instanceof EmptySlotComponent)) {
            remove(chess2);
            add(chess2 = new EmptySlotComponent(chess2.getChessboardPoint(), chess2.getLocation(), clickController,
                    CHESS_SIZE));
        }
        chess1.swapLocation(chess2);
        int row1 = chess1.getChessboardPoint().getX(), col1 = chess1.getChessboardPoint().getY();
        chessComponents[row1][col1] = chess1;
        int row2 = chess2.getChessboardPoint().getX(), col2 = chess2.getChessboardPoint().getY();
        chessComponents[row2][col2] = chess2;
        chess1.repaint();
        chess2.repaint();
    }

    public void initiateEmptyChessboard() {
        for (int i = 0; i < chessComponents.length; i++) {
            for (int j = 0; j < chessComponents[i].length; j++) {
                putChessOnBoard(new EmptySlotComponent(new ChessboardPoint(i, j), calculatePoint(i, j), clickController,
                        CHESS_SIZE));
            }
        }
    }

    public void swapColor() {
        currentColor = currentColor == ChessColor.BLACK ? ChessColor.WHITE : ChessColor.BLACK;
    }

    private void initPawnOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new PawnChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col),
                color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }

    private void initRookOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new RookChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col),
                color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }

    private void initKnightOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new KnightChessComponent(new ChessboardPoint(row, col),
                calculatePoint(row, col), color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }

    private void initBishopOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new BishopChessComponent(new ChessboardPoint(row, col),
                calculatePoint(row, col), color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }

    private void initKingOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new KingChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col),
                color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }

    private void initQueenOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new QueenChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col),
                color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    private Point calculatePoint(int row, int col) {
        return new Point(col * CHESS_SIZE, row * CHESS_SIZE);
    }

    public void loadGame(List<String> chessDatas) {
        chessHistory = chessDatas;
        if (chessDatas.size() >= 1) {
            String str = chessDatas.get(chessDatas.size() - 1);
            String[] strings = str.split("/");
            List<String> chessData = new ArrayList<>();
            Collections.addAll(chessData, strings);
            ChessColor black = ChessColor.BLACK, white = ChessColor.WHITE;
            for (int i = 0; i < 8; i++) {
                String s = chessData.get(i);
                for (int j = 0; j < s.length(); j++) {
                    char c = s.charAt(j);
                    switch (c) {
                        case 'R':
                            initRookOnBoard(i, j, black);
                            break;
                        case 'r':
                            initRookOnBoard(i, j, white);
                            break;
                        case 'N':
                            initKnightOnBoard(i, j, black);
                            break;
                        case 'n':
                            initKnightOnBoard(i, j, white);
                            break;
                        case 'B':
                            initBishopOnBoard(i, j, black);
                            break;
                        case 'b':
                            initBishopOnBoard(i, j, white);
                            break;
                        case 'Q':
                            initQueenOnBoard(i, j, black);
                            break;
                        case 'q':
                            initQueenOnBoard(i, j, white);
                            break;
                        case 'K':
                            initKingOnBoard(i, j, black);
                            break;
                        case 'k':
                            initKingOnBoard(i, j, white);
                            break;
                        case 'P':
                            initPawnOnBoard(i, j, black);
                            break;
                        case 'p':
                            initPawnOnBoard(i, j, white);
                            break;
                        case '_':
                            putChessOnBoard(new EmptySlotComponent(new ChessboardPoint(i, j), calculatePoint(i, j),
                                    clickController, CHESS_SIZE));
                            break;
                        default:
                            break;
                    }
                }
            }
            String player = chessData.get(8);
            if (player.equals("w"))
                currentColor = white;
            if (player.equals("b"))
                currentColor = black;
            this.repaint();
            setStatus();
        }
    }

    public void clear() {
        for (ChessComponent[] chessComponent : chessComponents)
            for (ChessComponent component : chessComponent)
                remove(component);
        this.repaint();
        initBoard();
        currentColor = ChessColor.WHITE;
        setStatus();
        while (chessHistory.size() > 0)
            chessHistory.remove(chessHistory.size() - 1);
        addHistory();
    }

    public void setStatus() {
        JLabel x = (JLabel) this.getParent().getComponent(1);
        String str = currentColor == ChessColor.WHITE ? "Player: White" : "Player: Black";
        x.setText(str);
    }

    public void initBoard() {
        initiateEmptyChessboard();
        ChessColor black = ChessColor.BLACK, white = ChessColor.WHITE;
        for (int i = 0; i < 8; i++) {
            initPawnOnBoard(1, i, black);
            initPawnOnBoard(6, i, white);
        }
        initRookOnBoard(0, 0, black);
        initRookOnBoard(0, 7, black);
        initKnightOnBoard(0, 1, black);
        initKnightOnBoard(0, 6, black);
        initBishopOnBoard(0, 2, black);
        initBishopOnBoard(0, 5, black);

        initRookOnBoard(7, 0, white);
        initRookOnBoard(7, 7, white);
        initKnightOnBoard(7, 1, white);
        initKnightOnBoard(7, 6, white);
        initBishopOnBoard(7, 2, white);
        initBishopOnBoard(7, 5, white);

        initKingOnBoard(0, 4, black);
        initQueenOnBoard(0, 3, black);
        initKingOnBoard(7, 4, white);
        initQueenOnBoard(7, 3, white);

    }

    public List<String> getChessHistory() {
        return chessHistory;
    }

    public void addHistory() {
        String curUser = getCurrentColor() == ChessColor.WHITE ? "w" : "b";
        ChessComponent[][] component = getChessComponents();
        StringBuilder buf = new StringBuilder();
        for (ChessComponent[] chessComponents : component) {
            for (ChessComponent chessComponent : chessComponents) {
                buf.append(chessComponent.toString());
            }
            buf.append("/");
        }
        buf.append(curUser);
        chessHistory.add(buf.toString());
    }

    public void removeHistory() {
        if (chessHistory.size() > 0) {
            chessHistory.remove(chessHistory.size() - 1);
        }
    }

    public void clearHistory() {
        chessHistory.clear();
    }

}
