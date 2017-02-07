package org.cayman.service;

import lombok.extern.slf4j.Slf4j;
import org.cayman.config.SpringApplicationConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringApplicationConfig.class, loader = SpringApplicationContextLoader.class)
public class GoogleServiceTest {
    @Autowired
    private GoogleService googleService;

    @Test
    public void download() throws Exception {
        String downloadPdfDir = "/Users/macuser/Desktop/Cayman2/JavaProjects/dev-lib/dev-lib-file-service/work/books/download/";
        String bookName = "Manning - Java Reflection In Action [2005]";
        String id = "0BzHFqXu4Rw8Yel9HLWNBbDhoR1k";
        String path = googleService.download(bookName, id);
        log.info("Downloaded file: " + path);
        assertTrue(path.equals(downloadPdfDir + bookName + ".pdf"));
    }

    @Test
    public void upload() throws Exception {
        String path = "/Users/macuser/Desktop/Cayman2/JavaProjects/dev-lib/dev-lib-file-service/work/books/upload/Jamie Allen - Effective Akka - 2013.pdf";
        String id = googleService.upload(path);
        assertTrue(!id.isEmpty());
        log.info("Uploaded file id = " + id);
    }

    @Test
    public void delete() throws Exception {
        String id = "0BzHFqXu4Rw8YMVFiS0tfMVg0Zkk";
        boolean result = googleService.delete(id);
        assertTrue(result);
    }
}