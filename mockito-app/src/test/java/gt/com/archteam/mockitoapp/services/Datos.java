package gt.com.archteam.mockitoapp.services;

import java.util.Arrays;
import java.util.List;

import gt.com.archteam.mockitoapp.models.Examen;

public class Datos {
    public final static List<Examen> EXAMENES = Arrays.asList(new Examen(5L, "Matematicas"), new Examen(6L, "Lenguaje"),
            new Examen(7L, "Historia"));

    public final static List<String> PREGUNTAS = Arrays.asList("aritmetica", "integrales", "derivadas", "trigonometria",
            "geometria");

    public final static Examen EXAMEN = new Examen(8L, "Fisica");
}
