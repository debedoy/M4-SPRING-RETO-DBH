package co.com.bancolombia.aplicacionbancaria.model.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Validated
public class TransaccionDTO {
    @NotNull(message = "El número de cuenta es requerido.")
    private long cuenta;

    @NotNull(message = "El monto es requerido.")
    @Positive(message = "El monto debe ser un valor positivo y mayor que cero.")
    private BigDecimal monto;

    @NotBlank(message = "La descripción no puede estar vacía.")
    private String descripcion;
    public TransaccionDTO(long cuenta, BigDecimal monto, String descripcion) {
        this.cuenta = cuenta;
        this.monto = monto;
        this.descripcion = descripcion;
    }

    public long getCuenta() {
        return cuenta;
    }

    public void setCuenta(long cuenta) {
        this.cuenta = cuenta;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "TransaccionDTO{" +
                "cuenta='" + cuenta + '\'' +
                ", monto=" + monto +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}