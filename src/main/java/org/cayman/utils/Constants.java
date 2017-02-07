package org.cayman.utils;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class Constants {
    @Value("${img.full.path}")
    private String imgFullPath;

    @Value("${img.short.path}")
    private String imgShortPath;

    @Value("${download.pdf.dir}")
    private String downloadPdfDir;

    @Value("${upload.pdf.dir}")
    private String uploadPdfDir;

    @Value("${google.credential.path}")
    private String credentialPath;

    @Value("${google.application.name}")
    private String applicationName;

    @Value("${google.data.store.dir.path}")
    private String dataStorePath;
}
