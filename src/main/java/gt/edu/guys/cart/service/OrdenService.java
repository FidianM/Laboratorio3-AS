package gt.edu.guys.cart.service;

import gt.edu.guys.cart.dto.ItemOrdenDTO;
import gt.edu.guys.cart.dto.OrdenDTO;
import gt.edu.guys.cart.dto.PagoDTO;
import gt.edu.guys.cart.dto.PagoRequest;
import gt.edu.guys.cart.entity.*;
import gt.edu.guys.cart.repository.OrdenRepository;
import gt.edu.guys.cart.repository.PagoRepository;
import gt.edu.guys.cart.service.pago.PagoContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final PagoRepository pagoRepository;
    private final CarritoService carritoService;
    private final PagoContext pagoContext;

    public OrdenService(OrdenRepository ordenRepository,
                        PagoRepository pagoRepository,
                        CarritoService carritoService,
                        PagoContext pagoContext) {
        this.ordenRepository = ordenRepository;
        this.pagoRepository = pagoRepository;
        this.carritoService = carritoService;
        this.pagoContext = pagoContext;
    }

    /**
     * Crea una Orden a partir de un carrito ACTIVO con items.
     */
    @Transactional
    public OrdenDTO crearOrdenDesdeCarrito(Long carritoId) {
        var carritoDTO = carritoService.obtenerCarrito(carritoId);

        if (!"ACTIVO".equals(carritoDTO.getEstado())) {
            throw new RuntimeException("Solo se puede crear orden desde un carrito ACTIVO");
        }
        if (carritoDTO.getItems() == null || carritoDTO.getItems().isEmpty()) {
            throw new RuntimeException("El carrito no tiene items");
        }

        Orden orden = new Orden(carritoDTO.getCliente(), carritoId, carritoDTO.getTotal());

        // Agregar items a la orden
        for (var itemDTO : carritoDTO.getItems()) {
            ItemOrden item = new ItemOrden(
                    orden,
                    itemDTO.getProductoId(),
                    itemDTO.getProductoNombre(),
                    itemDTO.getCantidad(),
                    itemDTO.getPrecioUnitario()
            );
            orden.getItems().add(item);
        }

        Orden savedOrden = ordenRepository.save(orden);

        // Marcar carrito como COMPLETADO
        carritoService.completarCarrito(carritoId);

        return toDTO(savedOrden);
    }

    public OrdenDTO obtenerOrden(Long id) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + id));
        return toDTO(orden);
    }

    public List<OrdenDTO> listarTodas() {
        return ordenRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Procesa el pago de una orden usando el patron STRATEGY.
     * El metodo de pago se selecciona dinamicamente en PagoContext.
     */
    @Transactional
    public PagoDTO procesarPago(Long ordenId, PagoRequest request) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + ordenId));

        if (orden.getEstado() != Orden.EstadoOrden.PENDIENTE) {
            throw new RuntimeException("La orden no esta en estado PENDIENTE");
        }

        // PATRON STRATEGY: el contexto elige la estrategia correcta
        Pago pago = pagoContext.ejecutar(
                request.getMetodoPago(),
                ordenId,
                orden.getTotal(),
                request.getDetalle()
        );

        // Actualizar estado de la orden segun resultado del pago
        if (pago.getEstado() == Pago.EstadoPago.APROBADO) {
            orden.setEstado(Orden.EstadoOrden.PAGADA);
        } else {
            orden.setEstado(Orden.EstadoOrden.CANCELADA);
        }
        ordenRepository.save(orden);

        return toPagoDTO(pago);
    }

    public List<PagoDTO> obtenerPagosPorOrden(Long ordenId) {
        return pagoRepository.findByOrdenId(ordenId)
                .stream().map(this::toPagoDTO).collect(Collectors.toList());
    }

    // ---- Mappers ----

    private OrdenDTO toDTO(Orden orden) {
        OrdenDTO dto = new OrdenDTO();
        dto.setId(orden.getId());
        dto.setCliente(orden.getCliente());
        dto.setCarritoId(orden.getCarritoId());
        dto.setFechaOrden(orden.getFechaOrden());
        dto.setEstado(orden.getEstado() != null ? orden.getEstado().name() : null);
        dto.setTotal(orden.getTotal());
        dto.setItems(orden.getItems().stream().map(this::toItemDTO).collect(Collectors.toList()));
        return dto;
    }

    private ItemOrdenDTO toItemDTO(ItemOrden item) {
        ItemOrdenDTO dto = new ItemOrdenDTO();
        dto.setId(item.getId());
        dto.setProductoId(item.getProductoId());
        dto.setProductoNombre(item.getProductoNombre());
        dto.setCantidad(item.getCantidad());
        dto.setPrecioUnitario(item.getPrecioUnitario());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }

    private PagoDTO toPagoDTO(Pago pago) {
        PagoDTO dto = new PagoDTO();
        dto.setId(pago.getId());
        dto.setOrdenId(pago.getOrdenId());
        dto.setMonto(pago.getMonto());
        dto.setMetodoPago(pago.getMetodoPago());
        dto.setReferenciaTransaccion(pago.getReferenciaTransaccion());
        dto.setEstado(pago.getEstado() != null ? pago.getEstado().name() : null);
        dto.setFechaPago(pago.getFechaPago());
        dto.setDetalle(pago.getDetalle());
        return dto;
    }
}
