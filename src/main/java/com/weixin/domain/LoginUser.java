package com.weixin.domain;

import lombok.Data;

@Data
public class LoginUser {

    private Long id;
    private String username;
    private String realName;
    private String role;
}
