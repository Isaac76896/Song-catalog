package databass.songcatalogapp.controller;

import databass.songcatalogapp.MainApplication;
import databass.songcatalogapp.Session;
import databass.songcatalogapp.database.DatabaseManager;
import databass.songcatalogapp.model.Playlist;
import databass.songcatalogapp.model.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HomeController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TextField homeSearchField;

    @FXML
    private ListView<Song> homeSearchResultsList;

    @FXML
    private Button playlistButton1;

    @FXML
    private Button playlistButton2;

    @FXML
    private Button playlistButton3;

    @FXML
    private Button playlistButton4;

    private final List<Playlist> userPlaylists = new ArrayList<>();

    @FXML
    public void initialize() {
        String username = Session.getCurrentUsername();

        if (username != null && !username.isBlank()) {
            welcomeLabel.setText("Welcome, " + username);
        } else {
            welcomeLabel.setText("Welcome");
        }

        String savedKeyword = Session.getSearchKeyword();
        if (savedKeyword != null) {
            homeSearchField.setText(savedKeyword);
        }

        configureHomeSearchResults();
        homeSearchResultsList.setVisible(false);
        homeSearchResultsList.setManaged(false);

        loadUserPlaylists();
        populatePlaylistButtons();
    }

    private void configureHomeSearchResults() {
        homeSearchResultsList.setCellFactory(listView -> new ListCell<>() {
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

        homeSearchResultsList.setOnMouseClicked(event -> {
            Song selectedSong = homeSearchResultsList.getSelectionModel().getSelectedItem();
            if (selectedSong != null) {
                Session.setSearchKeyword(selectedSong.getSongTitle());
                MainApplication.changeScene("search-view.fxml");
            }
        });
    }

    @FXML
    protected void onHomeSearchTyping() {
        String keyword = homeSearchField.getText().trim();
        Session.setSearchKeyword(keyword);

        if (keyword.isEmpty()) {
            homeSearchResultsList.getItems().clear();
            homeSearchResultsList.setVisible(false);
            homeSearchResultsList.setManaged(false);
            return;
        }

        loadHomeSearchPreview(keyword);
    }

    @FXML
    protected void onHomeSearchEnter() {
        String keyword = homeSearchField.getText().trim();
        Session.setSearchKeyword(keyword);
        MainApplication.changeScene("search-view.fxml");
    }

    private void loadHomeSearchPreview(String keyword) {
        ObservableList<Song> songs = FXCollections.observableArrayList();

        String sql = """
                SELECT DISTINCT s.song_id, s.song_title, ar.artist_name
                FROM songs s
                JOIN accredation ac ON s.song_id = ac.song_id
                JOIN artists ar ON ac.artist_id = ar.artist_id
                WHERE s.song_title LIKE ? OR ar.artist_name LIKE ?
                ORDER BY s.song_title
                LIMIT 5
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String likeValue = "%" + keyword + "%";
            stmt.setString(1, likeValue);
            stmt.setString(2, likeValue);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                songs.add(new Song(
                        rs.getInt("song_id"),
                        rs.getString("song_title"),
                        rs.getString("artist_name")
                ));
            }

            homeSearchResultsList.setItems(songs);

            boolean hasResults = !songs.isEmpty();
            homeSearchResultsList.setVisible(hasResults);
            homeSearchResultsList.setManaged(hasResults);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", e.getMessage());
        }
    }

    private void loadUserPlaylists() {
        userPlaylists.clear();

        String sql = """
                SELECT playlist_id, playlist_name, user_id
                FROM playlists
                WHERE user_id = ?
                ORDER BY playlist_name
                LIMIT 4
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Session.getCurrentUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                userPlaylists.add(new Playlist(
                        rs.getInt("playlist_id"),
                        rs.getString("playlist_name"),
                        rs.getInt("user_id")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", e.getMessage());
        }
    }

    private void populatePlaylistButtons() {
        Button[] buttons = {playlistButton1, playlistButton2, playlistButton3, playlistButton4};

        for (Button button : buttons) {
            button.setVisible(false);
            button.setManaged(false);
            button.setText("");
        }

        for (int i = 0; i < userPlaylists.size() && i < buttons.length; i++) {
            buttons[i].setText(userPlaylists.get(i).getPlaylistName());
            buttons[i].setVisible(true);
            buttons[i].setManaged(true);
        }
    }

    private void openPlaylistByIndex(int index) {
        if (index < 0 || index >= userPlaylists.size()) {
            showAlert("No Playlist", "That playlist is not available.");
            return;
        }

        Playlist selected = userPlaylists.get(index);
        Session.setSelectedPlaylistId(selected.getPlaylistId());
        Session.setSelectedPlaylistName(selected.getPlaylistName());

        MainApplication.changeScene("playlist-view.fxml");
    }

    @FXML
    protected void onPlaylist1Click() {
        openPlaylistByIndex(0);
    }

    @FXML
    protected void onPlaylist2Click() {
        openPlaylistByIndex(1);
    }

    @FXML
    protected void onPlaylist3Click() {
        openPlaylistByIndex(2);
    }

    @FXML
    protected void onPlaylist4Click() {
        openPlaylistByIndex(3);
    }

    @FXML
    protected void onHomeClick() {
        MainApplication.changeScene("home-view.fxml");
    }

    @FXML
    protected void onSearchClick() {
        Session.setSearchKeyword("");
        MainApplication.changeScene("search-view.fxml");
    }

    @FXML
    protected void onLibraryClick() {
        MainApplication.changeScene("library-view.fxml");
    }

    @FXML
    protected void onLogoutClick() {
        Session.clear();
        MainApplication.changeScene("login-view.fxml");
    }

    @FXML
    protected void onCreatePlaylistClick() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Playlist");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter playlist name:");

        Optional<String> result = dialog.showAndWait();

        if (result.isEmpty()) {
            return;
        }

        String playlistName = result.get().trim();

        if (playlistName.isEmpty()) {
            showAlert("Error", "Playlist name cannot be empty.");
            return;
        }

        String checkSql = "SELECT * FROM playlists WHERE user_id = ? AND playlist_name = ?";
        String nextIdSql = "SELECT COALESCE(MAX(playlist_id), 0) + 1 AS next_id FROM playlists";
        String insertSql = "INSERT INTO playlists (playlist_id, user_id, playlist_name, duration) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, Session.getCurrentUserId());
                checkStmt.setString(2, playlistName);

                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    showAlert("Error", "You already have a playlist with that name.");
                    return;
                }
            }

            int nextPlaylistId = 1;

            try (PreparedStatement nextIdStmt = conn.prepareStatement(nextIdSql);
                 ResultSet nextIdRs = nextIdStmt.executeQuery()) {

                if (nextIdRs.next()) {
                    nextPlaylistId = nextIdRs.getInt("next_id");
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, nextPlaylistId);
                insertStmt.setInt(2, Session.getCurrentUserId());
                insertStmt.setString(3, playlistName);
                insertStmt.setDouble(4, 0.0);
                insertStmt.executeUpdate();
            }

            MainApplication.changeScene("home-view.fxml");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert.AlertType type = title.equals("Database Error") || title.equals("Error") || title.equals("No Playlist")
                ? Alert.AlertType.ERROR
                : Alert.AlertType.INFORMATION;

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}