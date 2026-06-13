package com.hammerdough.controller;


import com.hammerdough.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {
    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.prefix}")
    private String visitPrefix;


    @PostMapping("/upload/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request){
        if(file.isEmpty()){
            return Result.error("请选择上传图片");
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;

        File targetFile = new File(uploadPath, fileName);

        try {

            file.transferTo(targetFile);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("图片上传失败");
        }

        // 格式：http://localhost:8080/upload/avatar/xxx.png
        String baseUrl = request.getScheme() + "://"
                + request.getServerName() + ":"
                + request.getServerPort();
        String avatarUrl = baseUrl + visitPrefix + fileName;

        return Result.success(avatarUrl);
    }
}
