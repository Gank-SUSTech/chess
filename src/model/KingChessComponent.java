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
 * 这个类表示国际象棋里面的 King
 */
public class KingChessComponent extends ChessComponent {
    /**
     * 黑车和白车的图片，static使得其可以被所有车对象共享
     * <br>
     * FIXME: 需要特别注意此处加载的图片是没有背景底色的！！！
     */
    private static Image KING_WHITE;
    private static Image KING_BLACK;

    /**
     * 棋子对象自身的图片，是上面两种中的一种
     */
    private Image kingImage;

    /**
     * 读取加载棋子的图片
     *
     * @throws IOException
     */
    public void loadResource() throws IOException {
        if (KING_WHITE == null) {
            KING_WHITE = ImageIO.read(new File("./images/king-white.png"));
        }

        if (KING_BLACK == null) {
            KING_BLACK = ImageIO.read(new File("./images/king-black.png"));
        }
    }

    /**
     * 在构造棋子对象的时候，调用此方法以根据颜色确定kingImage的图片是哪一种
     *
     * @param color 棋子颜色
     */

    private void initiateKingImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                kingImage = KING_WHITE;
            } else if (color == ChessColor.BLACK) {
                kingImage = KING_BLACK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public KingChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color,
                              ClickController listener, int size) {
        super(chessboardPoint, location, color, listener, size);
        initiateKingImage(color);
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
        List<ChessboardPoint> chessboardPointsList = canMoveTo(chessComponents);
        for (ChessboardPoint chessboardPoint : chessboardPointsList) {
            if (chessboardPoint.toString().equals(destination.toString()))
                return true;
        }
        return false;
    }

    @Override
    public List<ChessboardPoint> canMoveTo(ChessComponent[][] chessComponents) {
        List<ChessboardPoint> chessboardPointsList = new ArrayList<>();
        ChessboardPoint source = getChessboardPoint();
        int row = source.getX(), col = source.getY();

        int[][] dxdyArray = {{0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 0}, {1, 0}, {-1, 1}, {-1, -1}};
        for (int[] dxdy : dxdyArray) {
            int dx = dxdy[0], dy = dxdy[1];
            if (source.offset(dx, dy) != null) {
                if (chessComponents[row + dx][col + dy].getChessColor() != getChessColor()
                        || chessComponents[row + dx][col + dy] instanceof EmptySlotComponent)
                    chessboardPointsList.add(source.offset(dx, dy));
            }
        }

        chessboardPointsList.sort(Comparator.comparing(ChessboardPoint::getX).thenComparing(ChessboardPoint::getY));
        return chessboardPointsList;
    }

    /**
     * 注意这个方法，每当窗体受到了形状的变化，或者是通知要进行绘图的时候，就会调用这个方法进行画图。
     *
     * @param g 可以类比于画笔
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // g.drawImage(rookImage, 0, 0, getWidth() - 13, getHeight() - 20, this);
        g.drawImage(kingImage, 0, 0, getWidth(), getHeight(), this);
        g.setColor(Color.BLACK);
        if (isSelected()) { // Highlights the model if selected.
            g.setColor(Color.RED);
            g.drawOval(0, 0, getWidth(), getHeight());
        }

    }

    @Override
    public String toString() {
        return chessColor == ChessColor.WHITE ? "k" : "K";
    }
}
