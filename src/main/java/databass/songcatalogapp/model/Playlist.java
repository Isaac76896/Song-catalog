package databass.songcatalogapp.model;

public class Playlist {
    private final int playlistId;
    private final String playlistName;
    private final int userId;

    public Playlist(int playlistId, String playlistName, int userId) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.userId = userId;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return playlistName;
    }
}