package com.gjun.ecs.service;

import com.gjun.ecs.dto.request.AddOptionReq;
import com.gjun.ecs.dto.response.Outbound;

public interface CategoriesService {

    public Outbound addCategorie(AddOptionReq req) throws Exception;

    public Outbound allCategories() throws Exception;

    public Outbound deleteCategorie(Integer id) throws Exception;

    public Outbound updateCategorie(Integer id, AddOptionReq req) throws Exception;

    public Outbound getCategoriesByListName(String listName) throws Exception;
}
