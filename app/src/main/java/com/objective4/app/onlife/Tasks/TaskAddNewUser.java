package com.objective4.app.onlife.Tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import com.objective4.app.onlife.Controller.ConnectionController;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.R;

import org.json.JSONObject;

import java.net.ConnectException;

import static com.objective4.app.onlife.Controller.StaticMethods.makeSnackbar;

public class TaskAddNewUser extends AsyncTask<JSONObject, ModelPerson, JSONObject> {
    private Context context;
    private boolean networkFailure = false;

    public TaskAddNewUser(Context context){
        this.context = context;
    }

    @Override
    protected JSONObject doInBackground(JSONObject... params) {
        try{
            return new ConnectionController().makeHttpRequest(params[0]);
        }catch(ConnectException e){
            networkFailure = true;
        }catch (Exception ignored){}
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean==null)makeSnackbar(context, ((Activity) context).findViewById(R.id.ActivityMain_ImageViewLogo), R.string.error, Snackbar.LENGTH_LONG);
        else if(networkFailure) makeSnackbar(context, ((Activity) context).findViewById(R.id.ActivityMain_ImageViewLogo), R.string.no_connection, Snackbar.LENGTH_LONG);
    }
}
