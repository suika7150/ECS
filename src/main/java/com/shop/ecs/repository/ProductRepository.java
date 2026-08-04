package com.shop.ecs.repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shop.ecs.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

  List<ProductEntity> findByUserId(Long userId);

  @Modifying
  @Query("UPDATE ProductEntity p SET p.stock = p.stock + :quantity WHERE p.id = :id")
  void updateStock(@Param("id") Integer id, @Param("quantity") Integer quantity);

  @Modifying
  @Query("UPDATE ProductEntity p SET p.status = :status WHERE p.id = :id")
  void updateProductStatus(@Param("id") Integer id, @Param("status") String status);

  // 查商品時加悲觀鎖
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT p FROM ProductEntity p WHERE p.id = :id")
  ProductEntity findByIdForUpdate(@Param("id") Integer id);

  // 查詢商品列表
  @Query("SELECT p FROM ProductEntity p WHERE"
      + " LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR"
      + " LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%'))OR"
      + " LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  List<ProductEntity> findByNameContainingIgnoreCase(@Param("keyword") String keyword);

  // 篩選商品
  @Query("SELECT DISTINCT p.category FROM ProductEntity p WHERE p.category IS NOT NULL")
  List<String> findDistinctCategories();

  // 刪除超過指定時間且狀態為 DELETED 的商品
  @Modifying
  @Query(nativeQuery = true, value = "DELETE FROM product WHERE status = 'DELETED' AND updated_at < :targetTime")
  int deleteExpiredProducts(@Param("targetTime") java.time.LocalDateTime targetTime);
}
