package br.ce.wcaquino.servicos;

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

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

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
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 2, 5.0);
		Filme filme2 = new Filme("Filme 2", 2, 6.0);
		filmes.add(filme);
		filmes.add(filme2);
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificacao
		error.checkThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(11.0)));
		error.checkThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.not(6.0)));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), CoreMatchers.is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), 
				CoreMatchers.is(true));
	}
	
	//Forma Elegante
	@Test(expected = FilmeSemEstoqueException.class)
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque_01() throws Exception {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		filmes.add(filme);
		
		//acao
		service.alugarFilme(usuario, filmes);
	}
	
	//Forma não tão Elegante, porém permite validar pelo objeto da Exception
	@Test
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque_02() {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		filmes.add(filme);
		
		//acao
		try {
			service.alugarFilme(usuario, filmes);
		} catch (Exception e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Filme sem estoque"));
		}
	}
	
	//Forma Nova
	@Test
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque_03() throws Exception {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		filmes.add(filme);
		
		exception.expect(Exception.class);
		exception.expectMessage("Filme sem estoque");
		
		//acao
		service.alugarFilme(usuario, filmes);
	}

	@Test
	public void deveLancarExcecaoAoAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {

		Filme filme = new Filme("Filme 1", 2, 5.0);
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
		
		Usuario usuario = new Usuario("Usuario 1");
		
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
		
		Usuario usuario = new Usuario("Usuario 1");
		filmes.add(new Filme("Filme 1", 1, 5.0));
		
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		boolean ehSegunda = DataUtils.verificarDiaSemana(resultado.getDataRetorno(), Calendar.MONDAY);
		Assert.assertTrue(ehSegunda);
	}
}
