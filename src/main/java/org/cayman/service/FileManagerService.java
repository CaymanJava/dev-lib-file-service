package org.cayman.service;

import lombok.extern.slf4j.Slf4j;
import org.cayman.exception.ReadWriteFileException;
import org.cayman.utils.Constants;
import org.cayman.utils.Translit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@Slf4j
public class FileManagerService {

    private final Constants constants;

    @Autowired
    public FileManagerService(Constants constants) {
        this.constants = constants;
    }

    boolean deleteFile(String path) {
        try {
            File file = new File(path);
            return file.exists() && !file.isDirectory() && file.delete();
        } catch(Exception e) {
            log.warn("Exception while deleting file " + path);
            return false;
        }
    }

    String saveFile(MultipartFile file, String dir) {
        String fileName = Translit.cyr2lat(file.getOriginalFilename()
                .replaceAll(" ", "_")
                .replaceAll("\\?", ""));
        try (FileOutputStream outputStream = new FileOutputStream(dir + fileName)) {
            outputStream.write(file.getBytes());
            log.info("File with a name " + fileName + " was saved in a temporary directory.");
            return fileName;
        } catch (IOException e){
           log.warn("Exception while saving file " + file);
           throw new ReadWriteFileException(e.getMessage());
        }
    }

    @Scheduled(fixedRateString = "${delay.to.delete.old.files}")
    public void deleteOldDownloadedFiles() {
        log.info("Start deleting old files in downloads directory.");
        File dirWithOldFiles = new File(constants.getDownloadPdfDir());
        if (dirWithOldFiles.isDirectory()) {
            String[] paths = dirWithOldFiles.list();
            if (paths == null || paths.length == 0) return;
            for (String path : paths) {
                path = constants.getDownloadPdfDir() + path;
                LocalDateTime createdDateTime = getCreatedDateTime(path);
                if (createdDateTime != null && LocalDateTime.now().minusHours(1).isAfter(createdDateTime)) {
                    deleteFile(path);
                    log.info("Deleted file " + path);
                }
            }
        }
    }

    private LocalDateTime getCreatedDateTime(String filePath) {
        if (filePath == null || filePath.isEmpty()) return null;
        Path path = Paths.get(filePath);
        try {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            FileTime creationTime = attributes.creationTime();
            return LocalDateTime.ofInstant(creationTime.toInstant(), ZoneOffset.UTC);
        } catch(Exception e) {
            log.warn("Exception while getting creation time of " );
            return null;
        }
    }
}
