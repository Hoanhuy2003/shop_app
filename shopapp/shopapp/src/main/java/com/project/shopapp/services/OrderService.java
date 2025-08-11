package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderStatus;
import com.project.shopapp.models.User;
import com.project.shopapp.repositorys.OrderRepository;
import com.project.shopapp.repositorys.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService implements IOrderService{

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    public OrderService(UserRepository userRepository, OrderRepository orderRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Order createOrder(OrderDTO orderDTO) throws Exception {
        // tim xem user_id co ton tai khong
        User user= userRepository
                .findById(orderDTO.getUserID())
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng có id :"+orderDTO.getUserID()));
        // convert orderDTO -> Order
        // dùng thư viện Model Mapper
        modelMapper.typeMap(OrderDTO.class, Order.class).addMappings(mapper ->mapper.skip(Order::setId));
        // cap nhat ca truong cua don hang tu OrderDTO
        Order order = new Order();
        modelMapper.map(orderDTO,order);
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.PENDING);
        // kiểm tra shipping date >= ngày đặt
        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now() : orderDTO.getShippingDate();

        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Ngày ship không được ở quá khứ");
        }

        order.setShippingDate(shippingDate);
        order.setActive(true);
        orderRepository.save(order);

       // modelMapper.typeMap(Order.class,OrderResponse.class);

        return order;
    }

    @Override
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException {
        // check order
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new DataNotFoundException("Không tìm thấy đơn hàng có id: "+id));
        // check user
        User existingUser = userRepository.findById(orderDTO.getUserID()).orElseThrow(()->
                new DataNotFoundException("Không tìm thấy người dun có id: "+id));
        // map orderDTO -> order
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));

        modelMapper.map(orderDTO,order);
        order.setUser(existingUser);
        return orderRepository.save(order);

    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        // nếu tìm thấy thì xóa, xóa mềm
        if(order != null){
            order.setActive(false);
            orderRepository.save(order);
        }


    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
