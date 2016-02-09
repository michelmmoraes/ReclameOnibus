package br.edu.ifpa.reclameonibus.interfaces.api_sptrans_olhovivo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
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
import br.edu.ifpa.reclameonibus.auxiliares.DadosBrutosCenario;
import br.edu.ifpa.reclameonibus.auxiliares.RespostaCenario;
import br.edu.ifpa.reclameonibus.auxiliares.RespostaOnibus;
import br.edu.ifpa.reclameonibus.auxiliares.RespostaParada;
import br.edu.ifpa.reclameonibus.cidadao.Cidadao;
import br.edu.ifpa.reclameonibus.componentes.Linha;
import br.edu.ifpa.reclameonibus.componentes.Onibus;
import br.edu.ifpa.reclameonibus.componentes.Parada;
import br.edu.ifpa.reclameonibus.localizacao.Localizacao;
import br.edu.ifpa.reclameonibus.telas.onibus.TelaOnibus;
import br.edu.ifpa.reclameonibus.telas.outro.TelaOutro;
import br.edu.ifpa.reclameonibus.telas.parada.TelaParada;

public class API_OlhoVivo extends AsyncTask {
    public static final int DETECTAR_CENARIO = 1, DETECTAR_PARADA = 2,
            DETECTAR_ONIBUS = 3, LOCALIZACAO_GPS_OK = 88, API_SPTRANS_OK = 99,
            CONSULTAR_TODAS_LINHAS = 78;
    HttpClient httpClient;
    boolean appAutenticado = false;
    Context con;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    List<Linha> listaLinhas;

    public API_OlhoVivo(Context c, NotificationManager nm, NotificationCompat.Builder mb) {
        httpClient = new DefaultHttpClient();
        con = c;
        listaLinhas = new ArrayList<>();
    }

    public void notificar(int idNotificacao, String titulo, String texto, String ticker,
                          Class classe) {
        mBuilder.setContentTitle(titulo).setContentText(texto).setTicker(ticker);
        Intent resultIntent = new Intent(con, classe);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(con);
        stackBuilder.addParentStack(classe);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(idNotificacao, mBuilder.build());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mBuilder = new NotificationCompat.Builder(con)
                .setSmallIcon(R.drawable.onibusamarelo)
                .setContentTitle("My notification")
                .setContentText("Hello World!").setTicker("ticker");
        mNotificationManager =
                (NotificationManager) con.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        //objects {codigoAcao, cidadao}
        notificar(111111, "doinbackground",
                "testando notificação dentro do doinbackground do asynctask. Testar abrir TelaOutro", "lol...",
                TelaOutro.class);
        RespostaCenario respostaCenario = new RespostaCenario();
        respostaCenario.setIdCenario(RespostaCenario.OUTRO);
        int codigoAcao = (int) objects[0];
        Cidadao cidadao = (Cidadao) objects[1];
        if (!appAutenticado) {
            appAutenticado = autenticarSe();
            if (appAutenticado) {
                notificar(1111111, "app autenticado", "você autenticou-se. Testar abrir TelaOnibus",
                        "token=true", TelaOnibus.class);
                //publishProgress("Você autenticou-se!");
            } else {
                publishProgress("Houve um problema na sua autenticação!");
            }
        } else {
            publishProgress("Você já está autenticado!");
        }
        if (codigoAcao == API_OlhoVivo.DETECTAR_CENARIO) {
            Log.e("teste","codigoAcao == API_OlhoVivo.DETECTAR_CENARIO");
            return detectarCenarioAntigo(cidadao);
        } else if (codigoAcao == CONSULTAR_TODAS_LINHAS) {
            Log.e("teste","codigoAcao == CONSULTAR_TODAS_LINHAS");
            return buscarTodasLinhasAntigo();
        }
        return "teste retorno";
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        Toast.makeText(con, values[0].toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        notificar(789789,"onpostexecute","resultado é..."+o.toString(),"onpost!!!",TelaParada.class);
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

    private DadosBrutosCenario detectarCenario(Cidadao cidadao) {
        DadosBrutosCenario dadosBrutosCenario = new DadosBrutosCenario();
        dadosBrutosCenario.setArrayTodasParadas(detectarParada(cidadao));
        dadosBrutosCenario.setPosicoesOnibus(detectarOnibus(cidadao));
        return dadosBrutosCenario;
    }

    private JSONArray detectarParada(Cidadao cidadao) {
        HttpGet httpGet = new HttpGet("http://api.olhovivo.sptrans.com.br/v0" +
                "/Parada/Buscar?termosBusca=");
        JSONArray jsonArray = new JSONArray();
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            jsonArray = new JSONArray(EntityUtils.toString(httpEntity));
            return jsonArray;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private JSONArray detectarOnibus(Cidadao cidadao) {
        List<Linha> listaTodasLinhas = buscarTodasLinhasAntigo();
        JSONArray todosOnibus = new JSONArray();
        for (int i = 0; i < listaTodasLinhas.size(); i++) {
            HttpGet httpGet = new HttpGet("http://api.olhovivo.sptrans.com.br/v0" +
                    "/Posicao?codigoLinha=" + listaTodasLinhas.get(i).getCodigoLinha());
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                String respostaJSON = EntityUtils.toString(httpEntity);
                JSONObject jsonObject = new JSONObject(respostaJSON);
                JSONArray jsonArray = jsonObject.getJSONArray("vs");
                todosOnibus.put(jsonArray);
            } catch (JSONException | IOException e) {
                publishProgress("ERRO! " + e.getMessage());
                e.printStackTrace();
            }
        }
        return todosOnibus;
    }

    private JSONArray buscarTodasLinhas() {
        JSONArray arrayTodasLinhas = new JSONArray();
        for (int i = 0; i < 10; i++) {
            HttpGet httpGet = new HttpGet("http://api.olhovivo.sptrans.com.br/v0" +
                    "/Linha/Buscar?termosBusca=" + i);
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                String respostaJSON = EntityUtils.toString(httpEntity);
                JSONArray jsonArray = new JSONArray(respostaJSON);
                arrayTodasLinhas.put(jsonArray);
            } catch (IOException | JSONException e) {
                publishProgress("ERRO AO BUSCAR LINHAS!\n" + e.getMessage());
                e.printStackTrace();
            }
        }
        return arrayTodasLinhas;
    }

    private RespostaCenario detectarCenarioAntigo(Cidadao cidadao) {
        RespostaCenario respostaCenario = new RespostaCenario();
        //publishProgress("buscando paradas...");
        RespostaParada respostaParada = detectarParadaAntigo(cidadao);
        //publishProgress("paradas ok");
        if (respostaParada.isEstaNaParada()) {
            // O usuário está em uma parada
            respostaCenario.setIdCenario(RespostaCenario.PARADA);
            respostaCenario.setParada(respostaParada.getParada());
            return respostaCenario;
        } else {
            notificar(11111111, "Parada=false",
                    "Você não está em uma parada! Testar abrir TelaParada", "parada :(", TelaParada.class);
            //publishProgress("buscando ônibus...");
            RespostaOnibus respostaOnibus = detectarOnibusAntigo(cidadao);
            //publishProgress("ônibus ok");
            if (respostaOnibus.isEstaNoOnibus()) {
                // O usuário está em um ônibus
                respostaCenario.setIdCenario(RespostaCenario.ONIBUS);
                respostaCenario.setOnibus(respostaOnibus.getOnibus());
                return respostaCenario;
            } else {
                // O usuário está em qualquer outro lugar
                respostaCenario.setIdCenario(RespostaCenario.OUTRO);
                return respostaCenario;
            }
        }
    }

    private RespostaParada detectarParadaAntigo(Cidadao cidadao) {
        HttpGet httpGet = new HttpGet("http://api.olhovivo.sptrans.com.br/v0" +
                "/Parada/Buscar?termosBusca=");
        Log.e("parada", "detectando todas as paradas...");
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            Log.e("parada", "HttpResponse httpResponse = httpClient.execute(httpGet);");
            HttpEntity httpEntity = httpResponse.getEntity();
            Log.e("parada", "HttpEntity httpEntity = httpResponse.getEntity();");
            JSONArray jsonArray = new JSONArray(EntityUtils.toString(httpEntity));
            Log.e("parada", "JSONArray jsonArray = new JSONArray(EntityUtils.toString(httpEntity));");
            //publishProgress("qtd paradas=" + jsonArray.length());
            Log.e("parada", "foram encontradas " + jsonArray.length() + " paradas.");
            for (int i = 0; i < jsonArray.length(); i++) {
                Log.e("parada", "parada encontrada nº" + i);
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
                        Log.e("parada", "parada encontrada!ok!");
                        return respostaParada;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("parada", "ERRO: " + e.getMessage());
            e.printStackTrace();
        }
        RespostaParada respostaParada = new RespostaParada();
        respostaParada.setEstaNaParada(false);
        Log.e("parada", "busca de paradas foi concluída.");
        return respostaParada;
    }

    private RespostaOnibus detectarOnibusAntigo(Cidadao cidadao) {
        int totalOnibus = 0;
        //publishProgress("Buscando todas as linhas...");
        List<Linha> listaTodasLinhas = buscarTodasLinhasAntigo();
        int totalLinhas = listaTodasLinhas.size();
        Log.e("onibus_vermelho", "total de linhas encontradas=" + totalLinhas);
        int grupoLinhasPorVez = 50;
        int linhasRestantes = totalLinhas;
        int iniciarEm = 0;
        int terminarEm = grupoLinhasPorVez;
        //publishProgress("total de linhas/100 = " + totalLinhas / grupoLinhasPorVez + "\ntotal de linhas%100 = " + totalLinhas % grupoLinhasPorVez);
        //publishProgress("iniciando método GIGANTE detectar ônibus...");
        //while (linhasRestantes > 0) {
        Log.e("onibus_vermelho", "linhasRestantes para verificar=" + linhasRestantes);
        //for (int i = iniciarEm; i < terminarEm; i++) {
        for (int i = 0; i < totalLinhas; i++) {
            Log.e("onibus_vermelho", "verificando ônibus das linhas de " + iniciarEm + " até " + terminarEm);
            HttpGet httpGet = new HttpGet("http://api.olhovivo.sptrans.com.br/v0" +
                    "/Posicao?codigoLinha=" + listaTodasLinhas.get(i).getCodigoLinha());
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                String respostaJSON = EntityUtils.toString(httpEntity);
                JSONObject jsonObject = new JSONObject(respostaJSON);
                JSONArray jsonArray = jsonObject.getJSONArray("vs");
                Log.e("onibus_vermelho", "linha nº " + i + " tem " + jsonArray.length() + " ônibus circulando.");
                totalOnibus += jsonArray.length();
                for (int j = 0; j < jsonArray.length(); j++) {
                    Log.e("onibus_vermelho", "verificando ônibus " + j + " da linha " + i);
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
                publishProgress("ERRO! " + e.getMessage());
                e.printStackTrace();
            }
            notificar(222222, "Progresso busca por ônibus:",
                    (int) (((double) i / (double) totalLinhas) * 100) + " %", "...",
                    TelaOnibus.class);
            Log.e("onibus_vermelho", "andamento=" + ((double) i / (double) totalLinhas) * 100.00 + " %");
        }
           /* linhasRestantes = linhasRestantes - grupoLinhasPorVez;
            Log.e("onibus_vermelho", "linhasRestantes (após o for do grupo)=" + linhasRestantes);
            if (linhasRestantes < grupoLinhasPorVez) {
                terminarEm += linhasRestantes;
            } else {
                terminarEm += grupoLinhasPorVez;
            }
            iniciarEm += grupoLinhasPorVez;
            Log.e("onibus_vermelho", "iniciar em=" + iniciarEm + " terminar em " + terminarEm);
        }*/
        Log.e("onibus_vermelho", "total de ônibus=" + totalOnibus);
        RespostaOnibus respostaOnibus = new RespostaOnibus();
        respostaOnibus.setEstaNoOnibus(false);
        Log.e("onibus_vermelho", "Método encerrado");
        return respostaOnibus;
    }

    private List<Linha> buscarTodasLinhasAntigo() {
        List<Linha> listaLinhas = new ArrayList<>();
        JSONArray arrayTodasLinhas = new JSONArray();
        Log.d("linha", "buscando linhas...");
        for (int i = 0; i < 10; i++) {
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
        this.listaLinhas = listaLinhas;
        return listaLinhas;
    }

    public List<Linha> getListaLinhas() {
        return listaLinhas;
    }

    public void setListaLinhas(List<Linha> listaLinhas) {
        this.listaLinhas = listaLinhas;
    }
}
