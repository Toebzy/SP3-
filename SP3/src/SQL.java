import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SQL {
    static Connection connection;
    static String url = "jdbc:mysql://localhost:3306/?user=root" + "autoReconnect=true&useSSL=false";
    static String username = "root";
    static String password = "1234";


    static ArrayList<Movie> movies = new ArrayList<>();
    static ArrayList<Series> series = new ArrayList<>();

    public static void createMovieList() {
        int number = 1;
        String query = "SELECT * FROM sp3_media.movies";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String movieTitle = result.getString("Title");
                String releaseYear = result.getString("Release Year");
                String movieCategory = result.getString("Category");
                String[] categories = movieCategory.replaceAll(" ", "").split(",");
                String movieRating = result.getString("Rating");
                Movie movie = new Movie(movieTitle, releaseYear, categories, movieRating);
                movies.add(movie);
            }
            for (Movie i : movies) {
                System.out.println(number + ". " + i);
                number++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void createSeriesList() {
        int number = 1;
        String query = "SELECT * FROM sp3_media.series";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String movieTitle = result.getString("Title");
                String releaseYear = result.getString("Release Year");
                String movieCategory = result.getString("Category");
                String[] categories = movieCategory.replaceAll(" ", "").split(",");
                String movieRating = result.getString("Rating");
                String seriesSeasons = result.getString("Seasons/Episodes");
                String[] seasons = seriesSeasons.replaceAll(" ", "").split(",");
                Series serie = new Series(movieTitle, releaseYear, categories, movieRating, seasons);
                series.add(serie);
            }
            for (Series i : series) {
                System.out.println(number + ". " + i);
                number++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void getWatchedList() {

        String query = "SELECT watchedSeries, watchedMovies FROM sp3_media.user WHERE userName = ? and userPassword = ?";
        try {
            String watchedSeriesString = null;
            String watchedMoviesString = null;
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, startMenu.getCurrentUser().getUsername());
            statement.setString(2, startMenu.getCurrentUser().getPassword());

            ResultSet result = statement.executeQuery();
            if (result.next()) {

                watchedSeriesString = result.getString("savedSeries");
                watchedMoviesString = result.getString("savedMovies");
                String[] watchedSeries = watchedSeriesString.split(",");
                String[] watchedMovies = watchedMoviesString.split(",");

                System.out.println("|WATCHED MOVIES|");
                for (int i = 0; i < watchedMovies.length; i++) {
                    System.out.println((i + 1 + ". ") + watchedMovies[i]);
                }
                System.out.println("|WATCHED SERIES|");

                for (int i = 0; i < watchedSeries.length; i++) {
                    System.out.println((i + 1 + ". ") + watchedSeries[i]);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void getSavedList() {

        String query = "SELECT savedSeries, savedMovies FROM sp3_media.user WHERE userName = ? and userPassword = ?";
        try {
            String savedSeriesString = null;
            String savedMoviesString = null;
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, startMenu.getCurrentUser().getUsername());
            statement.setString(2, startMenu.getCurrentUser().getPassword());

            ResultSet result = statement.executeQuery();
            if (result.next()) {

                savedSeriesString = result.getString("savedSeries");
                savedMoviesString = result.getString("savedMovies");
                String[] savedSeries = savedSeriesString.split(",");
                String[] savedMovies = savedMoviesString.split(",");

                System.out.println("|SAVED MOVIES|");
                for (int i = 0; i < savedMovies.length; i++) {
                    if (savedMoviesString.contains("none")){
                        break;
                    }
                    System.out.println((i + 1 + ". ") + savedMovies[i]);
                }
                if(savedMovies.length<1 || savedMoviesString.contains("none")){
                    System.out.println("You have no saved movies");
                }
                System.out.println("|SAVED SERIES|");

                for (int i = 0; i < savedSeries.length; i++) {
                    if (savedSeriesString.contains("none")){
                        break;
                    }
                    System.out.println((i + 1 + ". ") + savedSeries[i]);
                }
                if(savedSeries.length<1 || savedSeriesString.contains("none")){
                    System.out.println("You have no saved series");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void addSavedSeries(String series) {

        String SeriesCheck = "SELECT * FROM sp3_media.user savedSeries WHERE userName = ? AND userPassword = ?";
        String savedSeries = null;
        try {

            PreparedStatement statement = connection.prepareStatement(SeriesCheck);
            statement.setString(1, startMenu.getCurrentUser().getUsername());
            statement.setString(2, startMenu.getCurrentUser().getPassword());

            ResultSet resultCheck = statement.executeQuery();
            if (resultCheck.next()) {
                savedSeries = resultCheck.getString("savedSeries");
            }
            if (savedSeries.contains(series)) {
                return;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        String query = "UPDATE sp3_media.user SET savedSeries = CONCAT (savedSeries, ?) WHERE userName = ? AND userPassword = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, series + ",");
            statement.setString(2, startMenu.getCurrentUser().getUsername());
            statement.setString(3, startMenu.getCurrentUser().getPassword());
            statement.executeUpdate();
            checkForNone(2);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addSavedMovie(String movie) {

        String movieCheck = "SELECT * FROM sp3_media.user savedMovies WHERE userName = ? AND userPassword = ?";
        String savedMovies = null;
        try {

            PreparedStatement statement = connection.prepareStatement(movieCheck);
            statement.setString(1, startMenu.getCurrentUser().getUsername());
            statement.setString(2, startMenu.getCurrentUser().getPassword());
            ResultSet resultCheck = statement.executeQuery();

            if (resultCheck.next()) {
                savedMovies = resultCheck.getString("savedMovies");
            }
            if (savedMovies.contains(movie)) {
                return;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        String query = "UPDATE sp3_media.user SET savedMovies = CONCAT (savedMovies, ?) WHERE userName = ? AND userPassword = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, movie + ",");
            statement.setString(2, startMenu.getCurrentUser().getUsername());
            statement.setString(3, startMenu.getCurrentUser().getPassword());
            statement.executeUpdate();
            checkForNone(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void watchedMedia(boolean isMovie, boolean isSeries, String mediaTitle) {
        String dupeCheck = "SELECT watchedSeries, watchedMovies FROM sp3_media.user WHERE userName = ? and userPassword = ?";
        String watchedSeriesString = null;
        String watchedMoviesString = null;
        try {
            PreparedStatement statement = SQL.connection.prepareStatement(dupeCheck);
            statement.setString(1, startMenu.getCurrentUser().getUsername());
            statement.setString(2, startMenu.getCurrentUser().getPassword());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                watchedSeriesString = result.getString("watchedSeries");
                watchedMoviesString = result.getString("watchedMovies");
                if (watchedSeriesString.contains(mediaTitle)) {
                    return;
                } else if (watchedMoviesString.contains(mediaTitle)) {
                    return;
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        if (isMovie) {
            String query = "UPDATE sp3_media.user SET watchedMovies = CONCAT (watchedMovies, ? ',') WHERE userName = ? AND userPassword = ?";

            try {
                PreparedStatement statement = SQL.connection.prepareStatement(query);
                statement.setString(1, mediaTitle);
                statement.setString(2, startMenu.getCurrentUser().getUsername());
                statement.setString(3, startMenu.getCurrentUser().getPassword());
                statement.executeUpdate();
                checkForNone(3);

            } catch (SQLException e) {
                System.out.println(e);
            }
        } else if (isSeries) {
            String query = "UPDATE sp3_media.user SET watchedSeries = CONCAT (watchedSeries, ? ',') WHERE userName = ? AND userPassword = ?";

            try {
                PreparedStatement statement = SQL.connection.prepareStatement(query);
                statement.setString(1, mediaTitle);
                statement.setString(2, startMenu.getCurrentUser().getUsername());
                statement.setString(3, startMenu.getCurrentUser().getPassword());
                statement.executeUpdate();
                checkForNone(4);

            } catch (SQLException e) {
                System.out.println(e);
            }
        }
    }
    public static void sqlCategorysearch(boolean isMovie, String catSearch){
        List<Movie> sqlCatMSearch = new ArrayList<>();
        List<Series> sqlCatSSearch = new ArrayList<>();
        int number = 1;
        try {
            if (isMovie) {
                String meow = "SELECT * FROM sp3_media.movies WHERE Category LIKE ?";
                PreparedStatement statement = connection.prepareStatement(meow);
                statement.setString(1, "%" + catSearch + "%");
                ResultSet resultCheck = statement.executeQuery();
                while (resultCheck.next()) {
                    String movieTitle = resultCheck.getString("Title");
                    String releaseYear = resultCheck.getString("Release Year");
                    String movieCategory = resultCheck.getString("Category");
                    String[] categories = movieCategory.replaceAll(" ", "").split(",");
                    String movieRating = resultCheck.getString("Rating");
                    Movie movie = new Movie(movieTitle, releaseYear, categories, movieRating);
                    sqlCatMSearch.add(movie);
                }
                for (Movie i : sqlCatMSearch) {
                    System.out.println(number + ". " + i);
                    number++;
                }
            }
            else if(!isMovie){
                String meow = "SELECT * FROM sp3_media.series WHERE Category LIKE ?";
                PreparedStatement statement = connection.prepareStatement(meow);
                statement.setString(1, "%" + catSearch + "%");
                ResultSet resultCheck = statement.executeQuery();
                while (resultCheck.next()) {
                    String movieTitle = resultCheck.getString("Title");
                    String releaseYear = resultCheck.getString("Release Year");
                    String movieCategory = resultCheck.getString("Category");
                    String[] categories = movieCategory.replaceAll(" ", "").split(",");
                    String movieRating = resultCheck.getString("Rating");
                    String seriesSeasons = resultCheck.getString("Seasons/Episodes");
                    String[] seasons = seriesSeasons.replaceAll(" ", "").split(",");
                    Series serie = new Series(movieTitle, releaseYear, categories, movieRating, seasons);
                    sqlCatSSearch.add(serie);
                }
                for (Series i : sqlCatSSearch) {
                    System.out.println(number + ". " + i);
                    number++;
                }
            }
        }
        catch (SQLException e){
            System.out.println(e);
        }
    }

    public static void searchMedia (boolean isMovie, String search) {
        List<Movie> sqlMovieSearch = new ArrayList<>();
        List<Series> sqlSeriesSearch = new ArrayList<>();
        try {

            if (isMovie) {
                String movieSearch = "SELECT * FROM sp3_media.movies WHERE Title LIKE ?";
                int number = 1;

                PreparedStatement statement = SQL.connection.prepareStatement(movieSearch);
                statement.setString(1, "%" + search + "%");
                ResultSet result = statement.executeQuery();
                while (result.next()) {
                    String movieTitle = result.getString("Title");
                    String releaseYear = result.getString("Release Year");
                    String movieCategory = result.getString("Category");
                    String[] categories = movieCategory.replaceAll(" ", "").split(",");
                    String movieRating = result.getString("Rating");
                    Movie movie = new Movie(movieTitle, releaseYear, categories, movieRating);
                    sqlMovieSearch.add(movie);
                }
                for (Movie i : sqlMovieSearch) {
                    System.out.println(number + ". " + i);
                    number++;
                }
                if (sqlMovieSearch.size() <= 0) {
                    mainMenu.spaces();
                    System.out.println("----------------------------------------------");
                    System.out.println("There are no matching movies, please try again");
                    System.out.println("----------------------------------------------");
                    mainMenu.runMainMenu();
                }
                System.out.println("--------------------------------------");
                System.out.println("Select a movie, or press '0' to return");
                System.out.println("--------------------------------------");
                Scanner choice = new Scanner(System.in);

                int nextChoice = choice.nextInt();

                if (nextChoice == 0) {
                    mainMenu.spaces();
                    System.out.println("------------------");
                    mainMenu.runMainMenu();

                } else if (nextChoice <= sqlMovieSearch.size()) {
                    String movieTitle = sqlMovieSearch.get(nextChoice - 1).getMediaTitle();
                    mainMenu.mediaPlayer(movieTitle, true, false);
                } else if (nextChoice > sqlMovieSearch.size() || nextChoice < sqlMovieSearch.size()) {

                    mainMenu.spaces();

                    System.out.println("-----------------------------------------");
                    System.out.println("The movie was not found, please try again");
                    System.out.println("-----------------------------------------");

                    SQL.searchMedia(true, search);

                }
            } else if (!isMovie) {
                String seriesSearch = "SELECT * FROM sp3_media.series WHERE Title LIKE ?";
                int number = 1;

                PreparedStatement statement = SQL.connection.prepareStatement(seriesSearch);
                statement.setString(1, "%" + search + "%");
                ResultSet result = statement.executeQuery();
                while (result.next()) {
                    String movieTitle = result.getString("Title");
                    String releaseYear = result.getString("Release Year");
                    String movieCategory = result.getString("Category");
                    String[] categories = movieCategory.replaceAll(" ", "").split(",");
                    String movieRating = result.getString("Rating");
                    String seriesSeasons = result.getString("Seasons/Episodes");
                    String[] seasons = seriesSeasons.replaceAll(" ", "").split(",");
                    Series serie = new Series(movieTitle, releaseYear, categories, movieRating, seasons);
                    sqlSeriesSearch.add(serie);
                }
                for (Series i : sqlSeriesSearch) {
                    System.out.println(number + ". " + i);
                    number++;
                }
                if (sqlSeriesSearch.size() <= 0) {
                    mainMenu.spaces();
                    System.out.println("----------------------------------------------");
                    System.out.println("There are no matching series, please try again");
                    System.out.println("----------------------------------------------");
                    mainMenu.runMainMenu();
                }
                System.out.println("--------------------------------------");
                System.out.println("Select a series, or press '0' to return");
                System.out.println("--------------------------------------");
                Scanner choice = new Scanner(System.in);

                int nextChoice = choice.nextInt();

                if (nextChoice == 0) {
                    mainMenu.spaces();
                    System.out.println("------------------");
                    mainMenu.runMainMenu();

                } else if (nextChoice <= sqlSeriesSearch.size()) {
                    String movieTitle = sqlSeriesSearch.get(nextChoice - 1).getMediaTitle();
                    mainMenu.mediaPlayer(movieTitle, false, true);
                } else if (nextChoice > sqlSeriesSearch.size() || nextChoice < sqlSeriesSearch.size()) {

                    mainMenu.spaces();

                    System.out.println("-----------------------------------------");
                    System.out.println("The series was not found, please try again");
                    System.out.println("-----------------------------------------");

                    SQL.searchMedia(false, search);

                }
            }
        }

        catch(SQLException e){
                System.out.println(e);
            }
        }


    public static void checkForNone(int i) {

        try {
            if (i == 1) {  //savedMovies
                String mediaCheck = "SELECT * FROM sp3_media.user savedMovies WHERE userName = ? AND userPassword = ?";
                String savedMedia = null;
                PreparedStatement statement = connection.prepareStatement(mediaCheck);

                statement.setString(1, startMenu.getCurrentUser().getUsername());
                statement.setString(2, startMenu.getCurrentUser().getPassword());
                ResultSet resultCheck = statement.executeQuery();
                if (resultCheck.next()) {
                    savedMedia = resultCheck.getString("savedMovies");
                }
                if (savedMedia.contains("none")) {
                    String queryNone = "UPDATE sp3_media.user SET savedMovies = REPLACE(savedMovies, 'none', '') WHERE userName = ? AND userPassword = ?";
                    PreparedStatement checkNone = connection.prepareStatement(queryNone);
                    checkNone.setString(1, startMenu.getCurrentUser().getUsername());
                    checkNone.setString(2, startMenu.getCurrentUser().getPassword());
                    checkNone.executeUpdate();
                }

            }
            if (i == 2) {  //savedSeries
                String mediaCheck = "SELECT * FROM sp3_media.user savedSeries WHERE userName = ? AND userPassword = ?";
                String savedMedia = null;
                PreparedStatement statement = connection.prepareStatement(mediaCheck);

                statement.setString(1, startMenu.getCurrentUser().getUsername());
                statement.setString(2, startMenu.getCurrentUser().getPassword());
                ResultSet resultCheck = statement.executeQuery();
                if (resultCheck.next()) {
                    savedMedia = resultCheck.getString("savedSeries");
                }
                if (savedMedia.contains("none")) {
                    String queryNone = "UPDATE sp3_media.user SET savedSeries = REPLACE(savedSeries, 'none', '') WHERE userName = ? AND userPassword = ?";
                    PreparedStatement checkNone = connection.prepareStatement(queryNone);
                    checkNone.setString(1, startMenu.getCurrentUser().getUsername());
                    checkNone.setString(2, startMenu.getCurrentUser().getPassword());
                    checkNone.executeUpdate();
                }

            }
            if (i == 3) {   //watchedMovies
                String mediaCheck = "SELECT * FROM sp3_media.user watchedMovies WHERE userName = ? AND userPassword = ?";
                String savedMedia = null;
                PreparedStatement statement = connection.prepareStatement(mediaCheck);

                statement.setString(1, startMenu.getCurrentUser().getUsername());
                statement.setString(2, startMenu.getCurrentUser().getPassword());
                ResultSet resultCheck = statement.executeQuery();
                if (resultCheck.next()) {
                    savedMedia = resultCheck.getString("watchedMovies");
                }
                if (savedMedia.contains("none")) {
                    String queryNone = "UPDATE sp3_media.user SET watchedMovies = REPLACE(watchedMovies, 'none', '') WHERE userName = ? AND userPassword = ?";
                    PreparedStatement checkNone = connection.prepareStatement(queryNone);
                    checkNone.setString(1, startMenu.getCurrentUser().getUsername());
                    checkNone.setString(2, startMenu.getCurrentUser().getPassword());
                    checkNone.executeUpdate();
                }
            }
            if (i == 4) {   //watchedSeries
                String mediaCheck = "SELECT * FROM sp3_media.user watchedSeries WHERE userName = ? AND userPassword = ?";
                String savedMedia = null;
                PreparedStatement statement = connection.prepareStatement(mediaCheck);

                statement.setString(1, startMenu.getCurrentUser().getUsername());
                statement.setString(2, startMenu.getCurrentUser().getPassword());
                ResultSet resultCheck = statement.executeQuery();
                if (resultCheck.next()) {
                    savedMedia = resultCheck.getString("watchedSeries");
                }
                if (savedMedia.contains("none")) {
                    String queryNone = "UPDATE sp3_media.user SET watchedSeries = REPLACE(watchedMovies, 'none', '') WHERE userName = ? AND userPassword = ?";
                    PreparedStatement checkNone = connection.prepareStatement(queryNone);
                    checkNone.setString(1, startMenu.getCurrentUser().getUsername());
                    checkNone.setString(2, startMenu.getCurrentUser().getPassword());
                    checkNone.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

    }

    public static void deleteMedia(boolean isMovie, String mediatitle) {

        try {
            if (isMovie) {
                String deleteMedia = "UPDATE sp3_media.user SET savedMovies = REPLACE(savedMovies, ?, '') WHERE userName = ? AND userPassword = ?";

                PreparedStatement checkNone = connection.prepareStatement(deleteMedia);
                checkNone.setString(1, mediatitle+",");
                checkNone.setString(2, startMenu.getCurrentUser().getUsername());
                checkNone.setString(3, startMenu.getCurrentUser().getPassword());
                checkNone.executeUpdate();

                String addNone = "UPDATE sp3_media.user SET savedMovies = 'none' WHERE savedMovies = '' AND userName = ? AND userPassword = ?";
                PreparedStatement addingNone = connection.prepareStatement(addNone);
                addingNone.setString(1, startMenu.getCurrentUser().getUsername());
                addingNone.setString(2, startMenu.getCurrentUser().getPassword());
                addingNone.executeUpdate();
            }

            if (!isMovie) {
                String deleteMedia = "UPDATE sp3_media.user SET savedSeries = REPLACE(savedSeries, ?, '') WHERE userName = ? AND userPassword = ?";

                PreparedStatement checkNone = connection.prepareStatement(deleteMedia);
                checkNone.setString(1, mediatitle+",");
                checkNone.setString(2, startMenu.getCurrentUser().getUsername());
                checkNone.setString(3, startMenu.getCurrentUser().getPassword());
                checkNone.executeUpdate();

                String addNone = "UPDATE sp3_media.user SET savedSeries = 'none' WHERE savedSeries = '' AND userName = ? AND userPassword = ?";
                PreparedStatement addingNone = connection.prepareStatement(addNone);
                addingNone.setString(1, startMenu.getCurrentUser().getUsername());
                addingNone.setString(2, startMenu.getCurrentUser().getPassword());
                addingNone.executeUpdate();
            }
        } catch (
                SQLException e) {
            System.out.println(e);
        }
    }




    public static boolean loginCheck (String username, String password){
        String loginCheck = "SELECT * FROM sp3_media.user WHERE userName = ? AND userPassword = ?";
        String passwordCheck = "admin";
        String usernameCheck = "admin";
        try {
            PreparedStatement statement = connection.prepareStatement(loginCheck);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultCheck = statement.executeQuery();
            if (resultCheck.next()) {
                passwordCheck = resultCheck.getString("userPassword");
                usernameCheck = resultCheck.getString("userName");
            }
            if (passwordCheck.contains(password) && usernameCheck.contains(username)) {
                return true;
            }
        }
        catch (SQLException e){
            System.out.println(e);
        }
        return false;
    }

    public static Connection establishConnection()
    {
        try
        {
            connection = DriverManager.getConnection(url, username, password);

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return connection;
    }

}
