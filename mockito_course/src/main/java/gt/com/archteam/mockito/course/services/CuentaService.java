package gt.com.archteam.mockito.course.services;

import java.math.BigDecimal;

import gt.com.archteam.mockito.course.models.Cuenta;

public interface CuentaService {
    Cuenta findById(Long id);

    int revisarTotalTransferencias(Long bancoId);

    BigDecimal revisarSaldo(Long cuentaId);

    void transferir(Long numCuentaOrigen, Long numCuentaDestino, BigDecimal monto, Long bancoId);
}
