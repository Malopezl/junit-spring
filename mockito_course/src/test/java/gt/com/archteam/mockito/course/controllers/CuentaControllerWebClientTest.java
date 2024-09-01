package gt.com.archteam.mockito.course.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gt.com.archteam.mockito.course.models.Cuenta;
import gt.com.archteam.mockito.course.models.TransaccionDto;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CuentaControllerWebClientTest {
    @Autowired
    private WebTestClient client;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Por alguna razon no reconoce 'localhost' por lo que se dejo la IP
        client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:8080").build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(3)
    void testTransferir() throws JsonProcessingException {
        // Given
        var transaccion = new TransaccionDto();
        transaccion.setCuentaOrigenId(1L);
        transaccion.setCuentaDestinoId(2L);
        transaccion.setBancoId(1L);
        transaccion.setMonto(new BigDecimal(100));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("message", "Transferencia realizada con exito");
        response.put("transaccion", transaccion);

        // When
        client.post().uri("/api/cuentas/transferir").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transaccion).exchange()
                // Then
                .expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody()
                .consumeWith(respuesta -> {
                    try {
                        var json = objectMapper.readTree(respuesta.getResponseBody());
                        assertEquals("Transferencia realizada con exito", json.path("message").asText());
                        assertEquals(1L, json.path("transaccion").path("cuentaOrigenId").asLong());
                        assertEquals(LocalDate.now().toString(), json.path("date").asText());
                        assertEquals("100", json.path("transaccion").path("monto").asText());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.message").value(is("Transferencia realizada con exito"))
                .jsonPath("$.message").value(valor -> assertEquals("Transferencia realizada con exito", valor))
                .jsonPath("$.message").isEqualTo("Transferencia realizada con exito")
                .jsonPath("$.transaccion.cuentaOrigenId").isEqualTo(transaccion.getCuentaOrigenId())
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                .json(objectMapper.writeValueAsString(response));
    }

    @Test
    @Order(1)
    void testDetalle() throws JsonProcessingException {
        var cuenta = new Cuenta(1L, "Andres", new BigDecimal("1000"));

        client.get().uri("/api/cuentas/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.persona").isEqualTo("Andres")
                .jsonPath("$.saldo").isEqualTo(1000)
                .json(objectMapper.writeValueAsString(cuenta));
    }

    @Test
    @Order(2)
    void testDetalle2() {
        client.get().uri("/api/cuentas/2").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(response -> {
                    var cuenta = response.getResponseBody();
                    assertNotNull(cuenta);
                    assertEquals("Jhon", cuenta.getPersona());
                    assertEquals("2000.00", cuenta.getSaldo().toPlainString());
                });
    }

    @Test
    @Order(6)
    void testGuardar() {
        //Given
        var cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));

        //When
        client.post().uri("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
        .bodyValue(cuenta).exchange()
        
        //Then
        .expectStatus().isCreated()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.id").isEqualTo(3)
        .jsonPath("$.persona").isEqualTo("Pepe")
        .jsonPath("$.persona").value(is("Pepe"))
        .jsonPath("$.saldo").isEqualTo(3000);
    }

    @Test
    @Order(7)
    void testGuardar2() {
        //Given
        var cuenta = new Cuenta(null, "Pepa", new BigDecimal("3500"));

        //When
        client.post().uri("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
        .bodyValue(cuenta).exchange()
        
        //Then
        .expectStatus().isCreated()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(Cuenta.class)
        .consumeWith(response -> {
            var cuenta1 = response.getResponseBody();
            assertNotNull(cuenta1);
            assertEquals(4, cuenta1.getId());
            assertEquals("Pepa", cuenta1.getPersona());
            assertEquals("3500", cuenta1.getSaldo().toPlainString());
        });
    }

    @Test
    @Order(4)
    void testListar() {
        client.get().uri("/api/cuentas").exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$[0].persona").isEqualTo("Andres")
        .jsonPath("$[0].id").isEqualTo(1)
        .jsonPath("$[0].saldo").isEqualTo(900)
        .jsonPath("$[1].persona").isEqualTo("Jhon")
        .jsonPath("$[1].id").isEqualTo(2)
        .jsonPath("$[1].saldo").isEqualTo(2100)
        .jsonPath("$").isArray()
        .jsonPath("$").value(hasSize(2));
    }

    @Test
    @Order(5)
    void testListar2() {
        client.get().uri("/api/cuentas").exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Cuenta.class)
        .consumeWith(response -> {
            var cuentas = response.getResponseBody();
            assertNotNull(cuentas);
            assertEquals(2, cuentas.size());
            assertEquals("Andres", cuentas.get(0).getPersona());
            assertEquals(1L, cuentas.get(0).getId());
            assertEquals("900.00", cuentas.get(0).getSaldo().toPlainString());
            assertEquals("Jhon", cuentas.get(1).getPersona());
            assertEquals(2L, cuentas.get(1).getId());
            assertEquals("2100.00", cuentas.get(1).getSaldo().toPlainString());
        })
        .hasSize(2)
        .value(hasSize(2));
    }

    @Test
    @Order(8)
    void testEliminar() {
        client.get().uri("/api/cuentas").exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Cuenta.class)
        .hasSize(4);

        client.delete().uri("/api/cuentas/3").exchange()
        .expectStatus().isNoContent()
        .expectBody().isEmpty();

        client.get().uri("/api/cuentas").exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Cuenta.class)
        .hasSize(3);

        client.get().uri("/api/cuentas/3").exchange()
        // .expectStatus().is5xxServerError();
        .expectStatus().isNotFound()
        .expectBody().isEmpty();
    }

}
