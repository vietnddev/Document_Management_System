package com.flowiee.dms.controller;

import com.flowiee.dms.core.BaseController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.api.prefix}/file")
@Tag(name = "File API", description = "Quản lý file đính kèm và hình ảnh sản phẩm")
public class FileStorageController extends BaseController {

}