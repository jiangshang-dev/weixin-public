package com.weixin.security;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import com.weixin.domain.LoginUser;
import com.weixin.domain.entity.SysUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class TokenService {

    @Value("${weixin.jwt-secret}")
    private String secret;

    @Value("${weixin.jwt-expire-hours:168}")
    private int expireHours;

    public String createToken(SysUser user) {
        return JWT.create()
                .setPayload("uid", user.getId())
                .setPayload("username", user.getUsername())
                .setPayload("realName", StrUtil.blankToDefault(user.getRealName(), user.getUsername()))
                .setPayload("role", "ADMIN")
                .setExpiresAt(DateUtil.offsetHour(new Date(), expireHours))
                .setKey(secret.getBytes(StandardCharsets.UTF_8))
                .sign();
    }

    public LoginUser parse(String token) {
        if (StrUtil.isBlank(token)) {
            return null;
        }
        try {
            JWT jwt = JWT.of(token).setKey(secret.getBytes(StandardCharsets.UTF_8));
            if (!jwt.verify()) {
                return null;
            }
            LoginUser loginUser = new LoginUser();
            loginUser.setId(Convert.toLong(jwt.getPayload("uid")));
            loginUser.setUsername(Convert.toStr(jwt.getPayload("username")));
            loginUser.setRealName(Convert.toStr(jwt.getPayload("realName")));
            loginUser.setRole(StrUtil.blankToDefault(Convert.toStr(jwt.getPayload("role")), "ADMIN"));
            return loginUser;
        } catch (Exception ignored) {
            return null;
        }
    }
}
