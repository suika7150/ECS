package com.shop.ecs.controller;

import com.shop.ecs.common.result.Outbound;
import com.shop.ecs.dto.request.ProductUploadReq;
import com.shop.ecs.service.ProductService;
import com.shop.ecs.service.ProductService.ImageInfo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product API", description = "商品相關 API")
public class ProductController {

  @Autowired
  private ProductService productService;

  @PostMapping
  @Operation(summary = "新增商品")
  public ResponseEntity<Outbound> createProduct(@Valid @RequestBody ProductUploadReq req)
      throws Exception {
    return ResponseEntity.ok(productService.saveProduct(req));
  }

  @GetMapping
  @Operation(summary = "取得所有商品 & 搜尋商品")
  public ResponseEntity<Outbound> getProducts(
      @RequestParam(required = false) String keyword) throws Exception {
    return ResponseEntity.ok(productService.searchProducts(keyword));
  }

  @GetMapping("/{id}")
  @Operation(summary = "取得商品詳情")
  public ResponseEntity<Outbound> getProductDetail(@PathVariable Integer id) throws Exception {
    return ResponseEntity.ok(productService.getProductById(id));
  }

  @PutMapping("/{id}")
  @Operation(summary = "更新商品")
  public ResponseEntity<Outbound> updateProduct(
      @PathVariable Integer id,
      @Valid @RequestBody ProductUploadReq req) throws Exception {
    return ResponseEntity.ok(productService.updateProduct(id, req));
  }

  @GetMapping("/list")
  @Operation(summary = "商品維護列表")
  public ResponseEntity<Outbound> productList() throws Exception {
    return ResponseEntity.ok(productService.productList());
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "刪除商品")
  public ResponseEntity<Outbound> deleteProduct(@PathVariable Integer id) throws Exception {
    return ResponseEntity.ok(productService.deleteProduct(id));
  }

  @GetMapping("/categories")
  @Operation(summary = "取得商品類別")
  public ResponseEntity<Outbound> getCategories() throws Exception {
    return ResponseEntity.ok(productService.getCategories());
  }

  @GetMapping("/{id}/image")
  @Operation(summary = "取得商品圖片流 (用於訂單詳情或列表)")
  public ResponseEntity<byte[]> getProductImage(@PathVariable Integer id) throws Exception {
    ImageInfo imageInfo = productService.getProductEntityById(id);

    if (imageInfo != null && imageInfo.imageData() != null) {

      return ResponseEntity.ok()
          .contentType(
              MediaType.parseMediaType(imageInfo.imageType())) // 動態設定 image/png 或 image/jpeg
          .body(imageInfo.imageData());
    }
    return ResponseEntity.notFound().build();
  }
}
