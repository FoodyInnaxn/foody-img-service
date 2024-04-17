package com.foody.imageuploadservice.business;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageUtil {
    public static List<MultipartFile> convertBase64ToMultipartFile(List<String> encodedImages) {
        List<MultipartFile> multipartFiles = new ArrayList<>();
        for (String encodedImage : encodedImages) {
                // Decode Base64 string to byte array
                byte[] imageBytes = Base64.decodeBase64(encodedImage);

                // Create MultipartFile from byte array
                MultipartFile multipartFile = new Base64MultipartFile(imageBytes);
                multipartFiles.add(multipartFile);
        }
        return multipartFiles;
    }

    // Custom implementation of MultipartFile for byte arrays
    private static class Base64MultipartFile implements MultipartFile {
        private final byte[] content;

        public Base64MultipartFile(byte[] content) {
            this.content = content;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getOriginalFilename() {
            return null;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return content;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {

        }
    }
}

