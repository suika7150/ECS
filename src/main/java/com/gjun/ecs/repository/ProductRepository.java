package com.gjun.ecs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gjun.ecs.entity.Product;

import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	@Modifying
	@Transactional
	@Query("UPDATE Product p SET p.states = :states WHERE p.id = :id")
	void updateProductStates(@Param("id") Integer id,
			@Param("states") String states);


	// 新增 查商品時加悲觀鎖
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT p FROM Product p WHERE p.id = :id")
	Product findByIdForUpdate(@Param("id") Integer id);
}
