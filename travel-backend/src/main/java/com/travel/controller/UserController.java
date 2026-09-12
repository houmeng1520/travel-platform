package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.travel.common.Result;
import com.travel.dto.LoginDTO;
import com.travel.dto.RegisterDTO;
import com.travel.entity.User;
import com.travel.service.impl.UserServiceImpl;
import com.travel.utils.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserServiceImpl userService;
    private final JwtUtils jwtUtils;

    // ===== 用户端接口 =====

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDTO dto) {
        try {
            return Result.success(userService.register(dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDTO dto) {
        try {
            return Result.success(userService.login(dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // ===== 头像上传 =====
    @PostMapping("/avatar/upload")
    public Result<String> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Long userId = getUserIdFromToken(authHeader);
        if (userId == null) {
            return Result.error("请先登录");
        }

        try {
            // 获取项目根目录
            String basePath = System.getProperty("user.dir");
            String uploadDir = basePath + "/uploads/avatars/";

            // 创建目录
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成文件名：userId_时间戳.扩展名
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = userId + "_" + System.currentTimeMillis() + ext;

            // 保存文件
            File dest = new File(uploadDir + filename);
            file.transferTo(dest);

            // 返回文件访问路径
            String avatarUrl = "/uploads/avatars/" + filename;

            // 更新用户头像
            User user = userService.getById(userId);
            if (user != null) {
                user.setAvatar(avatarUrl);
                userService.updateById(user);
            }

            return Result.success(avatarUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    // ===== 更新用户信息 =====
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody User user) {
        User existing = userService.getById(user.getId());
        if (existing == null) {
            return Result.error("用户不存在");
        }

        existing.setNickname(user.getNickname());
        existing.setPhone(user.getPhone());
        existing.setEmail(user.getEmail());
        existing.setGender(user.getGender());
        existing.setBirthday(user.getBirthday());

        return Result.success(userService.updateById(existing));
    }

    // ===== 管理后台接口 =====

    @GetMapping("/list")
    public Result<List<User>> getList() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(User::getCreateTime);
        return Result.success(userService.list(wrapper));
    }

    @GetMapping("/search")
    public Result<List<User>> search(@RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w ->
                    w.like(User::getUsername, keyword)
                            .or()
                            .like(User::getNickname, keyword)
                            .or()
                            .like(User::getEmail, keyword)
            );
        }
        wrapper.orderByDesc(User::getCreateTime);
        return Result.success(userService.list(wrapper));
    }

    @PutMapping("/status/{id}")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, id)
                .set(User::getStatus, status);
        boolean result = userService.update(wrapper);
        return Result.success(result);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(userService.removeById(id));
    }

    // ===== 工具方法 =====
    private Long getUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        try {
            String token = authHeader.substring(7);
            return jwtUtils.getUserIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }
}