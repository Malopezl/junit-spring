package gt.com.archteam.mockito.course.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import gt.com.archteam.mockito.course.models.Banco;

public interface BancoRepository extends JpaRepository<Banco, Long> {
    // List<Banco> findAll();
    // Banco findById(Long id);
    // void update(Banco banco);
}
