package com.shop.ecs.controller;

import com.shop.ecs.common.result.Outbound;
import com.shop.ecs.dto.request.OptionReq;
import com.shop.ecs.service.CategoriesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
@RequestMapping("/api/v1/options")
@Tag(name = "管理者介面 API", description = "管理者介面")
public class OptionController {

  @Autowired
  private CategoriesService categoriesService;

  @PostMapping
  @Operation(summary = "新增商品類別")
  public ResponseEntity<Outbound> createOption(@RequestBody OptionReq req) throws Exception {
    return ResponseEntity.ok(categoriesService.addCategorie(req));
  }

  @GetMapping
  @Operation(summary = "取得所有選項/類別")
  public ResponseEntity<Outbound> getOptions(
      @Parameter(description = "列表名稱/分類群組", required = false) @RequestParam(required = false) String listName)
      throws Exception {

    if (listName != null && !listName.trim().isEmpty()) {
      return ResponseEntity.ok(categoriesService.getCategoriesList(listName));
    }
    return ResponseEntity.ok(categoriesService.allCategories());
  }

  @PutMapping("/{id}")
  @Operation(summary = "更新選項/類別")
  public ResponseEntity<Outbound> updateOption(
      @PathVariable Integer id, @RequestBody OptionReq req) throws Exception {
    return ResponseEntity.ok(categoriesService.updateCategorie(id, req));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "刪除選項/類別")
  public ResponseEntity<Outbound> deleteOption(@PathVariable Integer id) throws Exception {
    return ResponseEntity.ok(categoriesService.deleteCategorie(id));
  }
}
