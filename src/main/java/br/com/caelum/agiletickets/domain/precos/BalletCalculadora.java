package br.com.caelum.agiletickets.domain.precos;

import java.math.BigDecimal;

import br.com.caelum.agiletickets.models.Sessao;

public class BalletCalculadora extends CalculadoraAbstract {

	@Override
	public BigDecimal calcula(Sessao sessao, Integer quantidade) {

		if ((sessao.getTotalIngressos() - sessao.getIngressosReservados())
				/ sessao.getTotalIngressos().doubleValue() <= 0.50)
			preco = sessao.getPreco().add(
					sessao.getPreco().multiply(BigDecimal.valueOf(0.20)));
		else
			preco = sessao.getPreco();

		if (sessao.getDuracaoEmMinutos() > 60)
			preco = preco.add(sessao.getPreco().multiply(
					BigDecimal.valueOf(0.10)));

		return preco.multiply(BigDecimal.valueOf(quantidade));

	}

}
