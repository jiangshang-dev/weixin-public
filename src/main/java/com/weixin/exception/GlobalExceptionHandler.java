package com.weixin.exception;

import cn.hutool.core.util.StrUtil;
import com.weixin.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public R<Void> handleService(ServiceException ex) {
        return R.fail(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public R<Void> handleUpload(MaxUploadSizeExceededException ex) {
        return R.fail(400, "图片不能超过 10MB");
    }

    @ExceptionHandler(Exception.class)
    public R<Void> handleOther(Exception ex) {
        log.error("未处理异常", ex);
        return R.fail(500, StrUtil.blankToDefault(ex.getMessage(), "服务异常"));
    }
}
