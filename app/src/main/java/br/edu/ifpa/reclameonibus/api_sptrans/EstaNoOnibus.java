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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.edu.ifpa.reclamenibus.R;
import br.edu.ifpa.reclameonibus.auxiliares.Notificacao;
import br.edu.ifpa.reclameonibus.auxiliares.RespostaOnibus;
import br.edu.ifpa.reclameonibus.cidadao.Cidadao;
import br.edu.ifpa.reclameonibus.componentes.Linha;
import br.edu.ifpa.reclameonibus.componentes.Onibus;
import br.edu.ifpa.reclameonibus.localizacao.Localizacao;
import br.edu.ifpa.reclameonibus.telas.onibus.TelaOnibus;
import br.edu.ifpa.reclameonibus.telas.onibus.TelaCondicoesOnibus;
import br.edu.ifpa.reclameonibus.telas.outro.TelaOutro;

public class EstaNoOnibus extends AsyncTask {
    HttpClient httpClient = new DefaultHttpClient();
    boolean appAutenticado = false;
    Context con;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;

    public EstaNoOnibus(Context con) {
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

    @Override
    protected Object doInBackground(Object[] params) {
        autenticarSe();
        Cidadao cidadao = (Cidadao) params[0];
        return detectarOnibus(cidadao);
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

    private RespostaOnibus detectarOnibus(Cidadao cidadao) {
        int totalOnibus = 0;
        //publishProgress("Buscando todas as linhas...");
        List<Linha> listaTodasLinhas = buscarTodasLinhas();
        int totalLinhas = listaTodasLinhas.size();
        Log.d("onibus_vermelho", "total de linhas encontradas=" + totalLinhas);
        int grupoLinhasPorVez = 50;
        int linhasRestantes = totalLinhas;
        int iniciarEm = 0;
        int terminarEm = grupoLinhasPorVez;
        //publishProgress("total de linhas/100 = " + totalLinhas / grupoLinhasPorVez + "\ntotal de linhas%100 = " + totalLinhas % grupoLinhasPorVez);
        //publishProgress("iniciando método GIGANTE detectar ônibus...");
        //while (linhasRestantes > 0) {
        Log.d("onibus_vermelho", "linhasRestantes para verificar=" + linhasRestantes);
        //for (int i = iniciarEm; i < terminarEm; i++) {
        //APAGAR LINHA ABAIXO!!!!!NÃO ESQUECER!
        totalLinhas = 50;
        for (int i = 0; i < totalLinhas; i++) {
            Log.d("onibus_vermelho", "verificando ônibus das linhas de " + iniciarEm + " até " + terminarEm);
            HttpGet httpGet = new HttpGet("http://api.olhovivo.sptrans.com.br/v0" +
                    "/Posicao?codigoLinha=" + listaTodasLinhas.get(i).getCodigoLinha());
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                String respostaJSON = EntityUtils.toString(httpEntity);
                JSONObject jsonObject = new JSONObject(respostaJSON);
                JSONArray jsonArray = jsonObject.getJSONArray("vs");
                Log.d("onibus_vermelho", "linha nº " + i + " tem " + jsonArray.length() + " ônibus circulando.");
                totalOnibus += jsonArray.length();
                for (int j = 0; j < jsonArray.length(); j++) {
                    Log.d("onibus_vermelho", "verificando ônibus " + j + " da linha " + i);
                    if (cidadao.getLocalizacao().getLatitude() ==
                            jsonArray.getJSONObject(j).getDouble("px")) {
                        if (cidadao.getLocalizacao().getLongitude() ==
                                jsonArray.getJSONObject(j).getDouble("py")) {
                            // O cidadão está em uma ônibus
                            RespostaOnibus respostaOnibus = new RespostaOnibus();
                            respostaOnibus.setEstaNoOnibus(true);
                            Onibus onibus = new Onibus();
                            onibus.setIdOnibus(Integer.parseInt(jsonArray.getJSONObject(j).getString("p")));
                            onibus.setReferencia("vazio por enquanto!");
                            Localizacao localizacao = new Localizacao();
                            localizacao.setLatitude(jsonArray.getJSONObject(j).getDouble("px"));
                            localizacao.setLongitude(jsonArray.getJSONObject(j).getDouble("py"));
                            onibus.setLocalizacao(localizacao);
                            respostaOnibus.setOnibus(onibus);
                            return respostaOnibus;
                        }
                    }
                }
            } catch (JSONException | IOException e) {
                Log.d("teste", e.getMessage());
                publishProgress("ERRO! " + e.getMessage());
                e.printStackTrace();
            }
            /*notificar(222222, "Progresso busca por ônibus:",
                    (int) (((double) i / (double) totalLinhas) * 100) + " %", "...",
                    TelaOnibus.class);*/
            Log.d("onibus_vermelho", "andamento=" + ((double) i / (double) totalLinhas) * 100.00 + " %");
        }
        Log.d("onibus_vermelho", "total de ônibus=" + totalOnibus);
        RespostaOnibus respostaOnibus = new RespostaOnibus();
        respostaOnibus.setEstaNoOnibus(false);
        Log.d("onibus_vermelho", "Método encerrado");
        return respostaOnibus;
    }

    private List<Linha> buscarTodasLinhas() {
        List<Linha> listaLinhas = new ArrayList<>();
        JSONArray arrayTodasLinhas = new JSONArray();
        Log.d("linha", "buscando linhas...");
        //Só para testar, só serão buscadas as linhas começadas em 1 e 2
        //Por isso, i=2
        for (int i = 7; i < 8; i++) {
            HttpGet httpGet = new HttpGet("http://api.olhovivo.sptrans.com.br/v0" +
                    "/Linha/Buscar?termosBusca=" + i);
            Log.d("linha", "buscando linhas... letreiros começando em " + i);
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                String respostaJSON = EntityUtils.toString(httpEntity);
                JSONArray jsonArray = new JSONArray(respostaJSON);
                arrayTodasLinhas.put(jsonArray);
                Log.d("linha", "letreiros começando em " + i + " têm " + jsonArray.length() + " linhas.");
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject linhaDaVez = jsonArray.getJSONObject(j);
                    Log.d("linha", "letreiro começa com " + i + ", linha nº " + j
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
                Log.d("linha", "buscando linhas... erro:" + e.getMessage());
                e.printStackTrace();
            }
        }
        Log.d("linha", "foram encontradas " + listaLinhas.size() + " linhas.");
        return listaLinhas;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        RespostaOnibus respostaOnibus = (RespostaOnibus) o;

        //respostaOnibus.setEstaNoOnibus(true);
        Onibus onibus = new Onibus();
        onibus.setIdOnibus(80808);
        onibus.setReferencia(onibus.getIdOnibus() + "");
        Linha linha = new Linha();
        linha.setCodigoLinha(1010);
        respostaOnibus.setOnibus(onibus);
        if (respostaOnibus.isEstaNoOnibus()) {
            notificar(Notificacao.CENARIO, "Você está em um ônibus",
                    "Ônibus:" + respostaOnibus.getOnibus().getReferencia(),
                    "Cenário detectado: DENTRO DO ÔNIBUS!",
                    TelaOnibus.class, onibus, linha,
                    R.drawable.onibus);
            notificar(Notificacao.CONDICOES_ONIBUS,
                    "Ônibus em más condições?", "Reclamar das condições do ônibus!",
                    "Más condições dentro do ônibus",
                    TelaCondicoesOnibus.class, onibus, linha,
                    R.drawable.condicoes_onibus);
        } else {
            //Remover comentários após testes
           notificar(Notificacao.CENARIO, "Você está em outro lugar",
                    "Você não está nem em um ponto de parada nem em um ônibus.",
                    "Cenário detectado: OUTRO LUGAR!",
                    TelaOutro.class, onibus, linha, R.drawable.outro_lugar);
        }
    }

    public void notificar(int idNotificacao, String titulo, String texto, String ticker,
                          Class classe, Onibus onibus, Linha linha,
                          int icone) {
        mBuilder.setContentTitle(titulo).setContentText(texto).setTicker(ticker);
        mBuilder.setSmallIcon(icone);
        Intent resultIntent = new Intent(con, classe);

        resultIntent.putExtra("codigolinha", linha.getCodigoLinha());
        resultIntent.putExtra("codigoonibus", onibus.getIdOnibus());
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
