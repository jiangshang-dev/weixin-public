package com.weixin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wx_ai_record")
public class AiRecord {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String industry;
    private String model;
    private String requirement;
    private String contentMd;
    /** 0 生成中 1 已完成 2 失败 */
    private Integer status;
    private String errorMessage;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
