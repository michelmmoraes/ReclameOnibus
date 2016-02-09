package br.edu.ifpa.reclameonibus.telas.parada;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.edu.ifpa.reclamenibus.R;
import br.edu.ifpa.reclameonibus.auxiliares.RespostaCenario;
import br.edu.ifpa.reclameonibus.componentes.Linha;
import br.edu.ifpa.reclameonibus.servicos.MonitoraOnibus;

public class TelaParada extends ActionBarActivity {
    TextView infoDestinoParada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_parada);
        Bundle extras = getIntent().getExtras();
        int cont = 0;
        int idParada = 0;
        String referencia = null;
        try {
            cont = extras.getInt("cont");
            idParada = (int) extras.getInt("idparada");
            referencia = extras.getString("referencia");
        } catch (Exception e) {
            //Log.e("parada",e.getMessage());
            e.printStackTrace();
        }
        //TextView tv = (TextView) findViewById(R.id.infoParada);
        //tv.setText("cont=" + cont + "\n" + idParada + "-" + referencia);
        setResult(200);
        infoDestinoParada = (TextView) findViewById(R.id.infoDestinoParada);
    }

    ProgressDialog progressDialog;

    public void exibirListaLinhas(View v) {
        MostraLinhas mostraLinhas = new MostraLinhas();
        mostraLinhas.execute();
    }

    private void monitorarProximoOnibus(int codigoLinha) {
        setTitle("Esperando ônibus...");
        RespostaCenario.CENARIO_DETECTADO = RespostaCenario.PARADA;
        Intent intent = new Intent(this, MonitoraOnibus.class);
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tela_parada, menu);
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

    class MostraLinhas extends AsyncTask {

        HttpClient httpClient = new DefaultHttpClient();
        boolean appAutenticado = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(TelaParada.this, "Aguarde...",
                    "Buscando linhas de ônibus...");
        }

        @Override
        protected Object doInBackground(Object[] params) {
            autenticarSe();
            return buscarTodasLinhas();
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage((CharSequence) values[0]);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            List<Linha> listaLinhas = (List<Linha>) o;
            final Linha linha = new Linha();
            final String[] lista;
            final Linha[] linhaSelecionada = new Linha[1];
            lista = new String[listaLinhas.size() + 1];
            for (int i = 0; i < listaLinhas.size(); i++) {
                lista[i] = listaLinhas.get(i).getDenominacaoTPTS() + " /\n " +
                        listaLinhas.get(i).getDenominacaoTSTP();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(TelaParada.this);
            builder.setTitle("Em qual linha de ônibus você vai?");
            final List<Linha> finalListaLinhas = listaLinhas;
            builder.setItems(lista, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    linha.setCodigoLinha(finalListaLinhas.get(which).getCodigoLinha());
                    infoDestinoParada.setText("Esperando um ônibus " +
                            " da linha " + lista[which] +
                            "(" + linha.getCodigoLinha() + ")");
                    Toast.makeText(TelaParada.this, "Linha selecionada: " +
                            finalListaLinhas.get(which).getCodigoLinha(), Toast.LENGTH_SHORT).show();
                    monitorarProximoOnibus(finalListaLinhas.get(which).getCodigoLinha());
                }
            });
            progressDialog.dismiss();
            builder.show();

        }

        private boolean autenticarSe() {
            HttpPost httpPost = new HttpPost("http://api.olhovivo.sptrans.com.br/v0/" +
                    "Login/Autenticar?token=" +
                    "79ff26567c7db5b8b7be53a1d0f7fa1f64572140dbb9a66e9945b2ba81c7b75d");
            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                boolean autenticou = Boolean.parseBoolean(EntityUtils.toString(httpEntity));
                appAutenticado = autenticou;
                return autenticou;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        private List<Linha> buscarTodasLinhas() {
            List<Linha> listaLinhas = new ArrayList<>();
            JSONArray arrayTodasLinhas = new JSONArray();
            Log.e("linha", "buscando linhas...");
            for (int i = 0; i < 10; i++) {
                HttpGet httpGet = new HttpGet("http://api.olhovivo.sptrans.com.br/v0" +
                        "/Linha/Buscar?termosBusca=" + i);
                publishProgress((i * 100 / 10) + "%");
                Log.e("linha", "buscando linhas... letreiros começando em " + i);
                try {
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    String respostaJSON = EntityUtils.toString(httpEntity);
                    JSONArray jsonArray = new JSONArray(respostaJSON);
                    arrayTodasLinhas.put(jsonArray);
                    Log.e("linha", "letreiros começando em " + i + " têm " + jsonArray.length() + " linhas.");
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject linhaDaVez = jsonArray.getJSONObject(j);
                        Log.e("linha", "letreiro começa com " + i + ", linha nº " + j
                                + ", codigoLinha=" + linhaDaVez.getInt("CodigoLinha"));
                        Linha linha = new Linha();
                        linha.setCodigoLinha(linhaDaVez.getInt("CodigoLinha"));
                        linha.setCircular(linhaDaVez.getBoolean("Circular"));
                        linha.setLetreiro(linhaDaVez.getString("Letreiro"));
                        linha.setSentido(linhaDaVez.getString("Sentido"));
                        linha.setTipo(linhaDaVez.getString("Tipo"));
                        linha.setDenominacaoTPTS(linhaDaVez.getString("DenominacaoTPTS"));
                        linha.setDenominacaoTSTP(linhaDaVez.getString("DenominacaoTSTP"));
                        listaLinhas.add(linha);
                    }
                } catch (IOException | JSONException e) {
                    publishProgress("ERRO AO BUSCAR LINHAS!\n" + e.getMessage());
                    Log.e("linha", "buscando linhas... erro:" + e.getMessage());
                    e.printStackTrace();
                }
            }
            Log.e("linha", "foram encontradas " + listaLinhas.size() + " linhas.");
            return listaLinhas;
        }
    }
}
