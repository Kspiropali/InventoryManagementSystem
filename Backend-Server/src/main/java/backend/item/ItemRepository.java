package backend.item;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@Repository
@Transactional
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i where i.expirationTime <= ?1")
    Optional<Item> findAllByExpirationTimeIsLessThanEqual(final Date date);

    @Query("select i from Item i where i.itemType = ?1")
    Optional<List<Item>> findItemsByItemType(final ItemType type);

    @Query("select i from Item i where i.itemType = ?1")
    Optional<Item> findItemByItemType(final ItemType type);

    @Query("select i from Item i where i.itemType = ?1 and i.expirationTime <= ?2")
    Optional<Item> findByItemTypeAndExpirationTimeIsLessThanEqual(final ItemType type, final Date date);

    @Query("select i from Item i where i.id = ?1")
    @NotNull
    Optional<Item> findById(final @NotNull Long id);

    @Query("select i from Item i where i.createdAt = ?1")
    Optional<Item> findByCreatedAt(final Date date);

    @Query("select i from Item i where i.createdAt <= ?1")
    Optional<Item> findByCreatedAtIsLessThanEqual(final Date date);

    @Query("select i from Item i where i.barcodeText = ?1")
    Item findItemByBarcodeText(String barcode);

    @Query("select i from Item i where i.name = ?1")
    Item findByName(String name);
}