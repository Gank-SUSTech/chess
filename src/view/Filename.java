package view;

public class Filename {
    public static final String pathnameBackGround [] = new String[ 3 ] ;
    public static final String chess [] = new String[ 12 ] ;
    public static final String ui [] = new String[ 5 ] ;
    public static final String music [] = new String[ 2 ] ;
    public Filename () {
        for ( int i = 0 ; i < pathnameBackGround.length ; i ++ ) {
            pathnameBackGround [ i ] = String . format( "0%d.jpeg" , i ) ;
        }
        ui [ 0 ] = "开始1.png" ;
        ui [ 1 ] = "撤销.png" ;
        ui [ 2 ] = "反撤销.png" ;
        music [ 0 ] = "GeorgeShearing-WinterWonderland.mp3";
        music [ 1 ] = "JoshStewart-TheBirthdaySong.mp3" ;
    }
}
