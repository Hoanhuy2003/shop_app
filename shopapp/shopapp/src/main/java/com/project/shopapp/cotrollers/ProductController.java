package com.project.shopapp.cotrollers;

import com.github.javafaker.Faker;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductListResponse;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.services.IProductService;
import com.project.shopapp.utils.MessageKeys;
import jakarta.validation.Path;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.security.auth.RefreshFailedException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/products")
public class ProductController {
    private  final IProductService productService;
    private final LocalizationUtils localizationUtils;



    @GetMapping("")
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ){

        // tạo Pageable
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                //Sort.by("createdAt").descending() // sắp xếp theo giảm dần
                Sort.by("id").ascending() // sắp xếp theo id tăng dần


        );
        Page<ProductResponse> productPage = productService.getAllProducts(pageRequest);
        // lấy tổng số trang
        int totalPages = productPage.getTotalPages();
        // lấy ra danh sách các products
        List<ProductResponse> products = productPage.getContent();
         return ResponseEntity.ok(ProductListResponse.builder()
                 .products(products)
                 .totalPages(totalPages).build());
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long productId){

        try {
            Product existingProduct = productService.getProductById(productId);
            return ResponseEntity.ok(ProductResponse.fromProduct(existingProduct));
            //return ResponseEntity.ok(existingProduct);  nếu muốn hiện tất cả
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable long id){
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(String.format("xóa thành công id = %d",id));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    // hàm update
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable long id,
            @RequestBody ProductDTO productDTO
    ){
        try {
            Product updateProduct = productService.updateProduct(id, productDTO);
            return ResponseEntity.ok(updateProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @PostMapping("") // cập nhật ảnh đại diện
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody ProductDTO productDTO,
            //@RequestPart("file") MultipartFile file, // ảnh
            BindingResult result
            ) {
        try{
            if(result.hasErrors()){
                List<String> errorMessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList(); // lấy ra những file lỗi
                return ResponseEntity.badRequest().body(errorMessage);
            }
            Product newProduct = productService.createProduct(productDTO);


             return ResponseEntity.ok(newProduct);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    // Lấy ra ảnh bằng request khác
    @PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // này sẽ hiện trên form data
    public ResponseEntity<?> uploadImages(
            @PathVariable("id") Long productId,
            @RequestParam("files")List<MultipartFile> files){
        try {
            Product existingProduct = productService.getProductById(productId);
//            if (existingProduct == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body("Không tìm thấy sản phẩm với ID: " + productId);
//            }
             files = files == null ? new ArrayList<MultipartFile>() : files;
             if(files.size() > 5    ){
                 return ResponseEntity.badRequest().body(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_MAX_5));
             }

            List<ProductImage> productImages = new ArrayList<>(); // danh sách các ảnh
            for(MultipartFile file : files){
                if(file.getSize()==0){
                    continue;
                }
                // Kiểm tra kích thước file và định dạng
                if(file.getSize() > 10*1024*1024){
                    // throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,"File quá lớn, chỉ tối đa 10MB");
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_LARGE));
                }
                //Kiểm tra file ảnh
                String contentType = file.getContentType();
                if(contentType == null || !contentType.startsWith("image/")){
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).
                            body(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE));
                }
                // lưu file và cập nhật thumbnail trong DB
                String filename = storeFile(file);
                // lưu vào đối tượng product trong DB
                ProductImage productImage = productService.createProductImage
                        (existingProduct.getId(), ProductImageDTO.builder()
                        .imageUrl(filename).build());
                productImages.add(productImage);
            }
            return ResponseEntity.ok().body(productImages);// trả về danh sách các ảnh
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName){
        try{
            java.nio.file.Path imagePath = Paths.get("uploads/"+imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());
            if(resource.exists()){
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            }else {
                return ResponseEntity.notFound().build();
            }


        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }
    // hàm lưu ảnh
    private String storeFile(MultipartFile file) throws  IOException {
        if(!isImageFile(file) || file.getOriginalFilename() == null){
            throw new IOException("không phải ảnh");
        }
          String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
          // thêm UUID để file đó là file duy nhất tránh trùng lặp
        String uniqueFilename = UUID.randomUUID().toString() + "_"+ filename;
          // đường dẫn đến thư mục lưu
        java.nio.file.Path uploadDir = Paths.get("uploads");
        // kiểm tra thư mục upload tồn tại chưa
        if(!Files.exists(uploadDir)){
            Files.createDirectories(uploadDir);
        }
        // đường dẫn đầy đủ đến file
        java.nio.file.Path destination = Paths.get(uploadDir.toString(),uniqueFilename);
        // sao chép thư mục
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFilename;
    }
    // kiểm tra ảnh
    private boolean isImageFile(MultipartFile file){
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }


    @PostMapping("/generateFakeProducts")  // bao giờ dùng thì public
    private ResponseEntity<String> generateFakeProducts(){
        Faker faker = new Faker();
        for(int i =0; i< 1_000_000; i++){
            String productName = faker.commerce().productName();
            if(productService.existsByName(productName)){
                continue;
            }
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price((float)faker.number().numberBetween(10, 90_000_000))
                    .thumbnail("")
                    .description(faker.lorem().sentence())
                    .categoryId((long)faker.number().numberBetween(3,7)).build();
            try {
                productService.createProduct(productDTO);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Fake Products created ");
    }
}
