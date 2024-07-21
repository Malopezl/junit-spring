package gt.com.archteam.mockitoapp.repositories;

import java.util.Collections;
import java.util.List;

import gt.com.archteam.mockitoapp.models.Examen;

public class ExamenRepositoryImpl implements ExamenRepository {

    @Override
    public List<Examen> findAll() {
        return Collections.emptyList();
    }

    @Override
    public Examen guardar(Examen examen) {
        return new Examen(null, null);
    }

}
