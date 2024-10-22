package co.com.bancolombia.aplicacionbancaria.controller;
import co.com.bancolombia.aplicacionbancaria.model.Transaccion;
import co.com.bancolombia.aplicacionbancaria.service.CuentaService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/cuenta")
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    // Obtener el saldo de la cuenta
    @GetMapping("/saldo/{cuentaId}")
    public BigDecimal obtenerSaldo(@PathVariable Long cuentaId) {
        return cuentaService.obtenerSaldo(cuentaId);
    }

    // Obtener las últimas transacciones de la cuenta
    @GetMapping("/transacciones/{cuentaId}")
    public List<Transaccion> obtenerUltimasTransacciones(@PathVariable Long cuentaId) {
        return cuentaService.obtenerUltimasTransacciones(cuentaId);
    }

    // Agrupación de métodos para depósitos
    @PostMapping("/deposito/{tipo}/{cuentaId}")
    public BigDecimal realizarDeposito(@PathVariable Long cuentaId,
                                       @PathVariable String tipo,
                                       @RequestParam BigDecimal monto) {
        validarMonto(monto);  // Validación de monto
        switch (tipo) {
            case "sucursal":
                return cuentaService.depositoSucursal(cuentaId, monto);
            case "cajero":
                return cuentaService.depositoCajero(cuentaId, monto);
            case "otra":
                return cuentaService.depositoOtraCuenta(cuentaId, monto);
            default:
                throw new IllegalArgumentException("Tipo de depósito inválido: " + tipo);
        }
    }

    // Agrupación de métodos para compras
    @PostMapping("/compra/{tipo}/{cuentaId}")
    public BigDecimal realizarCompra(@PathVariable Long cuentaId,
                                     @PathVariable String tipo,
                                     @RequestParam BigDecimal monto) {
        validarMonto(monto);  // Validación de monto
        switch (tipo) {
            case "fisica":
                return cuentaService.compraFisica(cuentaId, monto);
            case "web":
                return cuentaService.compraWeb(cuentaId, monto);
            default:
                throw new IllegalArgumentException("Tipo de compra inválido: " + tipo);
        }
    }

    // Retiro de cajero
    @PostMapping("/retiro/cajero/{cuentaId}")
    public BigDecimal realizarRetiroCajero(@PathVariable Long cuentaId, @RequestParam BigDecimal monto) {
        validarMonto(monto);  // Validación de monto
        return cuentaService.retiroCajero(cuentaId, monto);
    }

    // Método auxiliar para validar el monto
    private void validarMonto(BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }
    }
}