package gt.com.archteam.mockito.course;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import gt.com.archteam.mockito.course.exceptions.DineroInsuficienteException;
import gt.com.archteam.mockito.course.models.Banco;
import gt.com.archteam.mockito.course.models.Cuenta;
import gt.com.archteam.mockito.course.repositories.BancoRepository;
import gt.com.archteam.mockito.course.repositories.CuentaRepository;
import gt.com.archteam.mockito.course.services.CuentaService;

@SpringBootTest
class MockitoCourseApplicationTests {
	// @Mock
	@MockBean
	CuentaRepository cuentaRepository;
	// @Mock
	@MockBean
	BancoRepository bancoRepository;

	// @InjectMocks
	/* Spring si permite utilizar la interfaz en vez de usar la implementacion */
	@Autowired
	CuentaService service;

	@BeforeEach
	void setUp() {
		// cuentaRepository = mock(CuentaRepository.class);
		// bancoRepository = mock(BancoRepository.class);
		// service = new CuentaServiceImpl(cuentaRepository, bancoRepository);
	}

	@Test
	void contextLoads() {
		when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(Datos.crearCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(Datos.crearBanco());

		var saldoOrigen = service.revisarSaldo(1L);
		var saldoDestino = service.revisarSaldo(2L);
		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		service.transferir(1L, 2L, new BigDecimal("100"), 1L);

		saldoOrigen = service.revisarSaldo(1L);
		saldoDestino = service.revisarSaldo(2L);
		assertEquals("900", saldoOrigen.toPlainString());
		assertEquals("2100", saldoDestino.toPlainString());

		var total = service.revisarTotalTransferencias(1L);
		assertEquals(1, total);

		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(3)).findById(2L);
		verify(cuentaRepository, times(2)).save(any(Cuenta.class));

		verify(bancoRepository, times(2)).findById(1L);
		verify(bancoRepository).save(any(Banco.class));

		verify(cuentaRepository, times(6)).findById(anyLong());
		verify(cuentaRepository, never()).findAll();
	}

	@Test
	void contextLoads2() {
		when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(Datos.crearCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(Datos.crearBanco());

		var saldoOrigen = service.revisarSaldo(1L);
		var saldoDestino = service.revisarSaldo(2L);
		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		assertThrows(DineroInsuficienteException.class, () -> service.transferir(1L, 2L, new BigDecimal("1200"), 1L));

		saldoOrigen = service.revisarSaldo(1L);
		saldoDestino = service.revisarSaldo(2L);
		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		var total = service.revisarTotalTransferencias(1L);
		assertEquals(0, total);

		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(2)).findById(2L);
		verify(cuentaRepository, never()).save(any(Cuenta.class));

		verify(bancoRepository, times(1)).findById(1L);
		verify(bancoRepository, never()).save(any(Banco.class));

		verify(cuentaRepository, times(5)).findById(anyLong());
		verify(cuentaRepository, never()).findAll();
	}

	@Test
	void contextLoads3() {
		when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());

		var cuenta1 = service.findById(1L);
		var cuenta2 = service.findById(1L);

		assertSame(cuenta1, cuenta2);
		assertTrue(cuenta1 == cuenta2);
		assertEquals("Andres", cuenta1.getPersona());
		assertEquals("Andres", cuenta2.getPersona());

		verify(cuentaRepository, times(2)).findById(1L);
	}

	@Test
	void testFindAll() {
		// Given
		var datos = Arrays.asList(Datos.crearCuenta001().orElseThrow(), Datos.crearCuenta002().orElseThrow());
		when(cuentaRepository.findAll()).thenReturn(datos);

		// When
		var cuentas = service.findAll();

		// Then
		assertFalse(cuentas.isEmpty());
		assertEquals(2, cuentas.size());
		assertTrue(cuentas.contains(Datos.crearCuenta002().orElseThrow()));

		verify(cuentaRepository).findAll();
	}

	@Test
	void testSave() {
		// Given
		var cuentaNueva = new Cuenta(null, "Pepe", new BigDecimal("3000"));
		when(cuentaRepository.save(any())).then(invocation -> {
			Cuenta c = invocation.getArgument(0);
			c.setId(3L);
			return c;
		});

		// When
		var cuenta = service.save(cuentaNueva);
		
		// Then
		assertEquals("Pepe", cuenta.getPersona());
		assertEquals(3, cuenta.getId());
		assertEquals("3000", cuenta.getSaldo().toPlainString());

		verify(cuentaRepository).save(any());
	}
}
