package gt.com.archteam.mockitoapp.repositories;

import java.util.List;

import gt.com.archteam.mockitoapp.models.Examen;
import gt.com.archteam.mockitoapp.utils.Datos;

public class ExamenRepositoryImpl implements ExamenRepository {

    @Override
    public List<Examen> findAll() {
        return Datos.EXAMENES;
    }

    @Override
    public Examen guardar(Examen examen) {
        return Datos.EXAMEN;
    }

}
