package com.shop.ecs.service;

import com.shop.ecs.common.result.Outbound;
import com.shop.ecs.constant.OtpTypeEnum;
import com.shop.ecs.constant.ResultCode;
import com.shop.ecs.dto.request.ChangePswReq;
import com.shop.ecs.dto.request.LoginReq;
import com.shop.ecs.dto.request.RegisterReq;
import com.shop.ecs.dto.request.UpdateUserReq;
import com.shop.ecs.dto.request.VerifyLoginCodeReq;
import com.shop.ecs.dto.response.LoginResp;
import com.shop.ecs.dto.response.UserResp;
import com.shop.ecs.entity.EmailOtpEntity;
import com.shop.ecs.entity.UserEntity;
import com.shop.ecs.exception.ApplicationException;
import com.shop.ecs.repository.EmailOtpRepository;
import com.shop.ecs.utils.JwtUtil;

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

  // 發送 OTP
  public Outbound sendEmailCode(String email, OtpTypeEnum type) throws ApplicationException {

    if (email == null || email.isBlank()) {
      throw new ApplicationException(ResultCode.EMAIL_EMPTY);
    }

    boolean exists = userService.existsByEmail(email);

    if (type == OtpTypeEnum.REGISTER && exists) {
      throw new ApplicationException(ResultCode.EMAIL_IS_EXIST);
    }

    if (type == OtpTypeEnum.LOGIN && !exists) {
      throw new ApplicationException(ResultCode.USER_IS_NOT_EXIST);
    }

    if (type == OtpTypeEnum.RESET && !exists) {
      throw new ApplicationException(ResultCode.USER_IS_NOT_EXIST);
    }

    // 產生 OTP
    String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));

    // 過期時間（5分鐘）
    LocalDateTime expireTime = LocalDateTime.now().plusMinutes(5);
    EmailOtpEntity otp = new EmailOtpEntity();
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
    mailSender.send(message);

    return Outbound.ok("驗證碼已寄出");
  }

  // 驗證 OTP
  public Outbound verifyEmailCode(String email, String code, OtpTypeEnum type)
      throws ApplicationException {

    LocalDateTime now = LocalDateTime.now();

    EmailOtpEntity otp = emailOtpRepository
        .findTopByEmailAndTypeAndUsedFalseAndExpireTimeAfterOrderByIdDesc(
            email, type, now)
        .orElseThrow(() -> new ApplicationException(ResultCode.OTP_NOT_FOUND));

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

  // 使用者註冊
  public Outbound register(RegisterReq req) throws ApplicationException {
    // 帳號與 Email 重複檢查
    if (userService.existsByUsername(req.getUsername())) {
      throw new ApplicationException(ResultCode.ACCOUNT_IS_EXIST);
    }

    if (userService.existsByEmail(req.getEmail())) {
      throw new ApplicationException(ResultCode.EMAIL_IS_EXIST);
    }

    UserEntity userInfo = UserEntity.builder()
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

  // 使用者登入
  public Outbound login(LoginReq req) throws ApplicationException {

    UserEntity userInfo = userService.findUserByUsernameOrEmail(req.getUsername());

    if (userInfo == null) {
      throw new ApplicationException(ResultCode.USER_IS_NOT_EXIST);
    }

    if (!passwordEncoder.matches(req.getPassword(), userInfo.getPassword())) {
      throw new ApplicationException(ResultCode.PASSWORD_NOT_MATCH);
    }

    sendEmailCode(userInfo.getEmail(), OtpTypeEnum.LOGIN);

    return Outbound.ok("登入驗證碼已寄出");
  }

  // 2FA 驗證
  public Outbound verifyLoginCode(VerifyLoginCodeReq req) throws ApplicationException {

    UserEntity userInfo = userService.findUserByUsernameOrEmail(req.getUsername());

    if (userInfo == null) {
      throw new ApplicationException(ResultCode.USER_IS_NOT_EXIST);
    }

    verifyEmailCode(userInfo.getEmail(), req.getCode(), OtpTypeEnum.LOGIN);

    // 生成 Token
    String token = jwtUtil.generateToken(userInfo, req.isRememberMe());
    LoginResp resp = LoginResp.builder()
        .token(token)
        .role(userInfo.getRole())
        .username(userInfo.getUsername())
        .fullName(userInfo.getFullName())
        .rememberMe(req.isRememberMe())
        .build();

    return Outbound.ok(resp);
  }

  // 取得使用者資料
  public Outbound getCurrentUser(String username) throws ApplicationException {

    UserEntity userInfo = userService.findUserByUsername(username);

    if (userInfo == null) {
      throw new ApplicationException(ResultCode.USER_IS_NOT_EXIST);
    }

    UserResp resp = UserResp.builder()
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

    return Outbound.ok(resp);
  }

  // 更新使用者資料
  public Outbound updateUserProfile(String username, UpdateUserReq request) throws Exception {
    UserEntity userInfo = userService.findUserByUsername(username);

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

    UserEntity upUserInfo = userService.save(userInfo);

    UserResp resp = UserResp.builder()
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

  // 更新密碼
  public Outbound updatePassword(ChangePswReq request) throws ApplicationException {

    UserEntity userInfo = userService.findUserByUsername(request.getUsername());

    if (userInfo == null) {
      throw new ApplicationException(ResultCode.USER_IS_NOT_EXIST);
    }

    userInfo.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userService.save(userInfo);
    return Outbound.ok("密碼更新成功");
  }
}
