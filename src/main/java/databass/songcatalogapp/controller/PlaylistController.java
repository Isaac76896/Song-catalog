package databass.songcatalogapp.controller;

import databass.songcatalogapp.MainApplication;
import javafx.fxml.FXML;

public class PlaylistController {

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
    protected void onAddSongClick() {
        System.out.println("Add Song clicked");
    }
}