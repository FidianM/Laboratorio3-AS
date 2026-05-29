package gt.edu.guys.cart.service.pago;

import gt.edu.guys.cart.entity.Pago;
import gt.edu.guys.cart.repository.PagoRepository;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Estrategia concreta: Pago con PayPal
 * Simula autenticacion OAuth y transferencia via PayPal.
 */
@Component
public class PagoPaypalStrategy implements PagoStrategy {

    private final PagoRepository pagoRepository;

    public PagoPaypalStrategy(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    @Override
    public Pago procesarPago(Long ordenId, BigDecimal monto, String detalle) {
        Pago pago = new Pago(ordenId, monto, getMetodoPago());
        pago.setReferenciaTransaccion("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        pago.setEstado(Pago.EstadoPago.APROBADO);
        pago.setDetalle(detalle != null ? detalle : "Transferencia PayPal completada exitosamente");
        return pagoRepository.save(pago);
    }

    @Override
    public String getMetodoPago() {
        return "PAYPAL";
    }
}
