package br.edu.ifpa.reclameonibus.componentes;

import br.edu.ifpa.reclameonibus.localizacao.Localizacao;

public class Onibus {
    private int idOnibus;

    private String referencia;

    private Localizacao localizacao;

    public int getIdOnibus() {
        return idOnibus;
    }

    public void setIdOnibus(int idOnibus) {
        this.idOnibus = idOnibus;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }
}
