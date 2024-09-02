package gt.com.archteam.mockito.course.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gt.com.archteam.mockito.course.models.Cuenta;
import gt.com.archteam.mockito.course.models.TransaccionDto;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CuentaControllerRestTemplateTest {
    @Autowired
    private TestRestTemplate client;

    private ObjectMapper objectMapper;

    @LocalServerPort
    private int puerto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(3)
    void testListar() throws JsonMappingException, JsonProcessingException {
        var respuesta = client.getForEntity(getUri("/api/cuentas"), Cuenta[].class);
        List<Cuenta> cuentas = Arrays.asList(respuesta.getBody());

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());

        assertNotNull(cuentas);
        assertEquals(2, cuentas.size());
        assertEquals(1L, cuentas.get(0).getId());
        assertEquals("Andres", cuentas.get(0).getPersona());
        assertEquals("900.00", cuentas.get(0).getSaldo().toPlainString());
        assertEquals(2L, cuentas.get(1).getId());
        assertEquals("Jhon", cuentas.get(1).getPersona());
        assertEquals("2100.00", cuentas.get(1).getSaldo().toPlainString());

        var json = objectMapper.readTree(objectMapper.writeValueAsString(cuentas));
        assertEquals(1L, json.get(0).path("id").asLong());
        assertEquals("Andres", json.get(0).path("persona").asText());
        assertEquals("900.0", json.get(0).path("saldo").asText());
        assertEquals(2L, json.get(1).path("id").asLong());
        assertEquals("Jhon", json.get(1).path("persona").asText());
        assertEquals("2100.0", json.get(1).path("saldo").asText());
    }

    @Test
    @Order(2)
    void testDetalle() {
        var respuesta = client.getForEntity(getUri("/api/cuentas/1"), Cuenta.class);
        var cuenta = respuesta.getBody();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());

        assertNotNull(cuenta);
        assertEquals(1L, cuenta.getId());
        assertEquals("Andres", cuenta.getPersona());
        assertEquals("900.00", cuenta.getSaldo().toPlainString());
        assertEquals(new Cuenta(1L, "Andres", new BigDecimal("900.00")), cuenta);
    }

    @Test
    @Order(5)
    void testEliminar() {
        var respuesta = client.getForEntity(getUri("/api/cuentas"), Cuenta[].class);
        List<Cuenta> cuentas = Arrays.asList(respuesta.getBody());
        assertEquals(3, cuentas.size());

        /* Mas formas de realizar las peticiones de delete */
        // client.delete(getUri("/api/cuentas/3"));
        // Map<String, Long> pathVariables = new HashMap<>();
        // pathVariables.put("id", 3L);
        // var response = client.exchange(getUri("/api/cuentas/{id}"), HttpMethod.DELETE, null, Void.class, pathVariables);
        var response = client.exchange(getUri("/api/cuentas/3"), HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(response.hasBody());

        respuesta = client.getForEntity(getUri("/api/cuentas"), Cuenta[].class);
        cuentas = Arrays.asList(respuesta.getBody());
        assertEquals(2, cuentas.size());

        var respuestaDetalle = client.getForEntity(getUri("/api/cuentas/3"), Cuenta.class);
        assertEquals(HttpStatus.NOT_FOUND, respuestaDetalle.getStatusCode());
        assertFalse(respuestaDetalle.hasBody());
    }

    @Test
    @Order(4)
    void testGuardar() {
        var newCuenta = new Cuenta(null, "Pepa", new BigDecimal("3800"));

        /* Por defecto el header MediaType de la peticion es APPLICATION_JSON */
        var respuesta = client.postForEntity(getUri("/api/cuentas"), newCuenta, Cuenta.class);
        var cuenta = respuesta.getBody();

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());

        assertEquals(3L, cuenta.getId());
        assertEquals("Pepa", cuenta.getPersona());
        assertEquals("3800", cuenta.getSaldo().toPlainString());
    }

    @Test
    @Order(1)
    void testTransferir() throws JsonMappingException, JsonProcessingException {
        var transaccion = new TransaccionDto();
        transaccion.setMonto(new BigDecimal("100"));
        transaccion.setCuentaDestinoId(2L);
        transaccion.setCuentaOrigenId(1L);
        transaccion.setBancoId(1L);

        // var response = client.postForEntity("/api/cuentas/transferir", transaccion, String.class);
        var response = client.postForEntity(getUri("/api/cuentas/transferir"), transaccion, String.class);
        
        var json = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(json);
        assertTrue(json.contains("Transferencia realizada con exito"));

        var jsonNode = objectMapper.readTree(json);
        assertEquals("Transferencia realizada con exito", jsonNode.path("message").asText());
        assertEquals(LocalDate.now().toString(), jsonNode.path("date").asText());
        assertEquals("100", jsonNode.path("transaccion").path("monto").asText());
        assertEquals(1L, jsonNode.path("transaccion").path("cuentaOrigenId").asLong());

        Map<String, Object> response2 = new HashMap<>();
        response2.put("date", LocalDate.now().toString());
        response2.put("status", "OK");
        response2.put("message", "Transferencia realizada con exito");
        response2.put("transaccion", transaccion);

        assertEquals(objectMapper.writeValueAsString(response2), json);
    }

    private String getUri(String uri) {
        System.out.println(puerto);
        return "http://localhost:" + puerto + uri;
    }
}
