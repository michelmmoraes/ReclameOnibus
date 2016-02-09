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

public class TelaSuperlotacaoVistaFora extends ActionBarActivity {
    TextView tvReclamacao, tvDetalhes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_superlotacao_vista_fora);
        tvReclamacao = (TextView) findViewById(R.id.textoReclamacaoMotorista);
        tvDetalhes = (TextView) findViewById(R.id.textoDetalhes);
        Intent intent = getIntent();
        tvReclamacao.setText("O ônibus " + intent.getStringExtra("codigoonibus") +
                ", da linha " + intent.getStringExtra("codigolinha") + ", " +
                "passou superlotado por aqui!");
        tvDetalhes.setText("Parada: " + intent.getStringExtra("codigoparada") + " (" +
                intent.getStringExtra("nomeparada") + ")\n" +
                "Data: " + DateFormat.getDateInstance().format(new Date()) +
                " - Horário: " + intent.getStringExtra("infotempoatual"));
    }
    public void reclamar(View v) {
        CaixaDialogo caixaDialogo=new CaixaDialogo();
        caixaDialogo.exibir(this, "Ônibus lotado!",
                "Reclamação enviada com sucesso!",
                R.drawable.superlotacao,this);
    }
}
