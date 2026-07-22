package com.shop.ecs.service;

import java.util.List;

import com.shop.ecs.entity.UserEntity;

/** 使用者服務 */
public interface UserService {

  // 根據 username 查找使用者
  public UserEntity findUserByUsername(String username);

  // 根據 username 或 email 查找使用者
  public UserEntity findUserByUsernameOrEmail(String identifier);

  // 儲存使用者
  public UserEntity save(UserEntity userInfo);

  public boolean existsByUsername(String username);

  public boolean existsByEmail(String email);

  public List<UserEntity> findAll();
}
