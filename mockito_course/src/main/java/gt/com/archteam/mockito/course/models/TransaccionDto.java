package gt.com.archteam.mockito.course.models;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TransaccionDto {
    private Long cuentaOrigenId;
    private Long cuentaDestinoId;
    private BigDecimal monto;
    private Long bancoId;
}
