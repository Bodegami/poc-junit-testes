package br.ce.wcaquino.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.ce.wcaquino.servicos.CalculadoraTest;
import br.ce.wcaquino.servicos.CalculoValorLocacaoTest;
import br.ce.wcaquino.servicos.LocacaoServiceTest;

// O uso de Suites não é recomendado, pela constante manutenção a cada nova classe de teste
// Além disso, geralmente ele gera problemas com esteiras de ci/cd

@RunWith(Suite.class)
@SuiteClasses({
	CalculadoraTest.class,
	CalculoValorLocacaoTest.class,
	LocacaoServiceTest.class
})
public class SuiteExecucao {
	//Remova se puder
	
//	@BeforeClass
//	public static void before() {
//		System.out.println("before");
//	}
//	
//	@AfterClass
//	public static void after() {
//		System.out.println("after");
//	}
}
