package toy.project.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import toy.project.constant.ItemSellStatus;
import toy.project.dto.ItemFormDto;
import toy.project.entity.Item;
import toy.project.entity.ItemImg;
import toy.project.repository.ItemImgRepository;
import toy.project.repository.ItemRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ItemServiceTest {


    @Autowired
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemImgRepository itemImgRepository;

    /* MockMultipartFile 클래스를 이용하여 가짜 MultipartFile 리스트를 만들어서 반환해주는 메소드 */
    List<MultipartFile> createMultipartFiles() throws Exception {

        List<MultipartFile> multipartFileList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String path = "C:/shop/item";
            String imageName = "image" + i + ".jpg";

            MockMultipartFile mockMultipartFile = new MockMultipartFile(path, imageName, "image/jpg", new byte[]{1, 2, 3, 4});

            multipartFileList.add(mockMultipartFile);
        }

        return multipartFileList;
    }


    @Test
    @DisplayName("상품 등록 테스트")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveItem() throws Exception {
        /* 상품 등록 화면에서 입력 받는 상품 데이터를 세팅해줌. */
        ItemFormDto itemFormDto = new ItemFormDto();
        itemFormDto.setItemName("테스트 상품");
        itemFormDto.setItemSellStatus(ItemSellStatus.SELL);
        itemFormDto.setItemDetail("테스트 상품입니다.");
        itemFormDto.setPrice(1000);
        itemFormDto.setStockNumber(100);

        List<MultipartFile> multipartFileList = createMultipartFiles();
        /* 상품 데이터와 이미지 정보를 파라미터로 넘겨서 저장 후 저장된 상품의 아이디 값을 반환 값으로 리턴해줌. */
        Long itemId = itemService.saveItem(itemFormDto, multipartFileList);

        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);

        /* 입력한 상품 데이터와 실제로 저장된 상품 데이터가 같은지 확인 */
        assertEquals(itemFormDto.getItemName(), item.getItemName());
        assertEquals(itemFormDto.getItemSellStatus(), item.getItemSellStatus());
        assertEquals(itemFormDto.getItemDetail(), item.getItemDetail());
        assertEquals(itemFormDto.getPrice(), item.getPrice());
        assertEquals(itemFormDto.getStockNumber(), item.getStockNumber());
        /* 상품 이미지는 첫 번째 파일의 원본 이미지 파일 이름만 같은지 확인함. */
        assertEquals(multipartFileList.get(0).getOriginalFilename(), itemImgList.get(0).getOriImgName());
    }
}