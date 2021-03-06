package fadejimi.adegbulugbe.imdbmovietheater.app.models;

import java.io.Serializable;

/**
 * Created by Test on 9/18/2015.
 */
public class Image implements Serializable {
    public static final String SIZE_ORIGINAL = "original";
    public static final String SIZE_MID = "mid";
    public static final String SIZE_COVER = "cover";
    public static final String SIZE_THUMB = "thumb";

    public static final String TYPE_PROFILE = "profile";
    public static final String TYPE_POSTER = "poster";

    public String type;
    public String url;
    public String size;
    public int width;
    public int height;
}
