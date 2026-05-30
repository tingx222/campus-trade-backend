package com.campus.trade.controller;

import com.campus.trade.common.ResultVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${file.upload.path:./uploads/}")
    private String uploadPath;

    @PostMapping("/upload")
    public ResultVO<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResultVO.fail("请选择要上传的文件");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResultVO.fail("只能上传图片文件");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResultVO.fail("图片大小不能超过5MB");
        }
        try {
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String fileName = date + "_" + uuid + ext;
            File dest = new File(uploadPath + fileName);
            file.transferTo(dest);
            String url = "/uploads/" + fileName;
            return ResultVO.success("上传成功", url);
        } catch (IOException e) {
            return ResultVO.fail("上传失败：" + e.getMessage());
        }
    }
}