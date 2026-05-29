# Cart - Aplicación Monolítica de Carrito de Compras
**gt.edu.guys.cart** | Spring Boot 4.0.6 | Java 21 | MariaDB

---

## Configuración Inicial

### 1. Crear la base de datos en MariaDB
```sql
CREATE DATABASE cart_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Configurar credenciales en `application.properties`
```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/cart_db
spring.datasource.username=root
spring.datasource.password=root
```

### 3. Compilar y ejecutar
```bash
./gradlew bootRun
```
La aplicación levanta en: **http://localhost:8080**

---

## Arquitectura (Diagrama de Componentes)

```
REST Controller
      │
      ▼
Service Layer  ◄──►  DTOs
      │
      ▼
Repository Layer
      │
      ▼
   Database
      │
  ┌───┴────────────────────┐
  │                        │
Entity: Carrito    Entity: Producto
  │
Entity: ItemCarrito
```

---

## API Endpoints

### 📦 Productos

| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/productos` | Listar todos los productos |
| GET | `/api/productos/{id}` | Obtener producto por ID |
| POST | `/api/productos` | Crear nuevo producto |
| PUT | `/api/productos/{id}` | Actualizar producto |
| DELETE | `/api/productos/{id}` | Eliminar producto |

**Ejemplo crear producto:**
```json
POST /api/productos
{
  "nombre": "Laptop HP",
  "descripcion": "Intel i5, 8GB RAM",
  "precio": 4500.00,
  "stock": 10
}
```

---

### 🛒 Carritos

| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/carritos` | Listar todos los carritos |
| GET | `/api/carritos/{id}` | Ver carrito con items y total |
| POST | `/api/carritos?cliente=Juan` | Crear carrito para cliente |
| POST | `/api/carritos/{id}/items` | Agregar producto al carrito |
| DELETE | `/api/carritos/{cid}/items/{iid}` | Eliminar item del carrito |
| PUT | `/api/carritos/{id}/completar` | Marcar carrito como completado |
| PUT | `/api/carritos/{id}/cancelar` | Cancelar carrito (restaura stock) |

**Ejemplo agregar item:**
```json
POST /api/carritos/1/items
{
  "productoId": 2,
  "cantidad": 3
}
```

**Respuesta del carrito (con total calculado):**
```json
{
  "id": 1,
  "cliente": "Juan",
  "estado": "ACTIVO",
  "fechaCreacion": "2025-05-07T08:20:00",
  "items": [
    {
      "id": 1,
      "productoId": 2,
      "productoNombre": "Mouse Inalámbrico",
      "cantidad": 3,
      "precioUnitario": 150.00,
      "subtotal": 450.00
    }
  ],
  "total": 450.00
}
```

---

## Prueba con curl

```bash
# 1. Crear un producto
curl -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Mouse","descripcion":"Mouse USB","precio":150.00,"stock":50}'

# 2. Crear un carrito
curl -X POST "http://localhost:8080/api/carritos?cliente=Juan"

# 3. Agregar item al carrito (carritoId=1, productoId=1)
curl -X POST http://localhost:8080/api/carritos/1/items \
  -H "Content-Type: application/json" \
  -d '{"productoId":1,"cantidad":2}'

# 4. Ver carrito con total
curl http://localhost:8080/api/carritos/1

# 5. Completar carrito
curl -X PUT http://localhost:8080/api/carritos/1/completar
```

---

## Estados del Carrito
- **ACTIVO** → Se pueden agregar/quitar items
- **COMPLETADO** → Venta realizada (no modificable)
- **CANCELADO** → Cancelado (stock restaurado automáticamente)
