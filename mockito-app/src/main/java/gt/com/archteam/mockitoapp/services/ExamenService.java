package gt.com.archteam.mockitoapp.services;

import gt.com.archteam.mockitoapp.models.Examen;

public interface ExamenService {
    Examen findExamenPorNombre(String nombre);
}
