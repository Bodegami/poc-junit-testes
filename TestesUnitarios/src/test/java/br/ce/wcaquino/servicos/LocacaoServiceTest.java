package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
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
import br.ce.wcaquino.utils.DataUtils;
import buildermaster.BuilderMaster;

public class LocacaoServiceTest {
	
	private LocacaoService service;
	private List<Filme> filmes;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		service = new LocacaoService();
		filmes = new ArrayList<Filme>();
		LocacaoDAO dao = Mockito.mock(LocacaoDAO.class);
		service.setLocacaoDAO(dao);
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
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
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHoje());
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), is(true));
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
	
//	@Test
//	public void devePagar75PorcentoNoFilme_3() throws FilmeSemEstoqueException, LocadoraException {
//		Usuario usuario = new Usuario("Usuario 1");
//		filmes.addAll(Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0), new Filme("Filme 3", 2, 4.0)));
//		
//		Locacao resultado = service.alugarFilme(usuario, filmes);
//		
//		//4+4+3==11
//		Assert.assertThat(resultado.getValor(), CoreMatchers.is(11.0));
//		
//	}
//	
//	@Test
//	public void devePagar50PorcentoNoFilme_4() throws FilmeSemEstoqueException, LocadoraException {
//		Usuario usuario = new Usuario("Usuario 1");
//		filmes.addAll(Arrays.asList(
//				new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0), 
//				new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0)));
//		
//		Locacao resultado = service.alugarFilme(usuario, filmes);
//		
//		//4+4+3+2==13
//		Assert.assertThat(resultado.getValor(), CoreMatchers.is(13.0));
//		
//	}
//	
//	@Test
//	public void devePagar25PorcentoNoFilme_5() throws FilmeSemEstoqueException, LocadoraException {
//		Usuario usuario = new Usuario("Usuario 1");
//		filmes.addAll(Arrays.asList(
//				new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0),	new Filme("Filme 3", 2, 4.0), 
//				new Filme("Filme 4", 2, 4.0), new Filme("Filme 5", 2, 4.0)));
//		
//		Locacao resultado = service.alugarFilme(usuario, filmes);
//		
//		//4+4+3+2+1==14
//		Assert.assertThat(resultado.getValor(), CoreMatchers.is(14.0));
//		
//	}
//	
//	@Test
//	public void devePagar0PorcentoNoFilme_6() throws FilmeSemEstoqueException, LocadoraException {
//		Usuario usuario = new Usuario("Usuario 1");
//		filmes.addAll(Arrays.asList(
//				new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0),	new Filme("Filme 3", 2, 4.0), 
//				new Filme("Filme 4", 2, 4.0), new Filme("Filme 5", 2, 4.0), new Filme("Filme 6", 2, 4.0)));
//		
//		Locacao resultado = service.alugarFilme(usuario, filmes);
//		
//		//4+4+3+2+1+0==14
//		Assert.assertThat(resultado.getValor(), CoreMatchers.is(14.0));
//		
//	}
	
	@Test
	//@Ignore
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		Usuario usuario = umUsuario().agora();
		filmes.add(umFilme().agora());
		
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		//boolean ehSegunda = DataUtils.verificarDiaSemana(resultado.getDataRetorno(), Calendar.MONDAY);
		//Assert.assertTrue(ehSegunda);
		//Assert.assertThat(resultado.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
		Assert.assertThat(resultado.getDataRetorno(), MatchersProprios.caiEm(Calendar.MONDAY));
		Assert.assertThat(resultado.getDataRetorno(), MatchersProprios.caiNumaSegunda(Calendar.MONDAY));
	}
	
	public static void main(String[] args) {
		new BuilderMaster().gerarCodigoClasse(Locacao.class);
	}
}
