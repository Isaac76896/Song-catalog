package databass.songcatalogapp.model;

public class Song {
    private final int songId;
    private final String songTitle;
    private final String artistName;

    public Song(int songId, String songTitle, String artistName) {
        this.songId = songId;
        this.songTitle = songTitle;
        this.artistName = artistName;
    }

    public int getSongId() {
        return songId;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    @Override
    public String toString() {
        return songTitle + " - " + artistName;
    }
}