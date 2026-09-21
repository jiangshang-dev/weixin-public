package com.weixin.utils;

import cn.hutool.core.util.StrUtil;
import com.weixin.exception.ServiceException;

public final class AssertUtil {

    private AssertUtil() {
    }

    public static void isTrue(boolean expression, String message, Object... params) {
        if (!expression) {
            throw new ServiceException(message, params);
        }
    }

    public static void notNull(Object value, String message) {
        isTrue(value != null, message);
    }

    public static void isNotBlank(String content, String message) {
        if (StrUtil.isBlank(content)) {
            throw new ServiceException(message);
        }
    }
}
