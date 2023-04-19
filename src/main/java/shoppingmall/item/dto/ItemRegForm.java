package shoppingmall.item.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;
import shoppingmall.item.constant.ItemSellStatus;
import shoppingmall.item.entity.Item;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemRegForm {

    @NotBlank
    @Length(max = 30)
    private String itemName;

    @NotNull
    @Min(0)
    private Integer price;

    @NotBlank
    private String itemDetail;

    @NotNull
    @Min(0)
    private Integer stock;

    @NotNull
    private ItemSellStatus itemSellStatus;

    private List<MultipartFile> itemImgFiles;

    private static ModelMapper modelMapper = new ModelMapper();

    public static Item createMember(ItemRegForm itemRegFormDto) {
        Item item = modelMapper.map(itemRegFormDto, Item.class);
        item.setRegdate(LocalDateTime.now());
        return item;
    }
}
