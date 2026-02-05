package fr.isen.java2.db.daos;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;



import fr.isen.java2.db.entities.Genre;

public class GenreDao {

	/**
	 * This implementation lists all genres from the database.
	 * 
	 * @return a list of all genres
	 */
	public List<Genre> listGenres() {
		List<Genre> genres = new ArrayList<>();
		
		try (Connection connection = DataSourceFactory.getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet resultSet = statement.executeQuery("SELECT * FROM genre")) {
			
			while (resultSet.next()) {
				// To extract data from current row
				int id = resultSet.getInt("idgenre");
				String name = resultSet.getString("name");
				
				// To create Genre object and add to list
				genres.add(new Genre(id, name));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return genres;
	}

	/**
	 * This method retrieves a genre by its name.
	 * 
	 * @param name the name of the genre to retrieve
	 * @return the Genre object if found, null otherwise
	 */
	public Genre getGenre(String name) {
		try (Connection connection = DataSourceFactory.getConnection();
			 PreparedStatement statement = connection.prepareStatement("SELECT * FROM genre WHERE name = ?")) {
			
			// This prevents SQL injection by using a parameterized query
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
	 * This method adds a new genre to the database.
	 * 
	 * @param name the name of the genre to add
	 */
	public void addGenre(String name) {
		try (Connection connection = DataSourceFactory.getConnection();
			 PreparedStatement statement = connection.prepareStatement("INSERT INTO genre(name) VALUES(?)")) {
			
			
			statement.setString(1, name);
			
			
			statement.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}	
