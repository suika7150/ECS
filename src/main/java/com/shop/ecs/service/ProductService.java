package com.shop.ecs.service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.ecs.common.result.Outbound;
import com.shop.ecs.constant.ProductStatusEnum;
import com.shop.ecs.dto.request.ProductUploadReq;
import com.shop.ecs.dto.response.ProductResp;
import com.shop.ecs.dto.response.ProductDetailResp;
import com.shop.ecs.entity.ProductEntity;
import com.shop.ecs.entity.UserEntity;
import com.shop.ecs.repository.ProductRepository;
import com.shop.ecs.repository.UserRepository;
import com.shop.ecs.utils.ImageUtils;

@Service
public class ProductService {

  @Autowired
  private UserRepository userRepository;

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

    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = String.valueOf(principal);
    UserEntity user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("找不到該使用者: " + username));
    Long currentUserId = user.getId();

    ProductEntity product = ProductEntity.builder()
        .userId(currentUserId)
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
    ProductEntity product = productRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("ProductEntity not found"));

    if (!ProductStatusEnum.ON_SALE.getCode().equals(product.getStatus())) {
      throw new RuntimeException("商品已下架");
    }

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

    ProductEntity productEntity = productRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("找不到更新的商品: " + id));

    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = String.valueOf(principal);
    UserEntity user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("找不到該使用者: " + username));
    Long currentUserId = user.getId();

    if (!productEntity.getUserId().equals(currentUserId)) {
      throw new RuntimeException("操作失敗");
    }

    ImageInfo imageInfo = processBase64Image(req.getImageBase64(), req.getImageType());

    productEntity.setName(req.getName());
    productEntity.setCategory(req.getCategory());
    productEntity.setStock(req.getStock());
    productEntity.setPrice(req.getPrice());
    productEntity.setStatus(req.getStatus());
    productEntity.setDescription(req.getDescription());
    productEntity.setImageData(imageInfo.imageData);
    productEntity.setImageType(imageInfo.imageType);

    productRepository.save(productEntity);
    return Outbound.ok("商品更新成功");
  }

  @Transactional(readOnly = true)
  public Outbound productList() {

    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = String.valueOf(principal);

    UserEntity user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("找不到該使用者: " + username));

    Long currentUserId = user.getId();

    List<ProductResp> result = productRepository.findByUserId(currentUserId).stream()
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

    ProductEntity product = productRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("修改狀態後找不到該商品: " + id));

    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = String.valueOf(principal);
    UserEntity user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("找不到該使用者: " + username));
    Long currentUserId = user.getId();

    if (!product.getUserId().equals(currentUserId)) {
      throw new RuntimeException("操作失敗");
    }

    productRepository.delete(product);

    ProductResp resp = ProductResp.builder()
        .id(product.getId())
        .name(product.getName())
        .price(product.getPrice())
        .stock(product.getStock())
        .description(product.getDescription())
        .category(product.getCategory())
        .imageBase64(ImageUtils.toBase64Src(product.getImageData(), product.getImageType()))
        .status(ProductStatusEnum.DELETED.getCode())
        .build();

    return Outbound.ok(resp);
  }

  // 刪除超過指定時間且狀態為 DELETED 的商品 (排程使用)
  @Transactional(rollbackFor = Exception.class)
  public int clearExpiredProducts(int daysAgo) {
    LocalDateTime targetTime = LocalDateTime.now().minusDays(daysAgo);
    return productRepository.deleteExpiredProducts(targetTime);
  }

  // 取得商品類別
  @Transactional(readOnly = true)
  public Outbound getCategories() {
    List<String> categories = productRepository.findDistinctCategories();
    return Outbound.ok(categories);
  }

  // 商品圖片資訊封裝物件
  public record ImageInfo(byte[] imageData, String imageType) {
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
  public ImageInfo getProductEntityById(Integer id) {
    ProductEntity product = productRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("找不到編號為 " + id + " 的商品"));

    return new ImageInfo(product.getImageData(), product.getImageType());
  }
}
