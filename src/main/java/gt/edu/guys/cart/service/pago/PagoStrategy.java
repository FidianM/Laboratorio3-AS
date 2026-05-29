package gt.edu.guys.cart.service.pago;

import gt.edu.guys.cart.entity.Pago;
import java.math.BigDecimal;

/**
 * PATRON DE DISEÑO: STRATEGY
 *
 * Esta interfaz define el contrato para todos los metodos de pago.
 * Permite intercambiar algoritmos de pago (Efectivo, Tarjeta, PayPal)
 * sin modificar el codigo cliente (PagoService), cumpliendo el
 * principio Abierto/Cerrado de SOLID.
 *
 * Justificacion de eleccion:
 * - El proceso de pago varia segun el metodo (efectivo, tarjeta, PayPal).
 * - Cada metodo tiene su propia logica de procesamiento y validacion.
 * - Con Strategy se puede agregar un nuevo metodo de pago (ej: Bitcoin)
 *   simplemente creando una nueva clase, sin tocar las existentes.
 * - Es el patron mas usado en sistemas de e-commerce para pagos.
 */
public interface PagoStrategy {

    /**
     * Procesa el pago y retorna el objeto Pago con el resultado.
     */
    Pago procesarPago(Long ordenId, BigDecimal monto, String detalle);

    /**
     * Retorna el nombre del metodo de pago para identificacion.
     */
    String getMetodoPago();
}
