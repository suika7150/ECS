package com.shop.ecs.service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.ecs.common.result.Outbound;
import com.shop.ecs.constant.ProductStatusEnum;
import com.shop.ecs.dto.request.ProductUploadReq;
import com.shop.ecs.dto.response.ProductResp;
import com.shop.ecs.dto.response.ProductDetailResp;
import com.shop.ecs.entity.ProductEntity;
import com.shop.ecs.repository.ProductRepository;
import com.shop.ecs.utils.ImageUtils;

@Service
public class ProductService {

  @Autowired
  private ProductRepository productRepository;

  // 查詢商品
  @Transactional(readOnly = true)
  public Outbound searchProducts(String keyword) {
    List<ProductEntity> products;

    // 如果關鍵字是空的，就抓全部；否則才執行模糊搜尋
    if (keyword == null || keyword.trim().isEmpty()) {
      products = productRepository.findAll();
    } else {
      products = productRepository.findByNameContainingIgnoreCase(keyword.trim());
    }
    List<ProductDetailResp> result = products.stream()
        .filter(product -> ProductStatusEnum.ON_SALE.getCode().equals(product.getStatus()))
        .map(
            product -> {
              return ProductDetailResp.builder()
                  .id(product.getId())
                  .name(product.getName())
                  .price(product.getPrice())
                  .description(product.getDescription())
                  .category(product.getCategory())
                  .rating(null)
                  .imageBase64(
                      ImageUtils.toBase64Src(product.getImageData(), product.getImageType()))
                  .build();
            })
        .collect(Collectors.toList());

    return Outbound.ok(result);
  }

  @Transactional(rollbackFor = Exception.class)
  public Outbound saveProduct(ProductUploadReq req) {
    ImageInfo imageInfo = processBase64Image(req.getImageBase64(), req.getImageType());

    ProductEntity product = ProductEntity.builder()
        .name(req.getName())
        .category(req.getCategory())
        .price(req.getPrice())
        .stock(req.getStock())
        .description(req.getDescription())
        .status(req.getStatus())
        .imageData(imageInfo.imageData)
        .imageType(imageInfo.imageType)
        .build();

    ProductEntity newProduct = productRepository.save(product);

    ProductResp resp = ProductResp.builder()
        .id(newProduct.getId())
        .name(newProduct.getName())
        .price(newProduct.getPrice())
        .stock(newProduct.getStock())
        .status(newProduct.getStatus())
        .description(newProduct.getDescription())
        .category(newProduct.getCategory())
        .imageBase64(ImageUtils.toBase64Src(newProduct.getImageData(), newProduct.getImageType()))
        .build();

    return Outbound.ok(resp);
  }

  @Transactional(readOnly = true)
  public Outbound getProductById(Integer id) {
    ProductEntity product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("ProductEntity not found"));

    ProductResp resp = ProductResp.builder()
        .id(product.getId())
        .name(product.getName())
        .price(product.getPrice())
        .stock(product.getStock())
        .status(product.getStatus())
        .description(product.getDescription())
        .category(product.getCategory())
        .imageBase64(ImageUtils.toBase64Src(product.getImageData(), product.getImageType()))
        .build();

    return Outbound.ok(resp);
  }

  @Transactional(rollbackFor = Exception.class)
  public Outbound updateProduct(Integer id, ProductUploadReq req) {
    if (id == null) {
      throw new IllegalArgumentException("更新商品的ID不能為空");
    }
    
    productRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("找不到欲更新的商品: " + id));

    ImageInfo imageInfo = processBase64Image(req.getImageBase64(), req.getImageType());

    ProductEntity updateProduct = ProductEntity.builder()
        .id(id)
        .name(req.getName())
        .category(req.getCategory())
        .stock(req.getStock())
        .price(req.getPrice())
        .status(req.getStatus())
        .description(req.getDescription())
        .imageData(imageInfo.imageData)
        .imageType(imageInfo.imageType)
        .build();

    productRepository.save(updateProduct);
    return Outbound.ok("商品更新成功");
  }

  @Transactional(readOnly = true)
  public Outbound productList() {

    List<ProductResp> result = productRepository.findAll().stream()
        .map(
            product -> {
              return ProductResp.builder()
                  .id(product.getId())
                  .name(product.getName())
                  .price(product.getPrice())
                  .stock(product.getStock())
                  .description(product.getDescription())
                  .category(product.getCategory())
                  .imageBase64(
                      ImageUtils.toBase64Src(product.getImageData(), product.getImageType()))
                  .status(ProductStatusEnum.getDesc(product.getStatus()))
                  .build();
            })
        .collect(Collectors.toList());

    return Outbound.ok(result);
  }

  @Transactional(rollbackFor = Exception.class)
  public Outbound deleteProduct(Integer id) {
    if (id == null) {
          throw new IllegalArgumentException("刪除商品的ID不能為空");
    }

    productRepository.updateProductStatus(id, ProductStatusEnum.DELETED.getCode());

    ProductEntity product = productRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("修改狀態後找不到該商品: " + id));

    ProductResp resp = ProductResp.builder()
        .id(product.getId())
        .name(product.getName())
        .price(product.getPrice())
        .stock(product.getStock())
        .description(product.getDescription())
        .category(product.getCategory())
        .imageBase64(ImageUtils.toBase64Src(product.getImageData(), product.getImageType()))
        .status(ProductStatusEnum.getDesc(product.getStatus()))
        .build();

    return Outbound.ok(resp);
  }

  // 取得商品類別 
  @Transactional(readOnly = true)
  public Outbound getCategories() {
    List<String> categories = productRepository.findDistinctCategories();
    return Outbound.ok(categories);
  }

  // 用來傳遞圖片處理結果的 record。 Record 是 Java 14+ 的特性，適合用來傳遞不可變的資料物件
  private record ImageInfo(byte[] imageData, String imageType) {
  }

  // 處理 Base64 圖片字串，解析出圖片二進制資料和類型。
  private ImageInfo processBase64Image(String base64String, String existingImageType) {
    if (base64String == null || base64String.isBlank()) {
      return new ImageInfo(null, null); // 沒有圖片，返回空值
    }

    String imageType = existingImageType;
    String base64Content = base64String;

    // 移除 Data URI scheme 前綴並嘗試解析圖片類型
    if (base64String.startsWith("data:")) {
      int commaIndex = base64String.indexOf(',');
      if (commaIndex != -1) {
        String dataUri = base64String.substring(0, commaIndex);
        if (dataUri.contains(";base64")) {
          imageType = dataUri.substring(dataUri.indexOf(':') + 1, dataUri.indexOf(';'));
        }
        base64Content = base64String.substring(commaIndex + 1);
      }
    }

    try {
      byte[] imageBytes = Base64.getDecoder().decode(base64Content);
      return new ImageInfo(imageBytes, imageType);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("無效的 Base64 圖片格式", e);
    }
  }

  // 取得商品圖片
  public ProductEntity getProductEntityById(Integer id) {
    return productRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("找不到編號為 " + id + " 的商品"));
  }
}
