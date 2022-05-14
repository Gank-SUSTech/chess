package model;

import view.ChessboardPoint;
import controller.ClickController;

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
public class QueenChessComponent extends ChessComponent {
    /**
     * 黑车和白车的图片，static使得其可以被所有车对象共享
     * <br>
     * FIXME: 需要特别注意此处加载的图片是没有背景底色的！！！
     */
    private static Image QUEEN_WHITE;
    private static Image QUEEN_BLACK;

    /**
     * 棋子对象自身的图片，是上面两种中的一种
     */
    private Image queenImage;

    /**
     * 读取加载棋子的图片
     *
     * @throws IOException
     */
    public void loadResource() throws IOException {
        if (QUEEN_WHITE == null) {
            QUEEN_WHITE = ImageIO.read(new File("./images/queen-white.png"));
        }

        if (QUEEN_BLACK == null) {
            QUEEN_BLACK = ImageIO.read(new File("./images/queen-black.png"));
        }
    }


    /**
     * 在构造棋子对象的时候，调用此方法以根据颜色确定queenImage的图片是哪一种
     *
     * @param color 棋子颜色
     */

    private void initiateQueenImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                queenImage = QUEEN_WHITE;
            } else if (color == ChessColor.BLACK) {
                queenImage = QUEEN_BLACK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public QueenChessComponent (ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size) {
        super(chessboardPoint, location, color, listener, size);
        initiateQueenImage(color);
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
        List<ChessboardPoint> lChessboardPoints = new ArrayList<>();
        ChessboardPoint source = getChessboardPoint();
        ChessColor chessColor = getChessColor();
        int row = source.getX();
        int col = source.getY();
        for (int i = 1; i + row < 8; i++) {
            if (source.offset(i, 0) != null) {
                if (chessComponents[row + i][col] instanceof EmptySlotComponent) {
                    lChessboardPoints.add(source.offset(i, 0));
                } else {
                    if (chessComponents[row + i][col].getChessColor() != chessColor) {
                        lChessboardPoints.add(source.offset(i, 0));
                    }
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = -1; row + i >= 0; i--) {
            if (source.offset(i, 0) != null) {
                if (chessComponents[row + i][col] instanceof EmptySlotComponent) {
                    lChessboardPoints.add(source.offset(i, 0));
                } else {
                    if (chessComponents[row + i][col].getChessColor() != chessColor) {
                        lChessboardPoints.add(source.offset(i, 0));
                    }
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = 1; col + i < 8; i++) {
            if (source.offset(0, i) != null) {
                if (chessComponents[row][col + i] instanceof EmptySlotComponent) {
                    lChessboardPoints.add(source.offset(0, i));
                } else {
                    if (chessComponents[row][col + i].getChessColor() != chessColor) {
                        lChessboardPoints.add(source.offset(0, i));
                    }
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = -1; col + i >= 0; i--) {
            if (source.offset(0, i) != null) {
                if (chessComponents[row][col + i] instanceof EmptySlotComponent) {
                    lChessboardPoints.add(source.offset(0, i));
                } else {
                    if (chessComponents[row][col + i].getChessColor() != chessColor) {
                        lChessboardPoints.add(source.offset(0, i));
                    }
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = 1, j = 1; row + i < 8 && col + j < 8; i++, j++) {
            if (source.offset(i, j) != null) {
                if (chessComponents[row + i][col + j] instanceof EmptySlotComponent) {
                    lChessboardPoints.add(source.offset(i, j));
                } else {
                    if (chessComponents[row + i][col + j].getChessColor() == getChessColor())
                        break;
                    if (chessComponents[row + i][col + j].getChessColor() != getChessColor()) {
                        lChessboardPoints.add(source.offset(i, j));
                        break;
                    }
                }
            }
        }
        for (int i = -1, j = 1; row + i >= 0 && col + j < 8; i--, j++) {
            if (source.offset(i, j) != null) {
                if (chessComponents[row + i][col + j] instanceof EmptySlotComponent) {
                    lChessboardPoints.add(source.offset(i, j));
                } else {
                    if (chessComponents[row + i][col + j].getChessColor() == getChessColor())
                        break;
                    if (chessComponents[row + i][col + j].getChessColor() != getChessColor()) {
                        lChessboardPoints.add(source.offset(i, j));
                        break;
                    }
                }
            }
        }

        for (int i = -1, j = -1; row + i >= 0 && col + j >= 0; i--, j--) {
            if (source.offset(i, j) != null) {
                if (chessComponents[row + i][col + j] instanceof EmptySlotComponent) {
                    lChessboardPoints.add(source.offset(i, j));
                } else {
                    if (chessComponents[row + i][col + j].getChessColor() == getChessColor())
                        break;
                    if (chessComponents[row + i][col + j].getChessColor() != getChessColor()) {
                        lChessboardPoints.add(source.offset(i, j));
                        break;
                    }
                }
            }
        }

        for (int i = 1, j = -1; row + i < 8 && col + j >= 0; i++, j--) {
            if (source.offset(i, j) != null) {
                if (chessComponents[row + i][col + j] instanceof EmptySlotComponent) {
                    lChessboardPoints.add(source.offset(i, j));
                } else {
                    if (chessComponents[row + i][col + j].getChessColor() == getChessColor())
                        break;
                    if (chessComponents[row + i][col + j].getChessColor() != getChessColor()) {
                        lChessboardPoints.add(source.offset(i, j));
                        break;
                    }
                }
            }
        }
        lChessboardPoints.sort(Comparator.comparing(ChessboardPoint::getX).thenComparing(ChessboardPoint::getY));
        for (ChessboardPoint chessboardPoint : lChessboardPoints) {
            if (chessboardPoint.getX()==destination.getX() && chessboardPoint.getY()==destination.getY())
            return true;
        }
        return false;
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
        g.drawImage(queenImage, 0, 0, getWidth() , getHeight(), this);
        g.setColor(Color.BLACK);
        if (isSelected()) { // Highlights the model if selected.
            g.setColor(Color.RED);
            g.drawOval(0, 0, getWidth() , getHeight());
        }
    }

    @Override
    public String toString() {
        return  chessColor == ChessColor.WHITE ?"q":"Q";
    }
}
