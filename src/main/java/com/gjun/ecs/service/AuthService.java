package com.gjun.ecs.service;

import com.gjun.ecs.dto.request.ChangePswReq;
import com.gjun.ecs.dto.request.LoginReq;
import com.gjun.ecs.dto.request.RegisterReq;
import com.gjun.ecs.dto.request.UpdateUserReq;
import com.gjun.ecs.dto.response.LoginResp;
import com.gjun.ecs.dto.response.Outbound;
import com.gjun.ecs.dto.response.UserResponse;
import com.gjun.ecs.entity.EmailOtp;
import com.gjun.ecs.entity.UserInfo;
import com.gjun.ecs.enums.ResultCode;
import com.gjun.ecs.enums.OtpType;
import com.gjun.ecs.exception.ApplicationException;
import com.gjun.ecs.repository.EmailOtpRepository;
import com.gjun.ecs.utils.JwtUtil;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  @Autowired
  private UserService userService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private EmailOtpRepository emailOtpRepository;

  @Autowired
  private JavaMailSender mailSender;

  /**
   * 發送 OTP
   *
   * @param req
   * @return
   */
  public Outbound sendEmailCode(String email, OtpType type) throws ApplicationException {

    if (email == null || email.isBlank()) {
      throw new ApplicationException(ResultCode.EMAIL_EMPTY);
    }

    boolean exists = userService.existsByEmail(email);

    // 規則檢查
    if (type == OtpType.REGISTER && exists) {
      throw new ApplicationException(ResultCode.EMAIL_IS_EXIST);
    }

    if (type == OtpType.RESET && !exists) {
      throw new ApplicationException(ResultCode.USER_IS_NOT_EXIST);
    }

    // 產生 OTP
    String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));

    // 過期時間（5分鐘）
    LocalDateTime expireTime = LocalDateTime.now().plusMinutes(5);

    EmailOtp otp = new EmailOtp();
    otp.setEmail(email);
    otp.setCode(code);
    otp.setType(type);
    otp.setExpireTime(expireTime);
    otp.setUsed(false);

    emailOtpRepository.save(otp);

    // 寄信
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(email);
    message.setSubject("ECS 驗證碼");
    message.setText("你的驗證碼是：" + code + "（5分鐘內有效）");

    System.out.println("EMAIL = " + email);
    System.out.println("TYPE = " + type);
    mailSender.send(message);

    return Outbound.ok("驗證碼已寄出");
  }

  /**
   * 驗證 OTP
   *
   * @param req
   * @return
   */
  public Outbound verifyEmailCode(String email, String code, OtpType type)
      throws ApplicationException {

    LocalDateTime now = LocalDateTime.now();

    EmailOtp otp = emailOtpRepository
        .findTopByEmailAndTypeAndUsedFalseAndExpireTimeAfterOrderByIdDesc(
            email, type, now)
        .orElseThrow(() -> new ApplicationException(ResultCode.OTP_NOT_FOUND));

    System.out.println("OTP ID = " + otp.getId());
    System.out.println("OTP CODE = " + otp.getCode());
    System.out.println("TYPE = " + type);

    // 是否已使用
    if (Boolean.TRUE.equals(otp.getUsed())) {
      throw new ApplicationException(ResultCode.OTP_ALREADY_USED);
    }

    // 是否過期
    if (otp.getExpireTime().isBefore(LocalDateTime.now())) {
      throw new ApplicationException(ResultCode.OTP_EXPIRED);
    }

    // code 是否正確
    if (!otp.getCode().equals(code)) {
      throw new ApplicationException(ResultCode.OTP_INVALID);
    }

    // 設為已使用
    otp.setUsed(true);
    emailOtpRepository.save(otp);

    return Outbound.ok("驗證成功");
  }

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

    // OTP 驗證
    verifyEmailCode(
        req.getEmail(),
        req.getSmsCode(),
        OtpType.REGISTER);

    UserInfo userInfo = UserInfo.builder()
        .username(req.getUsername())
        .password(passwordEncoder.encode(req.getPassword()))
        .email(req.getEmail())
        .fullName(req.getFullName())
        .phone(req.getPhone())
        .gender(req.getGender())
        .birthday(req.getBirthday())
        .role("USER")
        .build();

    userService.save(userInfo);
    return Outbound.ok("註冊成功");
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

    if (!passwordEncoder.matches(req.getPassword(), userInfo.getPassword())) {
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
  public Outbound findUserByUsername(String username) throws ApplicationException {

    UserInfo userInfo = userService.findUserByUsername(username);

    if (userInfo == null) {
      throw new ApplicationException(ResultCode.USER_IS_NOT_EXIST);
    }

    UserResponse userResponse = UserResponse.builder()
        .id(userInfo.getId())
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
  public Outbound updateUserProfile(String username, UpdateUserReq request) throws Exception {
    UserInfo userInfo = userService.findUserByUsername(username);

    if (userInfo == null) {
      throw new ApplicationException(ResultCode.USER_IS_NOT_EXIST);
    }

    userInfo.setFullName(request.getFullName());
    userInfo.setPhone(request.getPhone());

    if (!userInfo.getEmail().equals(request.getEmail())
        && userService.existsByEmail(request.getEmail())) {
      throw new ApplicationException(ResultCode.EMAIL_IS_EXIST);
    }
    userInfo.setEmail(request.getEmail());
    userInfo.setGender(request.getGender());
    userInfo.setBirthday(request.getBirthday());

    UserInfo upUserInfo = userService.save(userInfo);

    UserResponse resp = UserResponse.builder()
        .id(upUserInfo.getId())
        .username(upUserInfo.getUsername())
        .email(upUserInfo.getEmail())
        .fullName(upUserInfo.getFullName())
        .phone(upUserInfo.getPhone())
        .role(upUserInfo.getRole())
        .createdAt(upUserInfo.getCreatedAt())
        .gender(upUserInfo.getGender())
        .birthday(upUserInfo.getBirthday())
        .build();

    return Outbound.ok(resp);

  }

  /**
   * 取得所有使用者資料
   *
   * @return
   */
  public Outbound getCurrentUser() {
    List<UserInfo> users = userService.findAll();
    List<UserResponse> userResponses = users.stream()
        .map(
            userInfo -> UserResponse.builder()
                .id(userInfo.getId())
                .username(userInfo.getUsername())
                .email(userInfo.getEmail())
                .fullName(userInfo.getFullName())
                .phone(userInfo.getPhone())
                .role(userInfo.getRole())
                .createdAt(userInfo.getCreatedAt())
                .build())
        .toList();
    return Outbound.ok(userResponses);
  }

  /**
   * 更新密碼
   *
   * @param request
   * @return
   */
  public Outbound updatePassword(ChangePswReq request) throws ApplicationException {

    UserInfo userInfo = userService.findUserByUsername(request.getUsername());

    if (userInfo == null) {
      throw new ApplicationException(ResultCode.USER_IS_NOT_EXIST);
    }

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
