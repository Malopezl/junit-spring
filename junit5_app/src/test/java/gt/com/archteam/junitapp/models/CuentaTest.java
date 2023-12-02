package gt.com.archteam.junitapp.models;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

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

}
