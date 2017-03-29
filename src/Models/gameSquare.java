package Models;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static Tools.PrintTools.println;

public class gameSquare extends Rectangle {
    private int x=0;
    private int y=0;

    public boolean hasPiece=false;

    public gameSquare(Color color){
        this.setWidth(18);
        this.setHeight(18);
        this.setFill(color);

        this.setOnMouseClicked((t)->{
            println(x+","+y);
        });


    }

    public void setX(int x){this.x=x; }
    public void setY(int y){this.y=y; }
    public int getx(){ return x;}
    public int gety(){ return y; }

}
