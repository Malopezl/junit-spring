package gt.com.archteam.junitapp.models;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import gt.com.archteam.junitapp.exceptions.DineroInsuficienteException;

class CuentaTest {

    @Test
    @DisplayName("probando nombre de la cuenta corriente")
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Juan", new BigDecimal("100.42"));
        /*
         * Para mostrar un mensaje en caso de error se puede utilizar esta forma pero no
         * es la recomendada ya que, al dejar el mensaje directo en caso de que no haya
         * error JUnit igual genera el espacio de memoria para este mensaje.
         * (La nueva forma solo es para JUnit5)
         * 
         * EJ:
         * assertNotNull(cuenta.getPersona(), "La cuenta no puede ser nula");
         */
        assertNotNull(cuenta.getPersona(), () -> "La cuenta no puede ser nula");
        assertEquals("Juan", cuenta.getPersona(), () -> "El nombre de la cuenta no es el que se esperaba");
    }

    @Test
    @DisplayName("probando el saldo de la cuenta corriente, que no sea nulo, mayor que cero, valor esperado")
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("100.42"));
        assertEquals(100.42, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("probando referencias que sean iguales")
    void testReferenciaCuenta() {
        Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("9800.9997"));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("9800.9997"));
        // assertNotEquals(cuenta2, cuenta1);
        assertEquals(cuenta2, cuenta1);
    }

    @Test
    @DisplayName("probando debitos sobre la cuenta corriente")
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
        Exception exception = assertThrows(DineroInsuficienteException.class,
                () -> cuenta.debito(new BigDecimal("150")));
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
    @Disabled("probando anotacion para omitir tests")
    @DisplayName("probando relaciones entre las cuentas y el banco")
    void testRelacionBancoCuentas() {
        fail();
        Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.99"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);
        banco.setNombre("Banco del estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal("500"));

        assertAll(
                () -> assertEquals("1000.99", cuenta2.getSaldo().toPlainString(),
                        () -> "El valor del saldo de la cuenta2 no es el esperado"),
                () -> assertEquals("3000", cuenta1.getSaldo().toPlainString(),
                        () -> "El valor del saldo de la cuenta1 no es el esperado"),
                () -> assertEquals(2, banco.getCuentas().size(),
                        () -> "El banco no tiene las cuentas esperadas"),
                () -> assertEquals("Banco del estado", cuenta1.getBanco().getNombre()), () -> assertEquals("Andres",
                        banco.getCuentas().stream().filter(cuenta -> cuenta.getPersona().equals("Andres")).findFirst()
                                .get()
                                .getPersona(),
                        () -> "El nombre del banco no es el esperado"),
                () -> assertTrue(banco.getCuentas().stream().anyMatch(c -> c.getPersona().equals("Andres")),
                        () -> "La cuenta no existe en el banco"));
    }

}
