package StockManagment.StockManagment.Stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/stock")
@RestController
public class StockController {
    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<StockEntity>> getAll(@PathVariable int location) {
        List<StockEntity> stocks = stockService.findAll(location);
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<StockEntity> getStockByBarcode(@PathVariable String barcode) {
        StockEntity stock = stockService.findByBarcode(barcode);
        return ResponseEntity.ok(stock);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockEntity> getStockById(@PathVariable Integer id) {
        StockEntity stock = stockService.findById(id);
        return ResponseEntity.ok(stock);
    }

    @PostMapping
    public ResponseEntity<?> createStock(@RequestBody StockEntity stock) {
        try {
            StockEntity created = stockService.create(stock);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            // fallback
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Server error: " + ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockEntity> updateStock(@PathVariable Integer id, @RequestBody StockEntity updated) {
        StockEntity stock = stockService.update(id, updated);
        return ResponseEntity.ok(stock);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable Integer id) {
        stockService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
