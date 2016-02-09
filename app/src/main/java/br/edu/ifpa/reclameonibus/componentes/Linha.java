package br.edu.ifpa.reclameonibus.componentes;

public class Linha {

    private int codigoLinha;
    private boolean circular;
    private String letreiro, sentido, tipo, denominacaoTPTS, denominacaoTSTP;

    public int getCodigoLinha() {
        return codigoLinha;
    }

    public void setCodigoLinha(int codigoLinha) {
        this.codigoLinha = codigoLinha;
    }

    public boolean isCircular() {
        return circular;
    }

    public void setCircular(boolean circular) {
        this.circular = circular;
    }

    public String getLetreiro() {
        return letreiro;
    }

    public void setLetreiro(String letreiro) {
        this.letreiro = letreiro;
    }

    public String getSentido() {
        return sentido;
    }

    public void setSentido(String sentido) {
        this.sentido = sentido;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDenominacaoTPTS() {
        return denominacaoTPTS;
    }

    public void setDenominacaoTPTS(String denominacaoTPTS) {
        this.denominacaoTPTS = denominacaoTPTS;
    }

    public String getDenominacaoTSTP() {
        return denominacaoTSTP;
    }

    public void setDenominacaoTSTP(String denominacaoTSTP) {
        this.denominacaoTSTP = denominacaoTSTP;
    }
}
