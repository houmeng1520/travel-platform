package com.travel;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        // 创建 BCrypt 加密器
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 要加密的密码
        String rawPassword = "houmeng999";

        // 加密
        String encodedPassword = encoder.encode(rawPassword);

        // 输出加密结果
        System.out.println("原始密码： " + rawPassword);
        System.out.println("加密后的密码： " + encodedPassword);
    }
}