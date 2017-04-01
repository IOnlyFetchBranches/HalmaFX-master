import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static Tools.PrintTools.*;


public class mainApp extends Application{
    public static List<Point2D> player1Start;
    public static List<Point2D> player2Start;
    public static MediaPlayer player;

    public static Stage crossClassStage;


    public static void main(String[] args){
        launch(args);
    }

    public void start(Stage primaryStage) {
        try {

            File fxml=new File(this.getClass().getResource("/main.fxml").getPath());
            if(fxml.exists()){
                print("");
            }
            readFiles(); //read in starting coords



            Pane root = new FXMLLoader().load(this.getClass().getResource("/main.fxml"));
            Scene scene=new Scene(root);

            root.setBackground(new Background(new BackgroundImage(new Image("Resources/homes.png"), BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,BackgroundSize.DEFAULT)));





            primaryStage.setResizable(false);
            primaryStage.setTitle("HalmaFX-Marcus Joachim");
            primaryStage.getIcons().add(new Image("Resources/icon5.png"));
            primaryStage.setScene(scene);

            primaryStage.setOnCloseRequest((t)-> System.exit(0));


            //this is the only way i could think of at the time to extend my stage to the controllers
            crossClassStage=primaryStage;
            crossClassStage.show();


            File song=new File(this.getClass().getResource("Resources/wunluv.wav").getPath());
            Media clip=new Media(song.toURI().toString());
            player =new MediaPlayer(clip);
            print(player.getTotalDuration() + "");




        } catch (IOException e) {
            errprint(e.getLocalizedMessage() +" " +e.getCause());
            printStack(e);
        }
    }
    public static void readFiles(){


        //Check Resources;
        mainApp ref=new mainApp();


        File cords=new File(ref.getClass().getResource("Resources/CordData.txt").getPath());
        if(!cords.exists()){
            errprint("Resource "+cords.getName()+" was not found! Should be in Resources/CordData.txt");
        }

        //Read Cords;
        try {
            //Create list to store
            player1Start=new ArrayList<Point2D>();
            player2Start=new ArrayList<Point2D>();

            //Create Readers
            BufferedReader br = new BufferedReader(new FileReader(cords));
            String line; int cycle=0;

            //begin Reading
            while((line=br.readLine()) != null) {
                cycle++; //track the line;

                StringTokenizer st = new StringTokenizer(line, ":");
                st.nextToken();
                while(st.hasMoreTokens()){
                    String pos=st.nextToken();
                    String x=pos.trim().substring(0,pos.indexOf(",")-1);
                    String y=pos.trim().substring(pos.indexOf(","),pos.length()-1);

                    //store
                    if(cycle==1) {//first cycle is FirstPlayer
                        player1Start.add(new Point2D(Double.parseDouble(x), Double.parseDouble(y)));
                    }
                    else if(cycle==2) { //logically this is second player :P
                        player2Start.add(new Point2D(Double.parseDouble(x), Double.parseDouble(y)));
                    }
                }
            }

        }catch(IOException e){
            errprint(e.getLocalizedMessage());

        }
    }




























}
