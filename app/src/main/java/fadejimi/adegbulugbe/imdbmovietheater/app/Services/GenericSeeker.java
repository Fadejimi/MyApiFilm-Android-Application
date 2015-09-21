package fadejimi.adegbulugbe.imdbmovietheater.app.Services;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Test on 9/18/2015.
 */
public abstract class GenericSeeker<E> {

    protected static final String BASE_URL_SEARCH = "http://www.myapifilms.com/imdb?";
    protected static final String BASE_URL_THEATER = "http://www.myapifilms.com/imdb/inTheaters?";
    protected static final String LANGUAGE_PATH = "lang=en-us&";
    protected static final String FORMAT = "format=XML&";
    protected static final String TOKEN = "token=8c277cae-9c14-4d87-9c95-8ad756f42b3c&";
    protected static final String SLASH = "&";

    protected HttpReciever httpRetriever = new HttpReciever();
    protected XmlParser xmlParser = new XmlParser();

    public abstract ArrayList<E> find(String query);
    public abstract ArrayList<E> find(String query, int maxResults);
    public abstract ArrayList<E> find();

    //public abstract String retrieveSearchMethodPath();

    protected String constructSearchUrl(String query){
        StringBuffer sb = new StringBuffer();
        sb.append(BASE_URL_SEARCH);
        sb.append("title="+ query);
        sb.append(SLASH);
        sb.append(FORMAT);
        //sb.append(retrieveSearchMethodPath());
        sb.append(LANGUAGE_PATH);
        sb.append(TOKEN);
        return sb.toString();
    }

    protected String constructTheaterUrl(){
        StringBuffer sb = new StringBuffer();
        sb.append(BASE_URL_THEATER);
        sb.append(FORMAT);
        sb.append(LANGUAGE_PATH);
        sb.append(TOKEN);
        return sb.toString();
    }
    public ArrayList<E> retrieveFirstResults(ArrayList<E> list, int maxResults){
        ArrayList<E> newList = new ArrayList<E>();
        int count = Math.min(list.size(), maxResults);
        for(int i=0; i<count; i++)
        {
            newList.add(list.get(i));
        }
        return newList;
    }
}
