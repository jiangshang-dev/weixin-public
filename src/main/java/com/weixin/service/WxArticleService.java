package com.weixin.service;

import cn.hutool.core.util.StrUtil;
import com.weixin.domain.entity.WxArticle;
import com.weixin.mapper.WxArticleMapper;
import com.weixin.utils.AssertUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WxArticleService {

    private final WxArticleMapper wxArticleMapper;
    private final WechatMpService wechatMpService;

    public List<WxArticle> latest() {
        return wxArticleMapper.selectList(com.baomidou.mybatisplus.core.toolkit.Wrappers.lambdaQuery(WxArticle.class)
                .orderByDesc(WxArticle::getId)
                .last("limit 50"));
    }

    public WxArticle getById(Long id) {
        AssertUtil.notNull(id, "文章不存在");
        WxArticle article = wxArticleMapper.selectById(id);
        AssertUtil.notNull(article, "文章不存在");
        return article;
    }

    public String uploadContentImage(MultipartFile file) {
        AssertUtil.notNull(file, "请选择图片");
        AssertUtil.isTrue(!file.isEmpty(), "请选择图片");
        return wechatMpService.uploadContentImage(file);
    }

    public WxArticle publishDraft(String title, String author, String digest, String content, String contentMd, MultipartFile cover) {
        String name = StrUtil.trim(title);
        String html = StrUtil.trim(content);
        AssertUtil.isNotBlank(name, "请填写标题");
        AssertUtil.isTrue(name.length() <= 64, "标题不能超过 64 个字");
        AssertUtil.isNotBlank(html, "请填写正文");
        AssertUtil.isTrue(html.length() <= 20000, "正文不能超过 2 万字符");
        AssertUtil.notNull(cover, "请上传封面");
        AssertUtil.isTrue(!cover.isEmpty(), "请上传封面");
        String authorName = StrUtil.sub(StrUtil.trim(author), 0, 16);
        String summary = StrUtil.sub(StrUtil.blankToDefault(StrUtil.trim(digest), StrUtil.sub(html.replaceAll("<[^>]+>", ""), 0, 54)), 0, 120);

        WxArticle article = new WxArticle();
        article.setTitle(name);
        article.setAuthor(authorName);
        article.setDigest(summary);
        article.setContent(html);
        article.setContentMd(StrUtil.nullToDefault(contentMd, ""));
        article.setStatus(0);
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        wxArticleMapper.insert(article);
        try {
            String thumbMediaId = wechatMpService.uploadCover(cover);
            String draftMediaId = wechatMpService.addDraft(name, authorName, summary, html, thumbMediaId);
            article.setThumbMediaId(thumbMediaId);
            article.setDraftMediaId(draftMediaId);
            article.setStatus(1);
            article.setSyncMessage("已写入公众号草稿箱");
        } catch (RuntimeException ex) {
            article.setStatus(2);
            article.setSyncMessage(StrUtil.sub(ex.getMessage(), 0, 500));
            article.setUpdateTime(LocalDateTime.now());
            wxArticleMapper.updateById(article);
            throw ex;
        }
        article.setUpdateTime(LocalDateTime.now());
        wxArticleMapper.updateById(article);
        return article;
    }
}
