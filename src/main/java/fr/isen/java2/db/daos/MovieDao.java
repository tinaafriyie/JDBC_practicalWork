package fr.isen.java2.db.daos;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;


import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;



public class MovieDao {

	/**
	 * Lists all movies with their associated genres.
	 * 
	 * @return a list of all movies
	 */
	public List<Movie> listMovies() {
		List<Movie> movies = new ArrayList<>();
		
		String query = "SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre";
		
		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet resultSet = statement.executeQuery(query)) {
			
			while (resultSet.next()) {
				// Extract movie fields - use "idmovie" not "id"
				int movieId = resultSet.getInt("idmovie");
				String title = resultSet.getString("title");
				Date releaseDate = resultSet.getDate("release_date");
				int duration = resultSet.getInt("duration");
				String director = resultSet.getString("director");
				String summary = resultSet.getString("summary");
				
				// Extract genre fields
				int genreId = resultSet.getInt("idgenre");
				String genreName = resultSet.getString("name");
				Genre genre = new Genre(genreId, genreName);
				
				// Create Movie object and add to list
				Movie movie = new Movie(movieId, title, releaseDate.toLocalDate(), genre, duration, director, summary);
				movies.add(movie);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return movies;
	}

	/**
	 * Lists all movies belonging to a specific genre.
	 * 
	 * @param genreName the name of the genre to filter by
	 * @return a list of movies in the specified genre
	 */
	public List<Movie> listMoviesByGenre(String genreName) {
		List<Movie> movies = new ArrayList<>();
		
		String query = "SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre WHERE genre.name = ?";
		
		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
			 PreparedStatement statement = connection.prepareStatement(query)) {
			
			statement.setString(1, genreName);
			
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					// Extract movie fields - use "idmovie" not "id"
					int movieId = resultSet.getInt("idmovie");
					String title = resultSet.getString("title");
					Date releaseDate = resultSet.getDate("release_date");
					int duration = resultSet.getInt("duration");
					String director = resultSet.getString("director");
					String summary = resultSet.getString("summary");
					
					// Extract genre fields
					int genreId = resultSet.getInt("idgenre");
					String genreName2 = resultSet.getString("name");
					Genre genre = new Genre(genreId, genreName2);
					
					// Create Movie object and add to list
					Movie movie = new Movie(movieId, title, releaseDate.toLocalDate(), genre, duration, director, summary);
					movies.add(movie);
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return movies;
	}

	/**
	 * Adds a new movie to the database and returns it with the generated ID.
	 * 
	 * @param movie the movie to add (without ID)
	 * @return the movie with its generated ID
	 */
	public Movie addMovie(Movie movie) {
		String query = "INSERT INTO movie(title, release_date, genre_id, duration, director, summary) VALUES(?, ?, ?, ?, ?, ?)";
		
		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
			 PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			
			statement.setString(1, movie.getTitle());
			statement.setDate(2, Date.valueOf(movie.getReleaseDate()));
			statement.setInt(3, movie.getGenre().getId());
			statement.setInt(4, movie.getDuration());
			statement.setString(5, movie.getDirector());
			statement.setString(6, movie.getSummary());
			
			statement.executeUpdate();
			
			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					int generatedId = generatedKeys.getInt(1);
					
					return new Movie(
						generatedId,
						movie.getTitle(),
						movie.getReleaseDate(),
						movie.getGenre(),
						movie.getDuration(),
						movie.getDirector(),
						movie.getSummary()
					);
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}