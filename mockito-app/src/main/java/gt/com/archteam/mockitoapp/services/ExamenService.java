package gt.com.archteam.mockitoapp.services;

import java.util.Optional;

import gt.com.archteam.mockitoapp.models.Examen;

public interface ExamenService {
    Optional<Examen> findExamenPorNombre(String nombre);

    Examen findExamenPorNombreConPreguntas(String nombre);

    Examen guardar(Examen examen);
}
