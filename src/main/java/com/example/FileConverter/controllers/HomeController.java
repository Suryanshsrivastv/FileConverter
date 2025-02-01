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
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/conv")
@CrossOrigin
public class HomeController {

    @RequestMapping("/welcome")
    public String home() {
        return "Welcome to the file converter ..";
    }

    @Autowired
    private FileConversionService fileConversionService;

    // Conversion for PPT to PDF
    @PostMapping("/ppt-to-pdf")
    public ResponseEntity<Map<String, String>> convertPptToPdf(@RequestParam("file") MultipartFile file) {
        try {
            // Convert the PPT file and get the Dropbox URL
            String downloadUrl = fileConversionService.convertPptToPdf(file);

            // Prepare the response
            Map<String, String> response = new HashMap<>();
            response.put("message", "PPT to PDF conversion successful");
            response.put("downloadUrl", downloadUrl);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Error response with exception message
            return ResponseEntity.badRequest().body(Map.of("error", "Error during conversion: " + e.getMessage()));
        }
    }

    // DOCX to PDF conversion
    @PostMapping("/docx-to-pdf")
    public ResponseEntity<Map<String, String>> convertDocxToPdf(@RequestParam("file") MultipartFile file) {
        try {
            // Convert the DOCX file and get the Dropbox URL
            String downloadUrl = fileConversionService.convertDocxToPdf(file);

            // Prepare the response
            Map<String, String> response = new HashMap<>();
            response.put("message", "DOCX to PDF conversion successful");
            response.put("downloadUrl", downloadUrl);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Error response with exception message
            return ResponseEntity.badRequest().body(Map.of("error", "Error during conversion: " + e.getMessage()));
        }
    }

    // TXT to PDF conversion
    @PostMapping("/txt-to-pdf")
    public ResponseEntity<Map<String, String>> convertTxtToPdf(@RequestParam("file") MultipartFile file) {
        try {
            // Convert the TXT file and get the Dropbox URL
            String downloadUrl = fileConversionService.convertTxtToPdf(file);

            // Prepare the response
            Map<String, String> response = new HashMap<>();
            response.put("message", "TXT to PDF conversion successful");
            response.put("downloadUrl", downloadUrl);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Error response with exception message
            return ResponseEntity.badRequest().body(Map.of("error", "Error during conversion: " + e.getMessage()));
        }
    }

    // PDF to DOCX conversion
    @PostMapping("/pdf-to-docx")
    public ResponseEntity<Map<String, String>> convertPdfToDocx(@RequestParam("file") MultipartFile file) {
        try {
            // Convert the PDF file and get the Dropbox URL
            String downloadUrl = fileConversionService.convertPdfToDocx(file);

            // Prepare the response
            Map<String, String> response = new HashMap<>();
            response.put("message", "PDF to DOCX conversion successful");
            response.put("downloadUrl", downloadUrl);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Error response with exception message
            return ResponseEntity.badRequest().body(Map.of("error", "Error during conversion: " + e.getMessage()));
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
}