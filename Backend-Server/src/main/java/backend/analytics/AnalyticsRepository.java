package backend.analytics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<AnalyticsModel, Long> {
    // find items sold at a specific date
    List<AnalyticsModel> findAllByDatePurchased(Date datePurchased);

    // find items sold after a specific date
    @Query(value = "SELECT * FROM _analytics WHERE date_purchased > ?1", nativeQuery = true)
    List<AnalyticsModel> findAllByDatePurchasedAfter(Date datePurchased);

    // find items sold before a specific date
    @Query(value = "SELECT * FROM _analytics WHERE date_purchased < ?1", nativeQuery = true)
    List<AnalyticsModel> findAllByDatePurchasedBefore(Date datePurchased);

    // find items sold between two dates
    @Query(value = "SELECT * FROM _analytics WHERE date_purchased BETWEEN ?1 AND ?2", nativeQuery = true)
    List<AnalyticsModel> findAllByDatePurchasedBetween(Date datePurchased, Date datePurchased2);

    // find items sold by name
    @Query(value = "SELECT * FROM _analytics WHERE name = ?1", nativeQuery = true)
    List<AnalyticsModel> findAllByName(String name);

    // find items sold by quantity
    @Query(value = "SELECT * FROM _analytics WHERE quantity = ?1", nativeQuery = true)
    List<AnalyticsModel> findAllByQuantity(int quantity);

    // find most selling item
    @Query(value = "SELECT * FROM _analytics ORDER BY quantity", nativeQuery = true)
    List<AnalyticsModel> findAllByOrderByQuantity();

    @Query(value = "SELECT * FROM _analytics ORDER BY total_profit DESC LIMIT 5", nativeQuery = true)
    List<AnalyticsModel> findTopByTotal_profit();

    List<AnalyticsModel> findAllByOrderByDatePurchasedDesc();
}
