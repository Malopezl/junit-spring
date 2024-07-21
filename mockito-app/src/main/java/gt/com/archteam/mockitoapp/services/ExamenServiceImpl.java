package gt.com.archteam.mockitoapp.services;

import java.util.Optional;

import gt.com.archteam.mockitoapp.models.Examen;
import gt.com.archteam.mockitoapp.repositories.ExamenRepository;
import gt.com.archteam.mockitoapp.repositories.PreguntaRepository;

public class ExamenServiceImpl implements ExamenService {
    private ExamenRepository examenRepository;
    private PreguntaRepository preguntaRepository;

    public ExamenServiceImpl(ExamenRepository examenRepository, PreguntaRepository preguntaRepository) {
        this.examenRepository = examenRepository;
        this.preguntaRepository = preguntaRepository;
    }

    @Override
    public Optional<Examen> findExamenPorNombre(String nombre) {
        return examenRepository.findAll().stream().filter(e -> e.getNombre().equalsIgnoreCase(nombre)).findFirst();
    }

    @Override
    public Examen findExamenPorNombreConPreguntas(String nombre) {
        Examen examen = findExamenPorNombre(nombre).orElseThrow();
        var preguntas = preguntaRepository.findPreguntasPorExamenId(examen.getId());
        examen.setPreguntas(preguntas);
        return examen;
    }

    @Override
    public Examen guardar(Examen examen) {
        if (!examen.getPreguntas().isEmpty()) {
            preguntaRepository.guardarVarias(examen.getPreguntas());
        }
        return examenRepository.guardar(examen);
    }

}
