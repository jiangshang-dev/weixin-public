package com.weixin.service;

import cn.hutool.core.util.StrUtil;
import com.weixin.domain.entity.SysUser;
import com.weixin.mapper.SysUserMapper;
import com.weixin.security.TokenService;
import com.weixin.utils.AssertUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public Map<String, Object> login(String username, String password) {
        AssertUtil.isNotBlank(username, "请输入账号");
        AssertUtil.isNotBlank(password, "请输入密码");
        SysUser user = sysUserMapper.selectOne(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getUsername, StrUtil.trim(username)));
        AssertUtil.notNull(user, "账号或密码错误");
        AssertUtil.isTrue(passwordEncoder.matches(password, user.getPassword()), "账号或密码错误");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", tokenService.createToken(user));
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        return result;
    }
}
