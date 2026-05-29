package gt.edu.guys.cart.service;

import gt.edu.guys.cart.dto.AgregarItemRequest;
import gt.edu.guys.cart.dto.CarritoDTO;
import gt.edu.guys.cart.dto.ItemCarritoDTO;
import gt.edu.guys.cart.entity.Carrito;
import gt.edu.guys.cart.entity.ItemCarrito;
import gt.edu.guys.cart.entity.Producto;
import gt.edu.guys.cart.repository.CarritoRepository;
import gt.edu.guys.cart.repository.ItemCarritoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final ProductoService productoService;

    public CarritoService(CarritoRepository carritoRepository,
                          ItemCarritoRepository itemCarritoRepository,
                          ProductoService productoService) {
        this.carritoRepository = carritoRepository;
        this.itemCarritoRepository = itemCarritoRepository;
        this.productoService = productoService;
    }

    public CarritoDTO crearCarrito(String cliente) {
        Carrito carrito = new Carrito(cliente);
        return toDTO(carritoRepository.save(carrito));
    }

    public CarritoDTO obtenerCarrito(Long id) {
        Carrito carrito = carritoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado: " + id));
        return toDTO(carrito);
    }

    public List<CarritoDTO> listarTodos() {
        return carritoRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public CarritoDTO agregarItem(Long carritoId, AgregarItemRequest request) {
        Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado: " + carritoId));

        if (carrito.getEstado() != Carrito.EstadoCarrito.ACTIVO) {
            throw new RuntimeException("El carrito no está activo");
        }

        Producto producto = productoService.findEntityById(request.getProductoId());

        if (producto.getStock() < request.getCantidad()) {
            throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
        }

        // Verificar si ya existe el producto en el carrito
        ItemCarrito itemExistente = carrito.getItems().stream()
                .filter(i -> i.getProducto().getId().equals(request.getProductoId()))
                .findFirst().orElse(null);

        if (itemExistente != null) {
            itemExistente.setCantidad(itemExistente.getCantidad() + request.getCantidad());
            itemCarritoRepository.save(itemExistente);
        } else {
            ItemCarrito item = new ItemCarrito(carrito, producto, request.getCantidad());
            carrito.getItems().add(item);
            itemCarritoRepository.save(item);
        }

        // Descontar stock
        producto.setStock(producto.getStock() - request.getCantidad());
        productoService.findEntityById(request.getProductoId()); // reload check
        return toDTO(carritoRepository.findById(carritoId).get());
    }

    @Transactional
    public CarritoDTO eliminarItem(Long carritoId, Long itemId) {
        Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado: " + carritoId));

        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado: " + itemId));

        // Restaurar stock
        Producto producto = item.getProducto();
        producto.setStock(producto.getStock() + item.getCantidad());

        carrito.getItems().remove(item);
        itemCarritoRepository.delete(item);
        return toDTO(carritoRepository.save(carrito));
    }

    @Transactional
    public CarritoDTO completarCarrito(Long carritoId) {
        Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado: " + carritoId));
        carrito.setEstado(Carrito.EstadoCarrito.COMPLETADO);
        return toDTO(carritoRepository.save(carrito));
    }

    @Transactional
    public CarritoDTO cancelarCarrito(Long carritoId) {
        Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado: " + carritoId));

        // Restaurar stock de todos los items
        carrito.getItems().forEach(item -> {
            Producto p = item.getProducto();
            p.setStock(p.getStock() + item.getCantidad());
        });

        carrito.setEstado(Carrito.EstadoCarrito.CANCELADO);
        return toDTO(carritoRepository.save(carrito));
    }

    private CarritoDTO toDTO(Carrito carrito) {
        CarritoDTO dto = new CarritoDTO();
        dto.setId(carrito.getId());
        dto.setCliente(carrito.getCliente());
        dto.setFechaCreacion(carrito.getFechaCreacion());
        dto.setEstado(carrito.getEstado() != null ? carrito.getEstado().name() : null);

        List<ItemCarritoDTO> items = carrito.getItems().stream().map(item -> {
            ItemCarritoDTO iDTO = new ItemCarritoDTO();
            iDTO.setId(item.getId());
            iDTO.setProductoId(item.getProducto().getId());
            iDTO.setProductoNombre(item.getProducto().getNombre());
            iDTO.setCantidad(item.getCantidad());
            iDTO.setPrecioUnitario(item.getPrecioUnitario());
            iDTO.setSubtotal(item.getSubtotal());
            return iDTO;
        }).collect(Collectors.toList());

        dto.setItems(items);

        BigDecimal total = items.stream()
                .map(ItemCarritoDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotal(total);

        return dto;
    }
}
