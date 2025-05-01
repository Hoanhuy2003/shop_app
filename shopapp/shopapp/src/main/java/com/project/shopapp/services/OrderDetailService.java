package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositorys.OrderDetailRepository;
import com.project.shopapp.repositorys.OrderRepository;
import com.project.shopapp.repositorys.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class OrderDetailService implements IOrderDetailService{
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;

    public OrderDetailService(OrderRepository orderRepository, ProductRepository productRepository, OrderDetailRepository orderDetailRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    @Override
    public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
        // tìm orderId
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(()->new DataNotFoundException("Không tìm thấy đơn có id:"+orderDetailDTO.getOrderId()));
        // tìm product
        Product product = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(()-> new DataNotFoundException("Không tìm thấy sản phẩm có id: "+orderDetailDTO.getProductId()));
        OrderDetail orderDetail = OrderDetail.builder()
                .order(order)
                .product(product)
                .price(orderDetailDTO.getPrice())
                .numberOfProduct(orderDetailDTO.getNumberOfProducts())
                .totalMoney(orderDetailDTO.getTotalMoney())
                .color(orderDetailDTO.getColor()).build();

        return orderDetailRepository.save(orderDetail);


    }

    @Override
    public OrderDetail getOrderDetail(Long id) throws DataNotFoundException {

        return orderDetailRepository.findById(id).orElseThrow(()->
                new DataNotFoundException("Không timd thấy chi tiết id:"+id));
    }

    @Override
    public OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
        // tìm xem orderdetail có tồn tại không
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id).orElseThrow(() ->
                new DataNotFoundException("Không timg thấy với id"+id));
        // tìm orderId
        Order existingOrder = orderRepository.findById(orderDetailDTO.getOrderId()).orElseThrow(()->
                new DataNotFoundException("Không tìm thấy vơi id"+orderDetailDTO.getOrderId()));
        // tìm product
        Product existingProduct = productRepository.findById(orderDetailDTO.getProductId()).orElseThrow(()->
                new DataNotFoundException("Không tim thaays sp có id:"+orderDetailDTO.getProductId()));
        existingOrderDetail.setPrice(orderDetailDTO.getPrice());
        existingOrderDetail.setNumberOfProduct(orderDetailDTO.getNumberOfProducts());
        existingOrderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
        existingOrderDetail.setColor(orderDetailDTO.getColor());
        existingOrderDetail.setOrder(existingOrder);
        existingOrderDetail.setProduct(existingProduct);

        return orderDetailRepository.save(existingOrderDetail);
    }

    @Override
    public void deleteById(Long id) {

        orderDetailRepository.deleteById(id);
    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}
