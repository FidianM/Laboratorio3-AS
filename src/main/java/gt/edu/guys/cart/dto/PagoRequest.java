package gt.edu.guys.cart.dto;

public class PagoRequest {
    // Metodos soportados: EFECTIVO, TARJETA, PAYPAL
    private String metodoPago;
    private String detalle;

    public PagoRequest() {}

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
}
