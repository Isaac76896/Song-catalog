package databass.songcatalogapp.controller;

import databass.songcatalogapp.MainApplication;
import databass.songcatalogapp.Session;
import databass.songcatalogapp.database.DatabaseManager;
import databass.songcatalogapp.model.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PlaylistController {

    @FXML
    private Label playlistTitleLabel;

    @FXML
    private ListView<Song> songListView;

    @FXML
    public void initialize() {
        playlistTitleLabel.setText(Session.getSelectedPlaylistName());
        loadPlaylistSongs();
    }

    private void loadPlaylistSongs() {
        ObservableList<Song> songs = FXCollections.observableArrayList();

        String sql = """
                SELECT DISTINCT s.song_id, s.song_title, ar.artist_name
                FROM existence e
                JOIN songs s ON e.song_id = s.song_id
                JOIN accredation ac ON s.song_id = ac.song_id
                JOIN artists ar ON ac.artist_id = ar.artist_id
                WHERE e.playlist_id = ?
                ORDER BY s.song_title
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Session.getSelectedPlaylistId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Song song = new Song(
                        rs.getInt("song_id"),
                        rs.getString("song_title"),
                        rs.getString("artist_name")
                );
                songs.add(song);
            }

            songListView.setItems(songs);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", e.getMessage(), Alert.AlertType.ERROR);
        }
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

    @FXML
    protected void onAddSongClick() {
        MainApplication.changeScene("search-view.fxml");
    }

    @FXML
    protected void onRemoveSelectedSongClick() {
        Song selectedSong = songListView.getSelectionModel().getSelectedItem();

        if (selectedSong == null) {
            showAlert("No Song Selected", "Please select a song first.", Alert.AlertType.ERROR);
            return;
        }

        String deleteSql = """
                DELETE FROM existence
                WHERE playlist_id = ? AND song_id = ?
                LIMIT 1
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSql)) {

            stmt.setInt(1, Session.getSelectedPlaylistId());
            stmt.setInt(2, selectedSong.getSongId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Success", "Song removed from playlist.", Alert.AlertType.INFORMATION);
                loadPlaylistSongs();
            } else {
                showAlert("Not Removed", "Could not remove the selected song.", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}