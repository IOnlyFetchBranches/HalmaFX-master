import Models.Piece;
import Models.gameSquare;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static Tools.PrintTools.errprint;
import static Tools.PrintTools.println;
import static java.lang.Thread.currentThread;
import static javafx.scene.layout.GridPane.getColumnIndex;
import static javafx.scene.layout.GridPane.getRowIndex;


public class mainController implements Initializable {

    private int[][] pieceLocation={ //very Important
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
    };
    private ArrayList<Point2D> legalMoves=new ArrayList<>();





    private static Point2D selectedPiece;
    private static Point2D selectedSpace;


    private static boolean spaceSelected=false;
    private static boolean pieceSelected=false;
    private static boolean extWindowOpen=false;
    private static boolean busy=false;
    private static boolean disablePending=false;
    private static boolean jumping=false;
    private static boolean playerTurn=false;
    private static boolean isTurnAuto=true;

    private static boolean musicToggle=false;
    private static boolean choiceConfirmToggle=true;

    private static List<Point2D> team1WinZone=new ArrayList<>();
    private static List<Point2D> team2WinZone=new ArrayList<>();
    private static Thread checker;




    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    @FXML
    private CheckBox musicCheck;
    @FXML
    private CheckBox disableConfirmCheck;
    @FXML
    private CheckBox autoTurnCheck;

    @FXML
    private GridPane board;

    @FXML
    private Button resetButton;
    @FXML
    private Button aboutButton;
    @FXML
    private Button helpButton;

    @FXML
    private Label turnText;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {


        //roll initial turn
        rollTurn();

        //color and populate board
        colorBoard();
        newBoard();





        //define checkboxes
        disableConfirmCheck.setOnAction((t)->{
            choiceConfirmToggle=!choiceConfirmToggle;
        });
        autoTurnCheck.setOnAction((t)->{
            if(isTurnAuto){
                rollTurn(); //roll if this is disabled
            }
            isTurnAuto=!isTurnAuto;

            if(!isTurnAuto) {

                turnText.setText("Manual Mode"); // set text to manual
            }
        });

        //define Reset and about buttons;
        resetButton.setOnAction((t)->{
            Platform.runLater(()->{

                if(!busy){
                    busy=true;
                    reset();

                    busy=false;
                }
            });
        });
        //finally help button
        helpButton.setOnAction((t)->{
            if(!extWindowOpen) {
                Stage helpStage = new Stage();

                StackPane root=new StackPane();

                TextArea help=new TextArea();
                help.setWrapText(true);
                help.setEditable(false);
                help.setText("RULES OF THE GAME! (Source: Wikipedia) \n\n" +
                        "Players randomly determine who will move first.\n" +
                        "Pieces can move in eight possible directions (orthogonally and diagonally).\n" +
                        "Each player's turn consists of moving a single piece of one's own color in one of the following plays:\n" +
                        "One move to an empty square:\n" +
                        "Place the piece in an empty adjacent square.\n" +
                        "This move ends the play.\n" +
                        "One or more jumps over adjacent pieces:\n" +
                        "An adjacent piece of any color can be jumped if there is an adjacent empty square on the directly opposite side of that piece.\n" +
                        "Place the piece in the empty square on the opposite side of the jumped piece.\n" +
                        "The piece that was jumped over is unaffected and remains on the board.\n" +
                        "After any jump, one may make further jumps using the same piece, or end the play.\n" +
                        "Once a piece has reached the opposing camp, a play cannot result in that piece leaving the camp.\n" +
                        "If the current play results in having every square of the opposing camp occupied by one's own pieces, the acting player wins. Otherwise, play proceeds clockwise around the board.");


                root.getChildren().add(help);

                Scene scene=new Scene(root);
                helpStage.setTitle("Help");
                helpStage.setResizable(false);
                helpStage.setAlwaysOnTop(true);

                helpStage.setScene(scene);
                helpStage.show();
                helpStage.setOnCloseRequest((window)->extWindowOpen=false);



            }
        });


        aboutButton.setOnAction((t)->{
            if(!extWindowOpen) {
                Stage aboutStage = new Stage();

                StackPane root=new StackPane();
                root.getChildren().add(new javafx.scene.control.Label("A Halma JavaFX Applet \nCreated By Demarcus Joachim \n Music was too :) www.soundcloud.com/dreamentact \n " +
                        "Image was a free wallpaper I did not make it \n The core Code was made in about ten hours, so bugs may exist, although I will be patching these in the future... \n " +
                        "Likely, the most obvious bugs will be gone but I'm sure with a bit of creativity there are a few left. \n\n Have Fun!! \n\n" +
                        "Note: I've opted to use JOptionPane aka Swing for all my dialogs, there's no other one line call and wait dialog FX solution out side of creating a stage or" +
                        "using an External Library such as FXExperience!"));

                Scene scene=new Scene(root);
                aboutStage.setTitle("About");

                aboutStage.setScene(scene);
                aboutStage.setAlwaysOnTop(true);
                aboutStage.setResizable(false);
                aboutStage.show();
                aboutStage.setOnCloseRequest((window)->extWindowOpen=false);



            }


        });



        //Define Piece Checker Task;

        Task<Integer> pCheck = new Task<Integer>() {
            @SuppressWarnings("Duplicates")
            @Override
            protected Integer call() throws Exception {
                int i=0;
                boolean isResetting;

                while (currentThread().isAlive()) {



                    Tools.PrintTools.print("Update "+ ++i +"\n");
                    resetLoc(); //clear grid
                    for (Node n : board.getChildren()) {
                        if (n instanceof Piece) {
                            Point2D p = new Point2D(((Piece) n).getx(), ((Piece) n).gety());
                            // Tools.PrintTools.print(p.toString()+ " ");
                            pieceLocation[(int) p.getX()][(int) p.getY()] = 1;

                        }
                    }
                    int player1WinZoneCount = 0;
                    int player2WinZoneCount = 0;

                    for (Node n : board.getChildren()) {

                        if (n instanceof Piece) {

                            Point2D p = new Point2D(((Piece) n).getx(), ((Piece) n).gety());
                            //check for team 1 win/zone pieces
                            if(((Piece) n).team==1) {
                                for (Point2D point : mainApp.player2Start) { //check against enemy zone
                                    {
                                        if ((((Piece) n).getx() == point.getX()) && (((Piece) n).gety() == point.getY())) { //piece loc matches zone loc
                                            player1WinZoneCount=player1WinZoneCount+1;
                                            println(player1WinZoneCount+"");
                                            //((Piece) n).select(); //for debug purposes, highlight whatever passes the condition

                                            //disable movement
                                            if(!jumping){
                                            n.setOnMouseClicked((t)->{
                                                Toolkit.getDefaultToolkit().beep();
                                            });
                                            ((Piece) n).setFill(Color.FORESTGREEN);
                                        }
                                        else{
                                                disablePending=true;
                                            }
                                        }}}


                                    if (player1WinZoneCount == mainApp.player1Start.size()) {
                                        println("win");

                                        JOptionPane.showMessageDialog(null, "Player 1 Wins!","We Have A Winner!",JOptionPane.INFORMATION_MESSAGE);
                                        int choice=JOptionPane.showConfirmDialog(null,"Would you like to play again?","Start Over?",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                                        if(choice==JOptionPane.YES_OPTION){
                                            Platform.runLater(()->reset());
                                            Thread.sleep(100);
                                            break;
                                        }
                                        else{
                                            //disable interaction;
                                            disable();
                                            Thread.currentThread().wait();

                                        }

                                    }
                            }
                            // check for team 2 win/zone pieces
                            if(((Piece) n).team==2) {
                                for (Point2D point : mainApp.player1Start) { //check against enemy zone
                                    {
                                        if ((((Piece) n).getx() == point.getX()) && (((Piece) n).gety() == point.getY())) { //piece loc matches zone loc
                                            player1WinZoneCount=player1WinZoneCount+1;
                                            println(player2WinZoneCount+"");
                                            //((Piece) n).select(); //for debug purposes, highlight whatever passes the condition

                                            //disable movement
                                            n.setOnMouseClicked((t)-> Toolkit.getDefaultToolkit().beep());
                                            ((Piece) n).setFill(Color.FORESTGREEN);
                                        }}}

                                if (player2WinZoneCount == mainApp.player1Start.size()) {
                                    println("win");

                                    JOptionPane.showMessageDialog(null, "Player 2 Wins!","We Have A Winner!",JOptionPane.INFORMATION_MESSAGE);
                                    int choice=JOptionPane.showConfirmDialog(null,"Would you like to play again?","Start Over?",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                                    if(choice==JOptionPane.YES_OPTION){
                                        Platform.runLater(()->reset());
                                        Thread.sleep(100);
                                        break;
                                    }
                                    else{
                                        //disable interaction;
                                        disable();
                                        Thread.currentThread().wait();

                                    }

                                }
                            }
                        }

                    }






                    Tools.PrintTools.print("\n");
                    Tools.PrintTools.print2DArrayInt(pieceLocation);
                    Thread.sleep(25); //change this lower when done debugging;

                }
                return null;
            }
        };









        checker=new Thread(pCheck);
        checker.setDaemon(true);
        checker.start();



        //music check code;
        musicCheck.setOnAction((t)->{
            if (musicToggle){
                mainApp.player.stop();
                musicToggle=false;
            }
            else {
                mainApp.player.play();
                musicToggle = true;
            }

        });






    }






    private void resetLoc(){
        int[][] org={
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        };

        //I'll keep the redundancy here, just for my comfort
        pieceLocation=org;




    }


    @SuppressWarnings("Duplicates")
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
    @SuppressWarnings("Duplicates")
    private void newBoard(){

        for(Point2D p:mainApp.player1Start){
            Piece gp=new Piece(Color.CRIMSON,1);
            gp.team=1;
            gp.setOnMouseClicked((t)-> {
                if (gp.team == 1 || !isTurnAuto) {
                    if (!playerTurn || !isTurnAuto) {//if it is their turn, isTurnAuto provides a bypass for players who want to decide for themselves

                        if (!pieceSelected) {
                            pieceSelected = true;
                            gp.select();

                            selectedPiece = new Point2D((double) gp.getx(), (double) gp.gety());

                            calculateMoves(gp, false);

                            //JOptionPane.showMessageDialog(null,"Selected Point -> "+ selectedPiece.toString()); //debug reasons
                        } else if (gp.isSelected && !spaceSelected) {
                            gp.select();

                            //clear up vars that need to be reset

                            selectedPiece = null;
                            pieceSelected = false;
                            legalMoves.clear();

                            // make sqr's illegal
                            for (Node n : board.getChildren()) {
                                if (n instanceof gameSquare) {
                                    if (((gameSquare) n).isLegal) {
                                        ((gameSquare) n).makeLegal();
                                    }
                                }
                            }


                        } else if (pieceSelected) {
                            JOptionPane.showMessageDialog(null, "You Already Have A Piece Selected! Choose A Place to Move or Deselect!");
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(null,"It is not your Turn!!!","Not Your Turn",JOptionPane.ERROR_MESSAGE);
                    }
                }
                if (gp.team == 2 && isTurnAuto) {
                    if (playerTurn && isTurnAuto) {//if it is their turn, isTurnAuto provides a bypass, in this case it voids this pathway if manual is triggered

                        if (!pieceSelected) {
                            pieceSelected = true;
                            gp.select();

                            selectedPiece = new Point2D((double) gp.getx(), (double) gp.gety());

                            calculateMoves(gp, false);

                            //JOptionPane.showMessageDialog(null,"Selected Point -> "+ selectedPiece.toString()); //debug reasons
                        } else if (gp.isSelected && !spaceSelected) {
                            gp.select();

                            //confirm cancel move here later

                            selectedPiece = null;
                            pieceSelected = false;
                            legalMoves.clear();

                            for (Node n : board.getChildren()) {
                                if (n instanceof gameSquare) {
                                    if (((gameSquare) n).isLegal) {
                                        ((gameSquare) n).makeLegal();
                                    }
                                }
                            }


                        } else if (pieceSelected) {
                            JOptionPane.showMessageDialog(null, "You Already Have A Piece Selected! Choose A Place to Move or Deselect!");
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(null,"It is not your Turn!!!","Not Your Turn",JOptionPane.ERROR_MESSAGE);
                    }
                }


            });




            gp.setX((int)p.getX());
            gp.setY((int)p.getY());
            board.add(gp,(int)p.getX(),(int)p.getY());

            if(pieceLocation[(int)p.getX()][(int)p.getY()] !=1){
                pieceLocation[(int)p.getX()][(int)p.getY()]=1;

            }



        }
        for(Point2D p:mainApp.player2Start){
            Piece gp=new Piece(Color.BLACK,2);
            gp.team=2;

            //just a copy of the same code for player 1
            gp.setOnMouseClicked((t)-> {
                if (gp.team == 1 || !isTurnAuto) {
                    if (!playerTurn || !isTurnAuto) {//if it is their turn, isTurnAuto provides a bypass for players who want to decide for themselves

                        if (!pieceSelected) {
                            pieceSelected = true;
                            gp.select();

                            selectedPiece = new Point2D((double) gp.getx(), (double) gp.gety());

                            calculateMoves(gp, false);

                            //JOptionPane.showMessageDialog(null,"Selected Point -> "+ selectedPiece.toString()); //debug reasons
                        } else if (gp.isSelected && !spaceSelected) {
                            gp.select();

                            //clear up vars that need to be reset

                            selectedPiece = null;
                            pieceSelected = false;
                            legalMoves.clear();

                            // make sqr's illegal
                            for (Node n : board.getChildren()) {
                                if (n instanceof gameSquare) {
                                    if (((gameSquare) n).isLegal) {
                                        ((gameSquare) n).makeLegal();
                                    }
                                }
                            }


                        } else if (pieceSelected) {
                            JOptionPane.showMessageDialog(null, "You Already Have A Piece Selected! Choose A Place to Move or Deselect!");
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(null,"It is not your Turn!!!","Not Your Turn",JOptionPane.ERROR_MESSAGE);
                    }
                }
                if (gp.team == 2 && isTurnAuto) {
                    if (playerTurn && isTurnAuto) {//if it is their turn, isTurnAuto provides a bypass, in this case it voids this pathway if manual is triggered

                        if (!pieceSelected) {
                            pieceSelected = true;
                            gp.select();

                            selectedPiece = new Point2D((double) gp.getx(), (double) gp.gety());

                            calculateMoves(gp, false);

                            //JOptionPane.showMessageDialog(null,"Selected Point -> "+ selectedPiece.toString()); //debug reasons
                        } else if (gp.isSelected && !spaceSelected) {
                            gp.select();

                            //confirm cancel move here later

                            selectedPiece = null;
                            pieceSelected = false;
                            legalMoves.clear();

                            for (Node n : board.getChildren()) {
                                if (n instanceof gameSquare) {
                                    if (((gameSquare) n).isLegal) {
                                        ((gameSquare) n).makeLegal();
                                    }
                                }
                            }


                        } else if (pieceSelected) {
                            JOptionPane.showMessageDialog(null, "You Already Have A Piece Selected! Choose A Place to Move or Deselect!");
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(null,"It is not your Turn!!!","Not Your Turn",JOptionPane.ERROR_MESSAGE);
                    }
                }


            });

            gp.setX((int)p.getX());
            gp.setY((int)p.getY());
            board.add(gp,(int)p.getX(),(int)p.getY());

            if(pieceLocation[(int)p.getX()][(int)p.getY()] !=1){
                pieceLocation[(int)p.getX()][(int)p.getY()]=1;

            }


        }




    }
    @SuppressWarnings("Duplicates")
    private void calculateMoves(Piece p, boolean jump) {

        if (jump) {
            if (!pieceSelected) {
                pieceSelected = true;
                p.select();

                selectedPiece = new Point2D((double) p.getx(), (double) p.gety());
            }
        }


        if (!jump) {
            if ((p.getx() + 1 < 16 && p.getx() - 1 > -1) && (p.gety() + 1 < 16 && p.gety() - 1 > -1)) { //so long as not on edge MARK:0

                if (pieceLocation[p.getx() + 1][p.gety()] == 0) { //space to the right
                    legalMoves.add(new Point2D((double) p.getx() + 1, (double) p.gety()));
                }
                if (pieceLocation[p.getx() + 1][p.gety()] == 1) { //PIECE to the right
                    if (p.getx() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx() + 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety()));

                        }
                    }
                }


                if (pieceLocation[p.getx() + 1][p.gety() + 1] == 0) { //diag bottom right
                    legalMoves.add(new Point2D((double) p.getx() + 1, (double) p.gety() + 1));
                }
                if (pieceLocation[p.getx() + 1][p.gety() + 1] == 1) { // PIECE diag bottom right
                    if ((p.getx() + 2 < 16) && (p.gety() + 2 < 16)) { //AND THERE IS ROOM
                        if (pieceLocation[p.getx() + 2][p.gety() + 2] == 0) { //Is There Space
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety() + 2));

                        }
                    }
                }


                if (pieceLocation[p.getx()][p.gety() + 1] == 0) { //Bottom space
                    legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() + 1));
                }
                if (pieceLocation[p.getx()][p.gety() + 1] == 1) { //PIECE bottom
                    if (p.gety() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() + 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() + 2));
                        }
                    }
                }

                if (pieceLocation[p.getx() - 1][p.gety()] == 0) { //left space
                    legalMoves.add(new Point2D((double) p.getx() - 1, (double) p.gety()));
                }

                if (pieceLocation[p.getx() - 1][p.gety()] == 1) { //PIECE to the left
                    if (p.getx() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety()));

                        }
                    }
                }


                if (pieceLocation[p.getx() - 1][p.gety() - 1] == 0) { //diag top left
                    legalMoves.add(new Point2D((double) p.getx() - 1, (double) p.gety() - 1));
                }
                if (pieceLocation[p.getx() - 1][p.gety() - 1] == 1) { //PIECE to the diag top left
                    if (p.getx() - 2 > -1 && p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety() - 2));

                        }
                    }
                }

                if (pieceLocation[p.getx()][p.gety() - 1] == 0) { //up space
                    legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() - 1));
                }
                if (pieceLocation[p.getx()][p.gety() - 1] == 1) { //PIECE to the up
                    if (p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() - 2));

                        }
                    }
                }

                if (pieceLocation[p.getx() - 1][p.gety() + 1] == 0) { //bot left diag
                    legalMoves.add(new Point2D((double) p.getx() - 1, (double) p.gety() + 1));
                }

                if (pieceLocation[p.getx() - 1][p.gety() + 1] == 1) { //PIECE to the diag bottom left
                    if (p.getx() - 2 > -1 && p.gety() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety() + 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety() + 2));

                        }
                    }
                }


                if (pieceLocation[p.getx() + 1][p.gety() - 1] == 0) { //top right diag
                    legalMoves.add(new Point2D((double) p.getx() + 1, (double) p.gety() - 1));
                }
                if (pieceLocation[p.getx() + 1][p.gety() - 1] == 1) { //PIECE to the diag top right
                    if (p.getx() + 2 < 16 && p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() + 2][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety() - 2));

                        }
                    }
                }

            }


            if ((p.getx() + 1 == 16 && p.getx() - 1 > -1) && (p.gety() + 1 < 16 && p.gety() - 1 > -1)) { //if on right edge MARK


                if (pieceLocation[p.getx()][p.gety() + 1] == 0) { //down space
                    legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() + 1));
                }
                if (pieceLocation[p.getx()][p.gety() + 1] == 1) { //PIECE bottom
                    if (p.gety() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() + 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() + 2));
                        }
                    }
                }

                if (pieceLocation[p.getx() - 1][p.gety()] == 0) { //left space
                    legalMoves.add(new Point2D((double) p.getx() - 1, (double) p.gety()));
                }
                if (pieceLocation[p.getx() - 1][p.gety()] == 1) { //PIECE to the left
                    if (p.getx() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety()));

                        }
                    }
                }

                if (pieceLocation[p.getx() - 1][p.gety() + 1] == 0) { //bot left diag
                    legalMoves.add(new Point2D((double) p.getx() - 1, (double) p.gety() + 1));
                }

                if (pieceLocation[p.getx() - 1][p.gety() + 1] == 1) { //PIECE to the diag bottom left
                    if (p.getx() - 2 > -1 && p.gety() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety() + 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety() + 2));

                        }
                    }
                }

                if (pieceLocation[p.getx() - 1][p.gety() - 1] == 0) { //diag top left
                    legalMoves.add(new Point2D((double) p.getx() - 1, (double) p.gety() - 1));
                }
                if (pieceLocation[p.getx() - 1][p.gety() - 1] == 1) { //PIECE to the diag top left
                    if (p.getx() - 2 > -1 && p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety() - 2));

                        }
                    }
                }

                if (pieceLocation[p.getx()][p.gety() - 1] == 0) { //upp space
                    legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() - 1));
                }
                if (pieceLocation[p.getx()][p.gety() - 1] == 1) { //PIECE to the up
                    if (p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() - 2));

                        }
                    }
                }
            }


            if ((p.getx() + 1 < 16 && p.getx() - 1 == -1) && (p.gety() + 1 < 16 && p.gety() - 1 > -1)) { //if on left edge MARK (done)

                if (pieceLocation[p.getx() + 1][p.gety()] == 0) { //space to the right
                    legalMoves.add(new Point2D((double) p.getx() + 1, (double) p.gety()));
                }
                if (pieceLocation[p.getx() + 1][p.gety()] == 1) { //PIECE to the right
                    if (p.getx() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx() + 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety()));
                        }
                    }
                }


                if (pieceLocation[p.getx() + 1][p.gety() + 1] == 0) { //diag bottom right
                    legalMoves.add(new Point2D((double) p.getx() + 1, (double) p.gety() + 1));
                }
                if (pieceLocation[p.getx() + 1][p.gety() + 1] == 1) { // PIECE diag bottom right
                    if ((p.getx() + 2 < 16) && (p.gety() + 2 < 16)) { //AND THERE IS ROOM
                        if (pieceLocation[p.getx() + 2][p.gety() + 2] == 0) { //Is There Space
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety() + 2));
                        }
                    }
                }

                if (pieceLocation[p.getx()][p.gety() + 1] == 0) { //down space
                    legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() + 1));
                }
                if (pieceLocation[p.getx()][p.gety() + 1] == 1) { //PIECE bottom
                    if (p.gety() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() + 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() + 2));
                        }
                    }
                }

                if (pieceLocation[p.getx()][p.gety() - 1] == 0) { //upp space
                    legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() - 1));
                }
                if (pieceLocation[p.getx()][p.gety() - 1] == 1) { //PIECE to the up
                    if (p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() - 2));

                        }
                    }
                }

                if (pieceLocation[p.getx() + 1][p.gety() - 1] == 0) { //top right diag
                    legalMoves.add(new Point2D((double) p.getx() + 1, (double) p.gety() - 1));
                }
                if (pieceLocation[p.getx() + 1][p.gety() - 1] == 1) { //PIECE to the diag top right
                    if (p.getx() + 2 < 16 && p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() + 2][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety() - 2));

                        }
                    }
                }


            }


            if ((p.getx() + 1 < 16 && p.getx() - 1 > -1) && (p.gety() + 1 == 16 && p.gety() - 1 > -1)) { //bottom MARK (done)

                if (pieceLocation[p.getx() + 1][p.gety()] == 0) { //space to the right
                    legalMoves.add(new Point2D((double) p.getx() + 1, (double) p.gety()));
                }
                if (pieceLocation[p.getx() + 1][p.gety()] == 1) { //PIECE to the right
                    if (p.getx() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx() + 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety()));
                        }
                    }
                }


                if (pieceLocation[p.getx() - 1][p.gety()] == 0) { //left space
                    legalMoves.add(new Point2D((double) p.getx() - 1, (double) p.gety()));
                }
                if (pieceLocation[p.getx() - 1][p.gety()] == 1) { //PIECE to the left
                    if (p.getx() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety()));

                        }
                    }
                }


                if (pieceLocation[p.getx() - 1][p.gety() - 1] == 0) { //diag top left
                    legalMoves.add(new Point2D((double) p.getx() - 1, (double) p.gety() - 1));
                }
                if (pieceLocation[p.getx() - 1][p.gety() - 1] == 1) { //PIECE to the diag top left
                    if (p.getx() - 2 > -1 && p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety() - 2));

                        }
                    }
                }


                if (pieceLocation[p.getx()][p.gety() - 1] == 0) { //up space
                    legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() - 1));
                }
                if (pieceLocation[p.getx()][p.gety() - 1] == 1) { //PIECE to the up
                    if (p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() - 2));

                        }
                    }
                }


                if (pieceLocation[p.getx() + 1][p.gety() - 1] == 0) { //top right diag
                    legalMoves.add(new Point2D((double) p.getx() + 1, (double) p.gety() - 1));
                }
                if (pieceLocation[p.getx() + 1][p.gety() - 1] == 1) { //PIECE to the diag top right
                    if (p.getx() + 2 < 16 && p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() + 2][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety() - 2));

                        }
                    }
                }


            }


            if ((p.getx() + 1 < 16 && p.getx() - 1 > -1) && (p.gety() + 1 < 16 && p.gety() - 1 == -1)) { //if on top MARK T

                if (pieceLocation[p.getx() + 1][p.gety()] == 0) { //space to the right
                    legalMoves.add(new Point2D((double) p.getx() + 1, (double) p.gety()));
                }
                if (pieceLocation[p.getx() + 1][p.gety()] == 1) { //PIECE to the right
                    if (p.getx() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx() + 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety()));
                        }
                    }
                }


                if (pieceLocation[p.getx() + 1][p.gety() + 1] == 0) { //diag bot right
                    legalMoves.add(new Point2D((double) p.getx() + 1, (double) p.gety() + 1));
                }
                if (pieceLocation[p.getx() + 1][p.gety() + 1] == 1) { // PIECE diag bottom right
                    if ((p.getx() + 2 < 16) && (p.gety() + 2 < 16)) { //AND THERE IS ROOM
                        if (pieceLocation[p.getx() + 2][p.gety() + 2] == 0) { //Is There Space
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety() + 2));
                        }
                    }
                }


                if (pieceLocation[p.getx()][p.gety() + 1] == 0) { //down space
                    legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() + 1));
                }
                if (pieceLocation[p.getx()][p.gety() + 1] == 1) { //PIECE bottom
                    if (p.gety() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() + 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() + 2));
                        }
                    }
                }

                if (pieceLocation[p.getx() - 1][p.gety()] == 0) { //left space
                    legalMoves.add(new Point2D((double) p.getx() - 1, (double) p.gety()));
                }
                if (pieceLocation[p.getx() - 1][p.gety()] == 1) { //PIECE to the left
                    if (p.getx() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety()));

                        }
                    }
                }


                if (pieceLocation[p.getx() - 1][p.gety() + 1] == 0) { //bot left diag
                    legalMoves.add(new Point2D((double) p.getx() - 1, (double) p.gety() + 1));
                }

                if (pieceLocation[p.getx() - 1][p.gety() + 1] == 1) { //PIECE to the diag bottom left
                    if (p.getx() - 2 > -1 && p.gety() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety() + 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety() + 2));

                        }
                    }
                }


            }


            if ((p.getx() + 1 == 16 && p.getx() - 1 > -1) && (p.gety() + 1 < 16 && p.gety() - 1 == -1)) { // top right corner Mark


                if (pieceLocation[p.getx()][p.gety() + 1] == 0) { //down space
                    legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() + 1));
                }
                if (pieceLocation[p.getx()][p.gety() + 1] == 1) { //PIECE bottom
                    if (p.gety() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() + 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() + 2));
                        }
                    }
                }

                if (pieceLocation[p.getx() - 1][p.gety()] == 0) { //left space
                    legalMoves.add(new Point2D((double) p.getx() - 1, (double) p.gety()));
                }
                if (pieceLocation[p.getx() - 1][p.gety()] == 1) { //PIECE to the left
                    if (p.getx() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety()));

                        }
                    }
                }

                if (pieceLocation[p.getx() - 1][p.gety() + 1] == 0) { //bot left diag
                    legalMoves.add(new Point2D((double) p.getx() - 1, (double) p.gety() + 1));
                }

                if (pieceLocation[p.getx() - 1][p.gety() + 1] == 1) { //PIECE to the diag bottom left
                    if (p.getx() - 2 > -1 && p.gety() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety() + 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety() + 2));

                        }
                    }
                }


            }


            if ((p.getx() + 1 == 16 && p.getx() - 1 > -1) && (p.gety() + 1 == 16 && p.gety() - 1 > -1)) { // bottom right corner Mark


                if (pieceLocation[p.getx() - 1][p.gety()] == 0) { //left space
                    legalMoves.add(new Point2D((double) p.getx() - 1, (double) p.gety()));
                }
                if (pieceLocation[p.getx() - 1][p.gety()] == 1) { //PIECE to the left
                    if (p.getx() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety()));

                        }
                    }
                }

                if (pieceLocation[p.getx() - 1][p.gety() - 1] == 0) { //diag top left
                    legalMoves.add(new Point2D((double) p.getx() - 1, (double) p.gety() - 1));
                }
                if (pieceLocation[p.getx() - 1][p.gety() - 1] == 1) { //PIECE to the diag top left
                    if (p.getx() - 2 > -1 && p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety() - 2));

                        }
                    }
                }

                if (pieceLocation[p.getx()][p.gety() - 1] == 0) { //upp space
                    legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() - 1));
                }
                if (pieceLocation[p.getx()][p.gety() - 1] == 1) { //PIECE to the up
                    if (p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() - 2));

                        }
                    }
                }
            }

            if ((p.getx() + 1 < 16 && p.getx() - 1 == -1) && (p.gety() + 1 < 16 && p.gety() - 1 == -1)) { //top left corner MARK (done)

                if (pieceLocation[p.getx() + 1][p.gety()] == 0) { //space to the right
                    legalMoves.add(new Point2D((double) p.getx() + 1, (double) p.gety()));
                }
                if (pieceLocation[p.getx() + 1][p.gety()] == 1) { //PIECE to the right
                    if (p.getx() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx() + 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety()));
                        }
                    }
                }


                if (pieceLocation[p.getx() + 1][p.gety() + 1] == 0) { //diag bottom right
                    legalMoves.add(new Point2D((double) p.getx() + 1, (double) p.gety() + 1));
                }
                if (pieceLocation[p.getx() + 1][p.gety() + 1] == 1) { // PIECE diag bottom right
                    if ((p.getx() + 2 < 16) && (p.gety() + 2 < 16)) { //AND THERE IS ROOM
                        if (pieceLocation[p.getx() + 2][p.gety() + 2] == 0) { //Is There Space
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety() + 2));
                        }
                    }
                }

                if (pieceLocation[p.getx()][p.gety() + 1] == 0) { //down space
                    legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() + 1));
                }
                if (pieceLocation[p.getx()][p.gety() + 1] == 1) { //PIECE bottom
                    if (p.gety() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() + 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() + 2));
                        }
                    }
                }

            }

            if ((p.getx() + 1 < 16 && p.getx() - 1 == -1) && (p.gety() + 1 == 16 && p.gety() - 1 > -1)) { //bottom left corner MARK (done)

                if (pieceLocation[p.getx() + 1][p.gety()] == 0) { //space to the right
                    legalMoves.add(new Point2D((double) p.getx() + 1, (double) p.gety()));
                }
                if (pieceLocation[p.getx() + 1][p.gety()] == 1) { //PIECE to the right
                    if (p.getx() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx() + 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety()));
                        }
                    }
                }

                if (pieceLocation[p.getx()][p.gety() - 1] == 0) { //upp space
                    legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() - 1));
                }
                if (pieceLocation[p.getx()][p.gety() - 1] == 1) { //PIECE to the up
                    if (p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() - 2));

                        }
                    }
                }

                if (pieceLocation[p.getx() + 1][p.gety() - 1] == 0) { //top right diag
                    legalMoves.add(new Point2D((double) p.getx() + 1, (double) p.gety() - 1));
                }
                if (pieceLocation[p.getx() + 1][p.gety() - 1] == 1) { //PIECE to the diag top right
                    if (p.getx() + 2 < 16 && p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() + 2][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety() - 2));

                        }
                    }
                }


            }


        }




        //for jump

        else {


            if ((p.getx() + 1 < 16 && p.getx() - 1 > -1) && (p.gety() + 1 < 16 && p.gety() - 1 > -1)) { //so long as not on edge MARK:0

                if (pieceLocation[p.getx() + 1][p.gety()] == 1) { //PIECE to the right
                    if (p.getx() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx() + 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety()));

                        }
                    }
                }

                if (pieceLocation[p.getx() + 1][p.gety() + 1] == 1) { // PIECE diag bottom right
                    if ((p.getx() + 2 < 16) && (p.gety() + 2 < 16)) { //AND THERE IS ROOM
                        if (pieceLocation[p.getx() + 2][p.gety() + 2] == 0) { //Is There Space
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety() + 2));

                        }
                    }
                }

                if (pieceLocation[p.getx()][p.gety() + 1] == 1) { //PIECE bottom
                    if (p.gety() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() + 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() + 2));
                        }
                    }
                }

                if (pieceLocation[p.getx() - 1][p.gety()] == 1) { //PIECE to the left
                    if (p.getx() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety()));

                        }
                    }
                }

                if (pieceLocation[p.getx() - 1][p.gety() - 1] == 1) { //PIECE to the diag top left
                    if (p.getx() - 2 > -1 && p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety() - 2));

                        }
                    }
                }

                if (pieceLocation[p.getx()][p.gety() - 1] == 1) { //PIECE to the up
                    if (p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() - 2));

                        }
                    }
                }

                if (pieceLocation[p.getx() - 1][p.gety() + 1] == 1) { //PIECE to the diag bottom left
                    if (p.getx() - 2 > -1 && p.gety() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety() + 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety() + 2));

                        }
                    }
                }

                if (pieceLocation[p.getx() + 1][p.gety() - 1] == 1) { //PIECE to the diag top right
                    if (p.getx() + 2 < 16 && p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() + 2][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety() - 2));

                        }
                    }
                }

            }


            if ((p.getx() + 1 == 16 && p.getx() - 1 > -1) && (p.gety() + 1 < 16 && p.gety() - 1 > -1)) { //if on right edge MARK

                if (pieceLocation[p.getx()][p.gety() + 1] == 1) { //PIECE bottom
                    if (p.gety() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() + 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() + 2));
                        }
                    }
                }

                if (pieceLocation[p.getx() - 1][p.gety()] == 1) { //PIECE to the left
                    if (p.getx() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety()));

                        }
                    }
                }

                if (pieceLocation[p.getx() - 1][p.gety() + 1] == 1) { //PIECE to the diag bottom left
                    if (p.getx() - 2 > -1 && p.gety() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety() + 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety() + 2));

                        }
                    }
                }

                if (pieceLocation[p.getx() - 1][p.gety() - 1] == 1) { //PIECE to the diag top left
                    if (p.getx() - 2 > -1 && p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety() - 2));

                        }
                    }
                }

                if (pieceLocation[p.getx()][p.gety() - 1] == 1) { //PIECE to the up
                    if (p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() - 2));

                        }
                    }
                }
            }


            if ((p.getx() + 1 < 16 && p.getx() - 1 == -1) && (p.gety() + 1 < 16 && p.gety() - 1 > -1)) { //if on left edge MARK (done)

                if (pieceLocation[p.getx() + 1][p.gety()] == 1) { //PIECE to the right
                    if (p.getx() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx() + 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety()));
                        }
                    }
                }

                if (pieceLocation[p.getx() + 1][p.gety() + 1] == 1) { // PIECE diag bottom right
                    if ((p.getx() + 2 < 16) && (p.gety() + 2 < 16)) { //AND THERE IS ROOM
                        if (pieceLocation[p.getx() + 2][p.gety() + 2] == 0) { //Is There Space
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety() + 2));
                        }
                    }
                }

                if (pieceLocation[p.getx()][p.gety() + 1] == 1) { //PIECE bottom
                    if (p.gety() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() + 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() + 2));
                        }
                    }
                }

                if (pieceLocation[p.getx()][p.gety() - 1] == 1) { //PIECE to the up
                    if (p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() - 2));

                        }
                    }
                }

                if (pieceLocation[p.getx() + 1][p.gety() - 1] == 1) { //PIECE to the diag top right
                    if (p.getx() + 2 < 16 && p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() + 2][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety() - 2));

                        }
                    }
                }


            }


            if ((p.getx() + 1 < 16 && p.getx() - 1 > -1) && (p.gety() + 1 == 16 && p.gety() - 1 > -1)) { //bottom MARK (done)

                if (pieceLocation[p.getx() + 1][p.gety()] == 1) { //PIECE to the right
                    if (p.getx() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx() + 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety()));
                        }
                    }
                }

                if (pieceLocation[p.getx() - 1][p.gety()] == 1) { //PIECE to the left
                    if (p.getx() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety()));

                        }
                    }
                }

                if (pieceLocation[p.getx() - 1][p.gety() - 1] == 1) { //PIECE to the diag top left
                    if (p.getx() - 2 > -1 && p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety() - 2));

                        }
                    }
                }


                if (pieceLocation[p.getx()][p.gety() - 1] == 1) { //PIECE to the up
                    if (p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() - 2));

                        }
                    }
                }

                if (pieceLocation[p.getx() + 1][p.gety() - 1] == 1) { //PIECE to the diag top right
                    if (p.getx() + 2 < 16 && p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() + 2][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety() - 2));

                        }
                    }
                }


            }


            if ((p.getx() + 1 < 16 && p.getx() - 1 > -1) && (p.gety() + 1 < 16 && p.gety() - 1 == -1)) { //if on top MARK T

                if (pieceLocation[p.getx() + 1][p.gety()] == 1) { //PIECE to the right
                    if (p.getx() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx() + 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety()));
                        }
                    }
                }

                if (pieceLocation[p.getx() + 1][p.gety() + 1] == 1) { // PIECE diag bottom right
                    if ((p.getx() + 2 < 16) && (p.gety() + 2 < 16)) { //AND THERE IS ROOM
                        if (pieceLocation[p.getx() + 2][p.gety() + 2] == 0) { //Is There Space
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety() + 2));
                        }
                    }
                }

                if (pieceLocation[p.getx()][p.gety() + 1] == 1) { //PIECE bottom
                    if (p.gety() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() + 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() + 2));
                        }
                    }
                }

                if (pieceLocation[p.getx() - 1][p.gety()] == 1) { //PIECE to the left
                    if (p.getx() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety()));

                        }
                    }
                }

                if (pieceLocation[p.getx() - 1][p.gety() + 1] == 1) { //PIECE to the diag bottom left
                    if (p.getx() - 2 > -1 && p.gety() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety() + 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety() + 2));

                        }
                    }
                }


            }


            if ((p.getx() + 1 == 16 && p.getx() - 1 > -1) && (p.gety() + 1 < 16 && p.gety() - 1 == -1)) { // top right corner Mark

                if (pieceLocation[p.getx()][p.gety() + 1] == 1) { //PIECE bottom
                    if (p.gety() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() + 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() + 2));
                        }
                    }
                }


                if (pieceLocation[p.getx() - 1][p.gety()] == 1) { //PIECE to the left
                    if (p.getx() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety()));

                        }
                    }
                }

                if (pieceLocation[p.getx() - 1][p.gety() + 1] == 1) { //PIECE to the diag bottom left
                    if (p.getx() - 2 > -1 && p.gety() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety() + 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety() + 2));

                        }
                    }
                }


            }


            if ((p.getx() + 1 == 16 && p.getx() - 1 > -1) && (p.gety() + 1 == 16 && p.gety() - 1 > -1)) { // bottom right corner Mark


                if (pieceLocation[p.getx() - 1][p.gety()] == 1) { //PIECE to the left
                    if (p.getx() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety()));

                        }
                    }
                }


                if (pieceLocation[p.getx() - 1][p.gety() - 1] == 1) { //PIECE to the diag top left
                    if (p.getx() - 2 > -1 && p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() - 2][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() - 2, (double) p.gety() - 2));

                        }
                    }
                }


                if (pieceLocation[p.getx()][p.gety() - 1] == 1) { //PIECE to the up
                    if (p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() - 2));

                        }
                    }
                }
            }

            if ((p.getx() + 1 < 16 && p.getx() - 1 == -1) && (p.gety() + 1 < 16 && p.gety() - 1 == -1)) { //top left corner MARK (done)


                if (pieceLocation[p.getx() + 1][p.gety()] == 1) { //PIECE to the right
                    if (p.getx() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx() + 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety()));
                        }
                    }
                }



                if (pieceLocation[p.getx() + 1][p.gety() + 1] == 1) { // PIECE diag bottom right
                    if ((p.getx() + 2 < 16) && (p.gety() + 2 < 16)) { //AND THERE IS ROOM
                        if (pieceLocation[p.getx() + 2][p.gety() + 2] == 0) { //Is There Space
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety() + 2));
                        }
                    }
                }


                if (pieceLocation[p.getx()][p.gety() + 1] == 1) { //PIECE bottom
                    if (p.gety() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() + 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() + 2));
                        }
                    }
                }

            }

            if ((p.getx() + 1 < 16 && p.getx() - 1 == -1) && (p.gety() + 1 == 16 && p.gety() - 1 > -1)) { //bottom left corner MARK (done)


                if (pieceLocation[p.getx() + 1][p.gety()] == 1) { //PIECE to the right
                    if (p.getx() + 2 < 16) { //AND THERES ROOM
                        if (pieceLocation[p.getx() + 2][p.gety()] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety()));
                        }
                    }
                }


                if (pieceLocation[p.getx()][p.gety() - 1] == 1) { //PIECE to the up
                    if (p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx()][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx(), (double) p.gety() - 2));

                        }
                    }
                }

                if (pieceLocation[p.getx() + 1][p.gety() - 1] == 1) { //PIECE to the diag top right
                    if (p.getx() + 2 < 16 && p.gety() - 2 > -1) { //AND THERES ROOM
                        if (pieceLocation[p.getx() + 2][p.gety() - 2] == 0) { //AND THERE IS AN OPEN SPACE
                            legalMoves.add(new Point2D((double) p.getx() + 2, (double) p.gety() - 2));

                        }
                    }
                }
            }
        }

        for(Point2D point: legalMoves){
            //paint legal moves
            for(Node n: board.getChildren()){
                if(n instanceof gameSquare){
                    Point2D sPoint=new Point2D(getColumnIndex(n),getRowIndex(n));
                    if((sPoint.getX()==point.getX()) && (sPoint.getY() ==point.getY())) {
                        println("Found " + sPoint);
                        ((gameSquare) n).makeLegal();


                        n.setOnMouseClicked((t)->{
                            ((gameSquare) n).select();
                            selectedSpace=new Point2D(((gameSquare) n).getx(),((gameSquare) n).gety());


                            if(choiceConfirmToggle) {
                                int choice = JOptionPane.showConfirmDialog(null, "Make this move?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                                if (choice == JOptionPane.NO_OPTION) {
                                    ((gameSquare) n).select();
                                    ((gameSquare) n).setFill(Color.GREEN);
                                    selectedSpace = null;
                                } else {
                                    makeMove(p);
                                }
                            }
                            else{
                                makeMove(p);
                            }


                        });

                    }
                }
            }





            //println(point.toString()); //for debug purps
            //JOptionPane.showMessageDialog(null,"Possible move-> " +point.toString());
        }



    }

    private void makeMove(Piece pg){
        ObservableList<Node> tempBoard=board.getChildren();
        //is it a jump?
        boolean jump=false;

        if(((selectedSpace.getX() > selectedPiece.getX()+1) || (selectedSpace.getY() > selectedPiece.getY()+1)) ||
                ((selectedSpace.getX() < selectedPiece.getX()-1) || (selectedSpace.getY() < selectedPiece.getY()-1))){
            int choice=JOptionPane.showConfirmDialog(null,"Continue Jumping?","Jump Detected",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
            if(choice==JOptionPane.YES_OPTION){
               jump = true;
            }
            else{
                if(disablePending){
                    pg.setOnMouseClicked((t)->{
                        Toolkit.getDefaultToolkit().beep();
                    });
                    ((Piece) pg).setFill(Color.FORESTGREEN);
                }

                if(isTurnAuto) {
                    rollTurn(); //roll if turn is auto
                }
                jump=false; //set for method to know to just end
                jumping=false; //trigger global var for checker Thread
                disablePending=false; //keep it from eternally banning every piece

            }

        }



        for(Node n: board.getChildren()){
            if(n instanceof Piece){
                if(((Piece) n).isSelected){

                    println("Found Selected");
                    ((Piece) n).setX(999);//remove other off the board grid
                    ((Piece) n).setY(999);


                    Piece p=(Piece)n;
                    p.setX((int)selectedSpace.getX());
                    p.setY((int)selectedSpace.getY());


                    //cleanup
                    ((Piece) n).setFill(Color.TRANSPARENT);
                    for(Node sqr:board.getChildren()){
                        if(sqr instanceof gameSquare ){
                            if(((gameSquare) sqr).isLegal){
                                ((gameSquare) sqr).makeLegal(); //reset colors and booleans
                                ((gameSquare) sqr).isLegal=false;
                            }
                            if(((gameSquare) sqr).isSelected){
                                ((gameSquare) sqr).isSelected=false;

                            }
                            sqr.setOnMouseClicked((t)->{
                                println(((gameSquare) sqr).getx()+", "+((gameSquare) sqr).gety());
                            });
                        }
                        if(sqr instanceof Piece ){
                            if(((Piece) sqr).isSelected){
                                ((Piece) sqr).select();
                            }
                        }


                        //RESET
                        spaceSelected=false;
                        pieceSelected=false;
                        selectedPiece =null;
                        selectedSpace=null;

                        legalMoves.clear(); //clear legal moves;



                        //finally move piece
                        try {

                            board.add(p, p.getx(), p.gety());

                        }catch(Exception e){
                            println(e.getCause()+" ");
                        }
                    }
                }
            }


        }

        //if auto turns are on perform that now
        if(!jump){    //Don't want to change the turn on a jumper! lol
            rollTurn();
        }



        //autotrigger move event in special jump mode
        if(jump){
            jumping=true;
            calculateMoves(pg,true);
        }
    }

    private void disable(){
        selectedSpace=null;
        selectedPiece =null;

        for (Node n:board.getChildren()){
            if(n instanceof Piece){
                n.setOnMouseClicked((t)->{
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null,"Board has Been Disabled, Please Reset :)","Can't do that!",JOptionPane.ERROR_MESSAGE);
                    try{
                        Thread.sleep(1000);
                    }catch(InterruptedException ie){
                        errprint(ie.getLocalizedMessage());
                    }
                });
            }

        }




    }

    private void reset(){
        selectedPiece =null;
        selectedSpace=null;

        pieceSelected=false;
        spaceSelected=false;



        resetLoc();

        println("Restarting...");
        try{
            Thread.sleep(1000);
            Platform.runLater(()->{
                board.getChildren().clear();
                colorBoard();
                newBoard();

            });




        }catch(Exception ie){
            println(ie.getLocalizedMessage());
        }




    }

    private void rollTurn(){
        int turn= 1+(int)(Math.random()*2);
        println(turn+ " player turn");

        if(turn ==1){
            playerTurn=false; //for player one;
            turnText.setText("Turn: Red [Player 1]");
        }
        if(turn==2){
            playerTurn=true;
            turnText.setText("Turn: Black [Player 2]");
        }

    }


    }








