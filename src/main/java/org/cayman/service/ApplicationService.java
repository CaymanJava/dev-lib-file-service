package org.cayman.service;


import org.cayman.dto.PdfParseDto;
import org.cayman.dto.PdfUploadDto;
import org.cayman.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ApplicationService {
    private final FileManagerService fileManagerService;
    private final GoogleService googleService;
    private final ParsingService parsingService;
    private final Constants constants;

    @Autowired
    public ApplicationService(FileManagerService fileManagerService, GoogleService googleService, ParsingService parsingService, Constants constants) {
        this.fileManagerService = fileManagerService;
        this.googleService = googleService;
        this.parsingService = parsingService;
        this.constants = constants;
    }

    public PdfUploadDto uploadPdf(MultipartFile file, int numberOfPicturePage) {
        String fileName = fileManagerService.saveFile(file, constants.getUploadPdfDir());
        String tmpPath = constants.getUploadPdfDir() + fileName;
        PdfParseDto pdfParseDto = parsingService.parsePdfFile(tmpPath, file.getOriginalFilename(), numberOfPicturePage);
        String id = googleService.upload(tmpPath);
        fileManagerService.deleteFile(tmpPath);
        return new PdfUploadDto(pdfParseDto.getPageCount(), id, pdfParseDto.getImgPath());
    }

    public String downloadPdf(String name, String id) {
        return googleService.download(name, id);
    }

    public boolean deletePdf(String id) {
        return googleService.delete(id);
    }

    public String uploadJpg(MultipartFile file) {
        String fileName = fileManagerService.saveFile(file, constants.getImgFullPath());
        return constants.getImgShortPath() + fileName;
    }

    public boolean deleteJpg(String path) {
        String[] parts = path.split("img/");
        if (parts.length <= 1) return false;
        path = constants.getImgFullPath() + parts[1];
        return fileManagerService.deleteFile(path);
    }

    public String replaceJpg(String oldPath, MultipartFile file) {
        deleteJpg(oldPath);
        return uploadJpg(file);
    }
}
