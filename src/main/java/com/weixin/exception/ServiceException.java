package com.weixin.exception;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private final int code;
    private final String message;

    public ServiceException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }

    public ServiceException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ServiceException(String message, Object... params) {
        super(StrUtil.format(message, params));
        this.code = 500;
        this.message = StrUtil.format(message, params);
    }
}
