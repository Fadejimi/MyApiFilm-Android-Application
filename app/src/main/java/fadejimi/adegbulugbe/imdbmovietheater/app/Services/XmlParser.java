package fadejimi.adegbulugbe.imdbmovietheater.app.Services;

import android.util.Log;
import fadejimi.adegbulugbe.imdbmovietheater.app.handlers.MovieHandler;
import fadejimi.adegbulugbe.imdbmovietheater.app.models.Movie;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by Test on 9/19/2015.
 */
public class XmlParser {

    private XMLReader initializeReader() throws ParserConfigurationException, SAXException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        // create a parser
        SAXParser parser = factory.newSAXParser();
        // create the reader (scanner)
        XMLReader xmlreader = parser.getXMLReader();
        return xmlreader;
    }

    public ArrayList<Movie> parseMoviesResponse(String xml) {

        try {

            XMLReader xmlreader = initializeReader();

            MovieHandler movieHandler = new MovieHandler();

            // assign our handler
            xmlreader.setContentHandler(movieHandler);
            // perform the synchronous parse
            xmlreader.parse(new InputSource(new StringReader(xml)));

            Log.d(getClass().getSimpleName(), String.valueOf(movieHandler.retrieveMoviesList()));
            return movieHandler.retrieveMoviesList();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}