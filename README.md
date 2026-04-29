# SongCatalogApp

## Database setup

The app reads database credentials from environment variables instead of storing a password in source code.

Set these before running `DatabaseTest` or the app:

```text
DB_URL=jdbc:mysql://localhost:3306/databass_db
DB_USER=root
DB_PASSWORD=your_mysql_password
```

In IntelliJ, add them to the run configuration under **Environment variables**.
