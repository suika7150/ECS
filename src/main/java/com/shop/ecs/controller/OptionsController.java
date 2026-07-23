package com.shop.ecs.controller;

import com.shop.ecs.common.result.Outbound;
import com.shop.ecs.dto.request.OptionReq;
import com.shop.ecs.service.CategoriesService;

import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/options")
@Tag(name = "Options", description = "選單管理")
public class OptionsController {

  @Autowired 
  private CategoriesService categoriesService;

  @PostMapping("/add")
  @Operation(summary = "新增商品類別")
  public ResponseEntity<Outbound> addOption(@RequestBody OptionReq req) throws Exception {
    return ResponseEntity.ok(categoriesService.addCategorie(req));
  }

  @GetMapping("/list")
  @Operation(summary = "取得所有商品類別")
  public ResponseEntity<Outbound> allOptions() throws Exception {
    return ResponseEntity.ok(categoriesService.allCategories());
  }

  @DeleteMapping("/delete/{id}")
  @Operation(summary = "刪除商品類別")
  public ResponseEntity<Outbound> deleteOption(@PathVariable Integer id) throws Exception {
    return ResponseEntity.ok(categoriesService.deleteCategorie(id));
  }

  @PutMapping("/update/{id}")
  @Operation(summary = "更新商品類別")
  public ResponseEntity<Outbound> updateOption(
      @PathVariable Integer id, @RequestBody OptionReq req) throws Exception {
    return ResponseEntity.ok(categoriesService.updateCategorie(id, req));
  }

  @GetMapping("/getByListName")
  @Operation(summary = "根據列表名稱取得商品類別")
  public ResponseEntity<Outbound> getCategoriesByListName(@Param("listName") String listName)
      throws Exception {
    return ResponseEntity.ok(categoriesService.getCategoriesList(listName));
  }
}
