package co.com.bancolombia.aplicacionbancaria.model.DTO;
public class CuentaDTO {

    private Long cuenta;

    public Long getCuenta() {
        return cuenta;
    }

    public void setCuenta(Long cuenta) {
        this.cuenta = cuenta;
    }

    @Override
    public String toString() {
        return "CuentaDTO{" +
                "cuenta=" + cuenta +
                '}';
    }
}