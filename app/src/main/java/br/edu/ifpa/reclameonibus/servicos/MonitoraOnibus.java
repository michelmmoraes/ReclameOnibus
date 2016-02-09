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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import br.edu.ifpa.reclamenibus.R;
import br.edu.ifpa.reclameonibus.api_sptrans.QueimaParadaDesembarque;
import br.edu.ifpa.reclameonibus.api_sptrans.QueimaParadaEmbarque;
import br.edu.ifpa.reclameonibus.auxiliares.Notificacao;
import br.edu.ifpa.reclameonibus.auxiliares.RespostaCenario;
import br.edu.ifpa.reclameonibus.componentes.Linha;
import br.edu.ifpa.reclameonibus.componentes.Onibus;
import br.edu.ifpa.reclameonibus.componentes.Parada;
import br.edu.ifpa.reclameonibus.telas.onibus.TelaOnibus;
import br.edu.ifpa.reclameonibus.telas.onibus.TelaCondutaInadequada;
import br.edu.ifpa.reclameonibus.telas.onibus.TelaDirecaoPerigosa;
import br.edu.ifpa.reclameonibus.telas.onibus.TelaSuperlotacaoVistaDentro;
import br.edu.ifpa.reclameonibus.telas.parada.TelaSuperlotacaoVistaFora;
import br.edu.ifpa.reclameonibus.telas.parada.TelaTempoEspera;

public class MonitoraOnibus extends Service implements LocationListener {
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    LocationManager locationManager;
    int tempo = (1000 * 60) * 1;   // 1min.
    int cont = 0;
    int periodo = 60000;  //  intervalo a cada repeticao
    Timer timer = new Timer();
    int cenarioDetectado = 0;

    public void notificar(int idNotificacao, String titulo, String texto, String ticker,
                          Class classe, String[] infoReclamacao, int contador,
                          int icone) {
        mBuilder.setContentTitle(titulo).setContentText(texto).setTicker(ticker);
        mBuilder.setSmallIcon(icone);
        Intent resultIntent = new Intent(this, classe);

        resultIntent.putExtra("nomeparada", infoReclamacao[0]);
        resultIntent.putExtra("codigoparada", infoReclamacao[1]);
        resultIntent.putExtra("codigolinha", infoReclamacao[2]);
        resultIntent.putExtra("codigoonibus", infoReclamacao[3]);
        resultIntent.putExtra("infotempoatual", infoReclamacao[4]);
        resultIntent.putExtra("infotempoprevisao", infoReclamacao[5]);
        resultIntent.putExtra("contador", contador);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(classe);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(idNotificacao, mBuilder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.tempo)
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
        if (RespostaCenario.CENARIO_DETECTADO == RespostaCenario.PARADA) {
            timer.scheduleAtFixedRate(
                    new TimerTask() {
                        public void run() {
                            Log.e("new timer task", "cont=" + cont);
                            cont++;
                            notificar(Notificacao.TEMPO_ESPERA,
                                    "O ônibus está demorando muito?",
                                    "Esperando pelo ônibus há " + cont + " minuto(s).",
                                    "O ônibus está demorando muito?", TelaTempoEspera.class,
                                    Notificacao.infoReclamacao, cont,
                                    R.drawable.tempo);
                            Log.e("após notificar", "cont=" + cont);
                        }
                    }, tempo, periodo);
        }
    }

    QueimaParadaEmbarque queimaParadaEmbarque;
    QueimaParadaDesembarque queimaParadaDesembarque = new QueimaParadaDesembarque(this);
    String[] infoReclamacao = {"?", "?", "?", "?", "?"};

    @Override
    public void onLocationChanged(Location location) {
        Log.d("teste", "onlocationchagend + " + RespostaCenario.CENARIO_DETECTADO);
        if (RespostaCenario.CENARIO_DETECTADO == RespostaCenario.PARADA) {
            queimaParadaEmbarque = new QueimaParadaEmbarque(this);
            //Só para testar
            Parada parada = new Parada();
            parada.setCodigoParada(260015039);
            Linha linha = new Linha();
            linha.setCodigoLinha(1877);
            queimaParadaEmbarque.execute(parada, linha);
        } else if (RespostaCenario.CENARIO_DETECTADO == RespostaCenario.ONIBUS) {
            //Só para testar
            Parada parada = new Parada();
            parada.setCodigoParada(260015039);
            Linha linha = new Linha();
            linha.setCodigoLinha(1877);
            Onibus onibus = new Onibus();
            onibus.setIdOnibus(82300);
            if (queimaParadaDesembarque.getStatus() != AsyncTask.Status.RUNNING) {
                queimaParadaDesembarque = new QueimaParadaDesembarque(this);
                queimaParadaDesembarque.execute(parada, linha, onibus);
            }
        }

        if (RespostaCenario.CENARIO_DETECTADO == RespostaCenario.PARADA) {
            notificar(Notificacao.SUPERLOTACAO_VISTA_FORA,
                    "O ônibus está superlotado?", "Reclamar da superlotação!",
                    "O ônibus está superlotado?", TelaSuperlotacaoVistaFora.class,
                    Notificacao.infoReclamacao, cont, R.drawable.superlotacao);
        } else if (RespostaCenario.CENARIO_DETECTADO == RespostaCenario.ONIBUS) {
            notificar(Notificacao.SUPERLOTACAO_VISTA_DENTRO,
                    "O ônibus está superlotado?", "Reclamar da superlotação!",
                    "O ônibus está superlotado?", TelaSuperlotacaoVistaDentro.class,
                    Notificacao.infoReclamacao, cont, R.drawable.superlotacao);
            notificar(Notificacao.DIRECAO_PERIGOSA,
                    "O motorista dirige com imprudência?",
                    "Reclamar da direção perigosa do motorista!",
                    "O motorista faz direção perigosa?",
                    TelaDirecaoPerigosa.class,
                    Notificacao.infoReclamacao, 0, R.drawable.direcao_perigosa);
            notificar(Notificacao.CONDUTA_INADEQUADA,
                    "Mau comportamento de cobrador ou motorista?",
                    "Reclamar da conduta inadequada do funcionário!",
                    "Má conduta de motorista ou cobrador?",
                    TelaCondutaInadequada.class,
                    Notificacao.infoReclamacao, 0, R.drawable.conduta_inadequada);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
