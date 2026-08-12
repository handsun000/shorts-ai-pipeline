package com.workfluffs.shortsai.domain.asset.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {
    
    private final String uploadDir = System.getProperty("user.dir") + "/uploads";
    
    public FileStorageService() {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if(created) {
                log.info("Created upload directory: {}", uploadDir);
            }
        }
    }
    
    public String saveFile(byte[] fileData, String extension) throws IOException {
        String fileName = UUID.randomUUID().toString() + extension;
        Path path = Paths.get(uploadDir, fileName);
        Files.write(path, fileData);
        return path.toAbsolutePath().toString();
    }
}
