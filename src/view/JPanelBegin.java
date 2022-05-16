package view;
import picture.picture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static view.Filename.pathnameBackGround;

public class JPanelBegin extends JPanel {
    private Image image ;
    private int fixX ;
    private int fixY ;
    private int width ;
    private int height ;
    private Image background ;
    static int i = 0 ;
    public JPanelBegin ( int fixX , int fixY , int width , int height ) {
        this . fixX = fixX ;
        this . fixY = fixY ;
        this . width = width ;
        this . height = height ;
        image =  Toolkit.getDefaultToolkit().getImage( new picture().getClass().getResource(  pathnameBackGround [ 0 ] ) );
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g . drawImage(  image , 0 , 0 , getWidth() , getHeight() ,  this) ;
        System . out . println(i ++  ) ;
    }
    public static void main ( String [] args ) {

    }

    public void setView ( Image image ) {
        background = image ;
    }
    public Image getImage () { return background ; }
}
