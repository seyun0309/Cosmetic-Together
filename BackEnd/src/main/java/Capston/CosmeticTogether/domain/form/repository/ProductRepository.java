package Capston.CosmeticTogether.domain.form.repository;

import Capston.CosmeticTogether.domain.form.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
