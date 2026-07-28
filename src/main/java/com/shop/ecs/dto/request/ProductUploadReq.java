package com.shop.ecs.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "產品上傳請求")
public class ProductUploadReq {

  @NotBlank
  @Schema(description = "產品名稱", example = "iPhone 13 Pro")
  private String name;

  @NotBlank
  @Schema(description = "產品類別", example = "手機")
  private String category;

  @NotNull
  @Min(value = 0)
  @Schema(description = "產品價格", example = "9999")
  private Integer price;

  @NotNull
  @Min(value = 0)
  @Schema(description = "產品庫存", example = "10")
  private Integer stock;

  @Size(max = 1000, message = "產品描述長度不能超過 1000 個字")
  @Schema(description = "產品描述", example = "iPhone 13 Pro")
  private String description;

  @NotBlank
  @Schema(description = "產品狀態", example = "銷售中")
  private String status;

  @NotBlank
  @Schema(description = "產品圖片 Base64 字串", example = "iVBORw0KGgoAAAANSUhEUgAA...")
  private String imageBase64;

  @NotBlank
  @Schema(description = "產品圖片格式", example = "image/png")
  private String imageType;
}
