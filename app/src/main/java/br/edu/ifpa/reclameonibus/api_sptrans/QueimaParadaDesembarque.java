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
import org.json.JSONObject;

import java.io.IOException;

import br.edu.ifpa.reclamenibus.R;
import br.edu.ifpa.reclameonibus.auxiliares.Notificacao;
import br.edu.ifpa.reclameonibus.componentes.Linha;
import br.edu.ifpa.reclameonibus.componentes.Onibus;
import br.edu.ifpa.reclameonibus.componentes.Parada;
import br.edu.ifpa.reclameonibus.telas.onibus.TelaQueimaParadaDesembarque;
import br.edu.ifpa.reclameonibus.telas.parada.TelaQueimaParadaEmbarque;

public class QueimaParadaDesembarque extends AsyncTask {
    HttpClient httpClient = new DefaultHttpClient();
    boolean appAutenticado = false;
    Context con;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    String[] infoReclamacao = {"?", "?", "?", "?", "?", "?"};

    public QueimaParadaDesembarque(Context c) {
        con = c;
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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        super.onPreExecute();
        mBuilder = new NotificationCompat.Builder(con)
                .setSmallIcon(R.drawable.onibusvermelho);
        mNotificationManager =
                (NotificationManager) con.getSystemService
                        (Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        // autenticarSe();
        if (autenticarSe()) {
            Parada parada = (Parada) params[0];
            Linha linha = (Linha) params[1];
            Onibus onibus = (Onibus) params[2];
            HttpGet httpGet = new HttpGet("http://api.olhovivo.sptrans.com.br/v0" +
                    "/Previsao?codigoParada=" + parada.getCodigoParada() +
                    "&codigoLinha=" + linha.getCodigoLinha());

            // String[] infoReclamacao = new String[6];
            String nomeParada = "?", codigoParada = "?",
                    codigoLinha = "?", codigoOnibus = "?",
                    infoTempoAtual = "?", infoTempoPrevisao = "?";

            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                JSONObject jsonObject = new JSONObject(EntityUtils.toString(httpEntity));
                JSONArray arrayOnibus = jsonObject.getJSONObject("p").getJSONArray("l")
                        .getJSONObject(0).getJSONArray("vs");

                String tempoChegadaNoDestino;
                int qtdOnibus = arrayOnibus.length();
                Log.e("teste", "qtdOnibus=" + qtdOnibus);
                for (int i = 0; i < qtdOnibus; i++) {
                    int idOnibusProcurado = onibus.getIdOnibus();
                    int codOnibusDaVez = arrayOnibus.getJSONObject(i).getInt("p");
                    Log.e("teste", "i=" + i);
                    if (idOnibusProcurado == codOnibusDaVez) {
                        infoTempoAtual = jsonObject.getString("hr");
                        nomeParada = jsonObject.getJSONObject("p").get("np").toString();
                        codigoParada = jsonObject.getJSONObject("p").get("cp").toString();
                        codigoLinha = jsonObject.getJSONObject("p").getJSONArray("l").
                                getJSONObject(0).getString("cl");
                        JSONObject previsaoChegada = jsonObject.getJSONObject("p").getJSONArray("l")
                                .getJSONObject(0).getJSONArray("vs").getJSONObject(i);
                        codigoOnibus = previsaoChegada.get("p").toString();
                        infoTempoPrevisao = previsaoChegada.get("t").toString();
                        Log.e("teste", "onibus encontrado ok=" +
                                arrayOnibus.getJSONObject(i).getInt("p"));
                    }
                }
                Log.e("teste", "fim do loop while arrayonibus");
                infoReclamacao[0] = nomeParada;
                infoReclamacao[1] = codigoParada;
                infoReclamacao[2] = codigoLinha;
                infoReclamacao[3] = codigoOnibus;
                infoReclamacao[4] = infoTempoAtual;
                infoReclamacao[5] = infoTempoPrevisao;
                Log.e("desembarque", nomeParada + "-" + codigoParada + "-" + codigoLinha + "-" + codigoOnibus +
                        "-" + infoTempoAtual + "-" + infoTempoPrevisao);
            } catch (Exception e) {
                Log.e("parada", "ERRO: " + e.getMessage());
                e.printStackTrace();
            }
            return infoReclamacao;
        } else {
            return "Aplicativo não pôde ser autenticado!";
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Notificacao.infoReclamacao = (String[]) o;
        notificar(Notificacao.QUEIMA_PARADA_EMBARQUE,
                "Queima de parada? Reclamar agora!",
                "Você chegará na parada " + Notificacao.infoReclamacao[1] +
                        " às " + Notificacao.infoReclamacao[5],
                "O motorista queimou a parada?",
                TelaQueimaParadaDesembarque.class, Notificacao.infoReclamacao,
                R.drawable.queima_parada);
    }

    public void notificar(int idNotificacao, String titulo, String texto, String ticker,
                          Class classe, String[] infoReclamacao,
                          int icone) {
        mBuilder.setContentTitle(titulo).setContentText(texto).setTicker(ticker);
        mBuilder.setSmallIcon(icone);
        Intent resultIntent = new Intent(con, classe);

        resultIntent.putExtra("nomeparada", infoReclamacao[0]);
        resultIntent.putExtra("codigoparada", infoReclamacao[1]);
        resultIntent.putExtra("codigolinha", infoReclamacao[2]);
        resultIntent.putExtra("codigoonibus", infoReclamacao[3]);
        resultIntent.putExtra("infotempoatual", infoReclamacao[4]);
        resultIntent.putExtra("infotempoprevisao", infoReclamacao[5]);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(con);
        stackBuilder.addParentStack(classe);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(idNotificacao, mBuilder.build());
    }
}
