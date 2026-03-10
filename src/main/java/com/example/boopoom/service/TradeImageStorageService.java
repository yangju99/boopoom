package com.example.boopoom.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
public class TradeImageStorageService {

    private final Path uploadRoot;

    public TradeImageStorageService(@Value("${boopoom.upload.dir:uploads}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public StoredImage store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalStateException("업로드할 이미지 파일이 비어 있습니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new IllegalStateException("이미지 파일만 업로드할 수 있습니다.");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() == null ? "image" : file.getOriginalFilename());
        String extension = extractExtension(originalFilename);
        String storageKey = UUID.randomUUID() + extension;

        try {
            Files.createDirectories(uploadRoot);
            Path destination = uploadRoot.resolve(storageKey);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new IllegalStateException("이미지 파일 저장에 실패했습니다.", e);
        }

        return new StoredImage(
                storageKey,
                "/uploads/" + storageKey,
                originalFilename,
                contentType,
                file.getSize()
        );
    }

    public StoredImage storeSeedFile(Path sourcePath) {
        if (sourcePath == null || !Files.exists(sourcePath)) {
            throw new IllegalStateException("시드 이미지 파일을 찾을 수 없습니다: " + sourcePath);
        }

        String originalFilename = sourcePath.getFileName().toString();
        String extension = extractExtension(originalFilename);
        String storageKey = "seed/" + UUID.randomUUID() + extension;

        Path destination = uploadRoot.resolve(storageKey).normalize();
        try {
            Files.createDirectories(destination.getParent());
            Files.copy(sourcePath, destination, StandardCopyOption.REPLACE_EXISTING);
            String contentType = Files.probeContentType(sourcePath);
            if (!StringUtils.hasText(contentType)) {
                contentType = guessContentType(extension);
            }
            return new StoredImage(
                    storageKey,
                    "/uploads/" + storageKey,
                    originalFilename,
                    contentType,
                    Files.size(destination)
            );
        } catch (IOException e) {
            throw new IllegalStateException("시드 이미지 파일 저장에 실패했습니다.", e);
        }
    }

    public void delete(String storageKey) {
        if (!StringUtils.hasText(storageKey)) {
            return;
        }
        try {
            Files.deleteIfExists(uploadRoot.resolve(storageKey));
        } catch (IOException e) {
            log.warn("파일 삭제 실패: storageKey={}", storageKey, e);
        }
    }

    private String extractExtension(String filename) {
        int index = filename.lastIndexOf('.');
        if (index < 0 || index == filename.length() - 1) {
            return "";
        }
        return filename.substring(index).toLowerCase(Locale.ROOT);
    }

    private String guessContentType(String extension) {
        if (".png".equals(extension)) {
            return "image/png";
        }
        if (".gif".equals(extension)) {
            return "image/gif";
        }
        if (".webp".equals(extension)) {
            return "image/webp";
        }
        return "image/jpeg";
    }

    public record StoredImage(
            String storageKey,
            String imageUrl,
            String originalFilename,
            String contentType,
            long sizeBytes
    ) {
    }
}
