package br.edu.ifpa.reclameonibus.telas;

import android.content.Intent;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import br.edu.ifpa.reclamenibus.R;
import br.edu.ifpa.reclameonibus.auxiliares.Notificacao;
import br.edu.ifpa.reclameonibus.interfaces.api_sptrans_olhovivo.API_OlhoVivo;
import br.edu.ifpa.reclameonibus.servicos.MonitoraLocalizacao;
import br.edu.ifpa.reclameonibus.telas.parada.TelaParada;


public class TelaPrincipal extends ActionBarActivity {
    ResultReceiverListener resultReceiverListener;
    boolean telaVisivel = true;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, "requestcode=" + requestCode + "\nresultcode=" + resultCode,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);
        telaVisivel = true;
        resultReceiverListener = new ResultReceiverListener(null);
        Intent intent = new Intent(this, MonitoraLocalizacao.class);
        intent.putExtra("resultreceiver", resultReceiverListener);
        startService(intent);
        Notificacao.contexto = this;        //bindService(intent, this, BIND_AUTO_CREATE);
        //Toast.makeText(this, "oncreate activity", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tela_principal, menu);
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

    class ResultReceiverListener extends ResultReceiver {
        public ResultReceiverListener(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(final int resultCode, final Bundle resultData) {
            if (resultCode == API_OlhoVivo.LOCALIZACAO_GPS_OK) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
            if (resultCode == API_OlhoVivo.API_SPTRANS_OK) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            } else if (resultCode == 999) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (telaVisivel) {
                            Intent intent = new Intent(TelaPrincipal.this, TelaParada.class);
                            intent.putExtra("cont", 0);
                            intent.putExtra("idparada", resultData.get("id").toString());
                            intent.putExtra("referencia", resultData.get("referencia").toString());
                            finishActivity(100);

                            startActivityForResult(intent, 100);
                            finish();
                        }
                    }
                });
            }
        }
    }


}
