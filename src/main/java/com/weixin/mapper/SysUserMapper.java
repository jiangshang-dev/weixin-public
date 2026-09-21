package com.weixin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weixin.domain.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
