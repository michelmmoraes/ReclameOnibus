package br.edu.ifpa.reclameonibus.reclamacao;

import br.edu.ifpa.reclameonibus.componentes.Linha;
import br.edu.ifpa.reclameonibus.componentes.Onibus;
import br.edu.ifpa.reclameonibus.componentes.Parada;

public class Problema {

	public Problema(String nomeProblema) {
		this.nomeProblema = nomeProblema;
	}

	private int idProblema;

	private String nomeProblema;

	private Onibus onibus;

	private Linha linha;

	private Parada parada;

	public String getNomeProblema() {
		return nomeProblema;
	}

	public void setNomeProblema(String nomeProblema) {
		this.nomeProblema = nomeProblema;
	}

	public Onibus getOnibus() {
		return onibus;
	}

	public void setOnibus(Onibus onibus) {
		this.onibus = onibus;
	}

	public Linha getLinha() {
		return linha;
	}

	public void setLinha(Linha linha) {
		this.linha = linha;
	}

	public Parada getParada() {
		return parada;
	}

	public void setParada(Parada parada) {
		this.parada = parada;
	}

	public int getIdProblema() {
		return idProblema;
	}

	public void setIdProblema(int idProblema) {
		this.idProblema = idProblema;
	}
}
