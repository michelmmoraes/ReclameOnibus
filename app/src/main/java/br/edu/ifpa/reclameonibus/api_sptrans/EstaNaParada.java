package br.edu.ifpa.reclameonibus.api_sptrans;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.edu.ifpa.reclamenibus.R;
import br.edu.ifpa.reclameonibus.auxiliares.Notificacao;
import br.edu.ifpa.reclameonibus.auxiliares.RespostaParada;
import br.edu.ifpa.reclameonibus.cidadao.Cidadao;
import br.edu.ifpa.reclameonibus.componentes.Parada;
import br.edu.ifpa.reclameonibus.localizacao.Localizacao;
import br.edu.ifpa.reclameonibus.telas.parada.TelaParada;
import br.edu.ifpa.reclameonibus.telas.parada.TelaCondicoesParada;

public class EstaNaParada extends AsyncTask {
    HttpClient httpClient = new DefaultHttpClient();
    boolean appAutenticado = false;
    Context con;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;

    public EstaNaParada(Context con) {
        this.con = con;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mBuilder = new NotificationCompat.Builder(con)
                .setSmallIcon(R.drawable.onibusamarelo);
        mNotificationManager =
                (NotificationManager) con.getSystemService
                        (Context.NOTIFICATION_SERVICE);
    }

    private boolean autenticarSe() {
        Log.e("teste","autenticando...");
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

    @Override
    protected Object doInBackground(Object[] params) {
        if (autenticarSe()) {
            Cidadao cidadao = (Cidadao) params[0];
            HttpGet httpGet = new HttpGet("http://api.olhovivo.sptrans.com.br/v0" +
                    "/Parada/Buscar?termosBusca=");
            Log.d("parada", "EstaNaParada - detectando todas as paradas...");
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                Log.d("parada", "HttpResponse httpResponse = httpClient.execute(httpGet);");
                HttpEntity httpEntity = httpResponse.getEntity();
                Log.d("parada", "HttpEntity httpEntity = httpResponse.getEntity();");
                JSONArray jsonArray = new JSONArray(EntityUtils.toString(httpEntity));
                Log.d("parada", "JSONArray jsonArray = new JSONArray(EntityUtils.toString(httpEntity));");
                //publishProgress("qtd paradas=" + jsonArray.length());
                Log.d("parada", "foram encontradas " + jsonArray.length() + " paradas.");
                //percorre cada parada encontrada
                for (int i = 0; i < jsonArray.length(); i++) {
                    Log.d("parada", "parada encontrada nº" + i);
                    if (cidadao.getLocalizacao().getLatitude() ==
                            jsonArray.getJSONObject(i).getDouble("Latitude")) {
                        if (cidadao.getLocalizacao().getLongitude() ==
                                jsonArray.getJSONObject(i).getDouble("Longitude")) {
                            // O cidadão está em uma parada
                            RespostaParada respostaParada = new RespostaParada();
                            respostaParada.setEstaNaParada(true);
                            Parada parada = new Parada();
                            parada.setCodigoParada(jsonArray.getJSONObject(i).getInt("CodigoParada"));
                            parada.setNome(jsonArray.getJSONObject(i).getString("Nome"));
                            parada.setEndereco(jsonArray.getJSONObject(i).getString("Endereco"));
                            Localizacao localizacao = new Localizacao();
                            localizacao.setLatitude(jsonArray.getJSONObject(i).getDouble("Latitude"));
                            localizacao.setLongitude(jsonArray.getJSONObject(i).getDouble("Longitude"));
                            parada.setLocalizacao(localizacao);
                            respostaParada.setParada(parada);
                            Log.d("parada", "parada encontrada!ok!");
                            return respostaParada;
                        }
                    }
                }
            } catch (Exception e) {
                Log.d("parada", "ERRO: " + e.getMessage());
                e.printStackTrace();
            }
        }
        RespostaParada respostaParada = new RespostaParada();
        respostaParada.setEstaNaParada(false);
        Log.d("parada", "busca de paradas foi concluída.");
        return respostaParada;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        RespostaParada respostaParada = (RespostaParada) o;
        Parada parada = new Parada();
        parada.setCodigoParada(3333);
        parada.setNome("Parada Teste");
       //respostaParada.setEstaNaParada(false);//Apagar após testes na TelaParada

        if (respostaParada.isEstaNaParada()) {
            notificar(Notificacao.CENARIO, "Você está em uma parada de ônibus!",
                    "Parada:" + " Nome Teste",
                    "Cenário detectado: Parada de Ônibus!",
                    TelaParada.class, parada,
                    R.drawable.parada);
            notificar(Notificacao.CONDICOES_PARADA,
                    "Parada em más condições?", "Reclamar das condições da parada!",
                    "Más condições da parada de ônibus",
                    TelaCondicoesParada.class, parada,
                    R.drawable.condicoes_parada);
        }
    }

    public void notificar(int idNotificacao, String titulo, String texto, String ticker,
                          Class classe, Parada parada,
                          int icone) {
        mBuilder.setContentTitle(titulo).setContentText(texto).setTicker(ticker);
        mBuilder.setSmallIcon(icone);
        Intent resultIntent = new Intent(con, classe);

        resultIntent.putExtra("nomeparada", parada.getNome());
        resultIntent.putExtra("codigoparada", parada.getCodigoParada());

        SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm:ss");
        Date data = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();
        String hora_atual = dateFormat_hora.format(data_atual);

        resultIntent.putExtra("infotempoatual", hora_atual);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(con);
        stackBuilder.addParentStack(classe);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(idNotificacao, mBuilder.build());
    }
}
