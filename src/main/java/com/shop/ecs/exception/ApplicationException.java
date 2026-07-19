package com.shop.ecs.exception;

import com.shop.ecs.constant.ResultCode;

import lombok.Getter;

@Getter
public class ApplicationException extends Exception {

	private static final long serialVersionUID = 1L;

	private final String code;
	private final String msg;

	public ApplicationException(ResultCode resultCode) {
		super(resultCode.getMsg());
		this.code = resultCode.getCode();
		this.msg = resultCode.getMsg();
	}
}
