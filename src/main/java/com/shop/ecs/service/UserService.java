package com.shop.ecs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.ecs.entity.UserEntity;
import com.shop.ecs.repository.UserRepository;

/** 使用者服務 */
@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  // 根據 username 查詢使用者 findUserByUsername
  public UserEntity getUser(String username){
    return userRepository.findByUsername(username).orElse(null);
  }

  // 根據 username 或 email 查詢使用者 findUserByUsernameOrEmail
  public UserEntity loginVerify(String identifier){
    return userRepository.findByUsername(identifier)
        .or(() -> userRepository.findByEmail(identifier))
        .orElse(null);
  }

  // 儲存使用者 save
  public UserEntity saveUser(UserEntity userInfo){
    return userRepository.save(userInfo);
  }

  //判斷帳號是否存在 existsByUsername
  public boolean existsUser(String username){
    return userRepository.existsByUsername(username);
  }

   // 判斷 Email 是否存在 existsByEmail
  public boolean existsEmail(String email){
    return userRepository.existsByEmail(email);
  }

  // 取得所有使用者 findAll
  public List<UserEntity> getAllUsers(){
    return userRepository.findAll();
  }
}
