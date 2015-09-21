package fadejimi.adegbulugbe.imdbmovietheater.app.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Test on 9/18/2015.
 */
public class Movie implements Serializable {
    public String idIMDB;
    public String plot;
    public String rating;
    public String simplePlot;
    public String title;
    public String urlPoster;
    public String year;

    public Movie()
    {

    }

    public Movie(String id, String plot, String rating, String simple, String title, String url, String y)
    {
        this.idIMDB = id;
        this.plot = plot;
        this.rating = rating;
        this.simplePlot = simple;
        this.title = title;
        this.urlPoster = url;
        this.year = y;
    }
    //public ArrayList<Image> imagesList;

    /*public String retrieveThumbnail(){
        if (imagesList != null && imagesList.isEmpty()) {
            for (Image movieImage : imagesList)
            {
                if (movieImage.size.equalsIgnoreCase(Image.SIZE_THUMB) &&
                        movieImage.type.equalsIgnoreCase(Image.TYPE_POSTER)){
                    return movieImage.url;
                }
            }
        }
        return null;
    }

    public String retrieveCoverImage() {
        if (imagesList!=null && !imagesList.isEmpty()) {
            for (Image movieImage : imagesList) {
                if (movieImage.size.equalsIgnoreCase(Image.SIZE_COVER) &&
                        movieImage.type.equalsIgnoreCase(Image.TYPE_POSTER)) {
                    return movieImage.url;
                }
            }
        }
        return null;
    }*/

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Movie [name=");
        builder.append(title);
        builder.append("]");
        return builder.toString();
    }
}
