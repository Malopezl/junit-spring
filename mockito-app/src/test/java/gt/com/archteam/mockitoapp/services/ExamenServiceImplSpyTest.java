package gt.com.archteam.mockitoapp.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import gt.com.archteam.mockitoapp.models.Examen;
import gt.com.archteam.mockitoapp.repositories.ExamenRepositoryImpl;
import gt.com.archteam.mockitoapp.repositories.PreguntaRepositoryImpl;

/* FORMA 2: usando esta anotacion */
@ExtendWith(MockitoExtension.class)
class ExamenServiceImplSpyTest {
    @Spy
    PreguntaRepositoryImpl preguntaRepository;
    @Spy
    ExamenRepositoryImpl repository;

    /* No puede ser la interfaz, por eso se utiliza la clase */
    @InjectMocks
    ExamenServiceImpl service;

    /*
     * Se puede implementar un captor de esta forma en vez de hacerlo sobre la
     * funcion
     */
    @Captor
    ArgumentCaptor<Long> captor;


    @Test
    void testSpy() {
        List<String> preguntas = Arrays.asList("aritmetica");
        // when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
        assertEquals(1, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));

        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }
}
