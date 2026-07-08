package com.shop.ecs.service;

import com.shop.ecs.dto.request.AddOptionReq;
import com.shop.ecs.dto.response.Outbound;

public interface CategoriesService {

  public Outbound addCategorie(AddOptionReq req) throws Exception;

  public Outbound allCategories() throws Exception;

  public Outbound deleteCategorie(Integer id) throws Exception;

  public Outbound updateCategorie(Integer id, AddOptionReq req) throws Exception;

  public Outbound getCategoriesByListName(String listName) throws Exception;
}
