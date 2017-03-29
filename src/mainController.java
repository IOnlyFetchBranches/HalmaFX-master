


import java.net.URL;
import java.util.ResourceBundle;

import Models.Piece;
import Models.gameSquare;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class mainController implements Initializable {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private GridPane board;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        assert board != null : "fx:id=\"board\" was not injected: check your FXML file 'main.fxml'.";

        colorBoard();
        newBoard();

        //start Piece Check;

        Task<Integer> pCheck;












    }

    private void colorBoard(){
        for(int row=0;row<16;row++){

            if(row%2==0) {
                for (int col = 0; col < 16; col++) {
                    if (col % 2 == 0) {
                        gameSquare sqr=new gameSquare(Color.GRAY);
                        sqr.setX(col);
                        sqr.setY(row);

                        sqr.setStyle("-fx-blend-mode: darken");

                        board.add(sqr,col,row);
                    }
                    else{
                        gameSquare sqr=new gameSquare(Color.PEACHPUFF);
                        sqr.setX(col);
                        sqr.setY(row);

                        sqr.setStyle("-fx-blend-mode: darken");

                        board.add(sqr,col,row);
                    }


                }
            }

            if(row%2!=0) {
                for (int col = 0; col < 16; col++) {
                    if (col % 2 == 0) {
                        gameSquare sqr=new gameSquare(Color.PEACHPUFF);
                        sqr.setX(col);
                        sqr.setY(row);

                        sqr.setStyle("-fx-blend-mode: darken");

                        board.add(sqr,col,row);
                    }
                    else{
                        gameSquare sqr=new gameSquare(Color.GRAY);
                        sqr.setX(col);
                        sqr.setY(row);

                        sqr.setStyle("-fx-blend-mode: darken");

                        board.add(sqr,col,row);
                    }


                }
            }




        }
    }
    private void newBoard(){

        for(Point2D p:mainApp.player1Start){
            board.add(new Piece(Color.CRIMSON),(int)p.getX(),(int)p.getY());
        }
        for(Point2D p:mainApp.player2Start){
            board.add(new Piece(Color.FORESTGREEN),(int)p.getX(),(int)p.getY());
        }



    }
}