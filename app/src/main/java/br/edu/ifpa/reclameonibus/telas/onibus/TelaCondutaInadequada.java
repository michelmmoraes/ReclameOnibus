package br.edu.ifpa.reclameonibus.telas.onibus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

import br.edu.ifpa.reclamenibus.R;
import br.edu.ifpa.reclameonibus.auxiliares.CaixaDialogo;

public class TelaCondutaInadequada extends ActionBarActivity {
    TextView tvReclamacaoMotorista, tvReclamacaoCobrador, tvDetalhes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_conduta_inadequada);
        tvReclamacaoMotorista = (TextView) findViewById(R.id.textoReclamacaoMotorista);
        tvReclamacaoCobrador = (TextView) findViewById(R.id.textoReclamacaoCobrador);
        tvDetalhes = (TextView) findViewById(R.id.textoDetalhes);
        Intent intent = getIntent();
        tvReclamacaoMotorista.setText("O motorista do ônibus "
                + intent.getStringExtra("codigoonibus") +
                ", da linha " + intent.getStringExtra("codigolinha") + "," +
                " teve uma conduta inadequada!");
        tvReclamacaoCobrador.setText("O cobrador do ônibus "
                + intent.getStringExtra("codigoonibus") +
                ", da linha " + intent.getStringExtra("codigolinha") + "," +
                " teve uma conduta inadequada!");
        tvDetalhes.setText("Data: " + DateFormat.getDateInstance().format(new Date()) +
                " - Horário: " + intent.getStringExtra("infotempoatual"));
    }
    public void reclamar(View v) {
        CaixaDialogo caixaDialogo=new CaixaDialogo();
        caixaDialogo.exibir(this, "Conduta inadequada do funcionário!",
                "Reclamação enviada com sucesso!",
                R.drawable.conduta_inadequada,this);
    }
}
