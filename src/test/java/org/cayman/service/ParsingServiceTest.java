package org.cayman.service;

import lombok.extern.slf4j.Slf4j;
import org.cayman.config.SpringApplicationConfig;
import org.cayman.dto.PdfParseDto;
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
public class ParsingServiceTest {
    @Autowired
    private ParsingService parsingService;


    @Test
    public void parsePdfFile() throws Exception {
        String path = "/Users/macuser/Desktop/Cayman2/JavaProjects/dev-lib/dev-lib-file-service/work/books/download/Manning - Java Reflection In Action [2005].pdf";
        String name = "Manning - Java Reflection In Action [2005]";
        PdfParseDto pdfParseDto = parsingService.parsePdfFile(path, name, 1);
        assertTrue(pdfParseDto.getImgPath().equals("/work/img/Manning-JavaReflectionInAction[2005].jpg"));
        log.info("Path = " + pdfParseDto.getImgPath());
        log.info("Page count = " + pdfParseDto.getPageCount());
    }
}