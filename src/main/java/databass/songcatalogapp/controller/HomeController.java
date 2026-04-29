package databass.songcatalogapp.controller;

import databass.songcatalogapp.MainApplication;
import javafx.fxml.FXML;

public class HomeController {

    @FXML
    protected void onHomeClick() {
        MainApplication.changeScene("home-view.fxml");
    }

    @FXML
    protected void onSearchClick() {
        MainApplication.changeScene("search-view.fxml");
    }

    @FXML
    protected void onLibraryClick() {
        MainApplication.changeScene("library-view.fxml");
    }

    @FXML
    protected void onCreatePlaylistClick() {
        System.out.println("Create Playlist clicked");
    }

    @FXML
    protected void onPlaylistClick() {
        MainApplication.changeScene("playlist-view.fxml");
    }
}