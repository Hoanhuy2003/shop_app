package com.project.shopapp.services;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.InvalidParamException;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.repositorys.CategoryRepository;
import com.project.shopapp.repositorys.ProductImageRepository;
import com.project.shopapp.repositorys.ProductRepository;
import com.project.shopapp.responses.ProductResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
//@RequiredArgsConstructor
public class ProductService implements IProductService{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productImageRepository = productImageRepository;
    }


    @Override
    public Product createProduct(ProductDTO productDTO) throws Exception {
        Category existingCategory= categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException
                        ("không cos category với id: "+productDTO.getCategoryId()));
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .thumbnail(productDTO.getThumbnail())
                .category(existingCategory).build();
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(long productId) throws Exception {
        return productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Không có sản phẩm với ID: " + productId));
    }


    @Override
    public Page<ProductResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest) {
        Page<Product> productPage;
        productPage = productRepository.searchProducts(categoryId, keyword, pageRequest);
        //phân trang
        return productPage
                .map(ProductResponse::fromProduct); // thay thế cho builder().build
    }

    @Override
    @Transactional
    public Product updateProduct(long id, ProductDTO productDTO) throws Exception {
        Product existingProduct = getProductById(id);
        if(existingProduct != null){
            Category existingCategory= categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException
                            ("không cos category với id: "+productDTO.getCategoryId()));
            existingProduct.setName(productDTO.getName());
            existingProduct.setCategory(existingCategory);
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setThumbnail(productDTO.getThumbnail());
            return productRepository.save(existingProduct);
        }
          return null;
    }

    @Override
    @Transactional
    public void deleteProduct(long id) {
        // kiểm tra xem có tồn tại không trước khi xóa
        Optional<Product> optionalProduct = productRepository.findById(id);
        optionalProduct.ifPresent(productRepository::delete);

    }


    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }
    @Override
    // Ảnh sản phẩm
    public ProductImage createProductImage(
            Long productId,
            ProductImageDTO productImageDTO) throws Exception {
        Product existingProduct= productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException
                        ("không cos category với id: "+productImageDTO.getProductId()));
        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
        // không cho thêm quá 5 ảnh trong 1 sản phẩm
       int size = productImageRepository.findByProductId(productId).size();
       if(size >= 5){
           throw new InvalidParamException("Số ảnh phải <=5");
       }
       return productImageRepository.save(newProductImage);

    }
}
