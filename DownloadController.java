package com.example.streaming;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.print.attribute.standard.Media;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@RestController
@RequestMapping("/api")
public class DownloadController {

    private final Logger logger = LoggerFactory.getLogger(DownloadController.class);

    @GetMapping(value = "/download/files", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StreamingResponseBody> downloadFiles(final HttpServletResponse response) {

        response.setContentType("application/zip");
        response.setHeader(
                "Content-Disposition",
                "attachment;filename=XXX.zip");

        StreamingResponseBody stream = out -> {

            final String home = System.getProperty("user.home");
            final File directory = new File(home + File.separator + "Desktop" + File.separator + "XXX");
            final ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());

            if(directory.exists() && directory.isDirectory()) {
                try {
                    File[] files = directory.listFiles();

                    if(files != null){
                        for (final File file : files) {
                            if(file.isFile()){
                                try(InputStream inputStream = new FileInputStream(file);){
                                    final ZipEntry zipEntry = new ZipEntry(file.getName());
                                    zipOut.putNextEntry(zipEntry);
                                    byte[] bytes = new byte[1024];
                                    int length;
                                    while ((length = inputStream.read(bytes)) >= 0) {
                                        zipOut.write(bytes, 0, length);
                                    }
                                }
                            }
                        }
                    }
                    zipOut.close();
                } catch (final IOException e) {
                    logger.error("Exception while reading and streaming data {} ", e);
                }
            }
        };
        logger.info("steaming response {} ", stream);
        return new ResponseEntity(stream, HttpStatus.OK);
    }

    @GetMapping(value = "/download/file", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> downloadFile(final HttpServletResponse response) {

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(
                "Content-Disposition",
                "inline;filename=download.jpg");

        StreamingResponseBody stream = out -> {

            final String home = System.getProperty("user.home");
            final File file = new File(home
                    + File.separator + "Desktop"
                    + File.separator + "XXX"
                    + File.separator + "download.jpg");

            if(file.isFile()){
                try(InputStream inputStream = new FileInputStream(file);){
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = inputStream.read(bytes)) >= 0) {
                        out.write(bytes, 0, length);
                    }
                }
            }
        };
        logger.info("steaming response {} ", stream);
        return new ResponseEntity(stream, HttpStatus.OK);
    }
}