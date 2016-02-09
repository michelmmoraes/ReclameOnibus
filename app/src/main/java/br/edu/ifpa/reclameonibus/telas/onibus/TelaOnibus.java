package br.edu.ifpa.reclameonibus.telas.onibus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import br.edu.ifpa.reclameonibus.componentes.Parada;
import br.edu.ifpa.reclameonibus.localizacao.Localizacao;
import br.edu.ifpa.reclameonibus.servicos.MonitoraOnibus;

public class TelaOnibus extends ActionBarActivity {
    TextView infoDestinoParada;
    Button botaoSelecionarParada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_onibus);
        infoDestinoParada = (TextView) findViewById(R.id.infoDestinoParada);
        botaoSelecionarParada = (Button) findViewById(R.id.botaoSelecionarDestino);
    }

    ProgressDialog progressDialog;

    public void exibirListaParadas(View v) {
        MostraParadas mostraParadas = new MostraParadas();
        mostraParadas.execute();
    }

    private void monitorarChegadaAoDestino(int codigoParada) {
        setTitle("Chegando ao destino...");
        RespostaCenario.CENARIO_DETECTADO = RespostaCenario.ONIBUS;
        Intent intent = new Intent(this, MonitoraOnibus.class);
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tela_onibus, menu);
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

    class MostraParadas extends AsyncTask {

        HttpClient httpClient = new DefaultHttpClient();
        boolean appAutenticado = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(TelaOnibus.this, "Aguarde...",
                    "Buscando paradas de ônibus...");
        }

        @Override
        protected Object doInBackground(Object[] params) {
            autenticarSe();
            return buscarTodasParadas();
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage("Progresso: " + values[0] + "%");
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            List<Parada> listaParadas = (List<Parada>) o;
            final Parada parada = new Parada();
            final String[] lista;
            final Parada[] paradaSelecionada = new Parada[1];
            lista = new String[listaParadas.size() + 1];
            for (int i = 0; i < listaParadas.size(); i++) {
                lista[i] = listaParadas.get(i).getEndereco() +
                        "\n(" + listaParadas.get(i).getCodigoParada() + ")";
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(TelaOnibus.this);
            builder.setTitle("Em qual parada você vai descer?");
            final List<Parada> finalListaParadas = listaParadas;
            builder.setItems(lista, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    parada.setCodigoParada(finalListaParadas.get(which)
                            .getCodigoParada());
                    infoDestinoParada.setText("Você pretende descer na parada "
                            + lista[which]);
                    Toast.makeText(TelaOnibus.this, "Parada selecionada: " +
                                    finalListaParadas.get(which).getCodigoParada(),
                            Toast.LENGTH_SHORT).show();
                    monitorarChegadaAoDestino(finalListaParadas.get(which).getCodigoParada());
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

        private List<Parada> buscarTodasParadas() {
            List<Parada> listaParadas = new ArrayList<>();
            JSONArray arrayTodasParadas = new JSONArray();
            HttpGet httpGet = new HttpGet("http://api.olhovivo.sptrans.com.br/v0" +
                    "/Parada/Buscar?termosBusca=");
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                String respostaJSON = EntityUtils.toString(httpEntity);
                JSONArray jsonArray = new JSONArray(respostaJSON);
                arrayTodasParadas.put(jsonArray);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject paradaDaVez = jsonArray.getJSONObject(i);
                    Parada parada = new Parada();
                    parada.setCodigoParada(paradaDaVez.getInt("CodigoParada"));
                    parada.setNome(paradaDaVez.getString("Nome"));
                    parada.setEndereco(paradaDaVez.getString("Endereco"));
                    Localizacao localizacao = new Localizacao();
                    localizacao.setLatitude(Double.valueOf(paradaDaVez.getString("Latitude")));
                    localizacao.setLongitude(Double.valueOf(paradaDaVez.getString("Longitude")));
                    parada.setLocalizacao(localizacao);
                    listaParadas.add(parada);
                }
            } catch (IOException | JSONException e) {
                publishProgress("ERRO AO BUSCAR PARADAS!\n" + e.getMessage());
                Log.e("linha", "buscando paradas... erro:" + e.getMessage());
                e.printStackTrace();
            }
            return listaParadas;
        }
    }
}
