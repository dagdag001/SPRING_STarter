import com.ecommerce.inventory.application.dto.AddProductRequest;
import com.ecommerce.inventory.application.ports.InventoryEventPublisher;
import com.ecommerce.inventory.application.usecase.ReserveStockUseCase;
import com.ecommerce.inventory.domain.entity.Product;
import com.ecommerce.inventory.domain.repository.ProductRepository;
import com.ecommerce.shared.messaging.event.StockFailedEvent;
import com.ecommerce.shared.messaging.event.StockReservedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryApplicationService implements ReserveStockUseCase {

    private final InventoryEventPublisher eventPublisher;
    private final ProductRepository productRepository;

    @Override
    public void reserve(String orderId) {
        // In a real app, you'd iterate items and reserve stock
        eventPublisher.publish(new StockReservedEvent(orderId));
    }

    public Product addProduct(AddProductRequest request) {
        Product product = Product.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName())
                .stockQuantity(request.getInitialStock())
                .build();
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
