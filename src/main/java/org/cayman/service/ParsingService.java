package org.cayman.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.cayman.dto.PdfParseDto;
import org.cayman.exception.ReadWriteFileException;
import org.cayman.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class ParsingService {
    private final static int DEFAULT_DPI = 75;

    private final Constants constants;

    @Autowired
    public ParsingService(Constants constants) {
        this.constants = constants;
    }

    PdfParseDto parsePdfFile(String path, String name, int numberOfPicturePage) {
        PDDocument pdDocument = loadPdf(path);
        PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);
        try {
            int pageCount = pdDocument.getNumberOfPages();
            name = name.replaceAll(" ", "_").replaceAll(".PDF", "").replaceAll(".pdf", "");

            String imgPath = constants.getImgFullPath() + name + ".jpg";
            makeScreenshot(pdfRenderer, imgPath, numberOfPicturePage);
            return new PdfParseDto(pageCount, constants.getImgShortPath() + name + ".jpg");
        } finally {
            try {
                pdDocument.close();
            } catch (IOException e) {/*NOP*/}
        }
    }

    private PDDocument loadPdf(String path) {
        try {
            return PDDocument.load(new File(path));
        } catch (IOException e) {
            log.warn("Exception while loading file " + path);
            throw new ReadWriteFileException(e.getMessage());
        }
    }

    private void makeScreenshot(PDFRenderer pdfRenderer, String picturePath, int numberOfPicturePage) {
        try {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(numberOfPicturePage - 1, DEFAULT_DPI, ImageType.RGB);
            ImageIOUtil.writeImage(bim, picturePath, DEFAULT_DPI);
        } catch (IOException e) {
            log.warn("Exception while making screenshot.");
            throw new ReadWriteFileException(e.getMessage());
        }
    }
}
