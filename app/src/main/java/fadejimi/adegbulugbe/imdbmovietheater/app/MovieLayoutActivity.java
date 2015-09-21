package fadejimi.adegbulugbe.imdbmovietheater.app;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.*;
import fadejimi.adegbulugbe.imdbmovietheater.app.Services.GenericSeeker;
import fadejimi.adegbulugbe.imdbmovietheater.app.Services.HttpReciever;
import fadejimi.adegbulugbe.imdbmovietheater.app.Services.MovieSeeker;
import fadejimi.adegbulugbe.imdbmovietheater.app.adapter.MovieAdapter;
import fadejimi.adegbulugbe.imdbmovietheater.app.io.FlushedInputStream;
import fadejimi.adegbulugbe.imdbmovietheater.app.io.Utils;
import fadejimi.adegbulugbe.imdbmovietheater.app.models.Movie;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MovieLayoutActivity extends ListActivity {
    private String BASE_URL = "http://m.imdb.com/title/";

    private static final int ITEM_VISIT_IMDB = 0;
    private static final int ITEM_VIEW_FULL_IMAGE = 1;
    private static final int ITEM_SEARCH_IMAGE = 2;

    private ArrayList<Movie> moviesList;
    private MovieAdapter moviesAdapter;

    private HttpReciever reciever = new HttpReciever();

    private ProgressDialog progressDialog;
    private ImageView imageView;
    private GenericSeeker<Movie> movieSeeker = new MovieSeeker();
    private ProgressDialog progressDialogOld;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_layout);

        moviesAdapter = new MovieAdapter(this, R.layout.activity_movie_data_row, new ArrayList<Movie>());
        //ListView lv = (ListView) findViewById(R.id.listView);
        setListAdapter(moviesAdapter);

        moviesAdapter.notifyDataSetChanged();

        //performSearch();
        (new PerformMovieSearchTask()).execute("http://www.myapifilms.com/imdb/inTheaters?format=JSON&lang=en-us&token=8c277cae-9c14-4d87-9c95-8ad756f42b3c");
    }


    private class PerformMovieSearchTask extends AsyncTask<String, Void, ArrayList<Movie>> {
        private final ProgressDialog dialog = new ProgressDialog(MovieLayoutActivity.this);

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            Log.d(getClass().getSimpleName(), String.valueOf(movieSeeker.find()));
            ArrayList<Movie> result = new ArrayList<Movie>();
            int responseCode = -1;
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(params[0]);
            //return movieSeeker.find();
            try {
                HttpResponse response = client.execute(httpget);
                StatusLine statusLine = response.getStatusLine();
                responseCode = statusLine.getStatusCode();
                // Read the stream
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while((line = reader.readLine()) != null){
                        builder.append(line);
                    }

                    //jsonResponse = new JSONObject(builder.toString());
                }
                else {
                    Log.i(getClass().getSimpleName(), String.format("Unsuccessful HTTP response code: %d", responseCode));
                }

                JSONArray jsonResult = new JSONArray(builder.toString());
                JSONObject arr = jsonResult.getJSONObject(1);
                JSONArray moviesArr = arr.getJSONArray("movies");

                for (int i=0; i < moviesArr.length(); i++) {
                    //Log.d(getClass().getSimpleName(), String.valueOf(convertMovie(moviesArr.getJSONObject(i))));
                    JSONObject movieData =  moviesArr.getJSONObject(i);
                    result.add(convertMovie(movieData));
                    Log.d(getClass().getSimpleName(), String.valueOf(convertMovie(movieData)));
                }

                return result;
            }
            catch(Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        private Movie convertMovie(JSONObject obj) throws JSONException {
            String idIDMB = obj.getString("idIMDB");
            String plot = obj.getString("plot");
            String rating = obj.getString("rating");
            String simplePlot = obj.getString("simplePlot");
            String title = obj.getString("title");
            String urlPoster = obj.getString("urlPoster");
            String year = obj.getString("year");
            return new Movie(idIDMB, plot, rating, simplePlot, title, urlPoster, year);
        }

        @Override
        protected void onPostExecute(final ArrayList<Movie> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            moviesAdapter.setItemList(result);
            moviesAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Downloading movies...");
            dialog.show();
        }
    }
    public void longToast(CharSequence message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(Menu.NONE, ITEM_VISIT_IMDB, 0, getString(R.string.visit_imdb)).setIcon(R.drawable.ic_visit_imdb);
        menu.add(Menu.NONE, ITEM_VIEW_FULL_IMAGE, 0, getString(R.string.view_full_image)).setIcon(
                R.drawable.ic_menu_zoom);
        menu.add(Menu.NONE, ITEM_SEARCH_IMAGE, 0, getString(R.string.search_title)).setIcon(R.drawable.ic_menu_search);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()){
            case ITEM_VISIT_IMDB:
                visitImdbMoviePage();
                return true;
            case ITEM_VIEW_FULL_IMAGE:
                viewFullImagePoster();
                return true;
            case ITEM_SEARCH_IMAGE:
                searchTitle();
                return true;
        }
        return false;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);

        final Movie movie = moviesAdapter.getItem((int)position);
        showMovieOverviewDialog(movie.title, movie.plot);
    }

    private void viewFullImagePoster()
    {
        final Movie movie = retrieveSelectedMovie();

        if(movie == null){
            longToast(getString(R.string.no_movie_selected));
            return;
        }

        String imageUrl = movie.urlPoster;
        if(Utils.isMissing(imageUrl)){
            longToast(getString(R.string.no_image_found));
            return;
        }

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.full_image_layout);

        final Button closeDialogButton = (Button) dialog.findViewById(R.id.close_full_image_dialog_button);
        imageView = (ImageView) dialog.findViewById(R.id.image_view);

        closeDialogButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        final ImageDownloaderTask task = new ImageDownloaderTask();
        task.execute(imageUrl);

        dialog.show();

        progressDialog = progressDialog.show(MovieLayoutActivity.this, "Please wait....",
                "Retrieving data...", true, true);

        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if(task != null){
                    task.cancel(true);
                }
            }
        });
    }

    private void searchTitle()
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        if(requestCode == 0 && resultCode == RESULT_OK) {
            if (data.hasExtra("movies")) {
                moviesList = (ArrayList<Movie>) getIntent().getSerializableExtra("movies");

                setListAdapter(moviesAdapter);

                if (moviesList!=null && !moviesList.isEmpty()) {

                    moviesAdapter.notifyDataSetChanged();
                    moviesAdapter.clear();
                    for (int i = 0; i < moviesList.size(); i++) {
                        moviesAdapter.add(moviesList.get(i));
                    }
                }

                moviesAdapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void visitImdbMoviePage()
    {
        final Movie movie = retrieveSelectedMovie();

        if (movie == null)
        {
            longToast(getString(R.string.no_movie_selected));
            return;
        }

        String imdbId = movie.idIMDB;
        if(Utils.isMissing(imdbId)){
            longToast(getString(R.string.no_imdb_id_found));
            return;
        }

        String imdbUrl = BASE_URL + imdbId;

        Intent imdbIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(imdbUrl));
        startActivity(imdbIntent);
    }

    private void showMovieOverviewDialog(final String title, final String overview)
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.movie_overview_dialog);

        dialog.setTitle(title);

        final TextView overViewTextView = (TextView) dialog.findViewById(R.id.movie_overview_text_view);
        overViewTextView.setText(overview);

        final Button closeButton = (Button) dialog.findViewById(R.id.movie_overview_close_button);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            InputStream in = reciever.retrieveStream(url);
            if(in == null){
                return null;
            }
            return BitmapFactory.decodeStream(new FlushedInputStream(in));
        }

        @Override
        protected void onPostExecute(final Bitmap result){
            runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    if (progressDialog != null){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    if (result != null){
                        imageView.setImageBitmap(result);
                    }
                }
            });
        }
    }

    private Movie retrieveSelectedMovie() {
        int position = getSelectedItemPosition();
        if (position == -1){
            return null;
        }
        return moviesAdapter.getItem(position);
    }
}

