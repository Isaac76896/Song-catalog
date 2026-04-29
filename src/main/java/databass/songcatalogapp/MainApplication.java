package databass.songcatalogapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application
{

    private static Stage mainStage;

    @Override
    public void start(Stage stage) throws Exception
    {
        mainStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        scene.getStylesheets().add(MainApplication.class.getResource("style.css").toExternalForm());

        stage.setTitle("Song Catalog");
        stage.setScene(scene);
        stage.show();
    }

    public static void changeScene(String fxmlFile)
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(fxmlFile));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            scene.getStylesheets().add(MainApplication.class.getResource("style.css").toExternalForm());

            mainStage.setScene(scene);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}