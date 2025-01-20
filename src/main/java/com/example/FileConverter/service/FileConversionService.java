package com.example.FileConverter.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
@Service
public class FileConversionService {

    public String convertFile(MultipartFile file, String fileType) throws IOException {
        switch (fileType.toLowerCase()) {
            case "ppt":
                return convertPptToPdf(file);
            case "docx":
                return convertDocxToPdf(file);
            case "pdf":
                return convertPdfToDocx(file);
            default:
                System.out.println("Unsupported file type: " + fileType);
                throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }
    }

    public String convertPptToPdf(MultipartFile file) throws IOException {
        XMLSlideShow ppt = new XMLSlideShow(file.getInputStream());
        String outputFilePath = "converted-" + System.currentTimeMillis() + ".pdf";

        try (PDDocument pdfDocument = new PDDocument()) {
            for (XSLFSlide slide : ppt.getSlides()) {
                BufferedImage slideImage = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = slideImage.createGraphics();
                graphics.setPaint(Color.WHITE);
                graphics.fillRect(0, 0, 1280, 720);
                slide.draw(graphics);

                PDPage page = new PDPage(new PDRectangle(1280, 720));
                pdfDocument.addPage(page);

                PDImageXObject image = PDImageXObject.createFromFileByContent(
                        convertBufferedImageToFile(slideImage), pdfDocument);

                try (var contentStream = new PDPageContentStream(pdfDocument, page)) {
                    contentStream.drawImage(image, 0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
                }
            }
            pdfDocument.save(outputFilePath);
        } catch (IOException e) {
            System.out.println("Error during PPT to PDF conversion: " + e.getMessage());
            throw new IOException("Error during PPT to PDF conversion: " + e.getMessage());
        }

        return outputFilePath;
    }

    private File convertBufferedImageToFile(BufferedImage image) throws IOException {
        File tempFile = File.createTempFile("slide", ".png");
        try {
            javax.imageio.ImageIO.write(image, "png", tempFile);
        } catch (IOException e) {
            System.out.println("Error creating temporary image file: " + e.getMessage());
            throw new IOException("Error creating temporary image file: " + e.getMessage());
        }
        return tempFile;
    }

    public String convertDocxToPdf(MultipartFile file) throws IOException {
        InputStream docxInputStream = file.getInputStream();
        String outputFilePath = "converted-" + System.currentTimeMillis() + ".pdf";

        try (PDDocument pdfDocument = new PDDocument()) {
            XWPFDocument docx = new XWPFDocument(docxInputStream);
            XWPFWordExtractor extractor = new XWPFWordExtractor(docx);
            String docText = extractor.getText();
            String[] lines = docText.split("\n");

            PDType1Font helveticaFont = PDType1Font.HELVETICA_BOLD;

            for (String line : lines) {
                String sanitizedLine = line.replaceAll("[\\x00-\\x1F\\x7F]", "");

                PDPage page = new PDPage();
                pdfDocument.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, page)) {
                    contentStream.beginText();
                    contentStream.setFont(helveticaFont, 12);
                    contentStream.newLineAtOffset(100, 700);
                    contentStream.showText(sanitizedLine);
                    contentStream.endText();
                }
            }

            pdfDocument.save(outputFilePath);
        } catch (IOException e) {
            System.out.println("Error during DOCX to PDF conversion: " + e.getMessage());
            throw new IOException("Error during DOCX to PDF conversion: " + e.getMessage());
        }

        return outputFilePath;
    }

    public String convertPdfToDocx(MultipartFile file) throws IOException {
        PDDocument pdfDocument = PDDocument.load(file.getInputStream());
        String outputFilePath = "converted-" + System.currentTimeMillis() + ".docx";

        try (XWPFDocument docx = new XWPFDocument()) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(pdfDocument);
            String[] lines = text.split("\n");

            for (String line : lines) {
                docx.createParagraph().createRun().setText(line);
            }

            try (FileOutputStream out = new FileOutputStream(outputFilePath)) {
                docx.write(out);
            }
        } catch (IOException e) {
            System.out.println("Error during PDF to DOCX conversion: " + e.getMessage());
            throw new IOException("Error during PDF to DOCX conversion: " + e.getMessage());
        }

        return outputFilePath;
    }

    public String convertTxtToPdf(MultipartFile file) throws IOException {
        InputStream txtInputStream = file.getInputStream();
        String outputFilePath = "converted-" + System.currentTimeMillis() + ".pdf";

        try (PDDocument pdfDocument = new PDDocument()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(txtInputStream));
            String line;
            PDType1Font helveticaFont = PDType1Font.HELVETICA_BOLD;

            while ((line = reader.readLine()) != null) {
                String sanitizedLine = line.replaceAll("[\\x00-\\x1F\\x7F]", "");

                PDPage page = new PDPage();
                pdfDocument.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, page)) {
                    contentStream.beginText();
                    contentStream.setFont(helveticaFont, 12);
                    contentStream.newLineAtOffset(100, 700);
                    contentStream.showText(sanitizedLine);
                    contentStream.endText();
                }
            }

            pdfDocument.save(outputFilePath);
        } catch (IOException e) {
            System.out.println("Error during TXT to PDF conversion: " + e.getMessage());
            throw new IOException("Error during TXT to PDF conversion: " + e.getMessage());
        }

        return outputFilePath;
    }
}
