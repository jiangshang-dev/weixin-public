package com.weixin.controller;

import com.weixin.common.R;
import com.weixin.domain.entity.WxArticle;
import com.weixin.service.AuthService;
import com.weixin.service.WxArticleService;
import com.weixin.utils.IpUtils;
import com.weixin.utils.UserUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;
    private final WxArticleService wxArticleService;

    @PostMapping("/admin/login")
    public R<Map<String, Object>> login(@RequestBody LoginRequest request) {
        return R.ok(authService.login(request.getUsername(), request.getPassword()));
    }

    @GetMapping("/admin/me")
    public R<Map<String, Object>> me(HttpServletRequest request) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("username", UserUtil.getUser() == null ? "" : UserUtil.getUser().getUsername());
        data.put("realName", UserUtil.getUser() == null ? "" : UserUtil.getUser().getRealName());
        data.put("ip", IpUtils.getIpAddr(request));
        return R.ok(data);
    }

    @GetMapping("/admin/articles")
    public R<List<WxArticle>> articles() {
        return R.ok(wxArticleService.latest());
    }

    @PostMapping("/admin/wechat/image")
    public R<Map<String, String>> image(@RequestParam("file") MultipartFile file) {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("url", wxArticleService.uploadContentImage(file));
        return R.ok(data);
    }

    @PostMapping("/admin/draft")
    public R<WxArticle> draft(@RequestParam String title,
                              @RequestParam(required = false) String author,
                              @RequestParam(required = false) String digest,
                              @RequestParam String content,
                              @RequestParam("cover") MultipartFile cover) {
        return R.ok(wxArticleService.publishDraft(title, author, digest, content, cover));
    }

    @GetMapping("/open/ping")
    public R<String> ping(HttpServletRequest request) {
        return R.ok("ok " + IpUtils.getIpAddr(request));
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
