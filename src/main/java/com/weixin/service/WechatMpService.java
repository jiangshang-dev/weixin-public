package com.weixin.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.weixin.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Slf4j
@Service
public class WechatMpService {

    private static final String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/stable_token";

    @Value("${weixin.mp.app-id}")
    private String appId;

    @Value("${weixin.mp.app-secret}")
    private String appSecret;

    private volatile String cachedToken;
    private volatile long expireAt;

    public String accessToken() {
        if (StrUtil.isNotBlank(cachedToken) && System.currentTimeMillis() < expireAt) {
            return cachedToken;
        }
        JSONObject body = JSONUtil.createObj()
                .set("grant_type", "client_credential")
                .set("appid", appId)
                .set("secret", appSecret);
        JSONObject json = postJson(TOKEN_URL, body);
        cachedToken = json.getStr("access_token");
        long seconds = json.getLong("expires_in", 7200L);
        expireAt = System.currentTimeMillis() + Math.max(60, seconds - 120) * 1000;
        return cachedToken;
    }

    public String uploadContentImage(MultipartFile file) {
        JSONObject json = upload("https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=" + accessToken(), file);
        String url = json.getStr("url");
        if (StrUtil.isBlank(url)) {
            throw new ServiceException("微信没有返回图片地址");
        }
        return url;
    }

    public String uploadCover(MultipartFile file) {
        String url = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=" + accessToken() + "&type=image";
        JSONObject json = upload(url, file);
        String mediaId = json.getStr("media_id");
        if (StrUtil.isBlank(mediaId)) {
            throw new ServiceException("微信没有返回封面素材编号");
        }
        return mediaId;
    }

    public String addDraft(String title, String author, String digest, String content, String thumbMediaId) {
        JSONObject article = JSONUtil.createObj()
                .set("title", title)
                .set("author", StrUtil.blankToDefault(author, ""))
                .set("digest", StrUtil.blankToDefault(digest, ""))
                .set("content", content)
                .set("thumb_media_id", thumbMediaId)
                .set("need_open_comment", 0)
                .set("only_fans_can_comment", 0);
        JSONArray articles = JSONUtil.createArray();
        articles.add(article);
        JSONObject json = postJson("https://api.weixin.qq.com/cgi-bin/draft/add?access_token=" + accessToken(),
                JSONUtil.createObj().set("articles", articles));
        String mediaId = json.getStr("media_id");
        if (StrUtil.isBlank(mediaId)) {
            throw new ServiceException("微信没有返回草稿编号");
        }
        return mediaId;
    }

    private JSONObject upload(String url, MultipartFile file) {
        String original = StrUtil.blankToDefault(file.getOriginalFilename(), "image.jpg");
        String ext = FileUtil.extName(original);
        if (StrUtil.isBlank(ext)) {
            ext = "jpg";
        }
        File temp = FileUtil.file(FileUtil.getTmpDir(), IdUtil.fastSimpleUUID() + "." + ext);
        try {
            file.transferTo(temp);
            String body = HttpRequest.post(url).form("media", temp).timeout(30000).execute().body();
            return parse(body);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException("上传微信图片失败：{}", ex.getMessage());
        } finally {
            FileUtil.del(temp);
        }
    }

    private JSONObject postJson(String url, JSONObject payload) {
        String body = HttpRequest.post(url)
                .body(payload.toString())
                .contentType("application/json")
                .timeout(20000)
                .execute()
                .body();
        return parse(body);
    }

    private JSONObject parse(String body) {
        if (!JSONUtil.isTypeJSON(body)) {
            throw new ServiceException("微信返回无法识别：{}", StrUtil.sub(body, 0, 180));
        }
        JSONObject json = JSONUtil.parseObj(body);
        Integer errcode = json.getInt("errcode");
        if (errcode != null && errcode != 0) {
            throw new ServiceException(explain(errcode, json.getStr("errmsg")));
        }
        return json;
    }

    private String explain(int errcode, String errmsg) {
        if (errcode == 40164 || errcode == 61004) {
            return "服务器 IP 不在公众号白名单。请到微信开发者平台「公众号 - 基础信息 - 开发密钥」把当前公网 IP 加进去。原始错误：" + errmsg;
        }
        if (errcode == 48001) {
            return "这个公众号没有草稿接口权限，未认证的个人号经常会这样。原始错误：" + errmsg;
        }
        if (errcode == 40001 || errcode == 40013 || errcode == 40125) {
            return "AppID 或 AppSecret 不正确。原始错误：" + errmsg;
        }
        return "微信接口失败（" + errcode + "）：" + StrUtil.blankToDefault(errmsg, "未知错误");
    }
}
