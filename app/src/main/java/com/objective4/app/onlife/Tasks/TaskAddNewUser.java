package com.objective4.app.onlife.Tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import com.objective4.app.onlife.Activities.ActivityMain;
import com.objective4.app.onlife.Controller.JSONParser;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.makeSnackbar;

public class TaskAddNewUser extends AsyncTask<ModelPerson, ModelPerson, Boolean> {
    private Context context;
    private JSONParser jsonParser;

    public TaskAddNewUser(Context context){
        jsonParser = new JSONParser();
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(ModelPerson... params) {
        ModelPerson n = params[0];
        //Parametros a enviar
        List<NameValuePair> p = new ArrayList<>();
        p.add(new BasicNameValuePair("tag", "newUser"));
        p.add(new BasicNameValuePair("id", n.getId() + ""));
        p.add(new BasicNameValuePair("id_phone", n.getId_phone()));
        p.add(new BasicNameValuePair("photo", n.getPhoto()));
        p.add(new BasicNameValuePair("name", n.getName()));
        p.add(new BasicNameValuePair("state", n.getState()));
        try {
            JSONObject json = jsonParser.makeHttpRequest(ActivityMain.SERVER_URL, "POST", p);
            boolean error = json.getBoolean("error");
            return error;
        }catch(ConnectException e){
            return null;
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean==null){
            makeSnackbar(context, ((Activity) context).findViewById(R.id.ActivityMain_ImageViewLogo), R.string.no_connection, Snackbar.LENGTH_LONG);
        }
    }
}
