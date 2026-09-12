package com.travel.controller;

import com.travel.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UploadController {

    /**
     * 单张图片上传
     * POST /api/upload/image
     * 参数：file（图片文件）
     * 返回：图片访问URL
     */
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = saveImage(file);
            return Result.success(url);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 多张图片上传
     * POST /api/upload/images
     * 参数：files（多个图片文件）
     * 返回：图片访问URL列表
     */
    @PostMapping("/images")
    public Result<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        try {
            List<String> urls = new ArrayList<>();
            for (MultipartFile file : files) {
                String url = saveImage(file);
                urls.add(url);
            }
            return Result.success(urls);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 保存图片到磁盘
     * @param file 上传的文件
     * @return 图片访问路径
     */
    private String saveImage(MultipartFile file) throws Exception {
        // 1. 获取项目根目录
        String basePath = System.getProperty("user.dir");
        // 例如：D:/study project/travel-backend

        // 2. 构建上传目录
        String uploadDir = basePath + "/uploads/images/";
        // 例如：D:/study project/travel-backend/uploads/images/

        // 3. 创建目录（如果不存在）
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();  // mkdirs() 创建多级目录
        }

        // 4. 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        // 例如：photo.jpg

        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 例如：.jpg
        }

        // 使用 UUID + 时间戳 防止文件名重复
        String filename = UUID.randomUUID().toString().replace("-", "") + "_" + System.currentTimeMillis() + ext;
        // 例如：a1b2c3d4e5f6_1234567890.jpg

        // 5. 保存文件到磁盘
        File dest = new File(uploadDir + filename);
        file.transferTo(dest);  // Spring 提供的文件保存方法

        // 6. 返回访问路径
        return "/uploads/images/" + filename;
        // 例如：/uploads/images/a1b2c3d4e5f6_1234567890.jpg
    }
}