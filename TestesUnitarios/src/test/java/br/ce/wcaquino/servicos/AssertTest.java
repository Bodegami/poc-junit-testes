package br.ce.wcaquino.servicos;

import static org.junit.Assert.*;

import org.junit.Test;

import br.ce.wcaquino.entidades.Usuario;

public class AssertTest {
	
	@Test
	public void test() {
		assertTrue(true);
		assertFalse(false);
		
		assertEquals(2, 2);
		
		//Para tipos flutuantes, é preciso passar um valor Delta como terceiro parametro
		//Lembrando que o delta representa a margem de erro
		assertEquals(0.51, 0.51, 0.01);
		assertEquals(0.51234, 0.513, 0.001);
		assertEquals(Math.PI, 3.14, 0.01);
		
		
		//Não é possível comparar tipos primitivos com classes, para isso é preciso fazer o Autoboxing ou Unboxing
		int num1 = 5;
		Integer num2 = 5;
		assertEquals(Integer.valueOf(num1), num2);
		assertEquals(num1, num2.intValue());
		
		
		assertEquals("bola", "bola");
		//assertEquals("bola", "Bola"); -> forma incorreta
		assertTrue("bola".equalsIgnoreCase("Bola"));
		assertTrue("bo".startsWith("bo"));
		
		
		//Para essa assertiva funcionar, a classe Usuario deve implementar o metodo equals
		//caso contrario, o assertEquals vai utilizar o metodo equals da classe mae Object
		//e fará a comparação pelo endereço de memoria
		Usuario usuario1 = new Usuario("Usuario 1");
		Usuario usuario2 = new Usuario("Usuario 1");
		assertEquals(usuario1, usuario2);
		
		
		//Caso a intenção seja fazer a comparação a nivel de instancia, podemos utilizar o assertSame
		//assertSame(usuario1, usuario2);
		assertNotSame(usuario1, usuario2);
		
		
		Usuario usuario3 = null;
		assertNull(usuario3);
		
		Usuario usuario4 = new Usuario("Usuario 4");
		assertNotNull(usuario4);
		
		//É possivel passar uma String como parametro que vai representar a mensagem em caso de falha
		assertNotEquals("Erro de comparacao", usuario1, usuario4);
		
		
	}

}
