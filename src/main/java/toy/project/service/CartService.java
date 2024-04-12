package toy.project.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.project.dto.CartDetailDto;
import toy.project.dto.CartItemDto;
import toy.project.entity.Cart;
import toy.project.entity.CartItem;
import toy.project.entity.Item;
import toy.project.entity.Member;
import toy.project.repository.CartItemRepository;
import toy.project.repository.CartRepository;
import toy.project.repository.ItemRepository;
import toy.project.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public Long addCart(CartItemDto cartItemDto, String loginId) {
        /* 장바구니에 담을 상품 엔티티를 조회함. */
        Item item = itemRepository.findById(cartItemDto.getItemId()).orElseThrow(EntityNotFoundException::new);
        /* 현재 로기인한 회원 엔티티 조회 */
        Member member = memberRepository.findByLoginId(loginId);

        /* 현재 로그인한 회원의 장바구니 엔티티 조회 */
        Cart cart = cartRepository.findByMemberId(member.getId());
        /* 상품을 처음으로 장바구니에 담을 경우 해당 회원의 장바구니 엔티티 생성 */
        if (cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        /* 현재 상품이 장바구니에 이미 들어가 있는지 조회 */
        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

        if (savedCartItem != null) {
            /* 장바구니에 이미 있던 상품일 경우 기존 수량에 현재 장바구니에 담을 수량 만큼을 더해줌 */
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        } else {
            /* 장바구니 엔티티, 상품 엔티티, 장바구니에 담을 수량을 이용하여 cartItem 엔티티를 생성 */
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            /* 장바구니에 들어갈 상품을 저장 */
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }


    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String loginId) {

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        /* 로그인한 회원을 찾음. */
        Member member = memberRepository.findByLoginId(loginId);
        System.out.println("member = " + member);
        /* 현재 로그인한 회원의 장바구니 엔티티를 조회함. */
        Cart cart = cartRepository.findByMemberId(member.getId());
        /* 장바구니에 상품을 한 번도 안 담았을 경우 장바구니 엔티티가 없으므로 빈 리스트를 반환 */
        if (cart == null) {
            return cartDetailDtoList;
        }

        /* 장바구니에 담겨 있는 상품 정보를 조회 */
        cartItemRepository.findCartDetailDtoList(cart.getId());

        return cartDetailDtoList;
    }
}
