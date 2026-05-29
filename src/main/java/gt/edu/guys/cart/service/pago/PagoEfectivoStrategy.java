package gt.edu.guys.cart.service.pago;

import gt.edu.guys.cart.entity.Pago;
import gt.edu.guys.cart.repository.PagoRepository;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Estrategia concreta: Pago en Efectivo
 * Simula procesamiento inmediato sin validaciones externas.
 */
@Component
public class PagoEfectivoStrategy implements PagoStrategy {

    private final PagoRepository pagoRepository;

    public PagoEfectivoStrategy(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    @Override
    public Pago procesarPago(Long ordenId, BigDecimal monto, String detalle) {
        Pago pago = new Pago(ordenId, monto, getMetodoPago());
        pago.setReferenciaTransaccion("EFE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        pago.setEstado(Pago.EstadoPago.APROBADO);
        pago.setDetalle(detalle != null ? detalle : "Pago en efectivo procesado correctamente");
        return pagoRepository.save(pago);
    }

    @Override
    public String getMetodoPago() {
        return "EFECTIVO";
    }
}
