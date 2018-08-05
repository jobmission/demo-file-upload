package com.revengemission.commons.fileservice.controller;

import com.revengemission.commons.fileservice.common.StorageFileNotFoundException;
import com.revengemission.commons.fileservice.service.StorageService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.*;


@Controller
public class FileServiceController {

    @Value("${storage.location.public}")
    private String publicStorageLocation;

    @Value("${storage.location.private}")
    private String privateStorageLocation;

    @Autowired
    StorageService storageService;

    /*附件形式*/
    /*@CrossOrigin
    @GetMapping("/files/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename, Principal principal) {

        Resource file = storageService.loadAsResource(Paths.get(privateStorageLocation, filename));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }*/

    /*附件形式*/
   /* @CrossOrigin
    @GetMapping("/public/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> servePublicFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(Paths.get(publicStorageLocation, filename));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }*/

    @RequestMapping(value = "/public/{filename}", method = RequestMethod.GET)
    public void downloadStreamPublic(HttpServletResponse response, @PathVariable String filename) throws IOException {

        // Get your file stream from wherever.
        InputStream myStream = Files.newInputStream(Paths.get(publicStorageLocation, filename));
        // Copy the stream to the response's output stream.
        IOUtils.copy(myStream, response.getOutputStream());
        response.flushBuffer();
    }

    @RequestMapping(value = "/protected/{filename}", method = RequestMethod.GET)
    public void downloadStream(HttpServletResponse response, @PathVariable String filename) throws IOException {

        // Get your file stream from wherever.
        InputStream myStream = Files.newInputStream(Paths.get(privateStorageLocation, filename));
        // Copy the stream to the response's output stream.
        IOUtils.copy(myStream, response.getOutputStream());
        response.flushBuffer();
    }

    @CrossOrigin
    @PostMapping("/upload/public")
    @ResponseBody
    public Map<String, Object> handleFileUpload(@RequestPart(value = "files", required = false) List<MultipartFile> files,
                                                Principal principal) {
        Map<String, Object> result = new HashMap<>();
        Set<String> fileNames = new LinkedHashSet<>();
        if (files != null && files.size() > 0) {
            files.forEach(multipartFile -> {
                storageService.store(Paths.get(publicStorageLocation), multipartFile);
                fileNames.add(multipartFile.getOriginalFilename());
            });
        }
        result.put("status", "1");
        result.put("files", fileNames);
        return result;
    }

    @CrossOrigin
    @PostMapping("/upload/protected")
    @ResponseBody
    public Map<String, Object> handleFileUploadProtected(@RequestPart(value = "files", required = false) List<MultipartFile> files,
                                                       Principal principal) {
        Map<String, Object> result = new HashMap<>();
        Set<String> fileNames = new LinkedHashSet<>();
        if (files != null && files.size() > 0) {
            files.forEach(multipartFile -> {
                storageService.store(Paths.get(privateStorageLocation), multipartFile);
                fileNames.add(multipartFile.getOriginalFilename());
            });
        }
        result.put("status", "1");
        result.put("files", fileNames);
        return result;
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
