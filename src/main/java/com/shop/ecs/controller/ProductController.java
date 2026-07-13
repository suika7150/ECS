package com.shop.ecs.controller;

import com.shop.ecs.dto.request.ProductUploadReq;
import com.shop.ecs.dto.response.Outbound;
import com.shop.ecs.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Product", description = "商品相關 API")
public class ProductController {

  @Autowired
  private ProductService productService;

  @PostMapping("/addProducts")
  @Operation(summary = "新增商品")
  public ResponseEntity<Outbound> uploadProduct(@RequestBody ProductUploadReq req)
      throws Exception {
    return ResponseEntity.ok(productService.saveProduct(req));
  }

  @GetMapping("/products")
  @Operation(summary = "取得所有商品資料 & 搜尋商品資料")
  public ResponseEntity<Outbound> getProducts(
      @RequestParam(value = "keyword", required = false) String keyword) throws Exception {
     
        if (keyword != null && !keyword.isEmpty()) {
      return ResponseEntity.ok(productService.searchProducts(keyword));
    }
    return ResponseEntity.ok(productService.getAllProducts());
  }

  @GetMapping("/products/{id}")
  @Operation(summary = "取得商品詳細資料")
  public ResponseEntity<Outbound> getProductDetail(@PathVariable Integer id) throws Exception {
    return ResponseEntity.ok(productService.getProductById(id));
  }

  @GetMapping("/products/edit/{id}")
  @Operation(summary = "編輯商品資料")
  public ResponseEntity<Outbound> getProductById(@PathVariable Integer id) throws Exception {
    return ResponseEntity.ok(productService.getProductById(id));
  }

  @PutMapping("/updateProducts/{id}")
  @Operation(summary = "更新商品")
  public ResponseEntity<Outbound> updateProduct(
      @PathVariable Integer id, @RequestBody ProductUploadReq req) throws Exception {
    return ResponseEntity.ok(productService.updateProduct(id, req));
  }

  @GetMapping("/products/list")
  @Operation(summary = "商品維護列表")
  public ResponseEntity<Outbound> productList() throws Exception {
    return ResponseEntity.ok(productService.productList());
  }

  @PutMapping("/deleteProducts/{id}")
  @Operation(summary = "刪除商品")
  public ResponseEntity<Outbound> deleteProduct(@PathVariable Integer id) throws Exception {
    return ResponseEntity.ok(productService.deleteProduct(id));
  }

  @GetMapping("/categories")
  @Operation(summary = "取得商品類別")
  public ResponseEntity<Outbound> getCategories() throws Exception {
    return ResponseEntity.ok(productService.getCategories());
  }

  @GetMapping("/products/{id}/image")
  @Operation(summary = "取得商品圖片流 (用於訂單詳情或列表)")
  public ResponseEntity<byte[]> getProductImage(@PathVariable Integer id) throws Exception {
    com.shop.ecs.entity.Product product = productService.getProductEntityById(id);

    if (product != null && product.getImageData() != null) {

      return ResponseEntity.ok()
          .contentType(
              MediaType.parseMediaType(product.getImageType())) // 動態設定 image/png 或 image/jpeg
          .body(product.getImageData());
    }
    return ResponseEntity.notFound().build();
  }
}
