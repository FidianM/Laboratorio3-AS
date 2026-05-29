package gt.edu.guys.cart.service.pago;

import gt.edu.guys.cart.entity.Pago;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CONTEXTO del patron Strategy.
 *
 * Recibe todas las estrategias disponibles via inyeccion de dependencias
 * y selecciona la correcta segun el metodo de pago solicitado.
 * El cliente (PagoService) solo interactua con este contexto,
 * desconociendo los detalles de cada estrategia.
 */
@Component
public class PagoContext {

    private final Map<String, PagoStrategy> estrategias;

    public PagoContext(List<PagoStrategy> listaEstrategias) {
        this.estrategias = listaEstrategias.stream()
                .collect(Collectors.toMap(PagoStrategy::getMetodoPago, s -> s));
    }

    public Pago ejecutar(String metodoPago, Long ordenId, BigDecimal monto, String detalle) {
        PagoStrategy strategy = estrategias.get(metodoPago.toUpperCase());
        if (strategy == null) {
            throw new RuntimeException("Metodo de pago no soportado: " + metodoPago
                    + ". Metodos disponibles: " + estrategias.keySet());
        }
        return strategy.procesarPago(ordenId, monto, detalle);
    }
}
