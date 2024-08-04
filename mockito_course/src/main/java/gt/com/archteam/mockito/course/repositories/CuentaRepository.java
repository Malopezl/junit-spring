package gt.com.archteam.mockito.course.repositories;

import java.util.List;

import gt.com.archteam.mockito.course.models.Cuenta;

public interface CuentaRepository {
    List<Cuenta> findAll();

    Cuenta findById(Long id);

    void update(Cuenta cuenta);
}
