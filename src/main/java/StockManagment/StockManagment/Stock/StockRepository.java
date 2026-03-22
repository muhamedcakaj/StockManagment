package StockManagment.StockManagment.Stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<StockEntity,Integer> {
    Optional<StockEntity> findByBarcodeAndLocation(String barcode, int location);
    List<StockEntity> findAllByLocation(int location);
    Optional<StockEntity> findByBarcode(String barcode);
}
