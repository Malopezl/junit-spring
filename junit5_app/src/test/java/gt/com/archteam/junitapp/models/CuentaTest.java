package gt.com.archteam.junitapp.models;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

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

    private TestInfo testInfo;
    private TestReporter testReporter;

    @BeforeEach
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter) {
        // process.env.ENVIRONMENT = "dev";
        this.cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        this.testInfo = testInfo;
        this.testReporter = testReporter;
        System.out.println("iniciando el metodo.");
        testReporter.publishEntry("Ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().get().getName()
                + " con las etiquetas: " + testInfo.getTags());
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

    /**
     * CuentaTestNombreSaldo
     */
    @Nested
    @Tag("cuenta")
    @DisplayName("probando atributos de la cuenta corriente")
    class CuentaTestNombreSaldo {

        @Test
        @DisplayName("probando nombre")
        void testNombreCuenta() {
            testReporter.publishEntry(testInfo.getTags().toString());
            if (testInfo.getTags().contains("cuenta")) {
                testReporter.publishEntry("hacer algo con la etiqueta cuenta");
            }
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
        @DisplayName("probando el saldo, que no sea nulo, mayor que cero, valor esperado")
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
    }

    /**
     * CuentaOperacionesTest
     */
    @Nested
    class CuentaOperacionesTest {

        @Test
        @Tag("cuenta")
        @DisplayName("probando debitos sobre la cuenta corriente")
        void testDebitoCuenta() {
            cuenta.debito(new BigDecimal("10"));
            assertNotNull(cuenta.getSaldo());
            assertEquals(90, cuenta.getSaldo().intValue());
            assertEquals("90.42", cuenta.getSaldo().toPlainString());
        }

        @Test
        @Tag("cuenta")
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal("10"));
            assertNotNull(cuenta.getSaldo());
            assertEquals(110, cuenta.getSaldo().intValue());
            assertEquals("110.42", cuenta.getSaldo().toPlainString());
        }

        @Test
        @Tag("cuenta")
        @Tag("banco")
        void testTransferirDineroCuentas() {
            Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
            Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.99"));

            Banco banco = new Banco();
            banco.setNombre("Banco del estado");
            banco.transferir(cuenta2, cuenta1, new BigDecimal("500"));
            assertEquals("1000.99", cuenta2.getSaldo().toPlainString());
            assertEquals("3000", cuenta1.getSaldo().toPlainString());
        }
    }

    @Test
    @Tag("cuenta")
    @Tag("error")
    void testDineroInsuficienteException() {
        Exception exception = assertThrows(DineroInsuficienteException.class,
                () -> cuenta.debito(new BigDecimal("150")));
        assertEquals("Dinero insuficiente", exception.getMessage());
    }

    @Test
    @Tag("cuenta")
    @Tag("banco")
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

    /**
     * SistemaOperativoTest
     */
    @Nested
    class SistemaOperativoTest {

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
    }

    /**
     * JavaVersionTest
     */
    @Nested
    class JavaVersionTest {

        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void testSoloJdk8() {

        }

        @Test
        @DisabledOnJre(JRE.JAVA_17)
        void testNoJDK17() {

        }
    }

    /**
     * SistemPropertiesTest
     */
    @Nested
    class SistemPropertiesTest {

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
    }

    /**
     * VariableAmbienteTest
     */
    @Nested
    class VariableAmbienteTest {

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
    @DisplayName("test saldo cuenta dev 2")
    void testSaldoCuentaDev2() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev, () -> {
            assertNotNull(cuenta.getSaldo());
            assertEquals(100.42, cuenta.getSaldo().doubleValue());
        });
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @DisplayName("probando Debito Cuenta Repetir")
    @RepeatedTest(value = 5, name = "{displayName} - repeticion numero: {currentRepetition} de {totalRepetitions}")
    void testDebitoCuentaRepetir(RepetitionInfo info) {
        if (info.getCurrentRepetition() == 3) {
            System.out.println("repeticion " + info.getCurrentRepetition());
        }
        cuenta.debito(new BigDecimal("10"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(90, cuenta.getSaldo().intValue());
        assertEquals("90.42", cuenta.getSaldo().toPlainString());
    }

    /**
     * 
     * Test parametrizados
     * 
     */
    @Nested
    @Tag("param")
    class PruebasParametrizadasTest {
        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @ValueSource(strings = { "100", "200", "300", "500", "700", "1000" })
        // @ValueSource(doubles = {100, 200, 300, 500, 700, 1000.12345})
        // El parametro que recibe el metodo debe ser del mismo tipo que el valueSource
        void testDebitoCuentaValueSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({ "1,100", "2,200", "3,300", "4,500", "5,700", "6,1000.12345" })
        void testDebitoCuentaCsvSource(String index, String monto) {
            System.out.println(index + " -> " + monto);
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({ "200,100", "250,200", "300,300", "510,500", "750,700", "1000.12345,1000.12345" })
        void testDebitoCuentaCsvSource2(String saldo, String monto) {
            System.out.println(saldo + " -> " + monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaCsvFileSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

    }

    @Tag("param")
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @MethodSource("montoList")
    void testDebitoCuentaMethodSource(String monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    static List<String> montoList() {
        return Arrays.asList("100", "200", "300", "500", "700", "1000.12345");
    }

    @Nested
    @Tag("timeout")
    class EjemploTimeoutTest {
        @Test
        @Timeout(1)
        void pruebaTimeout() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(100);
        }
    
        @Test
        @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
        void pruebaTimeout2() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(450);
        }

        @Test
        void testTimeoutAssertions() {
            assertTimeout(Duration.ofSeconds(1), () -> {
                TimeUnit.MILLISECONDS.sleep(900);
            });
        }
    }

}
