package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static br.ce.wcaquino.utils.DataUtils.verificarDiaSemana;
import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.matchers.MatchersProprios;

public class LocacaoServiceTest {
	
	private LocacaoService service;
	private List<Filme> filmes;
	private SPCService spcService;
	private LocacaoDAO dao;
	private EmailService emailService;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		service = new LocacaoService();
		filmes = new ArrayList<Filme>();
		dao = Mockito.mock(LocacaoDAO.class);
		service.setLocacaoDAO(dao);
		spcService = Mockito.mock(SPCService.class);
		service.setSpcService(spcService);
		emailService = Mockito.mock(EmailService.class);
		service.setEmailService(emailService);
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		Assume.assumeFalse(verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario
		Usuario usuario = umUsuario().agora();
		Filme filme = umFilme().comValor(5.0).agora();
		Filme filme2 = umFilme().comValor(6.0).agora();
		filmes.add(filme);
		filmes.add(filme2);
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificacao
		error.checkThat(locacao.getValor(), is(CoreMatchers.equalTo(11.0)));
		error.checkThat(locacao.getValor(), is(CoreMatchers.not(6.0)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHoje());
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
		error.checkThat(locacao.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDias(1));
	}
	
	//Forma Elegante
	@Test(expected = FilmeSemEstoqueException.class)
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque_01() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		Filme filme = umFilme().semEstoque().agora();
		filmes.add(filme);
		
		//acao
		service.alugarFilme(usuario, filmes);
	}
	
	//Forma não tão Elegante, porém permite validar pelo objeto da Exception
	@Test
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque_02() {
		//cenario
		Usuario usuario = umUsuario().agora();
		Filme filme = umFilmeSemEstoque().agora();
		filmes.add(filme);
		
		//acao
		try {
			service.alugarFilme(usuario, filmes);
			Assert.fail();
		} catch (Exception e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Filme sem estoque"));
		}
	}
	
	//Forma Nova
	@Test
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque_03() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		Filme filme = umFilmeSemEstoque().agora();
		filmes.add(filme);
		
		exception.expect(Exception.class);
		exception.expectMessage("Filme sem estoque");
		
		//acao
		service.alugarFilme(usuario, filmes);
	}

	@Test
	public void deveLancarExcecaoAoAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {

		Filme filme = umFilme().agora();
		filmes.add(filme);
		
		try {
			service.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("usuario vazio"));
		}
		
	}
	
	@Test
	public void deveLancarExcecaoAoAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
		
		Usuario usuario = umUsuario().agora();
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("filme vazio");
		
		service.alugarFilme(usuario, null);
	}
	
	@Test
	//@Ignore
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
		Assume.assumeTrue(verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		Usuario usuario = umUsuario().agora();
		filmes.add(umFilme().agora());
		
		Locacao resultado = service.alugarFilme(usuario, filmes);

		Assert.assertThat(resultado.getDataRetorno(), MatchersProprios.caiEm(Calendar.MONDAY));
		Assert.assertThat(resultado.getDataRetorno(), MatchersProprios.caiNumaSegunda(Calendar.MONDAY));
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws FilmeSemEstoqueException {
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//O mock espera exatamente o mesmo objeto informado no when
		//Ele sabe se é o mesmo objeto fazendo uma comparacao com o informado no when
		//e com o que foi passado na chamada metodo. Esse comparacao é feita usando o
		//equals da classe do objeto
		Mockito.when(spcService.possuiNegativacao(usuario)).thenReturn(true);
		
		try {
			service.alugarFilme(usuario, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usuario negativado"));
		}
		
		Mockito.verify(spcService).possuiNegativacao(usuario);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		Usuario usuario = umUsuario().agora();
		List<Locacao> locacoes = Arrays.asList(
						umLocacao()
						.comUsuario(usuario)
						.comDataRetorno(obterDataComDiferencaDias(-2))
						.agora());
		
		Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		service.notificarAtrasos();
		
		Mockito.verify(emailService).notificarAtraso(usuario);
	}
	
	//Usado apenas para gerar o Builder da Locacao
//	public static void main(String[] args) {
//		new BuilderMaster().gerarCodigoClasse(Locacao.class);
//	}
}
