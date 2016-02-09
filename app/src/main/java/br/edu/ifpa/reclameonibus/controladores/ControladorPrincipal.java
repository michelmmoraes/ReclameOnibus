package br.edu.ifpa.reclameonibus.controladores;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class ControladorPrincipal extends AsyncTask {
    ProgressDialog dialogo;
    Context contexto;

    public ControladorPrincipal(Context con) {
        contexto = con;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return null;
    }
}
