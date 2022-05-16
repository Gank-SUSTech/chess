package view;

import picture.picture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BeginFrame extends JFrame {
    Filename name = new Filename() ; // 所有的文件路径
    private final static int BeginWidth = 700 ;
    private final static int BeginHeight = 500 ;
    JComboBox <String> jMenuNUM = made( new String[] { "对战" ,"单人（白棋先手）" , "单人（黑棋后手）" , "双人"}) ;

    public BeginFrame ( String title ) {
        super( title ) ;
        this . setSize( BeginWidth , BeginHeight );
        this . setLocationRelativeTo( null ) ;
        this . setResizable( false ) ; // 不可调整大小

        JLabel jtitle = new JLabel("Chess" ) ;
        jtitle . setBounds( 500 , 75 , 100  , 70 );
        jtitle . setForeground( new Color (0x408ECC) ) ;
        jtitle . setFont( new Font( "Times New Roman", Font .BOLD + Font . ITALIC , 30 ) );
        JPanel jPanelBegin = new JPanelBegin( 0 , 0 , this . getWidth() , this . getHeight() ) ;
        jPanelBegin. setLayout( null );

        Image beginImage = Toolkit.getDefaultToolkit().getImage( new picture().getClass().getResource( name . pathnameBackGround [ 0 ] ) );
        JButton jButtonBegin = new JButton("开始游戏" ) ;
        jButtonBegin . setBackground( new Color ( 0x408ECC) ) ;
        jButtonBegin . setBounds( 500 , 390 , 85 , 30 );
        jButtonBegin . addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                begin () ;
            }
        });

        jMenuNUM . setBounds( 500 , 300 , 125 , 50 ) ;
        jMenuNUM . setFont( new Font( "黑体" , Font .  BOLD , 15 ) ) ;
        jMenuNUM . setBackground( new Color(0x408ECC));

        jPanelBegin . add( jMenuNUM ) ;
        jPanelBegin . add( jtitle ) ;
        jPanelBegin . add( jButtonBegin ) ;
        this .  add( jPanelBegin ) ;
        this . setDefaultCloseOperation( WindowConstants . EXIT_ON_CLOSE ) ;
    }

    public void begin () {
        int i = jMenuNUM . getSelectedIndex() ;
        System . out . println( i ) ;
        if ( i != 0 ) {
            this.dispose();
            JFrame jFrame = new ChessGameFrame(1000, 700, i );
            jFrame.setVisible(true);
        }

    }


    public JComboBox <String> made ( String [] name ) {
        JComboBox <String> jComboBox = new JComboBox<String>() ;
        for ( int i = 0 ; i < name . length ; i ++ ) {
            jComboBox . addItem( name [ i ] ) ;
        }
        return  jComboBox ;
    }
}
