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
import java.util.HashMap;

import static com.objective4.app.onlife.Controller.StaticMethods.makeSnackbar;

public class TaskAddNewUser extends AsyncTask<ModelPerson, ModelPerson, Boolean> {
    private Context context;
    private ConnectionController connection;
    private boolean networkFailure = false;

    public TaskAddNewUser(Context context){
        connection = new ConnectionController();
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(ModelPerson... params) {
        ModelPerson n = params[0];
        //Parametros a enviar
        HashMap<String,String> p = new HashMap<>();
        p.put("tag", "newUser");
        p.put("id", n.getId() + "");
        p.put("id_phone", n.getId_phone());
        p.put("photo", n.getPhoto());
        p.put("name", n.getName());
        p.put("state", n.getState());
        try {
            JSONObject json = connection.makeHttpRequest("person.php", p);
            boolean error = json.getBoolean("error");
            return error;
        }catch(ConnectException e){
            networkFailure = true;
        }catch (Exception ignored){
        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean==null)makeSnackbar(context, ((Activity) context).findViewById(R.id.ActivityMain_ImageViewLogo), R.string.error, Snackbar.LENGTH_LONG);
        else if(networkFailure) makeSnackbar(context, ((Activity) context).findViewById(R.id.ActivityMain_ImageViewLogo), R.string.no_connection, Snackbar.LENGTH_LONG);
    }
}
