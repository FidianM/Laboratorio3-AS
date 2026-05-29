package gt.edu.guys.cart.dto;

public class AgregarItemRequest {
    private Long productoId;
    private Integer cantidad;

    public AgregarItemRequest() {}

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
