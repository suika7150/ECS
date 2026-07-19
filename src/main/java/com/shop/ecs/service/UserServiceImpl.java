package com.shop.ecs.service;

import com.shop.ecs.entity.UserEntity;
import com.shop.ecs.repository.UserRepository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  /**
   * 根據 username 查找使用者
   *
   * @param username
   */
  @Override
  public UserEntity findUserByUsername(String username) {
    return userRepository.findByUsername(username).orElse(null);
  }

  /**
   * 根據 username 或 email 查找使用者
   *
   * @param email
   */
  @Override
  public UserEntity findUserByUsernameOrEmail(String identifier) {
    return userRepository.findByUsername(identifier)
        .or(() -> userRepository.findByEmail(identifier))
        .orElse(null);
  }

  /**
   * 儲存使用者
   *
   * @param userInfo
   */
  @Override
  public UserEntity save(UserEntity userInfo) {
    return userRepository.save(userInfo);
  }

  /**
   * 判斷帳號是否存在
   *
   * @param username
   */
  @Override
  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  /**
   * 判斷 Email 是否存在
   *
   * @param email
   */
  @Override
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  /** 取得所有使用者 */
  @Override
  public List<UserEntity> findAll() {
    return userRepository.findAll();
  }
}
