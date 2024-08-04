package gt.com.archteam.mockito.course.repositories;

import java.util.List;

import gt.com.archteam.mockito.course.models.Banco;

public interface BancoRepository {
    List<Banco> findAll();

    Banco findById(Long id);

    void update(Banco banco);
}
