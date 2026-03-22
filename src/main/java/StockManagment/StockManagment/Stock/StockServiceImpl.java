package StockManagment.StockManagment.Stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Autowired
    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    // READ ALL
    public List<StockEntity> findAll(int location) {
        return stockRepository.findAllByLocation(location);
    }

    // READ BY ID
    public StockEntity findById(Integer id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
    }

    public StockEntity findByBarcode(String barcode) {
        return stockRepository.findByBarcodeAndLocation(barcode,1)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
    }

    // CREATE
    public StockEntity create(StockEntity stockEntity) {
        if (stockEntity.getLocation() == 1) {
            return createOrUpdateMagazine(stockEntity);
        } else if (stockEntity.getLocation() == 2) {
            return createOrUpdateFridge(stockEntity);
        }
        throw new IllegalArgumentException("Invalid location: " + stockEntity.getLocation());
    }

    // MAGAZINE: add or update
    private StockEntity createOrUpdateMagazine(StockEntity stockEntity) {
        Optional<StockEntity> existing = stockRepository
                .findByBarcodeAndLocation(stockEntity.getBarcode(), 1);

        if (existing.isPresent()) {
            return update(existing.get().getId(), stockEntity);
        }
        return stockRepository.save(stockEntity);
    }

    // FRIDGE: must deduct from magazine first
    private StockEntity createOrUpdateFridge(StockEntity stockEntity) {
        StockEntity magazineStock = stockRepository
                .findByBarcodeAndLocation(stockEntity.getBarcode(), 1)
                .orElseThrow(() -> new IllegalStateException(
                        "Nuk mund të shtohet në frigorifer: produkti nuk u gjet në magazinë."
                ));

        if (magazineStock.getQuantity() < stockEntity.getQuantity()) {
            throw new IllegalStateException(
                    "Nuk mund të shtohet në frigorifer: magazina ka vetëm "
                            + magazineStock.getQuantity() + " njësi në dispozicion."
            );
        }

        // Deduct from magazine
        removeQuantityFromMagazine(magazineStock.getId(), stockEntity.getQuantity());

        // Add to fridge (create or update)
        Optional<StockEntity> fridgeStock = stockRepository
                .findByBarcodeAndLocation(stockEntity.getBarcode(), 2);

        if (fridgeStock.isPresent()) {
            return update(fridgeStock.get().getId(), stockEntity);
        }
        return stockRepository.save(stockEntity); // ✅ new fridge entry
    }

    // UPDATE: accumulate quantity
    public StockEntity update(Integer id, StockEntity updated) {
        StockEntity existing = findById(id);
        existing.setName(updated.getName());
        existing.setPrice(updated.getPrice());
        existing.setBarcode(updated.getBarcode());
        existing.setLocation(updated.getLocation());
        existing.setQuantity(existing.getQuantity() + updated.getQuantity()); // ✅ accumulate
        return stockRepository.save(existing);
    }

    // DEDUCT from magazine
    public StockEntity removeQuantityFromMagazine(Integer id, int quantity) {
        StockEntity existing = findById(id);
        existing.setQuantity(existing.getQuantity() - quantity);
        return stockRepository.save(existing);
    }

    // DELETE
    public void delete(Integer id) {
        stockRepository.deleteById(id);
    }
}
