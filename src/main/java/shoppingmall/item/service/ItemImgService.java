package shoppingmall.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shoppingmall.item.entity.Item;
import shoppingmall.item.entity.ItemImg;
import shoppingmall.item.respository.ItemImgRepository;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemImgService {

    private final ItemImgRepository itemImgRepository;

    @Value("${file.dir}")
    private String fileDir;

    public void regItemImg(MultipartFile multipartFile, Item item, boolean repImg) throws IOException {

        //storage에 이미지 파일 저장
        String uploadImgName = multipartFile.getOriginalFilename();
        String saveImgName = getSaveImgName(uploadImgName);
        multipartFile.transferTo(new File(getFullPath(saveImgName)));

        //DB item_img에 저장
        itemImgRepository.saveItemImg(new ItemImg(item, saveImgName, uploadImgName, repImg));
    }

    private String getFullPath(String fileName) {
        return fileDir + fileName;
    }

    private String getSaveImgName(String originalFilename) {
        String uuid = UUID.randomUUID().toString(); //uuid 얻기
        String extension = extractExtension(originalFilename); //확장자 얻기
        return uuid + "." + extension;
    }

    private String extractExtension(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}
