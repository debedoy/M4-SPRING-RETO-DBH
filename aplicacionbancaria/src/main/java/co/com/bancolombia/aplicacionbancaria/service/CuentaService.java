package co.com.bancolombia.aplicacionbancaria.service;

import co.com.bancolombia.aplicacionbancaria.config.CuentaException;
import co.com.bancolombia.aplicacionbancaria.model.CuentaBasica;
import co.com.bancolombia.aplicacionbancaria.model.Transaccion;
import co.com.bancolombia.aplicacionbancaria.repository.CuentaRepository;
import co.com.bancolombia.aplicacionbancaria.repository.TransaccionRepository;
import org.springframework.stereotype.Service;
import co.com.bancolombia.aplicacionbancaria.model.Cuenta;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final TransaccionRepository transaccionRepository;

    public CuentaService(CuentaRepository cuentaRepository, TransaccionRepository transaccionRepository) {
        this.cuentaRepository = cuentaRepository;
        this.transaccionRepository = transaccionRepository;
    }
    public BigDecimal obtenerSaldo(Long cuentaId) {
        Optional<Cuenta> cuentaOpt = cuentaRepository.findById(cuentaId);
        if (cuentaOpt.isEmpty()) {
            throw new CuentaException(cuentaId);
        }
        return cuentaOpt.get().getSaldo();
    }



    @Transactional
    public BigDecimal depositoSucursal(Long cuentaId, BigDecimal monto) {
        return realizarDeposito(cuentaId, monto, "DEPOSITO_SUCURSAL", BigDecimal.ZERO);
    }

    @Transactional
    public BigDecimal depositoCajero(Long cuentaId, BigDecimal monto) {
        Optional<Cuenta> cuentaOpt = cuentaRepository.findById(cuentaId);
        if (cuentaOpt.isEmpty()) {
            throw new CuentaException(cuentaId);
        }
        Cuenta cuenta = cuentaOpt.get();
        BigDecimal costo = BigDecimal.ZERO;
        if (cuenta instanceof CuentaBasica) {
            costo = BigDecimal.valueOf(2);
        }
        BigDecimal nuevoSaldo = cuenta.getSaldo().add(monto).subtract(costo);
        cuenta.setSaldo(nuevoSaldo);
        cuentaRepository.save(cuenta);
        registrarTransaccion(cuenta, "DEPOSITO_CAJERO", monto, costo);
        return nuevoSaldo;
    }

    @Transactional
    public BigDecimal depositoOtraCuenta(Long cuentaId, BigDecimal monto) {
        BigDecimal costo = BigDecimal.valueOf(1.5);
        return realizarDeposito(cuentaId, monto, "DEPOSITO_OTRA_CUENTA", costo);
    }

    @Transactional
    public BigDecimal compraFisica(Long cuentaId, BigDecimal monto) {
        Optional<Cuenta> cuentaOpt = cuentaRepository.findById(cuentaId);
        if (cuentaOpt.isEmpty()) {
            throw new CuentaException(cuentaId);
        }
        Cuenta cuenta = cuentaOpt.get();
        BigDecimal nuevoSaldo = cuenta.getSaldo().subtract(monto);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Saldo insuficiente");
        }
        cuenta.setSaldo(nuevoSaldo);
        cuentaRepository.save(cuenta);
        registrarTransaccion(cuenta, "COMPRA_FISICA", monto, BigDecimal.ZERO);
        return nuevoSaldo;
    }


    @Transactional
    public BigDecimal compraWeb(Long cuentaId, BigDecimal monto) {
        BigDecimal costo = BigDecimal.valueOf(5);
        return realizarCompra(cuentaId, monto, "COMPRA_WEB", costo);
    }

    @Transactional
    public BigDecimal retiroCajero(Long cuentaId, BigDecimal monto) {
        BigDecimal costo = BigDecimal.valueOf(1);
        return realizarRetiro(cuentaId, monto, "RETIRO_CAJERO", costo);
    }

    private BigDecimal realizarDeposito(Long cuentaId, BigDecimal monto, String tipo, BigDecimal costo) {
        Optional<Cuenta> cuentaOpt = cuentaRepository.findById(cuentaId);
        if (cuentaOpt.isEmpty()) {
            throw new CuentaException(cuentaId);
        }
        Cuenta cuenta = cuentaOpt.get();
        BigDecimal nuevoSaldo = cuenta.getSaldo().add(monto).subtract(costo);
        cuenta.setSaldo(nuevoSaldo);
        cuentaRepository.save(cuenta);
        registrarTransaccion(cuenta, tipo, monto, costo);
        return nuevoSaldo;
    }

    private BigDecimal realizarCompra(Long cuentaId, BigDecimal monto, String tipo, BigDecimal costo) {
        Optional<Cuenta> cuentaOpt = cuentaRepository.findById(cuentaId);
        if (cuentaOpt.isEmpty()) {
            throw new NoSuchElementException("Cuenta no encontrada");
        }
        Cuenta cuenta = cuentaOpt.get();
        BigDecimal nuevoSaldo = cuenta.getSaldo().subtract(monto).subtract(costo);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Saldo insuficiente");
        }
        cuenta.setSaldo(nuevoSaldo);
        cuentaRepository.save(cuenta);
        registrarTransaccion(cuenta, tipo, monto, costo);
        return nuevoSaldo;
    }

    private BigDecimal realizarRetiro(Long cuentaId, BigDecimal monto, String tipo, BigDecimal costo) {
        Optional<Cuenta> cuentaOpt = cuentaRepository.findById(cuentaId);
        if (cuentaOpt.isEmpty()) {
            throw new NoSuchElementException("Cuenta no encontrada");
        }
        Cuenta cuenta = cuentaOpt.get();
        BigDecimal nuevoSaldo = cuenta.getSaldo().subtract(monto).subtract(costo);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Saldo insuficiente");
        }
        cuenta.setSaldo(nuevoSaldo);
        cuentaRepository.save(cuenta);
        registrarTransaccion(cuenta, tipo, monto, costo);
        return nuevoSaldo;
    }

    private void registrarTransaccion(Cuenta cuenta, String tipo, BigDecimal monto, BigDecimal costo) {
        Transaccion transaccion = new Transaccion();
        transaccion.setCuenta(cuenta);
        transaccion.setTipo(tipo);
        transaccion.setMonto(monto);
        transaccion.setFechaHora(LocalDateTime.now());
        transaccion.setCodigoUnico(generateUniqueCode());
        transaccionRepository.save(transaccion);
    }

    private String generateUniqueCode() {
        return "TX-" + System.currentTimeMillis();
    }

    public List<Transaccion> obtenerUltimasTransacciones(Long cuentaId) {
        Optional<Cuenta> cuentaOpt = cuentaRepository.findById(cuentaId);
        if (cuentaOpt.isEmpty()) {
            throw new CuentaException(cuentaId);
        }
        return transaccionRepository.findTop5ByCuentaOrderByFechaHoraDesc(cuentaOpt.get());
    }
}
