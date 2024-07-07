package gt.com.archteam.mockitoapp.repositories;

import java.util.Collections;
import java.util.List;

import gt.com.archteam.mockitoapp.models.Examen;

public class ExamenRepositoryImpl implements ExamenRepository {

    @Override
    public List<Examen> findAll() {
        return Collections.emptyList();
        // return Arrays.asList(new Examen(5L, "Matematicas"), new Examen(6L, "Lenguaje"), new Examen(7L, "Historia"));
    }

}
