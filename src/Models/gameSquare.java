package Models;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import static Tools.PrintTools.println;

public class gameSquare extends Rectangle {
    private int x=0;
    private int y=0;

    public  boolean isLegal=false;
    public boolean isSelected=false;

    private Paint orgFill;

    public int team=0;

    public gameSquare(Color color){
        this.setWidth(18);
        this.setHeight(18);
        this.setFill(color);
        this.orgFill=this.getFill();
        this.setOnMouseClicked((t)->{
            println(x+","+y);
        });


    }

    public void setX(int x){this.x=x; }
    public void setY(int y){this.y=y; }
    public int getx(){ return x;}
    public int gety(){ return y; }

    public void makeLegal(){
        if(!isLegal){
            isLegal=true;
            this.setFill(Color.GREEN);
        }
        else{
            isLegal=false;
            this.setFill(orgFill);
        }
    }
    public void select(){
        if(!isSelected){
            isSelected=true;
            this.setFill(Color.WHITE);
        }
        else{
            isSelected=false;
            this.setFill(orgFill);

        }
    }

}
