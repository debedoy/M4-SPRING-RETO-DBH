package co.com.bancolombia.aplicacionbancaria.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
@Entity
@DiscriminatorValue("PREMIUM")
public class CuentaPremium extends Cuenta {

}