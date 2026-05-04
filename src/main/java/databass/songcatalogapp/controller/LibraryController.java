package databass.songcatalogapp.controller;

import databass.songcatalogapp.MainApplication;
import databass.songcatalogapp.Session;
import databass.songcatalogapp.database.DatabaseManager;
import databass.songcatalogapp.model.Playlist;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LibraryController {

    @FXML
    private ListView<Playlist> playlistListView;

    @FXML
    public void initialize() {
        loadUserPlaylists();
    }

    private void loadUserPlaylists() {
        ObservableList<Playlist> playlists = FXCollections.observableArrayList();

        String sql = "SELECT playlist_id, playlist_name, user_id FROM playlists WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Session.getCurrentUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Playlist playlist = new Playlist(
                        rs.getInt("playlist_id"),
                        rs.getString("playlist_name"),
                        rs.getInt("user_id")
                );
                playlists.add(playlist);
            }

            playlistListView.setItems(playlists);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", e.getMessage());
        }
    }

    @FXML
    protected void onOpenPlaylistClick() {
        Playlist selected = playlistListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("No Playlist Selected", "Please select a playlist first.");
            return;
        }

        Session.setSelectedPlaylistId(selected.getPlaylistId());
        Session.setSelectedPlaylistName(selected.getPlaylistName());

        MainApplication.changeScene("playlist-view.fxml");
    }

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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}