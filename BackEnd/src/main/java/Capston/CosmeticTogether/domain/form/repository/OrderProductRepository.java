package Capston.CosmeticTogether.domain.form.repository;

import Capston.CosmeticTogether.domain.form.domain.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
}
