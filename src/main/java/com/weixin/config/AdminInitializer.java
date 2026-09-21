package com.weixin.config;

import com.weixin.domain.entity.SysUser;
import com.weixin.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (sysUserMapper.selectCount(null) > 0) {
            return;
        }
        SysUser user = new SysUser();
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("admin123"));
        user.setRealName("管理员");
        user.setCreateTime(LocalDateTime.now());
        sysUserMapper.insert(user);
        log.info("已创建默认管理员 admin");
    }
}
