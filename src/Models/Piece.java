package Models;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Piece extends Circle{
    private int x=0;
    private int y=0;



    public Piece(Color color){

        this.setRadius(7);
        this.setFill(color);
        this.setStyle("-fx-blend-mode: src-over");

    }

    public void setX(int x){this.x=x; }
    public void setY(int y){this.y=y; }
    public int getx(){ return x;}
    public int gety(){ return y; }

}
