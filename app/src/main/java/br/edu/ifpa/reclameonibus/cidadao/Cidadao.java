package br.edu.ifpa.reclameonibus.cidadao;

import br.edu.ifpa.reclameonibus.localizacao.Localizacao;

public class Cidadao{

	private int idCidadao;

	private String nomeCidadao;

	private Localizacao localizacao;

	public int getIdCidadao() {
		return idCidadao;
	}

	public void setIdCidadao(int idCidadao) {
		this.idCidadao = idCidadao;
	}

	public String getNomeCidadao() {
		return nomeCidadao;
	}

	public void setNomeCidadao(String nomeCidadao) {
		this.nomeCidadao = nomeCidadao;
	}

	public Localizacao getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}
}
