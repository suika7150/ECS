package com.gjun.ecs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gjun.ecs.dto.request.ProductUploadReq;
import com.gjun.ecs.dto.response.Outbound;
import com.gjun.ecs.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api")
@Tag(name = "Product", description = "商品相關 API")
public class ProductController {

  @Autowired
  private ProductService productService;

  /**
   * 新增商品
   * 
   * @param req 商品資料
   * @return
   */
  @PostMapping(path = "/addProducts", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "新增商品")
  public ResponseEntity<Outbound> uploadProduct(
      @RequestBody ProductUploadReq req) throws Exception {
    Outbound resp = productService.saveProduct(req);
    return ResponseEntity.ok(resp);
  }

  /**
   * 取得所有商品資料 & 搜尋商品資料
   * 
   * @return
   */
  @GetMapping("/products")
  @Operation(summary = "取得所有商品資料 & 搜尋商品資料")
  public ResponseEntity<Outbound> getProducts(@RequestParam(value = "keyword", required = false) String keyword) throws Exception {
    Outbound resp ;
    
    if(keyword !=null && !keyword.isEmpty()){
      // 如果有提供 keyword，則呼叫搜尋方法
      resp = productService.searchProducts(keyword);
    }else{
      resp = productService.getAllProducts();
    }

    return ResponseEntity.ok(resp);
  }

/**
   * 取得商品資料
   * 
   * @param id 商品ID
   * @return
   */
@GetMapping("/products/{id}")
@Operation(summary = "取得商品詳細資料")
public ResponseEntity<Outbound> getProductDetail(@PathVariable Integer id) throws Exception {
    //沿用已有的 Service 方法來獲取商品資料
    Outbound resp = productService.getProductById(id);
    return ResponseEntity.ok(resp);
}

  /**
   * 取得商品資料
   * 
   * @param id 商品ID
   * @return
   */
  @GetMapping("/products/edit/{id}")
  @Operation(summary = "編輯商品資料")
  public ResponseEntity<Outbound> getProductById(@PathVariable Integer id) throws Exception {
    Outbound resp = productService.getProductById(id);
    return ResponseEntity.ok(resp);
  }

  /**
   * 更新商品
   * 
   * @param id  商品ID
   * @param req 更新資料
   * @return
   */
  @PutMapping("/updateProducts/{id}")
  @Operation(summary = "更新商品")
  public ResponseEntity<Outbound> updateProduct(@PathVariable Integer id,
      @RequestBody ProductUploadReq req) throws Exception {
    Outbound resp = productService.updateProduct(id, req);
    return ResponseEntity.ok(resp);
  }

  /**
   * 商品維護列表
   * 
   * @return
   */
  @GetMapping("/products/list")
  @Operation(summary = "商品維護列表")
  public ResponseEntity<Outbound> productList() throws Exception {
    Outbound resp = productService.productList();
    return ResponseEntity.ok(resp);
  }

  /**
   * 刪除商品
   * 
   * @param id
   * @return
   */
  @PutMapping("/deleteProduct/{id}")
  @Operation(summary = "刪除商品")
  public ResponseEntity<Outbound> deleteProduct(@PathVariable Integer id) throws Exception {
    Outbound resp = productService.deleteProduct(id);
    return ResponseEntity.ok(resp);
  }


  /**
  * 取得商品圖片
  * * @param id 商品ID
  * @return 圖片二進位資料
  */
  @GetMapping("/products/{id}/image")
  @Operation(summary = "取得商品圖片流 (用於訂單詳情或列表)")
  public ResponseEntity<byte[]> getProductImage(@PathVariable Integer id) throws Exception {
    
    com.gjun.ecs.entity.Product product = productService.getProductEntityById(id);

    if(product != null && product.getImageData() != null){

      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(product.getImageType())) // 動態設定 image/png 或 image/jpeg
          .body(product.getImageData());
    }
    return ResponseEntity.notFound().build();
  }
}
