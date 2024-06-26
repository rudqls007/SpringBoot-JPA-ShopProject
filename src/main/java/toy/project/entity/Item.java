package toy.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import toy.project.config.BaseEntity;
import toy.project.constant.ItemSellStatus;
import toy.project.dto.ItemFormDto;
import toy.project.exception.OutOfStockException;


/**
 * Item 클래스를 entity로 선언함.
 * 또한 @Table 어노테이션을 통해 어떤 테이블과 매핑될지를 지정함.
 * Item 테이블과 매핑되도록 name을 item으로 지정했음.
 */
@Entity
@Table(name = "item")
@Getter @Setter @ToString
public class Item extends BaseEntity {

    /**
     * @Id = entity로 선언한 클래스는 반드시 기본키를 가져야 함.
     * @Column = 테이블에 매핑될 컬럼의 이름을 설정해 줌.
     * item 테이블의 item_id 컬럼이 매핑되도록 지정해주었음.
     * @GeneratedValue = 기본키 생성 전략
     */
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; //상품 코드

    @Column(nullable = false, length = 50)
    private String itemName; //상품명

    @Column(name = "price", nullable = false)
    private int price; //가격

    @Column(nullable = false)
    private int stockNumber; //재고 수량

    /**
     * @Lob
     * LOB은 가변길의를 갖는 큰 데이터를 저장하는데 사용하는 데이터형이다.
     * CLOB은 문자기반을 데이터를 저장하는데 사용된다.
     * BLOB은 binary 데이터를 저장하는데 사용된다.
     * @Lob은 일반적인 데이터베이스에서 저장하는 길이인 255개 이상의 문자를 저장하고 싶을 때 지정한다.
     */
    @Lob
    @Column(nullable = false)
    private String itemDetail; //상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; //상품 판매 상태

//    private LocalDateTime regTime; //등록 시간
//
//    private LocalDateTime updateTime; //수정 시간



    public void updateItem(ItemFormDto itemFormDto) {
        this.itemName = itemFormDto.getItemName();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();


    }


    /* 상품을 주문할 경우 상품의 재고를 감소시키는 로직 */
    public void removeStock(int stockNumber) {
        /* 상품의 재고 수량에서 주문 후 남은 재고 수량을 구함. */
        int restStock = this.stockNumber - stockNumber;
        if (restStock < 0) {
            /* 상품의 재고가 주문 수량보다 적을 경우 재고 부족 예외를 발생시킴 */
            throw new OutOfStockException(
                    "상품의 재고가 부족합니다. (현재 재고 수량 :" + this.stockNumber + ")");
        }
        /* 주문 후 남은 재고 수량을 상품의 현재 재고 값으로 할당함. */
        this.stockNumber = restStock;
    }

    /* 상품의 재고를 증가시키는 메소드 */
    public void addStock(int stockNumber){

        this.stockNumber += stockNumber;
    }
}
