package gt.edu.guys.cart.controller;

import gt.edu.guys.cart.dto.OrdenDTO;
import gt.edu.guys.cart.dto.PagoDTO;
import gt.edu.guys.cart.dto.PagoRequest;
import gt.edu.guys.cart.service.OrdenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
@CrossOrigin(origins = "*")
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    // GET /api/ordenes
    @GetMapping
    public ResponseEntity<List<OrdenDTO>> listarTodas() {
        return ResponseEntity.ok(ordenService.listarTodas());
    }

    // GET /api/ordenes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<OrdenDTO> obtenerOrden(@PathVariable Long id) {
        return ResponseEntity.ok(ordenService.obtenerOrden(id));
    }

    // POST /api/ordenes/carrito/{carritoId}
    // Crea una orden a partir de un carrito activo
    @PostMapping("/carrito/{carritoId}")
    public ResponseEntity<OrdenDTO> crearOrdenDesdeCarrito(@PathVariable Long carritoId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ordenService.crearOrdenDesdeCarrito(carritoId));
    }

    // POST /api/ordenes/{id}/pagar
    // Procesa el pago usando el patron Strategy
    // Body: { "metodoPago": "EFECTIVO" | "TARJETA" | "PAYPAL", "detalle": "..." }
    @PostMapping("/{id}/pagar")
    public ResponseEntity<PagoDTO> procesarPago(@PathVariable Long id,
                                                 @RequestBody PagoRequest request) {
        return ResponseEntity.ok(ordenService.procesarPago(id, request));
    }

    // GET /api/ordenes/{id}/pagos
    @GetMapping("/{id}/pagos")
    public ResponseEntity<List<PagoDTO>> obtenerPagos(@PathVariable Long id) {
        return ResponseEntity.ok(ordenService.obtenerPagosPorOrden(id));
    }
}
