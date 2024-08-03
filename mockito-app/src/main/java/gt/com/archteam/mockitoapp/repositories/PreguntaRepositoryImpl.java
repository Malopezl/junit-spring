package gt.com.archteam.mockitoapp.repositories;

import java.util.List;

import gt.com.archteam.mockitoapp.utils.Datos;

public class PreguntaRepositoryImpl implements PreguntaRepository {

    @Override
    public List<String> findPreguntasPorExamenId(Long id) {
        return Datos.PREGUNTAS;
    }

    @Override
    public void guardarVarias(List<String> preguntas) {
        System.out.println("PreguntaRepositoryImpl.guardarVarias");
    }
    
}
