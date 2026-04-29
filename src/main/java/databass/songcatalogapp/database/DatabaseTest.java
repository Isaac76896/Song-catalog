package databass.songcatalogapp.database;

import java.sql.Connection;

public class DatabaseTest {
    public static void main(String[] args) {
        try (Connection conn = DatabaseManager.getConnection()) {
            System.out.println("Connected successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}