package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.entity.File;
import cn.net.yunlou.bole.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * FileName: FileController Description: Created By laughtiger Created At 2025/12/4 01:07 Modified
 * By Modified At
 */
@RestController
@RequestMapping("file")
@Tag(name = "21.文件管理", description = "文件相关接口")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "文件上传")
    public BusinessResponse<File> uploadFile(@RequestPart("file") MultipartFile file) {

        return BusinessResponse.success(fileService.uploadFile(file));
    }
}
