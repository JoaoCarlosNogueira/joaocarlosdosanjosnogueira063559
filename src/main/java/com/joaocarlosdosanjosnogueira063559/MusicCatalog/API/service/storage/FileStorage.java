package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.service.storage;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {
    String uploadFile(MultipartFile file) throws Exception;
    String generateFileUrl(String fileName) throws Exception;
}
