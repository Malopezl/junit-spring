package gt.com.archteam.mockito.course.models;

import java.math.BigDecimal;

import gt.com.archteam.mockito.course.exceptions.DineroInsuficienteException;
import lombok.Data;

@Data
public class Cuenta {
    private Long id;
    private String persona;
    private BigDecimal saldo;

    public Cuenta(Long id, String persona, BigDecimal saldo) {
        this.id = id;
        this.persona = persona;
        this.saldo = saldo;
    }

    public void debito(BigDecimal monto) {
        var nuevoSaldo = this.saldo.subtract(monto);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new DineroInsuficienteException("Dinero insuficiente en la cuenta.");
        }
        this.saldo = nuevoSaldo;
    }

    public void credito(BigDecimal monto) {
        this.saldo = this.saldo.add(monto);
    }
}
