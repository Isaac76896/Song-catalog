# SongCatalogApp

A JavaFX music catalog application connected to a MySQL database.  
The app allows users to log in with existing database accounts, browse playlists, view songs inside playlists, search songs and artists, create playlists, and add or remove songs from playlists.

## Database Configuration

For security, the application does **not** store database credentials directly in the source code.  
Instead, it reads them from environment variables.

Set the following environment variables before running either `DatabaseTest` or the full application:

```bash
DB_URL=jdbc:mysql://localhost:3306/databass_db
DB_USER=root
DB_PASSWORD=your_mysql_password
