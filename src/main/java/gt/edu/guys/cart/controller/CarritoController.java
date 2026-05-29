package gt.edu.guys.cart.controller;

import gt.edu.guys.cart.dto.AgregarItemRequest;
import gt.edu.guys.cart.dto.CarritoDTO;
import gt.edu.guys.cart.service.CarritoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carritos")
@CrossOrigin(origins = "*")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    // GET /api/carritos
    @GetMapping
    public ResponseEntity<List<CarritoDTO>> listarTodos() {
        return ResponseEntity.ok(carritoService.listarTodos());
    }

    // GET /api/carritos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CarritoDTO> obtenerCarrito(@PathVariable Long id) {
        return ResponseEntity.ok(carritoService.obtenerCarrito(id));
    }

    // POST /api/carritos?cliente=Juan
    @PostMapping
    public ResponseEntity<CarritoDTO> crearCarrito(@RequestParam String cliente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carritoService.crearCarrito(cliente));
    }

    // POST /api/carritos/{id}/items
    @PostMapping("/{id}/items")
    public ResponseEntity<CarritoDTO> agregarItem(@PathVariable Long id,
                                                   @RequestBody AgregarItemRequest request) {
        return ResponseEntity.ok(carritoService.agregarItem(id, request));
    }

    // DELETE /api/carritos/{carritoId}/items/{itemId}
    @DeleteMapping("/{carritoId}/items/{itemId}")
    public ResponseEntity<CarritoDTO> eliminarItem(@PathVariable Long carritoId,
                                                    @PathVariable Long itemId) {
        return ResponseEntity.ok(carritoService.eliminarItem(carritoId, itemId));
    }

    // PUT /api/carritos/{id}/completar
    @PutMapping("/{id}/completar")
    public ResponseEntity<CarritoDTO> completar(@PathVariable Long id) {
        return ResponseEntity.ok(carritoService.completarCarrito(id));
    }

    // PUT /api/carritos/{id}/cancelar
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<CarritoDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(carritoService.cancelarCarrito(id));
    }
}
