package com.shop.ecs.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "product")
@SQLDelete(sql = "UPDATE product SET status = 'DELETED' WHERE id = ?")
@SQLRestriction("status <> 'DELETED'")
public class ProductEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "category", nullable = false)
  private String category;

  @Column(name = "price", nullable = false)
  private Integer price;

  @Column(name = "stock", nullable = false)
  private Integer stock;

  @Column(name = "description", nullable = false)
  private String description;

  @Lob
  @Column(name = "image_data", columnDefinition = "LONGBLOB")
  private byte[] imageData;

  @Column(name = "image_type")
  private String imageType;

  @Column(name = "status", nullable = false)
  private String status;
}
