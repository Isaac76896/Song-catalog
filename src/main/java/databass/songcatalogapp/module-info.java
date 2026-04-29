module databass.songcatalogapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    opens databass.songcatalogapp to javafx.fxml;
    opens databass.songcatalogapp.controller to javafx.fxml;

    exports databass.songcatalogapp;
    exports databass.songcatalogapp.controller;
    exports databass.songcatalogapp.database;
}
