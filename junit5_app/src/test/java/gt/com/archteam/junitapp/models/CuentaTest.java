package gt.com.archteam.junitapp.models;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import gt.com.archteam.junitapp.exceptions.DineroInsuficienteException;

class CuentaTest {

    @Test
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Juan", new BigDecimal("100.42"));
        cuenta.setPersona("Juan");
        assertEquals("Juan", cuenta.getPersona());
    }

    @Test
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("100.42"));
        assertEquals(100.42, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testReferenciaCuenta() {
        Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("9800.9997"));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("9800.9997"));
        // assertNotEquals(cuenta2, cuenta1);
        assertEquals(cuenta2, cuenta1);
    }

    @Test
    void testDebitoCuenta() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("100.42"));
        cuenta.debito(new BigDecimal("10"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(90, cuenta.getSaldo().intValue());
        assertEquals("90.42", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("100.42"));
        cuenta.credito(new BigDecimal("10"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(110, cuenta.getSaldo().intValue());
        assertEquals("110.42", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteException() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("100.42"));
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal("150"));
        });
        assertEquals("Dinero insuficiente", exception.getMessage());
    }

    @Test
    void testTransferirDineroCuentas() {
        Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.99"));

        Banco banco = new Banco();
        banco.setNombre("Banco del estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal("500"));
        assertEquals("1000.99", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta1.getSaldo().toPlainString());
    }

    @Test
    void testRelacionBancoCuentas() {
        Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.99"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);
        banco.setNombre("Banco del estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal("500"));

        assertAll(() -> assertEquals("1000.9", cuenta2.getSaldo().toPlainString()),
                () -> assertEquals("3000", cuenta1.getSaldo().toPlainString()),
                () -> assertEquals(2, banco.getCuentas().size()),
                () -> assertEquals("Banco del estado.", cuenta1.getBanco().getNombre()), () -> assertEquals("Andres",
                        banco.getCuentas().stream().filter(cuenta -> cuenta.getPersona().equals("Andres")).findFirst()
                                .get()
                                .getPersona()),
                () -> assertTrue(banco.getCuentas().stream().anyMatch(c -> c.getPersona().equals("Andres"))));
    }

}
