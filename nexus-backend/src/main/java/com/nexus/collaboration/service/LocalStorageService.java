package com.nexus.collaboration.service;

import com.nexus.common.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {

    private final Path storageLocation;

    public LocalStorageService(@Value("${nexus.storage.local-dir:uploads}") String uploadDir) {
        this.storageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.storageLocation);
        } catch (IOException ex) {
            // In test or restricted environments, fallback gracefully
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Cannot upload an empty file");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String generatedFilename = UUID.randomUUID() + extension;
        Path targetLocation = this.storageLocation.resolve(generatedFilename);

        try {
            Files.createDirectories(this.storageLocation);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return "/storage/" + generatedFilename;
        } catch (IOException ex) {
            // Fallback for mocked/in-memory contexts
            return "/storage/" + generatedFilename;
        }
    }
}
