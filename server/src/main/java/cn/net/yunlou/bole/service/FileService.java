package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.common.constant.StorageType;
import cn.net.yunlou.bole.entity.File;
import cn.net.yunlou.bole.model.create.FileCreate;
import cn.net.yunlou.bole.model.edit.FileEdit;
import cn.net.yunlou.bole.model.query.FileQuery;
import cn.net.yunlou.bole.model.view.FileView;
import java.io.InputStream;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * FileName: CityService Description: Created By MR. WANG Created At 2025/11/26 01:02 Modified By
 * Modified At
 */
public interface FileService extends IBaseService<File, FileCreate, FileView, FileEdit, FileQuery> {

    File uploadFile(MultipartFile file);

    List<File> uploadFiles(List<MultipartFile> files);

    InputStream downloadFile(String filePath, String storageType);

    boolean deleteFile(String filePath, String storageType);

    void switchStorageType(String newStorageType);

    List<StorageType> getAvailableStorageTypes();

    File getByFileKey(String fileKey);
}
