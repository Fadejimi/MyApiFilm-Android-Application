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

import java.io.InputStream;
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

        performSearch();
    }

    private void performSearch() {
        progressDialogOld = ProgressDialog.show(MovieLayoutActivity.this, "Please wait...",
                "Retrieving data...", true, true);

        PerformMovieSearchTask task = new PerformMovieSearchTask();
        task.execute();
        progressDialogOld.setOnCancelListener(new CancelTaskOnCancelListener(task));
    }

    private class CancelTaskOnCancelListener implements DialogInterface.OnCancelListener {
        private AsyncTask<?, ?, ?> task;

        public CancelTaskOnCancelListener(AsyncTask<?, ?, ?> task) {
            this.task = task;
        }
        @Override
        public void onCancel(DialogInterface dialogInterface) {
            if(task != null)
            {
                task.cancel(true);
            }
        }
    }

    private class PerformMovieSearchTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(String... params) {
            return movieSeeker.find();
        }

        @Override
        protected void onPostExecute(final List<Movie> result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog!=null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                        longToast("Error Check your internet connection");
                    }
                    if (result!=null) {
                        /*for (Movie movie : result) {
                            longToast(movie.title + " - " + movie.rating);
                        }*/
                        moviesList = (ArrayList<Movie>) result;
                        moviesAdapter = new MovieAdapter(MovieLayoutActivity.this, R.layout.activity_movie_data_row,
                                moviesList);

                        setListAdapter(moviesAdapter);

                        if (moviesList != null && !moviesList.isEmpty()){

                            moviesAdapter.notifyDataSetChanged();
                            moviesAdapter.clear();
                            for(int i = 0; i < moviesList.size(); i++){
                                moviesAdapter.add(moviesList.get(i));
                            }
                        }

                        moviesAdapter.notifyDataSetChanged();
                    }
                }
            });
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

