package com.example.recipebookbackend.service;

import com.example.recipebookbackend.dto.ProductRequest;
import com.example.recipebookbackend.dto.ProductResponse;
import com.example.recipebookbackend.entity.DishIngredient;
import com.example.recipebookbackend.entity.Product;
import com.example.recipebookbackend.enums.AdditionalFlag;
import com.example.recipebookbackend.enums.ProductCategory;
import com.example.recipebookbackend.enums.ProductReadiness;
import com.example.recipebookbackend.exception.BadRequestException;
import com.example.recipebookbackend.exception.NotFoundException;
import com.example.recipebookbackend.exception.ProductInUseException;
import com.example.recipebookbackend.repository.DishIngredientRepository;
import com.example.recipebookbackend.repository.ProductRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final DishIngredientRepository dishIngredientRepository;

    public ProductService(ProductRepository productRepository,
                          DishIngredientRepository dishIngredientRepository) {
        this.productRepository = productRepository;
        this.dishIngredientRepository = dishIngredientRepository;
    }

    public ProductResponse create(ProductRequest request) {
        validateProductRequest(request);

        Product product = new Product();
        fillProduct(product, request);

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    public ProductResponse update(Long id, ProductRequest request) {
        validateProductRequest(request);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Продукт с id=" + id + " не найден"));

        fillProduct(product, request);

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Продукт с id=" + id + " не найден"));
        return toResponse(product);
    }

    public List<ProductResponse> getAll(String search,
                                        ProductCategory category,
                                        ProductReadiness readiness,
                                        Boolean vegan,
                                        Boolean glutenFree,
                                        Boolean sugarFree,
                                        String sortBy,
                                        String sortDir) {
        List<Product> products = productRepository.findAll();

        products = products.stream()
                .filter(p -> matchesSearch(p, search))
                .filter(p -> category == null || p.getCategory() == category)
                .filter(p -> readiness == null || p.getReadiness() == readiness)
                .filter(p -> matchesFlag(p, AdditionalFlag.VEGAN, vegan))
                .filter(p -> matchesFlag(p, AdditionalFlag.GLUTEN_FREE, glutenFree))
                .filter(p -> matchesFlag(p, AdditionalFlag.SUGAR_FREE, sugarFree))
                .toList();

        Comparator<Product> comparator = getProductComparator(sortBy);
        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }

        return products.stream()
                .sorted(comparator)
                .map(this::toResponse)
                .toList();
    }

    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Продукт с id=" + id + " не найден"));

        List<DishIngredient> usages = dishIngredientRepository.findByProductId(id);
        if (!usages.isEmpty()) {
            List<String> dishNames = usages.stream()
                    .map(ingredient -> ingredient.getDish().getName())
                    .distinct()
                    .toList();

            throw new ProductInUseException(
                    "Удаление невозможно. Продукт используется в блюдах.",
                    product.getId(),
                    dishNames
            );
        }

        productRepository.delete(product);
    }

    private void fillProduct(Product product, ProductRequest request) {
        product.setName(request.getName().trim());
        product.setPhotos(request.getPhotos() == null ? new ArrayList<>() : request.getPhotos());
        product.setCalories(request.getCalories());
        product.setProteins(request.getProteins());
        product.setFats(request.getFats());
        product.setCarbs(request.getCarbs());
        product.setComposition(request.getComposition());
        product.setCategory(request.getCategory());
        product.setReadiness(request.getReadiness());
        product.setFlags(request.getFlags() == null ? new HashSet<>() : request.getFlags());
    }

    private void validateProductRequest(ProductRequest request) {
        if (request.getPhotos() != null && request.getPhotos().size() > 5) {
            throw new BadRequestException("У продукта может быть максимум 5 фотографий");
        }

        double bjuSum = request.getProteins() + request.getFats() + request.getCarbs();
        if (bjuSum > 100.0) {
            throw new BadRequestException("Сумма БЖУ продукта на 100 г не может превышать 100");
        }
    }

    private boolean matchesSearch(Product product, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }
        return product.getName() != null &&
                product.getName().toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT));
    }

    private boolean matchesFlag(Product product, AdditionalFlag flag, Boolean required) {
        if (required == null) {
            return true;
        }
        boolean contains = product.getFlags() != null && product.getFlags().contains(flag);
        return required == contains;
    }

    private Comparator<Product> getProductComparator(String sortBy) {
        if (sortBy == null || sortBy.isBlank() || sortBy.equalsIgnoreCase("name")) {
            return Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
        }

        return switch (sortBy.toLowerCase(Locale.ROOT)) {
            case "calories" -> Comparator.comparing(Product::getCalories);
            case "proteins" -> Comparator.comparing(Product::getProteins);
            case "fats" -> Comparator.comparing(Product::getFats);
            case "carbs" -> Comparator.comparing(Product::getCarbs);
            default -> Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
        };
    }

    public Product getEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Продукт с id=" + id + " не найден"));
    }

    public ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPhotos(product.getPhotos());
        response.setCalories(product.getCalories());
        response.setProteins(product.getProteins());
        response.setFats(product.getFats());
        response.setCarbs(product.getCarbs());
        response.setComposition(product.getComposition());
        response.setCategory(product.getCategory());
        response.setReadiness(product.getReadiness());
        response.setFlags(product.getFlags() == null ? Set.of() : product.getFlags());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }
}