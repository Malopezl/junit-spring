package gt.com.archteam.mockito.course;

import java.math.BigDecimal;

import gt.com.archteam.mockito.course.models.Banco;
import gt.com.archteam.mockito.course.models.Cuenta;

public class Datos {
    public static final Cuenta CUENTA_001 = new Cuenta(1L, "Andres", new BigDecimal("1000"));
    public static final Cuenta CUENTA_002 = new Cuenta(2L, "Jhon", new BigDecimal("2000"));
    public static final Banco BANCO = new Banco(1L, "El banco financiero", 0);
}
