package model;

import controller.ClickController;
import view.ChessboardPoint;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 这个类表示国际象棋里面的
 */
public class PawnChessComponent extends ChessComponent {
    /**
     * 黑车和白车的图片，static使得其可以被所有车对象共享
     * <br>
     * FIXME: 需要特别注意此处加载的图片是没有背景底色的！！！
     */
    private static Image PAWN_WHITE;
    private static Image PAWN_BLACK;

    /**
     * 棋子对象自身的图片，是上面两种中的一种
     */
    private Image pawnImage;

    /**
     * 读取加载棋子的图片
     */
    public void loadResource() throws IOException {
        if (PAWN_WHITE == null) {
            PAWN_WHITE = ImageIO.read(new File("./images/pawn-white.png"));
        }

        if (PAWN_BLACK == null) {
            PAWN_BLACK = ImageIO.read(new File("./images/pawn-black.png"));
        }
    }


    /**
     * 在构造棋子对象的时候，调用此方法以根据颜色确定pawnImage的图片是哪一种
     *
     * @param color 棋子颜色
     */

    private void initiatePawnImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                pawnImage = PAWN_WHITE;
            } else if (color == ChessColor.BLACK) {
                pawnImage = PAWN_BLACK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PawnChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size) {
        super(chessboardPoint, location, color, listener, size);
        initiatePawnImage(color);
    }

    /**
     * 棋子的移动规则
     *
     * @param chessComponents 棋盘
     * @param destination     目标位置，如(0, 0), (0, 7)等等
     * @return 棋子移动的合法性
     */

    @Override
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        List<ChessboardPoint> lChessboardPoints = canMoveTo(chessComponents);
        for (ChessboardPoint chessboardPoint : lChessboardPoints) {
            if (chessboardPoint.toString().equals(destination.toString()))
                return true;
        }
        return false;
    }

    @Override
    public List<ChessboardPoint> canMoveTo(ChessComponent[][] chessComponents) {
        List<ChessboardPoint> lChessboardPoints = new ArrayList<>();
        ChessboardPoint source = getChessboardPoint();
        int row = source.getX(), col = source.getY();
        ChessColor chessColor = getChessColor();
        switch (chessColor) {
            case BLACK: {
                if (source.getX() == 1) { //First step
                    if (chessComponents[2][col] instanceof EmptySlotComponent) {
                        lChessboardPoints.add(source.offset(1, 0));
                        if (chessComponents[3][col] instanceof EmptySlotComponent)
                            lChessboardPoints.add(source.offset(2, 0));
                    }

                } else { //not first step
                    if (source.offset(1, 0) != null) {
                        if (chessComponents[row + 1][col] instanceof EmptySlotComponent) {
                            lChessboardPoints.add(source.offset(1, 0));
                        }
                    }
                }
                if (source.offset(1, 1) != null) { // chess can eat
                    if (chessComponents[row + 1][col + 1].getChessColor() == ChessColor.WHITE)
                        lChessboardPoints.add(source.offset(1, 1));
                }
                if (source.offset(1, -1) != null) {// chess can eat
                    if (chessComponents[row + 1][col - 1].getChessColor() == ChessColor.WHITE)
                        lChessboardPoints.add(source.offset(1, -1));
                }
                break;
                // Capture the passing way Pawn
                // Should Design here
            }
            case WHITE: {
                if (source.getX() == 6) {
                    if (chessComponents[5][col] instanceof EmptySlotComponent) {
                        lChessboardPoints.add(source.offset(-1, 0));
                        if (chessComponents[4][col] instanceof EmptySlotComponent)
                            lChessboardPoints.add(source.offset(-2, 0));
                    }

                } else {
                    if (source.offset(-1, 0) != null) {
                        if (chessComponents[row - 1][col] instanceof EmptySlotComponent) {
                            lChessboardPoints.add(source.offset(-1, 0));
                        }
                    }
                }
                if (source.offset(-1, -1) != null) {
                    if (chessComponents[row - 1][col - 1].getChessColor() == ChessColor.BLACK)
                        lChessboardPoints.add(source.offset(-1, -1));
                }
                if (source.offset(-1, 1) != null) {
                    if (chessComponents[row - 1][col + 1].getChessColor() == ChessColor.BLACK)
                        lChessboardPoints.add(source.offset(-1, 1));
                }
                break;
            }
            default: {
                break;
            }
        }
        lChessboardPoints.sort(Comparator.comparing(ChessboardPoint::getX).thenComparing(ChessboardPoint::getY));
        return lChessboardPoints;
    }

    /**
     * 注意这个方法，每当窗体受到了形状的变化，或者是通知要进行绘图的时候，就会调用这个方法进行画图。
     *
     * @param g 可以类比于画笔
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        g.drawImage(rookImage, 0, 0, getWidth() - 13, getHeight() - 20, this);
        g.drawImage(pawnImage, 0, 0, getWidth(), getHeight(), this);
        g.setColor(Color.BLACK);
        if (isSelected()) { // Highlights the model if selected.
            g.setColor(Color.RED);
            g.drawOval(0, 0, getWidth(), getHeight());
        }

    }

    @Override
    public String toString() {
        return chessColor == ChessColor.WHITE ? "p" : "P";
    }
}
