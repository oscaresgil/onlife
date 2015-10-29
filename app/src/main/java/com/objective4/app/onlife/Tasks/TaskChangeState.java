package com.objective4.app.onlife.Tasks;

import android.os.AsyncTask;
import com.objective4.app.onlife.Controller.ConnectionController;
import org.json.JSONObject;
import java.net.ConnectException;
import java.util.HashMap;

public class TaskChangeState extends AsyncTask<String,Void,Boolean> {
    private ConnectionController connection;

    public TaskChangeState() {
        connection = new ConnectionController();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String ... params) {
        String id = params[0];
        String state = params[1];

        HashMap<String,String> p = new HashMap<>();
        p.put("tag", "changeState");
        p.put("id", id);
        p.put("state", state);

        try{
            JSONObject response = connection.makeHttpRequest("person.php", p);
            return response.getBoolean("error");
        }catch(ConnectException e){
            return null;
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
