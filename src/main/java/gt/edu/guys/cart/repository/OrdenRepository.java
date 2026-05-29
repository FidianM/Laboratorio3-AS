package gt.edu.guys.cart.repository;

import gt.edu.guys.cart.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findByCliente(String cliente);
    List<Orden> findByCarritoId(Long carritoId);
}
