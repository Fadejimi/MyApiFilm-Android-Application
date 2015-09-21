package fadejimi.adegbulugbe.imdbmovietheater.app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import fadejimi.adegbulugbe.imdbmovietheater.app.Services.GenericSeeker;
import fadejimi.adegbulugbe.imdbmovietheater.app.Services.MovieSeeker;
import fadejimi.adegbulugbe.imdbmovietheater.app.models.Movie;
import org.w3c.dom.Text;

import java.util.List;


public class MainActivity extends ActionBarActivity {
    private static final String EMPTY_STRING = "";

    private EditText searchEditText;
    private TextView searchTypeTextView;
    private Button searchButton;

    private GenericSeeker<Movie> movieSeeker = new MovieSeeker();
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchEditText = (EditText) findViewById(R.id.search_edit_text);
        searchTypeTextView = (TextView) findViewById(R.id.search_type_text_view);
        searchButton = (Button) findViewById(R.id.search_button);

        searchButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = searchEditText.getText().toString();
                performSearch(query);
            }
        });
    }

    private void performSearch(String query) {
        progressDialog = ProgressDialog.show(MainActivity.this, "Please wait...",
                "Retrieving data...", true, true);

        PerformMovieSearchTask task = new PerformMovieSearchTask();
        task.execute(query);
        progressDialog.setOnCancelListener(new CancelTaskOnCancelListener(task));
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
            String query = params[0];
            return movieSeeker.find(query);
        }

        @Override
        protected void onPostExecute(final List<Movie> result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog!=null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    if (result!=null) {
                        Intent intent = new Intent();
                        intent.putExtra("movies", (java.io.Serializable) result);
                        for (Movie movie : result) {
                            longToast(movie.title + " - " + movie.rating);
                        }
                        setResult(RESULT_OK, intent);
                    }
                }
            });
        }
    }

    public void longToast(CharSequence message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private View.OnClickListener radioButtonListener = new View.OnClickListener() {
        public void onClick(View view)
        {
            RadioButton radioButton = (RadioButton) view;
            searchTypeTextView.setText(radioButton.getText().toString());
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

