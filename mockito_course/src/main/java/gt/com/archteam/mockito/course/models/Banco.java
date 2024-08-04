package gt.com.archteam.mockito.course.models;

import lombok.Data;

@Data
public class Banco {
    private Long id;
    private String nombre;
    private Integer totalTransferencias;

    public Banco(Long id, String nombre, Integer totalTransferencias) {
        this.id = id;
        this.nombre = nombre;
        this.totalTransferencias = totalTransferencias;
    }
}
