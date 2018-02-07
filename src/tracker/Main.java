package tracker;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.lang.String;


public class Main extends Application {

    private static Main instance;

    public static String screen1ID = "Tracker";
    public static String screen1File = "Tracker.fxml";

    private Stage stage;

    public Main() {
        instance = this;
    }

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void start(Stage stage) throws Exception {

        this.stage = stage;

        ScreensController mainContainer = new ScreensController();
        mainContainer.loadScreen(Main.screen1ID, Main.screen1File);

        mainContainer.setScreen(Main.screen1ID);

        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.setTitle("Receipt Tracker");
        //stage.getIcons().add(new Image("images/recipeBook.png"));
        stage.show();
    }

    public void resize(double w, double h){
        stage.setWidth(w);
        stage.setHeight(h);
    }

    public static void main (String[]args){
        launch(args);
    }

}