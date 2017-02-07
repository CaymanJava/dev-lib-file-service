package org.cayman.service;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.cayman.exception.DownloadFileException;
import org.cayman.exception.UploadFileException;
import org.cayman.google.GoogleDriveHolder;
import org.cayman.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Service
@Data
@Slf4j
public class GoogleService {
    private static final String MIME_TYPE_PDF = "application/pdf";

    private final GoogleDriveHolder driveHolder;
    private final Constants constants;

    @Autowired
    public GoogleService(GoogleDriveHolder driveHolder, Constants constants) {
        this.driveHolder = driveHolder;
        this.constants = constants;
    }

    String download(String bookName, String id) {
        log.info("Start download file (id = " + id + ", bookName = " + bookName + ").");
        String path = constants.getDownloadPdfDir() + bookName;
        ByteArrayOutputStream byteOutputStream = downloadFileFromGoogleDrive(id);
        writeByteArrayOutPutStreamToFile(path, byteOutputStream);
        return path;
    }

    String upload(String path) {
        log.info("Start upload file " + path);
        java.io.File filePath = new java.io.File(path);
        File fileMetadata = new File();
        fileMetadata.setName(filePath.getName());
        fileMetadata.setMimeType(MIME_TYPE_PDF);

        FileContent mediaContent = new FileContent(MIME_TYPE_PDF, filePath);
        return uploadFileToGoogleDrive(fileMetadata, mediaContent);
    }

    boolean delete(String id) {
        Drive service = driveHolder.getDriveService();
        try {
            service.files().delete(id).execute();
            return true;
        } catch (IOException e) {
            log.warn("Exception while deleting file (id = " + id + ").");
            return false;
        }
    }

    private String uploadFileToGoogleDrive(File fileMetadata, FileContent mediaContent) {
        try {
            Drive drive = driveHolder.getDriveService();
            File uploadFile = drive.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
            return uploadFile.getId();
        } catch (IOException e) {
            log.error("Exception while uploading file to Google Drive.");
            throw new UploadFileException(e.getMessage());
        }
    }

    private void writeByteArrayOutPutStreamToFile(String path, ByteArrayOutputStream byteOutputStream) {
        try (OutputStream outputStream = new FileOutputStream(path)) {
            byteOutputStream.writeTo(outputStream);
        } catch (Exception e) {
            log.error("Exception while writing bytes to file " + path);
            throw new DownloadFileException(e.getMessage());
        }
    }

    private ByteArrayOutputStream downloadFileFromGoogleDrive(String id) {
        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            Drive drive = driveHolder.getDriveService();
            drive.files().get(id).executeMediaAndDownloadTo(byteOutputStream);
            return byteOutputStream;
        } catch (Exception e) {
            log.error("Exception while downloading file (id = " + id + ") from Google Drive.", e);
            throw new DownloadFileException(e.getMessage());
        }
    }
}
