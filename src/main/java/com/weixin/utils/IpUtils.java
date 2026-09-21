package com.weixin.utils;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class IpUtils {

    private IpUtils() {
    }

    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String ip = first(
                request.getHeader("x-forwarded-for"),
                request.getHeader("X-Real-IP"),
                request.getRemoteAddr()
        );
        if (StrUtil.isBlank(ip)) {
            return "";
        }
        if (ip.contains(",")) {
            ip = StrUtil.trim(ip.split(",")[0]);
        }
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }
        return Validator.isIpv4(ip) || Validator.isIpv6(ip) ? ip : StrUtil.sub(ip, 0, 64);
    }

    private static String first(String... values) {
        for (String value : values) {
            if (StrUtil.isNotBlank(value) && !"unknown".equalsIgnoreCase(value)) {
                return value;
            }
        }
        return "";
    }
}
