package com.weixin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wx_article")
public class WxArticle {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String author;
    private String digest;
    private String content;
    private String thumbMediaId;
    private String draftMediaId;
    private Integer status;
    private String syncMessage;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
