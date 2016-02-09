package br.edu.ifpa.reclameonibus.interfaces.api_sptrans_olhovivo;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifpa.reclameonibus.componentes.Linha;

public class DetectaCenario extends AsyncTask<Integer,String,Double>{
    HttpClient httpClient;
    boolean appAutenticado = false;
    Context con;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    List<Linha> listaLinhas;
    public DetectaCenario(Context c, NotificationManager nm, NotificationCompat.Builder mb) {
        httpClient = new DefaultHttpClient();
        con = c;
        listaLinhas = new ArrayList<>();
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        System.out.println("onPreExecute()");
    }

    @Override
    protected Double doInBackground(Integer... params) {
        System.out.println("doInBackground(Integer... params)");
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        System.out.println("onProgressUpdate(String... values)");
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Double aDouble) {
        System.out.println("onPostExecute(Double aDouble)");
        super.onPostExecute(aDouble);
    }
}
