package shoppingmall.item.entity;

import lombok.Getter;

@Getter
public class ItemImg {

    private long itemImgNum;
    private Item item;
    private String saveImgName;
    private String uploadImgName;
    private boolean repImg;

    public ItemImg(Item item, String saveImgName, String uploadImgName, boolean repImg) {
        this.item = item;
        this.saveImgName = saveImgName;
        this.uploadImgName = uploadImgName;
        this.repImg = repImg;
    }
}
