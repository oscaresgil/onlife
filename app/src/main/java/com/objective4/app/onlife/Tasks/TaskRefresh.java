package com.objective4.app.onlife.Tasks;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;

import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.R;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.objective4.app.onlife.Controller.StaticMethods.makeSnackbar;

public class TaskRefresh extends TaskGetFriends {
    private Context context;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public TaskRefresh(Context context, SwipeRefreshLayout mSwipeRefreshLayout){
        super(context,false);
        this.context = context;
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
    }

    @Override protected void onPreExecute() {
        super.onPreExecute();
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        return super.doInBackground(params);
    }

    @Override
    protected void onPostExecute(JSONObject friends) {
        super.onPostExecute(friends);
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setEnabled(true);
        if (friends!=null) makeSnackbar(context,((Activity)context).findViewById(R.id.ActivityHome_CoordinatorLayout),R.string.toast_contacts_refreshed, Snackbar.LENGTH_SHORT);
    }
}
