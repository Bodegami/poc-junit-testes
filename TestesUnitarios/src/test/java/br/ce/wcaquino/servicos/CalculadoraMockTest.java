package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


public class CalculadoraMockTest {
	
	@Mock
	private Calculadora calcMock;
	
	@Spy
	private Calculadora calcSpy;
	
	@Spy
	private EmailService emailServiceSpy;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void devoMostrarDiferencaEntreMockESpy() {
		Mockito.when(calcMock.somar(1, 2)).thenReturn(5);
		//Mockito.when(calcSpy.somar(1, 2)).thenReturn(5);
		Mockito.doReturn(5).when(calcSpy).somar(1, 2);
		Mockito.doNothing().when(calcSpy).imprime();
		
		//Retorna 0 que é o valor default quando passado valor diferente da expectativa
		System.out.println("Mock: " + calcMock.somar(1, 2));
		
		//Chama o metodo real quando passado valor diferente da expectativa
		//Por conta dessa caracteristica, o Spy não funciona com interfaces, apenas classes concretas
		System.out.println("Spy: " + calcSpy.somar(1, 2));
		
		System.out.println("Mock");
		calcMock.imprime();
		System.out.println("Spy");
		calcSpy.imprime();
	}
	
	
	
	
	
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
