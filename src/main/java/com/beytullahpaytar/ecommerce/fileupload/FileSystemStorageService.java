package com.beytullahpaytar.ecommerce.fileupload;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService {

    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp");

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {

        if(properties.getLocation().trim().isEmpty()){
            throw new StorageException("File upload location can not be Empty.");
        }

        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new StorageException("Image must not be larger than 10 MB.");
            }

            String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
            int extensionIndex = originalFilename.lastIndexOf('.');
            if (extensionIndex < 0) {
                throw new StorageException("Image must have a supported file extension.");
            }

            String extension = originalFilename.substring(extensionIndex).toLowerCase(Locale.ROOT);
            if (!ALLOWED_EXTENSIONS.contains(extension) || !ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
                throw new StorageException("Only JPG, PNG, and WebP images are allowed.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                byte[] header = inputStream.readNBytes(12);
                if (!hasValidImageSignature(header, extension)) {
                    throw new StorageException("Uploaded file is not a valid image.");
                }
            }

            String randomName = "tempFile" + UUID.randomUUID().toString().replace("-", "") + extension;

            Path destinationFile = this.rootLocation.resolve(
                            Paths.get(Objects.requireNonNull(randomName)))
                    .normalize().toAbsolutePath();


            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new StorageException(
                        "Cannot store file outside current directory.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
            return randomName;
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file. ", e);
        }
    }

    private boolean hasValidImageSignature(byte[] header, String extension) {
        if (extension.equals(".jpg") || extension.equals(".jpeg")) {
            return header.length >= 3
                    && (header[0] & 0xff) == 0xff
                    && (header[1] & 0xff) == 0xd8
                    && (header[2] & 0xff) == 0xff;
        }
        if (extension.equals(".png")) {
            byte[] png = {(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};
            if (header.length < png.length) return false;
            for (int i = 0; i < png.length; i++) {
                if (header[i] != png[i]) return false;
            }
            return true;
        }
        return extension.equals(".webp")
                && header.length >= 12
                && header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
                && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P';
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    public void delete(String filename) {
        Path filePath = load(filename);
        if (Files.exists(filePath)) {
            try {
                Files.delete(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void deleteTempFiles() {
        try {
            Files.walk(rootLocation)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        if (file.getFileName().toString().startsWith("tempFile")) {
                            try {
                                Files.delete(file);
                            } catch (IOException e) {
                                System.err.println("Error deleting file: " + file);
                            }
                        }
                    });
        } catch (IOException e) {
            System.err.println("Error accessing the directory to delete temp files.");
        }
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
