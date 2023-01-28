package backend.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@SuppressWarnings("unused")
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i where i.expirationTime <= ?1")
    Optional<Item> findAllByExpirationTimeIsLessThanEqual(final Date date);

    @Query("select i from Item i where i.itemType = ?1")
    Optional<Item> findByItemType(final ItemType type);

    @Query("select i from Item i where i.itemType = ?1 and i.expirationTime <= ?2")
    Optional<Item> findByItemTypeAndExpirationTimeIsLessThanEqual(final ItemType type, final Date date);

    @Query("select i from Item i where i.id = ?1")
    Optional<Item> findById(final Long id);

    @Query("select i from Item i where i.createdAt = ?1")
    Optional<Item> findByCreatedAt(final Date date);

    @Query("select i from Item i where i.createdAt <= ?1")
    Optional<Item> findByCreatedAtIsLessThanEqual(final Date date);
}