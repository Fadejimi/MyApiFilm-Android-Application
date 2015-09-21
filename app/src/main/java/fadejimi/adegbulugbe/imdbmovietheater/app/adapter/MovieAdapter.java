package fadejimi.adegbulugbe.imdbmovietheater.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fadejimi.adegbulugbe.imdbmovietheater.app.R;
import fadejimi.adegbulugbe.imdbmovietheater.app.Services.HttpReciever;
import fadejimi.adegbulugbe.imdbmovietheater.app.io.FlushedInputStream;
import fadejimi.adegbulugbe.imdbmovietheater.app.models.Movie;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Test on 9/19/2015.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private HttpReciever httpRetriever = new HttpReciever();

    private ArrayList<Movie> movieDataItems;

    private Activity context;

    public MovieAdapter(Activity context, int textViewResourceId, ArrayList<Movie> movieDataItems) {
        super(context, textViewResourceId, movieDataItems);
        this.context = context;
        this.movieDataItems = movieDataItems;
    }

    public void setItemList(ArrayList<Movie> itemList) {
        this.movieDataItems = itemList;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.activity_movie_data_row, null);
        }

        Movie movie = movieDataItems.get(position);

        if (movie != null) {

            // name
            TextView nameTextView = (TextView) view.findViewById(R.id.name_text_view);
            nameTextView.setText(movie.title);

            // rating
            TextView ratingTextView = (TextView) view.findViewById(R.id.rating_text_view);
            ratingTextView.setText("Rating: " + movie.rating);

            // released
            TextView releasedTextView = (TextView) view.findViewById(R.id.released_text_view);
            releasedTextView.setText("Release Date: " + movie.year);

            // certification
            TextView plotTextView = (TextView) view.findViewById(R.id.plot_text_view);
            plotTextView.setText("Plot: " + movie.simplePlot);

            // Date
            TextView dateTextView = (TextView) view.findViewById(R.id.dateTextView);
            Calendar c = Calendar.getInstance();
            System.out.println("Current time => "+c.getTime());

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = df.format(c.getTime());
            dateTextView.setText("Date: " + formattedDate);

            // thumb image
            ImageView imageView = (ImageView) view.findViewById(R.id.movie_thumb_icon);
            String url = movie.urlPoster;

            if (url!=null) {
                Bitmap bitmap = fetchBitmapFromCache(url);
                if (bitmap==null) {
                    new BitmapDownloaderTask(imageView).execute(url);
                }
                else {
                    imageView.setImageBitmap(bitmap);
                }
            }
            else {
                imageView.setImageBitmap(null);
            }

        }

        return view;

    }

    private LinkedHashMap<String, Bitmap> bitmapCache = new LinkedHashMap<String, Bitmap>();

    private void addBitmapToCache(String url, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (bitmapCache) {
                bitmapCache.put(url, bitmap);
            }
        }
    }

    private Bitmap fetchBitmapFromCache(String url) {

        synchronized (bitmapCache) {
            final Bitmap bitmap = bitmapCache.get(url);
            if (bitmap != null) {
                // Bitmap found in cache
                // Move element to first position, so that it is removed last
                bitmapCache.remove(url);
                bitmapCache.put(url, bitmap);
                return bitmap;
            }
        }

        return null;

    }

    private class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {

        private String url;
        private final WeakReference<ImageView> imageViewReference;

        public BitmapDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            url = params[0];
            InputStream is = httpRetriever.retrieveStream(url);
            if (is==null) {
                return null;
            }
            return BitmapFactory.decodeStream(new FlushedInputStream(is));
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            addBitmapToCache(url, bitmap);

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

}