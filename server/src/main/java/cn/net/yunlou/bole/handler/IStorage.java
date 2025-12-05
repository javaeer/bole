package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.constant.StorageType;
import cn.net.yunlou.bole.entity.File;
import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

/** 策略 接口 */
public interface IStorage {

    /** 获取存储类型 */
    StorageType getType();

    /**
     * 存储文件
     *
     * @param file 上传的文件
     * @return 存储后的文件信息
     */
    File store(MultipartFile file);

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    boolean delete(String filePath);

    /**
     * 获取文件访问URL
     *
     * @param filePath 文件路径
     * @return 访问URL
     */
    String getAccessUrl(String filePath);

    /**
     * 下载文件
     *
     * @param filePath 文件路径
     * @return 文件流
     */
    InputStream download(String filePath);

    /**
     * 检查文件是否存在
     *
     * @param filePath 文件路径
     * @return 是否存在
     */
    boolean exists(String filePath);

    /**
     * 获取文件信息
     *
     * @param filePath 文件路径
     * @return 文件信息
     */
    FileInfo getFileInfo(String filePath);
}
