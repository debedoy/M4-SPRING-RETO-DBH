package co.com.bancolombia.aplicacionbancaria.repository;
import co.com.bancolombia.aplicacionbancaria.model.Cuenta;
import co.com.bancolombia.aplicacionbancaria.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransaccionRepository extends JpaRepository <Transaccion, Long>{
    List<Transaccion> findTop5ByCuentaOrderByFechaHoraDesc(Cuenta cuenta);
}
