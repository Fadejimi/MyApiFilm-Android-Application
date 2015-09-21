package fadejimi.adegbulugbe.imdbmovietheater.app.Services;


import android.util.Log;
import fadejimi.adegbulugbe.imdbmovietheater.app.models.Movie;

import java.util.ArrayList;

/**
 * Created by Test on 9/18/2015.
 */
public class MovieSeeker extends GenericSeeker<Movie> {

    @Override
    public ArrayList<Movie> find(String query) {
        ArrayList<Movie> moviesList = retrieveMoviesList(query);
        return moviesList;
    }

    @Override
    public ArrayList<Movie> find(String query, int maxResults) {
        ArrayList<Movie> moviesList = retrieveMoviesList(query);
        return retrieveFirstResults(moviesList, maxResults);
    }

    @Override
    public ArrayList<Movie> find() {
        ArrayList<Movie> moviesList = retrieveMoviesList();
        return moviesList;
    }

    public ArrayList<Movie> retrieveMoviesList(String query) {
        String url = constructSearchUrl(query);
        String response = httpRetriever.retrieve(url);
        Log.d(getClass().getSimpleName(), response);
        return xmlParser.parseMoviesResponse(response);
    }

    public ArrayList<Movie> retrieveMoviesList()
    {
        String url = constructTheaterUrl();
        String response = httpRetriever.retrieve(url);
        Log.d(getClass().getSimpleName(), response);
        return xmlParser.parseMoviesResponse(response);
    }
}
