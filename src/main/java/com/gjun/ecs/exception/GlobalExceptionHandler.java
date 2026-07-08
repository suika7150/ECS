package com.gjun.ecs.exception;

import com.gjun.ecs.dto.response.Outbound;
import com.gjun.ecs.enums.ResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApplicationException.class)
  public ResponseEntity<Outbound> handleApplication(ApplicationException ex) {
    Outbound outbound = Outbound.builder().code(ex.getCode()).msg(ex.getMsg()).build();
    return ResponseEntity.status(HttpStatus.OK).body(outbound);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Outbound> handleException(Exception ex) {
    Outbound outbound = Outbound.error(ResultCode.FAIL, ex.getMessage());
    return ResponseEntity.status(HttpStatus.OK).body(outbound);
  }
}
