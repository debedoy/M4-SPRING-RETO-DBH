package co.com.bancolombia.aplicacionbancaria.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BASICA")
public class CuentaBasica extends Cuenta {

}