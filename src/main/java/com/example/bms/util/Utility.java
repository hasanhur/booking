package com.example.bms.util;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.security.Principal;

import org.springframework.web.multipart.MultipartFile;

public class Utility {
    static String getRandomString(int n) {
        String chars = "abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            sb.append(chars.charAt((int)(chars.length() * Math.random())));
        }

        return sb.toString();
    }

    public static String saveImage(MultipartFile file, String pathName, String formatName) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes());
        BufferedImage image = ImageIO.read(inputStream);
        String path = getRandomString(64);
        ImageIO.write(image, formatName, new File("public/img/shop/" + path + "." + formatName));
        return path;
    }
}
