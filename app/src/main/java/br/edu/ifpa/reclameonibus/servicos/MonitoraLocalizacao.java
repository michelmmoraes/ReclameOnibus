package br.edu.ifpa.reclameonibus.servicos;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;

import br.edu.ifpa.reclamenibus.R;
import br.edu.ifpa.reclameonibus.api_sptrans.EstaNaParada;
import br.edu.ifpa.reclameonibus.api_sptrans.EstaNoOnibus;
import br.edu.ifpa.reclameonibus.auxiliares.Notificacao;
import br.edu.ifpa.reclameonibus.auxiliares.RespostaCenario;
import br.edu.ifpa.reclameonibus.auxiliares.RespostaOnibus;
import br.edu.ifpa.reclameonibus.auxiliares.RespostaParada;
import br.edu.ifpa.reclameonibus.cidadao.Cidadao;
import br.edu.ifpa.reclameonibus.componentes.Onibus;
import br.edu.ifpa.reclameonibus.componentes.Parada;
import br.edu.ifpa.reclameonibus.interfaces.api_sptrans_olhovivo.API_OlhoVivo;
import br.edu.ifpa.reclameonibus.localizacao.Localizacao;
import br.edu.ifpa.reclameonibus.telas.onibus.TelaOnibus;

public class MonitoraLocalizacao extends Service implements LocationListener {

    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    LocationManager locationManager;
    Cidadao cidadao;
    Onibus onibus;
    Parada parada;
    boolean capturouLocalizacao;
    int contadorLocalizacoes;
    ResultReceiver resultReceiver;
    API_OlhoVivo olhoVivo;

    public MonitoraLocalizacao() {
        cidadao = new Cidadao();
        onibus = new Onibus();
        parada = new Parada();
        olhoVivo = new API_OlhoVivo(this, mNotificationManager, mBuilder);
//        Notificacao notificacao = new Notificacao(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        Toast.makeText(this, "onBind", Toast.LENGTH_SHORT).show();
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Notificacao.contexto = this;
        capturouLocalizacao = false;
        contadorLocalizacoes = 0;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, this);
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.onibusvermelho)
                .setContentTitle("My notification")
                .setContentText("Hello World!").setTicker("ticker");
        Intent resultIntent = new Intent(this, TelaOnibus.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(TelaOnibus.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //mNotificationManager.notify(1, mBuilder.build());
        //Toast.makeText(this, "onCreate service", Toast.LENGTH_SHORT).show();
        //testando Timer

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Toast.makeText(this, "onStartCommand\nflags=" + flags + "\nstartId=" + startId + "\ncont=" + contadorLocalizacoes, Toast.LENGTH_SHORT).show();
        try {
            resultReceiver = intent.getParcelableExtra("resultreceiver");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (capturouLocalizacao) {
            //Toast.makeText(this, "onstartcommand...\nsolicitação \b" + olhoVivo.getStatus().toString(), Toast.LENGTH_SHORT).show();
            if (olhoVivo.getStatus() == AsyncTask.Status.FINISHED) {
                //detectarCenario();
                notificar(777, "detectarcenario desativado",
                        "asynctask api olhovivo desativada temporariamente", "sss");
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void notificar(int idNotificacao, String titulo, String texto, String ticker) {
        mBuilder.setContentTitle(titulo).setContentText(texto).setTicker(ticker);
        mNotificationManager.notify(idNotificacao, mBuilder.build());
    }

    public void atualizarLocalizacaoCidadao(Location location) {
        Localizacao localizacao = new Localizacao();
        localizacao.setLatitude(location.getLatitude());
        localizacao.setLongitude(location.getLongitude());
        localizacao.setData(new Date(location.getTime()));
        cidadao.setLocalizacao(localizacao);
    }

    @Override
    public void onLocationChanged(Location location) {
        capturouLocalizacao = true;
        contadorLocalizacoes++;
        atualizarLocalizacaoCidadao(location);
        if (contadorLocalizacoes == 1) {
            atualizarTela(API_OlhoVivo.LOCALIZACAO_GPS_OK);
            /*notificar(API_OlhoVivo.LOCALIZACAO_GPS_OK,
                    "Detectamos sua localização por GPS!",
                    "Latitude=" + cidadao.getLocalizacao().getLatitude() +
                            "\nLongitude=" + cidadao.getLocalizacao().getLongitude(),
                    "Primeira localização!");*/
            EstaNoOnibus estaNoOnibus = new EstaNoOnibus(this);
            EstaNaParada estaNaParada = new EstaNaParada(this);
            RespostaCenario respostaCenario = new RespostaCenario();
            RespostaParada respostaParada = new RespostaParada();
            respostaParada.setEstaNaParada(false);
            RespostaOnibus respostaOnibus = new RespostaOnibus();
            respostaOnibus.setEstaNoOnibus(false);
            notificar(Notificacao.CENARIO, "Detectando onde você está...",
                    "Aguarde alguns instantes...", "Procurando sua localização...");
            estaNaParada.execute(cidadao);
            estaNoOnibus.execute(cidadao);

        } else {
            // RETIRAR COMENTÁRIOS APÓS TESTES
            /*notificar(API_OlhoVivo.LOCALIZACAO_GPS_OK, "Monitorando sua localização por GPS...",
                    "Latitude=" + cidadao.getLocalizacao().getLatitude() +
                            "\nLongitude=" + cidadao.getLocalizacao().getLongitude(),
                    "Primeira localização!\nSua localização mudou " + contadorLocalizacoes + " vezes.");
            atualizarTela(API_OlhoVivo.LOCALIZACAO_GPS_OK);
            if (olhoVivo.getStatus() == AsyncTask.Status.FINISHED) {
                detectarCenario();
            }*/
        }
    }

    public RespostaParada detectarParada(JSONArray todasAsParadas) throws JSONException {
        for (int i = 0; i < todasAsParadas.length(); i++) {
            if (cidadao.getLocalizacao().getLatitude() ==
                    todasAsParadas.getJSONObject(i).getDouble("latitude")) {
                if (cidadao.getLocalizacao().getLongitude() ==
                        todasAsParadas.getJSONObject(i).getDouble("longitude")) {
                    // O cidadão está em uma parada
                    RespostaParada respostaParada = new RespostaParada();
                    respostaParada.setEstaNaParada(true);
                    Parada parada = new Parada();
                    parada.setCodigoParada(todasAsParadas.getJSONObject(i).getInt("codigoParada"));
                    parada.setNome(todasAsParadas.getJSONObject(i).getString("nome"));
                    parada.setEndereco(todasAsParadas.getJSONObject(i).getString("endereco"));
                    Localizacao localizacao = new Localizacao();
                    localizacao.setLatitude(todasAsParadas.getJSONObject(i).getDouble("latitude"));
                    localizacao.setLongitude(todasAsParadas.getJSONObject(i).getDouble("lobgitude"));
                    parada.setLocalizacao(localizacao);
                    respostaParada.setParada(parada);
                    return respostaParada;
                }
            }
        }
        RespostaParada respostaParada = new RespostaParada();
        respostaParada.setEstaNaParada(false);
        return respostaParada;
    }

    public RespostaOnibus detectarOnibus(JSONArray todosOsOnibus) throws JSONException {
        for (int j = 0; j <
                20;
            //todosOsOnibus.length();
             j++) {
            if (cidadao.getLocalizacao().getLatitude() ==
                    todosOsOnibus.getJSONObject(j).getDouble("px")) {
                if (cidadao.getLocalizacao().getLongitude() ==
                        todosOsOnibus.getJSONObject(j).getDouble("py")) {
                    // O cidadão está em uma ônibus
                    RespostaOnibus respostaOnibus = new RespostaOnibus();
                    respostaOnibus.setEstaNoOnibus(true);
                    Onibus onibus = new Onibus();
                    onibus.setIdOnibus(Integer.parseInt(todosOsOnibus.getJSONObject(j).getString("p")));
                    onibus.setReferencia("vazio por enquanto!");
                    Localizacao localizacao = new Localizacao();
                    localizacao.setLatitude(todosOsOnibus.getJSONObject(j).getDouble("px"));
                    localizacao.setLongitude(todosOsOnibus.getJSONObject(j).getDouble("py"));
                    onibus.setLocalizacao(localizacao);
                    respostaOnibus.setOnibus(onibus);
                    return respostaOnibus;
                }
            }
        }
        Toast.makeText(this, "terminou detectaronibus do service!", Toast.LENGTH_SHORT).show();
        RespostaOnibus respostaOnibus = new RespostaOnibus();
        respostaOnibus.setEstaNoOnibus(false);
        return respostaOnibus;
    }

    public void detectarCenario() {
        Toast.makeText(this, "Detectando cenário...", Toast.LENGTH_SHORT).show();
        RespostaCenario respostaCenario = new RespostaCenario();
        olhoVivo = new API_OlhoVivo(this, mNotificationManager, mBuilder);
        olhoVivo.execute(API_OlhoVivo.DETECTAR_CENARIO, cidadao);
        /*try {
            olhoVivo = new API_OlhoVivo(this, mNotificationManager, mBuilder);
            respostaCenario = (RespostaCenario)olhoVivo.execute(API_OlhoVivo.DETECTAR_CENARIO, cidadao).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }*/
        Bundle bundle = new Bundle();
        respostaCenario.setIdCenario(RespostaCenario.PARADA);
        switch (respostaCenario.getIdCenario()) {
            case RespostaCenario.PARADA:
                bundle.putString("cenario", "PARADA");
                break;
            case RespostaCenario.ONIBUS:
                bundle.putString("cenario", "ÔNIBUS");
                break;
            case RespostaCenario.OUTRO:
                bundle.putString("cenario", "OUTRO");
                break;
        }
        resultReceiver.send(API_OlhoVivo.API_SPTRANS_OK, bundle);
        notificar(API_OlhoVivo.API_SPTRANS_OK, "Acesso à API da SPTRANS OK!",
                "Seu cenário é " + bundle.getString("cenario"), "Cenário detectado :)");
    }

    public void atualizarTela(int codigoAtualizacao) {
        if (codigoAtualizacao == API_OlhoVivo.LOCALIZACAO_GPS_OK) {
            Bundle bundle = new Bundle();
            bundle.putString("ok1", "ok1");
            try {
                resultReceiver.send(API_OlhoVivo.LOCALIZACAO_GPS_OK, bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (codigoAtualizacao == API_OlhoVivo.API_SPTRANS_OK) {
            Bundle bundle = new Bundle();
            bundle.putString("ok2", "ok2");
            try {
                resultReceiver.send(API_OlhoVivo.API_SPTRANS_OK, bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        stopService(new Intent(this, MonitoraLocalizacao.class));
    }
}
