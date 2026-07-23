package com.shop.ecs.service;

import com.shop.ecs.common.result.Outbound;
import com.shop.ecs.dto.request.OptionReq;
import com.shop.ecs.dto.response.OptionResp;
import com.shop.ecs.dto.response.SelectOptionResp;
import com.shop.ecs.entity.CategoryEntity;
import com.shop.ecs.repository.CategoriesRepository;
import com.shop.ecs.service.CategoriesService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriesService {

  @Autowired 
  private CategoriesRepository categoriesRepository;

  // 新增商品類別
  @Transactional(rollbackFor = Exception.class)
  public Outbound addCategorie(OptionReq req) throws Exception {

    CategoryEntity Categories =
        CategoryEntity.builder()
            .listName(req.getListName())
            .name(req.getName())
            .value(req.getValue())
            .sortOrder(req.getSortOrder())
            .isActive(req.getIsActive())
            .description(req.getDescription())
            .build();

    CategoryEntity categoryEntity = categoriesRepository.save(Categories);

    OptionResp resp = 
        OptionResp.builder()
          .id(categoryEntity.getId())
          .listName(categoryEntity.getListName())
          .name(categoryEntity.getName())
          .value(categoryEntity.getValue())
          .sortOrder(categoryEntity.getSortOrder())
          .isActive(categoryEntity.getIsActive())
          .description(categoryEntity.getDescription())
          .build();
    return Outbound.ok(resp);
  }

  // 取得所有商品類別
  @Transactional(readOnly = true)
  public Outbound allCategories() throws Exception {
    List<OptionResp> result =
        categoriesRepository.findAll().stream()
            .sorted(
                Comparator.comparing(CategoryEntity::getListName)
                    .thenComparing(CategoryEntity::getSortOrder))
            .map(
                Categories -> {
                  return OptionResp.builder()
                      .id(Categories.getId())
                      .listName(Categories.getListName())
                      .name(Categories.getName())
                      .value(Categories.getValue())
                      .sortOrder(Categories.getSortOrder())
                      .isActive(Categories.getIsActive())
                      .description(Categories.getDescription())
                      .build();
                })
            .collect(Collectors.toList());

    return Outbound.ok(result);
  }

  // 刪除商品類別
  @Transactional(rollbackFor = Exception.class)
  public Outbound deleteCategorie(Integer id) throws Exception {
    categoriesRepository.deleteById(id);
    return Outbound.ok("刪除成功");
  }

  // 更新商品類別
  @Transactional(rollbackFor = Exception.class)
  public Outbound updateCategorie(Integer id, OptionReq req) throws Exception {
    CategoryEntity categorie =
        categoriesRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));

    CategoryEntity updateCategories =
        CategoryEntity.builder()
            .id(categorie.getId())
            .listName(req.getListName())
            .name(req.getName())
            .value(req.getValue())
            .sortOrder(req.getSortOrder())
            .isActive(req.getIsActive())
            .description(req.getDescription())
            .build();

    categoriesRepository.save(updateCategories);

    return Outbound.ok("更新成功");
  }

  // 根據列表名稱取得商品類別
  @Transactional(readOnly = true)
  public Outbound getCategoriesList(String listName) throws Exception {
    List<CategoryEntity> categories =
        categoriesRepository.findByListNameAndIsActiveTrueOrderBySortOrderAsc(listName);

    if (categories.isEmpty()) {
      throw new RuntimeException("CategoryEntity not found");
    }

    List<SelectOptionResp> result =
        categories.stream()
            .map(
                categorie -> {
                  return SelectOptionResp.builder()
                      .label(categorie.getName())
                      .value(categorie.getValue())
                      .sortOrder(categorie.getSortOrder())
                      .build();
                })
            .collect(Collectors.toList());

    return Outbound.ok(result);
  }
}
