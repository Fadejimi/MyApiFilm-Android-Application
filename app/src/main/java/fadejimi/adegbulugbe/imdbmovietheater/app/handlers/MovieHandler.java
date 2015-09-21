package fadejimi.adegbulugbe.imdbmovietheater.app.handlers;

import fadejimi.adegbulugbe.imdbmovietheater.app.models.Image;
import fadejimi.adegbulugbe.imdbmovietheater.app.models.Movie;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * Created by Test on 9/18/2015.
 */
public class MovieHandler extends DefaultHandler {
    private StringBuffer buffer = new StringBuffer();

    private ArrayList<Movie> moviesList;
    private Movie movie;
    private ArrayList<Image> movieImageList;
    private Image movieImage;

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes accts)
            throws SAXException {
        buffer.setLength(0);

        if(localName.equals("movies"))
        {

                moviesList = new ArrayList<Movie>();

        }
        else if (localName.equals("movie")) {
            movie = new Movie();
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {
        if (localName.equals("movie")){
            moviesList.add(movie);
        }
        else if (localName.equals("idIMDB")){
            movie.idIMDB = buffer.toString();
        }
        else if (localName.equals("plot")){
            movie.plot = buffer.toString();
        }
        else if (localName.equals("rating")){
            movie.rating = buffer.toString();
        }
        else if(localName.equals("title")){
            movie.title = buffer.toString();
        }
        else if (localName.equals("urlPoster")){
            movie.urlPoster = buffer.toString();
        }
        else if (localName.equals("year")){
            movie.year = buffer.toString();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length){
        buffer.append(ch, start, length);
    }

    public ArrayList<Movie> retrieveMoviesList()
    {
        return moviesList;
    }
}
