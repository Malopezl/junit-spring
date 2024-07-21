package gt.com.archteam.mockitoapp.repositories;

import java.util.List;

import gt.com.archteam.mockitoapp.models.Examen;

public interface ExamenRepository {
    List<Examen> findAll();

    Examen guardar(Examen examen);
}
