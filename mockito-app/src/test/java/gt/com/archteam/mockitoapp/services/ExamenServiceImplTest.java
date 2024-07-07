package gt.com.archteam.mockitoapp.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import gt.com.archteam.mockitoapp.models.Examen;
import gt.com.archteam.mockitoapp.repositories.ExamenRepository;

class ExamenServiceImplTest {

    @Test
    void testFindExamenPorNombre() {
        ExamenRepository repository = mock(ExamenRepository.class);
        ExamenService service = new ExamenServiceImpl(repository);
        var datos = Arrays.asList(new Examen(5L, "Matematicas"), new Examen(6L, "Lenguaje"),
                new Examen(7L, "Historia"));

        when(repository.findAll()).thenReturn(datos);
        Examen examen = service.findExamenPorNombre("Matematicas");

        assertNotNull(examen);
        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
    }

    @Test
    void testFindExamenPorNombreListaVacia() {
        ExamenRepository repository = mock(ExamenRepository.class);
        ExamenService service = new ExamenServiceImpl(repository);
        List<Examen> datos = Collections.emptyList();

        when(repository.findAll()).thenReturn(datos);
        Examen examen = service.findExamenPorNombre("Matematicas");

        assertNotNull(examen);
        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
    }
}
