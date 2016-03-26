package br.com.caelum.agiletickets.domain.precos;

import java.math.BigDecimal;

import br.com.caelum.agiletickets.models.Sessao;

public class TeatroCalculadora extends CalculadoraAbstract {

	@Override
	public BigDecimal calcula(Sessao sessao, Integer quantidade) {

		preco = sessao.getPreco();

		return preco.multiply(BigDecimal.valueOf(quantidade));

	}

}
