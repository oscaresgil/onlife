package com.objective4.app.onlife.Tasks;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;

import com.kenny.snackbar.SnackBar;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.R;

import java.util.ArrayList;

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
    protected ArrayList<ModelPerson> doInBackground(String... params) {
        return super.doInBackground(params);
    }

    @Override
    protected void onPostExecute(ArrayList<ModelPerson> friends) {
        super.onPostExecute(friends);
        mSwipeRefreshLayout.setRefreshing(false);
        SnackBar.show((Activity) context, R.string.toast_contacts_refreshed);
    }
}
