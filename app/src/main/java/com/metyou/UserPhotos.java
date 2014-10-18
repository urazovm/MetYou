package com.metyou;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.metyou.fragments.friends.refresher.CustomSpinner;

import org.json.JSONObject;


public class UserPhotos extends Activity {

    private static final String TAG = "UserPhotos";
    private GridView gridView;
    private CustomSpinner customSpinner;
    private TextView emptyText;
    private String socialId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_photos);
        setTitle(getIntent().getStringExtra("firstName") + "'s photos");
        socialId = getIntent().getStringExtra("socialId");

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);

        gridView = (GridView) findViewById(R.id.grid_photos);
        //gridView.setAdapter(new ImageAdapter());
        View emptyView = findViewById(android.R.id.empty);
        customSpinner = (CustomSpinner) emptyView.findViewById(R.id.spinner);
        emptyText = (TextView) emptyView.findViewById(R.id.empty_text);
        gridView.setEmptyView(emptyView);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.user_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Request request = new Request(Session.getActiveSession(),
                "/" + socialId + "/photos",
                null,
                HttpMethod.GET,
                new Request.Callback() {
                    public void onCompleted(Response response) {
                        customSpinner.setVisibility(View.INVISIBLE);
                        processPhotos(response);
                    }
                }
        );
        Bundle params = new Bundle();
        params.putString("limit", "10");

        request.setParameters(params);
        request.executeAsync();
    }

    private void processPhotos(Response response) {
        GraphObject graphObject = response.getGraphObject();
        GraphObjectList<GraphObject> graphObjectList = graphObject.getPropertyAsList("data", GraphObject.class);
        for (GraphObject obj : graphObjectList) {
            Log.d(TAG, (String)obj.getProperty("source"));
        }
    }
}
