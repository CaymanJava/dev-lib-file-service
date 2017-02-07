package org.cayman.google;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.cayman.exception.InitializeGoogleException;
import org.cayman.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;

@Component
@Data
@Slf4j
public class GoogleDriveHolder {

    private final Constants constants;

    private Credential credential;
    private HttpTransport httpTransport;
    private JsonFactory jsonFactory;
    private Drive drive;

    @Autowired
    public GoogleDriveHolder(Constants constants) {
        this.constants = constants;
    }

    public Drive getDriveService() {
        if (drive == null) {
            drive = new Drive.Builder(
                    getNetHttpTransport(), getJsonFactory(), credential)
                    .setApplicationName(constants.getApplicationName())
                    .build();
            log.info("Initialize Google Drive.");
        }
        return drive;
    }

    @PostConstruct
    private void initCredential() {
        InputStream in = getInputStream();
        JsonFactory jsonFactory = getJsonFactory();
        GoogleClientSecrets clientSecrets = getGoogleClientSecret(in, jsonFactory);
        GoogleAuthorizationCodeFlow flow = getGoogleAuthorizationCodeFlow(jsonFactory, clientSecrets);
        credential = getAuthorizationCodeInstalledApp(flow);
        log.info("Initialize credential.");
    }

    private JsonFactory getJsonFactory() {
        if (jsonFactory == null) {
            jsonFactory = JacksonFactory.getDefaultInstance();
            log.info("Initialize JSONFactory.");
        }
        return jsonFactory;
    }

    private Credential getAuthorizationCodeInstalledApp(GoogleAuthorizationCodeFlow flow) {
        try {
            return new AuthorizationCodeInstalledApp(
                    flow, new LocalServerReceiver()).authorize("user");
        } catch (IOException e) {
            throw new InitializeGoogleException(e.getMessage());
        }
    }

    private GoogleAuthorizationCodeFlow getGoogleAuthorizationCodeFlow(JsonFactory jsonFactory, GoogleClientSecrets clientSecrets) {
        try {
            return new GoogleAuthorizationCodeFlow.Builder(
                    getNetHttpTransport(),
                    jsonFactory,
                    clientSecrets,
                    new ArrayList<>(DriveScopes.all()))
                    .setDataStoreFactory(getFileDataStoreFactory())
                    .setAccessType("offline")
                    .build();
        } catch (IOException e) {
            throw new InitializeGoogleException(e.getMessage());
        }
    }

    private GoogleClientSecrets getGoogleClientSecret(InputStream in, JsonFactory jsonFactory)  {
        try {
            return GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));
        } catch (IOException e) {
            throw new InitializeGoogleException(e.getMessage());
        }
    }

    private FileDataStoreFactory getFileDataStoreFactory() {
        try {
            return new FileDataStoreFactory(new File(constants.getDataStorePath()));
        } catch (IOException e) {
            log.error("Cannot initialize FileDataStoreFactory", e);
            throw new InitializeGoogleException(e.getMessage());
        }
    }

    private HttpTransport getNetHttpTransport() {
        try {
            if (httpTransport == null) {
                httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            }
            return httpTransport;
        } catch (Exception e) {
            log.error("Cannot initialize GoogleNetHttpTransport", e);
            throw new InitializeGoogleException(e.getMessage());
        }
    }

    private InputStream getInputStream() {
        try {
            return new FileInputStream(new File(constants.getCredentialPath()));
        } catch (FileNotFoundException e) {
            log.error("Cannot read file with credential", e);
            throw new InitializeGoogleException(e.getMessage());
        }
    }
}
