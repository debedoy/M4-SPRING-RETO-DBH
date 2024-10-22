package co.com.bancolombia.aplicacionbancaria.config;

public class CuentaException extends RuntimeException {
    public CuentaException(Long id) {
        super("No se pudo encontrar la cuenta : " + id);
    }
}