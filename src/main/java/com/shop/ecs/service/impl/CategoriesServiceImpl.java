package com.shop.ecs.service.impl;

import com.shop.ecs.common.result.Outbound;
import com.shop.ecs.dto.request.AddOptionReq;
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

@Service
public class CategoriesServiceImpl implements CategoriesService {

  @Autowired private CategoriesRepository categoriesRepository;

  @Override
  public Outbound addCategorie(AddOptionReq req) throws Exception {

    CategoryEntity categories =
        CategoryEntity.builder()
            .listName(req.getListName())
            .name(req.getName())
            .value(req.getValue())
            .sortOrder(req.getSortOrder())
            .isActive(req.getIsActive())
            .description(req.getDescription())
            .build();
    try {
      categoriesRepository.save(categories);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return Outbound.ok("Category added successfully");
  }

  @Override
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
                      .key(Categories.getName())
                      .value(Categories.getValue())
                      .sortOrder(Categories.getSortOrder())
                      .isActive(Categories.getIsActive())
                      .description(Categories.getDescription())
                      .build();
                })
            .collect(Collectors.toList());

    return Outbound.ok(result);
  }

  @Override
  public Outbound deleteCategorie(Integer id) throws Exception {
    categoriesRepository.deleteById(id);
    return Outbound.ok("Category deleted successfully");
  }

  @Override
  public Outbound updateCategorie(Integer id, AddOptionReq req) throws Exception {
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

    return Outbound.ok("Category updated successfully");
  }

  @Override
  public Outbound getCategoriesByListName(String listName) throws Exception {
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
