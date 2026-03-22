package StockManagment.StockManagment.Stock;

import java.util.List;

public interface StockService {

    List<StockEntity> findAll(int location);
    StockEntity findById(Integer id);
    StockEntity findByBarcode(String barcode);
    StockEntity create(StockEntity stockEntity);
    StockEntity update(Integer id, StockEntity updated);
    void delete(Integer id);
}
