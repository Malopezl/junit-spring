package gt.com.archteam.mockito.course.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "bancos")
@NoArgsConstructor
public class Banco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(name = "total_transferencias")
    private Integer totalTransferencias;

    public Banco(Long id, String nombre, Integer totalTransferencias) {
        this.id = id;
        this.nombre = nombre;
        this.totalTransferencias = totalTransferencias;
    }
}
