package com.gjun.ecs.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gjun.ecs.dto.request.ChangePswReq;
import com.gjun.ecs.dto.request.LoginReq;
import com.gjun.ecs.dto.request.RegisterReq;
import com.gjun.ecs.dto.request.UpdateUserReq;
import com.gjun.ecs.dto.response.LoginResp;
import com.gjun.ecs.dto.response.Outbound;
import com.gjun.ecs.dto.response.UserResponse;
import com.gjun.ecs.entity.UserInfo;
import com.gjun.ecs.enums.ResultCode;
import com.gjun.ecs.exception.ApplicationException;
import com.gjun.ecs.utils.JwtUtil;

@Service
public class AuthService {

  @Autowired
  private UserService userService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtUtil jwtUtil;

  /**
   * 使用者註冊
   * 
   * @param req
   * @return
   */
  public Outbound register(RegisterReq req) throws ApplicationException {
    // 帳號與 Email 重複檢查
    if (userService.existsByUsername(req.getUsername())) {
      throw new ApplicationException(ResultCode.ACCOUNT_IS_EXIST);
    }

    if (userService.existsByEmail(req.getEmail())) {
      throw new ApplicationException(ResultCode.EMAIL_IS_EXIST);
    }

    // 模擬信箱驗證碼檢查
    // 只要有輸入驗證碼就放行
    if (req.getSmsCode() == null || req.getSmsCode().isEmpty()) {
    throw new ApplicationException(ResultCode.SMS_CODE_ERROR);
    }

    UserInfo userInfo = UserInfo.builder().username(req.getUsername())
        .password(passwordEncoder.encode(req.getPassword()))
        .email(req.getEmail()).fullName(req.getFullName())
        .phone(req.getPhone()).gender(req.getGender())
        .birthday(req.getBirthday()).role("USER").build();

    userService.save(userInfo);
    return Outbound.ok("註冊成功");

  }

  /**
 * 模擬發送信箱驗證碼
 */
  public Outbound sendEmailCode(String email) throws ApplicationException{
    // 檢查 Email 是否已被註冊過
    if(userService.existsByEmail(email)){
      // 如果已存在，直接拋出異常，讓前端進入 catch 區塊
      throw new ApplicationException(ResultCode.EMAIL_IS_EXIST);
    }
    // 生成隨機 6 位數驗證碼
    String mockCode = String.valueOf((int)((Math.random() * 9 +1)*100000));
  
    // 日誌記錄，暫時先印出來
    System.out.println("========================================");
    System.out.println("【ECS 系統日誌】正在向信箱 " + email + " 發送驗證碼");
    System.out.println("驗證碼為: " + mockCode);
    System.out.println("========================================");
  
    // 實務上這裡要存入 Redis 並設定 5 分鐘過期
    // 模擬階段先回傳成功
    return Outbound.ok(mockCode);
  }

  /**
   * 使用者登入
   * 
   * @param req
   * @return
   */
  public Outbound login(LoginReq req) throws ApplicationException {

    UserInfo userInfo = userService.findUserByUsername(req.getUsername());

    if (userInfo == null) {
      throw new ApplicationException(ResultCode.USER_IS_NOT_EXIST);
    }

    if (!passwordEncoder.matches(req.getPassword(),
        userInfo.getPassword())) {
      throw new ApplicationException(ResultCode.PASSWORD_NOT_MATCH);
    }

    // 生成 Token
    String token = jwtUtil.generateToken(userInfo, req.isRememberMe());

    // 回傳給 Controller 的 DTO，Controller 從這裡拿 token 寫入 Cookie
    LoginResp loginResp = LoginResp.builder()
        .token(token)
        .role(userInfo.getRole())
        .username(userInfo.getUsername())
        .fullName(userInfo.getFullName())
        .rememberMe(req.isRememberMe())
        .build();

    return Outbound.ok(loginResp);
  }

  /**
   * 取得使用者資料
   * 
   * @param token
   * @return
   */
  public Outbound findUserByUsername(String username)throws ApplicationException {

    UserInfo userInfo = userService.findUserByUsername(username);

    if (userInfo == null) {
      throw new ApplicationException(ResultCode.USER_IS_NOT_EXIST);
    }

    UserResponse userResponse = UserResponse.builder().id(userInfo.getId())
        .username(userInfo.getUsername())
        .email(userInfo.getEmail())
        .fullName(userInfo.getFullName())
        .phone(userInfo.getPhone())
        .role(userInfo.getRole())
        .createdAt(userInfo.getCreatedAt())
        .gender(userInfo.getGender())
        .birthday(userInfo.getBirthday())
        .build();

    return Outbound.ok(userResponse);
  }

  /**
   * 更新使用者資料
   * 
   * @param username
   * @param request
   * @return
   */
  public Outbound updateUserProfile(String username,
      UpdateUserReq request) throws Exception {
    UserInfo userInfo = userService.findUserByUsername(username);

    if (userInfo == null) {
      throw new ApplicationException(ResultCode.USER_IS_NOT_EXIST);
    }

    userInfo.setFullName(request.getFullName());
    userInfo.setPhone(request.getPhone());
    userInfo.setEmail(request.getEmail());
    userInfo.setGender(request.getGender());
    userInfo.setBirthday(request.getBirthday());

    try {
      UserInfo upUserInfo = userService.save(userInfo);
      return Outbound.ok(upUserInfo);
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }

  }

  /**
   * 取得所有使用者資料
   * 
   * @return
   */

  public Outbound getCurrentUser() {
    List<UserInfo> users = userService.findAll();
    List<UserResponse> userResponses = users.stream()
        .map(userInfo -> UserResponse.builder().id(userInfo.getId())
            .username(userInfo.getUsername())
            .email(userInfo.getEmail())
            .fullName(userInfo.getFullName())
            .phone(userInfo.getPhone()).role(userInfo.getRole())
            .createdAt(userInfo.getCreatedAt()).build())
        .toList();
    return Outbound.ok(userResponses);
  }

  /**
   * 更新密碼
   * 
   * @param request
   * @return
   */
  public Outbound updatePassword(ChangePswReq request) {
    UserInfo userInfo = userService
        .findUserByUsername(request.getUsername());
    userInfo.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userService.save(userInfo);
    return Outbound.ok("密碼更新成功");
  }

  /**
   * 登出使用者
   * 
   * @return
   */
  public Outbound logout() {
    return Outbound.ok("登出成功");
  }

}
