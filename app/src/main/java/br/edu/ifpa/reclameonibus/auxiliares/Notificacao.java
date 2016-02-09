package br.edu.ifpa.reclameonibus.auxiliares;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import br.edu.ifpa.reclamenibus.R;
import br.edu.ifpa.reclameonibus.telas.onibus.TelaOnibus;

public class Notificacao {
    public static int CENARIO = 0,
            TEMPO_ESPERA = 1,
            QUEIMA_PARADA_EMBARQUE = 2,
            SUPERLOTACAO_VISTA_FORA = 3,
            SUPERLOTACAO_VISTA_DENTRO = 4,
            DIRECAO_PERIGOSA = 5,
            CONDUTA_INADEQUADA = 6,
            CONDICOES_PARADA = 7,
            CONDICOES_ONIBUS = 8;
    public static String[] infoReclamacao = new String[6];
    static NotificationManager mNotificationManager;
    static NotificationCompat.Builder mBuilder;
    public static Context contexto;

    public Notificacao(Context context) {
        contexto = context;
        mBuilder = new NotificationCompat.Builder(contexto)
                .setSmallIcon(R.drawable.tempo)
                .setContentTitle("My notification")
                .setContentText("Hello World!").setTicker("ticker");
        Intent resultIntent = new Intent(contexto, TelaOnibus.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(contexto);
        stackBuilder.addParentStack(TelaOnibus.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager =
                (NotificationManager)
                        contexto.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static void notificar(int idNotificacao, String titulo, String texto,
                                 String ticker, Class classe, String[] infoReclamacao,
                                 int contador, int icone) {
        mBuilder.setContentTitle(titulo).setContentText(texto).setTicker(ticker);
        mBuilder.setSmallIcon(icone);
        Intent resultIntent = new Intent(contexto, classe);

        if (infoReclamacao.length == 6) {
            resultIntent.putExtra("nomeparada", infoReclamacao[0]);
            resultIntent.putExtra("codigoparada", infoReclamacao[1]);
            resultIntent.putExtra("codigolinha", infoReclamacao[2]);
            resultIntent.putExtra("codigoonibus", infoReclamacao[3]);
            resultIntent.putExtra("infotempoatual", infoReclamacao[4]);
            resultIntent.putExtra("infotempoprevisao", infoReclamacao[5]);
            resultIntent.putExtra("contador", contador);
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(contexto);
        stackBuilder.addParentStack(classe);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(idNotificacao, mBuilder.build());
    }
}
