package fr.isen.java2.db.daos;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;



import fr.isen.java2.db.entities.Genre;

public class GenreDao {

	/**
	 * Lists all genres from the database.
	 * 
	 * @return a list of all genres
	 */
	public List<Genre> listGenres() {
		List<Genre> genres = new ArrayList<>();
		
		// Try-with-resources ensures automatic closing of Connection, Statement, and ResultSet
		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet resultSet = statement.executeQuery("SELECT * FROM genre")) {
			
			// Iterate through all rows in the result set
			while (resultSet.next()) {
				// Extract data from current row
				int id = resultSet.getInt("idgenre");
				String name = resultSet.getString("name");
				
				// Create Genre object and add to list
				genres.add(new Genre(id, name));
			}
			
		} catch (SQLException e) {
			// Log the error (in production, use a proper logger)
			e.printStackTrace();
		}
		
		return genres;
	}

	/**
	 * Retrieves a genre by its name.
	 * 
	 * @param name the name of the genre to retrieve
	 * @return the Genre object if found, null otherwise
	 */
	public Genre getGenre(String name) {
		// Try-with-resources for automatic resource management
		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
			 PreparedStatement statement = connection.prepareStatement("SELECT * FROM genre WHERE name = ?")) {
			
			// Set the parameter (prevents SQL injection)
			statement.setString(1, name);
			
			// Execute query
			try (ResultSet resultSet = statement.executeQuery()) {
				// Check if a result exists
				if (resultSet.next()) {
					int id = resultSet.getInt("idgenre");
					String genreName = resultSet.getString("name");
					return new Genre(id, genreName);
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// Return null if genre not found
		return null;
	}

	/**
	 * Adds a new genre to the database.
	 * 
	 * @param name the name of the genre to add
	 */
	public void addGenre(String name) {
		// Try-with-resources for automatic resource management
		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
			 PreparedStatement statement = connection.prepareStatement("INSERT INTO genre(name) VALUES(?)")) {
			
			// Set the parameter (prevents SQL injection)
			statement.setString(1, name);
			
			// Execute the insert statement
			statement.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}	
