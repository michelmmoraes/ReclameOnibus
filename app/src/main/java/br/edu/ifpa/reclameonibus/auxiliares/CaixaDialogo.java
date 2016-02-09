package br.edu.ifpa.reclameonibus.auxiliares;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import br.edu.ifpa.reclamenibus.R;

public class CaixaDialogo {

    public void exibir(Context c, String titulo, String mensagem,
                       int icone, final Activity tela) {
        AlertDialog alertDialog = new AlertDialog.Builder(c).create();
        alertDialog.setTitle(titulo);
        alertDialog.setMessage(mensagem);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                tela.finish();
            }
        });
        alertDialog.setIcon(icone);
        alertDialog.show();
    }
}
