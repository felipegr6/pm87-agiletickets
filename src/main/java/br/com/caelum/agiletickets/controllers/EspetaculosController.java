package br.com.caelum.agiletickets.controllers;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.validation.Valid;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import br.com.caelum.agiletickets.domain.Agenda;
import br.com.caelum.agiletickets.domain.DiretorioDeEstabelecimentos;
import br.com.caelum.agiletickets.domain.precos.CalculadoraDePrecos;
import br.com.caelum.agiletickets.models.Espetaculo;
import br.com.caelum.agiletickets.models.Estabelecimento;
import br.com.caelum.agiletickets.models.Periodicidade;
import br.com.caelum.agiletickets.models.Sessao;
import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.validator.SimpleMessage;
import br.com.caelum.vraptor.validator.Validator;

import com.google.common.base.Strings;

import static br.com.caelum.vraptor.view.Results.status;

@Controller
public class EspetaculosController {

	private NumberFormat CURRENCY = NumberFormat
			.getCurrencyInstance(new Locale("pt", "BR"));

	private Result result;
	private Validator validator;
	private Agenda agenda;
	private DiretorioDeEstabelecimentos estabelecimentos;
	private Estabelecimento estabelecimento;

	/** @deprecated CDI eyes only */
	protected EspetaculosController() {
	}

	@Inject
	public EspetaculosController(Result result, Validator validator,
			Agenda agenda, DiretorioDeEstabelecimentos estabelecimentos) {
		this.result = result;
		this.validator = validator;
		this.agenda = agenda;
		this.estabelecimentos = estabelecimentos;
	}

	/**
	 * Inclui a lista de estabelecimentos.
	 * 
	 * @return
	 */
	@Get("/espetaculos")
	public List<Espetaculo> lista() {
		result.include("estabelecimentos", estabelecimentos.todos());
		return agenda.espetaculos();
	}

	/**
	 * Aqui eh onde fazemos as varias validacoes: se nao tiver nome, avisa o
	 * usuario; se nao tiver descricao, avisa o usuario.
	 * 
	 * @param espetaculo
	 */
	@Post("/espetaculos")
	public void adiciona(@Valid Espetaculo espetaculo) {

		validator.onErrorRedirectTo(this).lista();

		agenda.cadastra(espetaculo);
		result.redirectTo(this).lista();

	}

	@Get("/espetaculo/{espetaculoId}/sessoes")
	public void sessoes(Long espetaculoId) {

		Espetaculo espetaculo = carregaEspetaculo(espetaculoId);

		result.include("espetaculo", espetaculo);

	}

	/**
	 * Aqui faz a magica! cria sessoes baseado no periodo de inicio e fim
	 * passados pelo usuario
	 * 
	 * @param espetaculoId
	 * @param inicio
	 * @param fim
	 * @param horario
	 * @param periodicidade
	 */
	@Post("/espetaculo/{espetaculoId}/sessoes")
	public void cadastraSessoes(Long espetaculoId, LocalDate inicio,
			LocalDate fim, LocalTime horario, Periodicidade periodicidade) {

		Espetaculo espetaculo = carregaEspetaculo(espetaculoId);

		List<Sessao> sessoes = espetaculo.criaSessoes(inicio, fim, horario,
				periodicidade);

		agenda.agende(sessoes);

		result.include("message", sessoes.size()
				+ " sessões criadas com sucesso");
		result.redirectTo(this).lista();

	}

	@Get("/sessao/{id}")
	public void sessao(Long id) {

		Sessao sessao = agenda.sessao(id);

		if (sessao == null)
			result.notFound();

		result.include("sessao", sessao);

	}

	@Post
	@Path("/sessao/{sessaoId}/reserva")
	public void reserva(Long sessaoId, final Integer quantidade) {

		Sessao sessao = agenda.sessao(sessaoId);

		if (sessao == null) {
			result.notFound();
			return;
		}

		if (quantidade < 1) {
			validator.add(new SimpleMessage("",
					"Você deve escolher um lugar ou mais"));
		}

		if (!sessao.podeReservar(quantidade)) {
			validator.add(new SimpleMessage("",
					"Não existem ingressos disponíveis"));
		}

		// em caso de erro, redireciona para a lista de sessao
		validator.onErrorRedirectTo(this).sessao(sessao.getId());

		BigDecimal precoTotal = CalculadoraDePrecos.calcula(sessao, quantidade);

		sessao.reserva(quantidade);

		result.include("message", "Sessão reservada com sucesso por "
				+ CURRENCY.format(precoTotal));

		result.redirectTo(IndexController.class).index();
	}

	private Espetaculo carregaEspetaculo(Long espetaculoId) {
		Espetaculo espetaculo = agenda.espetaculo(espetaculoId);
		if (espetaculo == null) {
			validator.add(new SimpleMessage("", ""));
		}
		validator.onErrorUse(status()).notFound();
		return espetaculo;
	}

	// metodo antigo. aqui soh por backup
	private Estabelecimento criaEstabelecimento(Long id) {
		return estabelecimentos.todos().get(0);
	}

}
