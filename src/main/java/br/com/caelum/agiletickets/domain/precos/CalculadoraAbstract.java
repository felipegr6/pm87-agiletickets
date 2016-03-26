package br.com.caelum.agiletickets.domain.precos;

import java.math.BigDecimal;

import br.com.caelum.agiletickets.models.Sessao;

public abstract class CalculadoraAbstract {

	protected BigDecimal preco;
	
	public CalculadoraAbstract(){
		preco = BigDecimal.ZERO;
	}

	public abstract BigDecimal calcula(Sessao sessao, Integer quantidade);

}