package Capston.CosmeticTogether.domain.form.repository;

import Capston.CosmeticTogether.domain.form.domain.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
