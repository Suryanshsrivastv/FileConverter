package com.example.FileConverter.controllers;

import com.example.FileConverter.service.FileConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
@RestController
@RequestMapping("/conv")
public class HomeController {

    @RequestMapping("/welcome")
    public String home() {
        return "Welcome to the file converter ..";
    }

    @Autowired
    private FileConversionService fileConversionService;

    // Conversion for PPT to PDF
    @PostMapping("/ppt-to-pdf")
    public ResponseEntity<?> convertPptToPdf(@RequestParam("file") MultipartFile file) {
        try {
            String outputFilePath = fileConversionService.convertPptToPdf(file);
            return ResponseEntity.ok("File converted successfully. Path: " + outputFilePath);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during conversion: " + e.getMessage());
        }
    }

    // Conversion for any file type with the type parameter
    @PostMapping("/convert")
    public ResponseEntity<String> convertFile(@RequestParam("file") MultipartFile file, @RequestParam("type") String fileType) {
        try {
            String filePath = fileConversionService.convertFile(file, fileType);
            return ResponseEntity.status(HttpStatus.OK).body("File converted successfully. Download it at /conv/download/" + filePath);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during conversion: " + e.getMessage());
        }
    }

    // File download
    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("fileName") String fileName) {
        try {
            // Assuming the file is saved in the root directory or specify the full path if different
            File file = new File(fileName);
            if (file.exists()) {
                byte[] fileContent = Files.readAllBytes(file.toPath());

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

                // Optionally, set content type based on file extension
                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
                switch (fileExtension.toLowerCase()) {
                    case "pdf":
                        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
                        break;
                    case "docx":
                        headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                        break;
                    default:
                        headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
                        break;
                }

                return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // DOCX to PDF conversion
    @PostMapping("/docx-to-pdf")
    public ResponseEntity<?> convertDocxToPdf(@RequestParam("file") MultipartFile file) {
        try {
            String outputFilePath = fileConversionService.convertDocxToPdf(file);
            return ResponseEntity.ok("DOCX converted successfully. Path: " + outputFilePath);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during conversion: " + e.getMessage());
        }
    }

    // TXT to PDF conversion
    @PostMapping("/txt-to-pdf")
    public ResponseEntity<?> convertTxtToPdf(@RequestParam("file") MultipartFile file) {
        try {
            String outputFilePath = fileConversionService.convertTxtToPdf(file);
            return ResponseEntity.ok("TXT converted successfully. Path: " + outputFilePath);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during conversion: " + e.getMessage());
        }
    }

    // PDF to DOCX conversion
    @PostMapping("/pdf-to-docx")
    public ResponseEntity<?> convertPdfToDocx(@RequestParam("file") MultipartFile file) {
        try {
            String outputFilePath = fileConversionService.convertPdfToDocx(file);
            return ResponseEntity.ok("PDF converted successfully. Path: " + outputFilePath);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during conversion: " + e.getMessage());
        }
    }
}

