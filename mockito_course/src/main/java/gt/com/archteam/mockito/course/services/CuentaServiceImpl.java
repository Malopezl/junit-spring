package gt.com.archteam.mockito.course.services;

import java.math.BigDecimal;

import gt.com.archteam.mockito.course.models.Cuenta;
import gt.com.archteam.mockito.course.repositories.BancoRepository;
import gt.com.archteam.mockito.course.repositories.CuentaRepository;

public class CuentaServiceImpl implements CuentaService {
    private CuentaRepository cuentaRepository;
    private BancoRepository bancoRepository;

    public CuentaServiceImpl(CuentaRepository cuentaRepository, BancoRepository bancoRepository) {
        this.cuentaRepository = cuentaRepository;
        this.bancoRepository = bancoRepository;
    }

    @Override
    public Cuenta findById(Long id) {
        return cuentaRepository.findById(id);
    }

    @Override
    public int revisarTotalTransferencias(Long bancoId) {
        var banco = bancoRepository.findById(bancoId);
        return banco.getTotalTransferencias();
    }

    @Override
    public BigDecimal revisarSaldo(Long cuentaId) {
        var cuenta = cuentaRepository.findById(cuentaId);
        return cuenta.getSaldo();
    }

    @Override
    public void transferir(Long numCuentaOrigen, Long numCuentaDestino, BigDecimal monto, Long bancoId) {
        var banco = bancoRepository.findById(bancoId);
        int totalTransferencias = banco.getTotalTransferencias();
        banco.setTotalTransferencias(++totalTransferencias);
        bancoRepository.update(banco);

        Cuenta cuentaOrigen = cuentaRepository.findById(numCuentaOrigen);
        cuentaOrigen.debito(monto);
        cuentaRepository.update(cuentaOrigen);

        Cuenta cuentaDestino = cuentaRepository.findById(numCuentaDestino);
        cuentaDestino.credito(monto);
        cuentaRepository.update(cuentaDestino);
    }

}
