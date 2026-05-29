package gt.edu.guys.cart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrdenDTO {
    private Long id;
    private String cliente;
    private Long carritoId;
    private LocalDateTime fechaOrden;
    private String estado;
    private BigDecimal total;
    private List<ItemOrdenDTO> items;

    public OrdenDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public Long getCarritoId() { return carritoId; }
    public void setCarritoId(Long carritoId) { this.carritoId = carritoId; }
    public LocalDateTime getFechaOrden() { return fechaOrden; }
    public void setFechaOrden(LocalDateTime fechaOrden) { this.fechaOrden = fechaOrden; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public List<ItemOrdenDTO> getItems() { return items; }
    public void setItems(List<ItemOrdenDTO> items) { this.items = items; }
}
