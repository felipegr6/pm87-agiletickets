package br.com.caelum.agiletickets.domain.precos;

import java.math.BigDecimal;

import br.com.caelum.agiletickets.models.Sessao;

public class CalculadoraDePrecos {

	public static BigDecimal calcula(Sessao sessao, Integer quantidade) {

		CalculadoraAbstract calculadora;

		switch (sessao.getEspetaculo().getTipo()) {

		case CINEMA:
			calculadora = new CinemaCalculadora();
			break;
		case SHOW:
			calculadora = new ShowCalculadora();
			break;
		case BALLET:
			calculadora = new BalletCalculadora();
			break;
		case ORQUESTRA:
			calculadora = new OrquestraCalculadora();
			break;
		case TEATRO:
			calculadora = new TeatroCalculadora();
			break;
		default:
			calculadora = new SemTipoCalculadora();

		}

		return calculadora.calcula(sessao, quantidade);

	}

}