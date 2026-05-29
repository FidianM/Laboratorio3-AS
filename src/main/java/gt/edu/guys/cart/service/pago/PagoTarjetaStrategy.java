package gt.edu.guys.cart.service.pago;

import gt.edu.guys.cart.entity.Pago;
import gt.edu.guys.cart.repository.PagoRepository;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Estrategia concreta: Pago con Tarjeta de Credito/Debito
 * Simula validacion de tarjeta y autorizacion bancaria.
 */
@Component
public class PagoTarjetaStrategy implements PagoStrategy {

    private final PagoRepository pagoRepository;

    public PagoTarjetaStrategy(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    @Override
    public Pago procesarPago(Long ordenId, BigDecimal monto, String detalle) {
        Pago pago = new Pago(ordenId, monto, getMetodoPago());

        // Simulacion: se aprueba si el monto es menor a Q10,000
        boolean aprobado = monto.compareTo(new BigDecimal("10000")) < 0;

        pago.setReferenciaTransaccion("TAR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        pago.setEstado(aprobado ? Pago.EstadoPago.APROBADO : Pago.EstadoPago.RECHAZADO);
        pago.setDetalle(aprobado
                ? (detalle != null ? detalle : "Tarjeta autorizada correctamente")
                : "Tarjeta rechazada: monto supera limite permitido");

        return pagoRepository.save(pago);
    }

    @Override
    public String getMetodoPago() {
        return "TARJETA";
    }
}
