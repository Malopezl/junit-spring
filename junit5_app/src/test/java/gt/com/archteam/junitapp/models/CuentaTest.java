package gt.com.archteam.junitapp.models;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;

import gt.com.archteam.junitapp.exceptions.DineroInsuficienteException;

/*
 * Anotacion para generar una instancia por toda la clase y no por test. 
 * PD: No es recomendable hacer esto... 
 * PD2: se le puede quitar el static al beforeAll
 * y afterAll ya que se genera solo 1 instancia
 */
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {
    private Cuenta cuenta;

    @BeforeEach
    void initMetodoTest() {
        // process.env.ENVIRONMENT = "dev";
        this.cuenta = new Cuenta("Andres", new BigDecimal("100.42"));
        System.out.println("iniciando el metodo.");
    }

    @AfterEach
    void tearDown() {
        System.out.println("finalizando el metodo de prueba.");
    }

    @BeforeAll
    static void beforeClass() {
        System.out.println("Inicializando el test");
    }

    @AfterAll
    static void afterClass() {
        System.out.println("Finalizando el test");
    }

    @Test
    @DisplayName("probando nombre de la cuenta corriente")
    void testNombreCuenta() {
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
        assertEquals("Andres", cuenta.getPersona(), () -> "El nombre de la cuenta no es el que se esperaba");
    }

    @Test
    @DisplayName("probando el saldo de la cuenta corriente, que no sea nulo, mayor que cero, valor esperado")
    void testSaldoCuenta() {
        assertEquals(100.42, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("probando referencias que sean iguales")
    void testReferenciaCuenta() {
        cuenta = new Cuenta("John Doe", new BigDecimal("9800.9997"));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("9800.9997"));
        // assertNotEquals(cuenta2, cuenta1);
        assertEquals(cuenta2, cuenta);
    }

    @Test
    @DisplayName("probando debitos sobre la cuenta corriente")
    void testDebitoCuenta() {
        cuenta.debito(new BigDecimal("10"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(90, cuenta.getSaldo().intValue());
        assertEquals("90.42", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        cuenta.credito(new BigDecimal("10"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(110, cuenta.getSaldo().intValue());
        assertEquals("110.42", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteException() {
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

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testSoloWindows() {

    }

    @Test
    @EnabledOnOs({ OS.LINUX, OS.MAC })
    void testSoloLinuxMac() {

    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testNoWindows() {

    }

    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void testSoloJdk8() {

    }

    @Test
    @DisabledOnJre(JRE.JAVA_17)
    void testNoJDK17() {

    }

    @Test
    void imprimirSystemProperties() {
        Properties properties = System.getProperties();
        properties.forEach((k, v) -> System.out.println(k + ": " + v));
    }

    @Test
    @EnabledIfSystemProperty(named = "java.version", matches = ".*17.*")
    void testJavaVersion() {

    }

    @Test
    @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
    void testSolo64() {

    }

    @Test
    @EnabledIfSystemProperty(named = "user.name", matches = "mlopezl")
    void testUsername() {

    }

    @Test
    @EnabledIfSystemProperty(named = "ENV", matches = "dev")
    void testDev() {

    }

    @Test
    void imprimirVariablesAmbiente() {
        Map<String, String> getenv = System.getenv();
        getenv.forEach((k, v) -> System.out.println(k + " = " + v));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = "/somedir")
    void testHome() {

    }

    @Test
    @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "dev")
    void testEnv() {

    }

    @Test
    @DisplayName("test saldo cuenta dev")
    void testSaldoCuentaDev() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumeFalse(esDev);
        assertNotNull(cuenta.getSaldo());
        assertEquals(100.42, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("test saldo cuenta dev")
    void testSaldoCuentaDev2() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev, () -> {
            assertNotNull(cuenta.getSaldo());
            assertEquals(100.42, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        });
    }

}
