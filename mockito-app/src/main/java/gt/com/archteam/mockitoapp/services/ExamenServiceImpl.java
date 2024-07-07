package gt.com.archteam.mockitoapp.services;

import gt.com.archteam.mockitoapp.models.Examen;
import gt.com.archteam.mockitoapp.repositories.ExamenRepository;

public class ExamenServiceImpl implements ExamenService {
    private ExamenRepository examenRepository;

    public ExamenServiceImpl(ExamenRepository examenRepository) {
        this.examenRepository = examenRepository;
    }

    @Override
    public Examen findExamenPorNombre(String nombre) {
        return examenRepository.findAll().stream().filter(e -> e.getNombre().equalsIgnoreCase(nombre)).findFirst()
                .orElseThrow();
    }

}
