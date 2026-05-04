package databass.songcatalogapp;

public class Session {
    private static int currentUserId;
    private static String currentUsername;
    private static int selectedPlaylistId;
    private static String selectedPlaylistName;
    private static String searchKeyword;

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static void setCurrentUserId(int currentUserId) {
        Session.currentUserId = currentUserId;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static void setCurrentUsername(String currentUsername) {
        Session.currentUsername = currentUsername;
    }

    public static int getSelectedPlaylistId() {
        return selectedPlaylistId;
    }

    public static void setSelectedPlaylistId(int selectedPlaylistId) {
        Session.selectedPlaylistId = selectedPlaylistId;
    }

    public static String getSelectedPlaylistName() {
        return selectedPlaylistName;
    }

    public static void setSelectedPlaylistName(String selectedPlaylistName) {
        Session.selectedPlaylistName = selectedPlaylistName;
    }

    public static String getSearchKeyword() {
        return searchKeyword;
    }

    public static void setSearchKeyword(String searchKeyword) {
        Session.searchKeyword = searchKeyword;
    }

    public static void clear() {
        currentUserId = 0;
        currentUsername = null;
        selectedPlaylistId = 0;
        selectedPlaylistName = null;
        searchKeyword = null;
    }
}