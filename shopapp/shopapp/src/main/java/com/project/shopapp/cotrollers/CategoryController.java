package com.project.shopapp.cotrollers;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;
import com.project.shopapp.responses.UpdateCategoryResponse;
import com.project.shopapp.services.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("${api.prefix}/categories")
//@Validated
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;


    @PostMapping("")
    // nếu tham số truyền vào là 1 Oject(đối tượng) => Data Transfer Oject (DTO)
    public ResponseEntity<?> createCategories(@Valid @RequestBody  CategoryDTO categoryDTO,
                                              BindingResult result){
        if(result.hasErrors()){
            List<String> errorMessage = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList(); // lấy ra những file lỗi
            return ResponseEntity.badRequest().body(errorMessage);
        }
        categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok("Thêm mới category thành công");
    }

    @GetMapping("") //localhost:8080/api/v1/categories?page=1&limit=10
    public ResponseEntity<List<Category>> getAllCategories(
            @RequestParam(value = "page", defaultValue = "1") int page, // gọi key và value
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ){
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateCategoryResponse> updateCategories(@PathVariable Long id,
                                                                   @Valid @RequestBody CategoryDTO categoryDTO,
                                                                   HttpServletRequest request){
        categoryService.updateCategory(id,categoryDTO);
        Locale locale = localeResolver.resolveLocale(request);

        return ResponseEntity.ok(UpdateCategoryResponse.builder()
                        .message(messageSource.getMessage("category.update_category.category_successfully",null,locale))
                .build());
    }
    @DeleteMapping ("/{id}")
    public ResponseEntity<String> deleteCategories(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("xóa"+id);
    }

}

