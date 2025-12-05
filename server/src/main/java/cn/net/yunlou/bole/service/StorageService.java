package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.constant.StorageType;
import cn.net.yunlou.bole.entity.File;
import java.io.InputStream;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    File upload(MultipartFile file);

    InputStream download(String fileName, String storageType);

    String getFileUrl(String fileName, String storageType);

    boolean delete(String fileName, String storageType);

    void switchStorageType(String newStorageType);

    List<StorageType> getAvailableStorageTypes();
}
