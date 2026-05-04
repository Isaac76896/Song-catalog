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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SearchController {

    @FXML
    private TextField searchField;

    @FXML
    private ListView<Song> songListView;

    @FXML
    public void initialize() {
        configureSongListView();

        String keyword = Session.getSearchKeyword();
        if (keyword == null) {
            keyword = "";
        }

        searchField.setText(keyword);
        loadSongs(keyword);
    }

    private void configureSongListView() {
        songListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Song song, boolean empty) {
                super.updateItem(song, empty);

                if (empty || song == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Label titleLabel = new Label(song.getSongTitle());
                titleLabel.getStyleClass().add("result-title");

                Label subtitleLabel = new Label(song.getArtistName());
                subtitleLabel.getStyleClass().add("result-subtitle");

                VBox container = new VBox(titleLabel, subtitleLabel);
                container.setSpacing(2);

                setGraphic(container);
                setText(null);
            }
        });
    }

    @FXML
    protected void onSearchTyping() {
        String keyword = searchField.getText().trim();
        Session.setSearchKeyword(keyword);
        loadSongs(keyword);
    }

    @FXML
    protected void onSearchSongsClick() {
        String keyword = searchField.getText().trim();
        Session.setSearchKeyword(keyword);
        loadSongs(keyword);
    }

    private void loadSongs(String keyword) {
        ObservableList<Song> songs = FXCollections.observableArrayList();

        String sql = """
                SELECT DISTINCT s.song_id, s.song_title, ar.artist_name
                FROM songs s
                JOIN accredation ac ON s.song_id = ac.song_id
                JOIN artists ar ON ac.artist_id = ar.artist_id
                WHERE s.song_title LIKE ? OR ar.artist_name LIKE ?
                ORDER BY s.song_title
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String likeValue = "%" + keyword + "%";
            stmt.setString(1, likeValue);
            stmt.setString(2, likeValue);

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
            showAlert("Database Error", e.getMessage());
        }
    }

    @FXML
    protected void onAddSelectedSongClick() {
        Song selectedSong = songListView.getSelectionModel().getSelectedItem();

        if (selectedSong == null) {
            showAlert("No Song Selected", "Please select a song first.");
            return;
        }

        String checkSql = "SELECT * FROM existence WHERE playlist_id = ? AND song_id = ?";
        String insertSql = "INSERT INTO existence (existence_id, playlist_id, song_id) VALUES (?, ?, ?)";
        String nextIdSql = "SELECT COALESCE(MAX(existence_id), 0) + 1 AS next_id FROM existence";

        try (Connection conn = DatabaseManager.getConnection()) {

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, Session.getSelectedPlaylistId());
                checkStmt.setInt(2, selectedSong.getSongId());

                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    showAlert("Already Added", "That song is already in this playlist.");
                    return;
                }
            }

            int nextExistenceId = 1;

            try (PreparedStatement nextIdStmt = conn.prepareStatement(nextIdSql);
                 ResultSet nextIdRs = nextIdStmt.executeQuery()) {

                if (nextIdRs.next()) {
                    nextExistenceId = nextIdRs.getInt("next_id");
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, nextExistenceId);
                insertStmt.setInt(2, Session.getSelectedPlaylistId());
                insertStmt.setInt(3, selectedSong.getSongId());
                insertStmt.executeUpdate();
            }

            showAlert("Success", "Song added to playlist.");
            MainApplication.changeScene("playlist-view.fxml");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", e.getMessage());
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

    private void showAlert(String title, String message) {
        Alert.AlertType type = title.equals("Success")
                ? Alert.AlertType.INFORMATION
                : Alert.AlertType.ERROR;

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}