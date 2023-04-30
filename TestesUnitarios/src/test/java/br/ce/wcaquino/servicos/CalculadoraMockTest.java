package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class CalculadoraMockTest {
	
	@Test
	public void teste() {
		
		//Ponto importante sobre os Matchers, é que se o metodo que chamamos esperar mais de um parametro
		//e no passarmos um matcher, todos os outros parametros devem ser matchers.
		//Caso seja preciso passar um parametro especifico e nos outros os marchers,
		//podemos utilizar o "eq" passando o valor dentro das aspas.
		Calculadora calc = Mockito.mock(Calculadora.class);
		
		ArgumentCaptor<Integer> argCapt = ArgumentCaptor.forClass(Integer.class);
		Mockito.when(calc.somar(argCapt.capture(), argCapt.capture())).thenReturn(5);
		
		
		Assert.assertEquals(5, calc.somar(1, 10000));
		System.out.println(argCapt.getAllValues());
	}

}
