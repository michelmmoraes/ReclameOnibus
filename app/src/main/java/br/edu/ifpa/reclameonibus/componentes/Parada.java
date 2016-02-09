package br.edu.ifpa.reclameonibus.componentes;

import br.edu.ifpa.reclameonibus.localizacao.Localizacao;

public class Parada {

	private int codigoParada;

	private String nome, endereco;

	private Localizacao localizacao;

	public int getCodigoParada() {
		return codigoParada;
	}

	public void setCodigoParada(int codigoParada) {
		this.codigoParada = codigoParada;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Localizacao getLocalizacao() {
		return localizacao;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}
}
