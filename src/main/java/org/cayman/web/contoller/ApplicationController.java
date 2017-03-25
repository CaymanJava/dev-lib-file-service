package org.cayman.web.contoller;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.cayman.dto.PdfUploadDto;
import org.cayman.exception.ReadWriteFileException;
import org.cayman.service.ApplicationService;
import org.cayman.utils.Translit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping(value = "/")
@Slf4j
public class ApplicationController {

    private final ApplicationService applicationService;

    @Autowired
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    //TODO delete
    @RequestMapping(method = RequestMethod.GET)
    public String root(){
        return "root";
    }

    @RequestMapping(value = "pdf/upload", method = RequestMethod.POST)
    public @ResponseBody PdfUploadDto uploadPdf(@RequestParam("file") MultipartFile file,
                                                @RequestParam("page") Integer number) {
        return applicationService.uploadPdf(file, number);
    }

    @RequestMapping(value = "pdf/open", method = RequestMethod.GET)
    public void openPdf(@RequestParam("id") String id,
                        @RequestParam("name") String name,
                        HttpServletResponse response) throws UnsupportedEncodingException {
        if (!name.endsWith(".pdf")) {
            name = name + ".pdf";
        }
        String path = applicationService.downloadPdf(name, id);
        copyFileToHttpResponse(path, response);
        response.setContentType("application/pdf; charset=UTF-8");
    }

    @RequestMapping(value = "pdf/download", method = RequestMethod.GET)
    public HttpEntity<FileSystemResource> downloadPdf(@RequestParam("id") String id,
                                                      @RequestParam("name") String name) {
        if (!name.endsWith(".pdf") || !name.endsWith(".PDF")) {
            name = name + ".pdf";
        }

        String path = applicationService.downloadPdf(name, id);
        File file = new File(path);
        HttpHeaders header = getHttpHeaders(name, file);
        return new HttpEntity<>(new FileSystemResource(file),
                header);
    }

    @RequestMapping(value = "pdf/delete", method = RequestMethod.GET)
    public @ResponseBody Boolean deletePdf(@RequestParam("id") String id) {
        return applicationService.deletePdf(id);
    }

    @RequestMapping(value = "jpg/upload", method = RequestMethod.POST)
    public @ResponseBody String uploadJpg(@RequestParam("file") MultipartFile file) {
        return applicationService.uploadJpg(file);
    }

    @RequestMapping(value = "jpg/replace", method = RequestMethod.POST)
    public @ResponseBody String uploadJpg(@RequestParam("file") MultipartFile file,
                                          @RequestParam("link") String link) {
        return applicationService.replaceJpg(link, file);
    }

    @RequestMapping(value = "jpg/delete", method = RequestMethod.GET)
    public @ResponseBody Boolean deleteJpg(@RequestParam("link") String link) {
        return applicationService.deleteJpg(link);
    }

    private HttpHeaders getHttpHeaders(String name, File file)  {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.parseMediaType("application/pdf; charset=UTF-8"));
        header.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + Translit.cyr2lat(name.replace(",", "")));
        header.set("Access-Control-Allow-Origin", "*");
        header.setContentLength(file.length());
        return header;
    }

    private void copyFileToHttpResponse(String path, HttpServletResponse response) {
        try (FileInputStream inputStream = FileUtils.openInputStream(new File(path))){
            IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            log.warn("Exception while opening stream from " + path);
            throw new ReadWriteFileException(e.getMessage());
        }
    }
}
