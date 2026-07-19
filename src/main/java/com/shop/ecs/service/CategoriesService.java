package com.shop.ecs.service;

import com.shop.ecs.common.result.Outbound;
import com.shop.ecs.dto.request.AddOptionReq;

public interface CategoriesService {

  public Outbound addCategorie(AddOptionReq req) throws Exception;

  public Outbound allCategories() throws Exception;

  public Outbound deleteCategorie(Integer id) throws Exception;

  public Outbound updateCategorie(Integer id, AddOptionReq req) throws Exception;

  public Outbound getCategoriesByListName(String listName) throws Exception;
}
