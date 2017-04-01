package Models;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;


public class Piece extends Circle implements Cloneable{
    private int x=0;
    private int y=0;
    public int team;
    private Paint orgFill;

    public boolean isSelected=false;


    public Piece(Color color,int team){

        this.setRadius(7);
        this.setFill(color);
        this.setStyle("-fx-blend-mode: src-over");
        this.team=team;



    }
    public void select(){
        if(!isSelected){
            orgFill=this.getFill();
            this.setFill(Color.WHITE);
            isSelected=true;
        }
        else{
            this.setFill(orgFill);
            isSelected=false;
        }
    }

    public void setX(int x){this.x=x; }
    public void setY(int y){this.y=y; }
    public int getx(){ return x;}
    public int gety(){ return y; }

}
