package gt.com.archteam.mockito.course.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gt.com.archteam.mockito.course.models.Cuenta;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    // List<Cuenta> findAll();
    // Cuenta findById(Long id);
    // void update(Cuenta cuenta);
    @Query("select c from Cuenta c where c.persona=?1")
    Optional<Cuenta> findByPersona(String person);
}
