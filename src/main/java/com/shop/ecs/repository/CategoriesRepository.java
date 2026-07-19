package com.shop.ecs.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shop.ecs.entity.CategoryEntity;

public interface CategoriesRepository extends JpaRepository<CategoryEntity, Integer> {

  @Query(
      nativeQuery = true,
      value =
          "SELECT * FROM generic_options WHERE list_name = ?1 AND is_active = true  ORDER BY"
              + " sort_order ASC")
  List<CategoryEntity> findByListNameAsc(String listName);

  List<CategoryEntity> findByListNameAndIsActiveTrueOrderBySortOrderAsc(String listName);

  @Query(
      nativeQuery = true,
      value =
          "SELECT * FROM generic_options WHERE list_name = ?1 AND is_active = true ORDER BY"
              + " sort_order DESC")
  List<CategoryEntity> findByListNameDesc(String listName);

  List<CategoryEntity> findByListNameAndIsActiveTrueOrderBySortOrderDesc(String listName);
}
