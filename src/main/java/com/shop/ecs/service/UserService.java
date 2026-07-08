package com.shop.ecs.service;

import java.util.List;

import com.shop.ecs.entity.UserInfo;

/** 使用者服務 */
public interface UserService {

  /** 根據 username 查找使用者 */
  public UserInfo findUserByUsername(String username);

  /** 根據 username 或 email 查找使用者 */
  public UserInfo findUserByUsernameOrEmail(String identifier);

  /**
   * 儲存使用者
   *
   * @param userInfo
   */
  public UserInfo save(UserInfo userInfo);

  public boolean existsByUsername(String username);

  public boolean existsByEmail(String email);

  public List<UserInfo> findAll();
}
