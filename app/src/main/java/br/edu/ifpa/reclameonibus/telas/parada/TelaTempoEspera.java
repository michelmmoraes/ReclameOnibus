package br.edu.ifpa.reclameonibus.telas.parada;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

import br.edu.ifpa.reclamenibus.R;
import br.edu.ifpa.reclameonibus.auxiliares.CaixaDialogo;

public class TelaTempoEspera extends ActionBarActivity {
    TextView tvReclamacao, tvDetalhes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_tempo_espera);
        tvReclamacao = (TextView) findViewById(R.id.textoReclamacaoMotorista);
        tvDetalhes = (TextView) findViewById(R.id.textoDetalhes);
        Intent intent = getIntent();
        tvReclamacao.setText("O ônibus da linha " + intent.getStringExtra("codigolinha") +
                " já demorou " + intent.getIntExtra("contador", 0) + " minuto(s) para passar!");
        tvDetalhes.setText("Parada: " + intent.getStringExtra("codigoparada") + " (" +
                intent.getStringExtra("nomeparada") + ")\n" +
                "Data: " + DateFormat.getDateInstance().format(new Date()) +
                " - Horário: " + intent.getStringExtra("infotempoatual"));
    }

    public void reclamar(View v) {
        CaixaDialogo caixaDialogo=new CaixaDialogo();
        caixaDialogo.exibir(this,"Demora na espera pelo ônibus!",
                "Reclamação enviada com sucesso!",
                R.drawable.tempo,this);
    }
}
