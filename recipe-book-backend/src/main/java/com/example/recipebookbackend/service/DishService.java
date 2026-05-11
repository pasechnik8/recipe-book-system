package com.example.recipebookbackend.service;

import com.example.recipebookbackend.dto.DishCalculationResponse;
import com.example.recipebookbackend.dto.DishIngredientRequest;
import com.example.recipebookbackend.dto.DishIngredientResponse;
import com.example.recipebookbackend.dto.DishRequest;
import com.example.recipebookbackend.dto.DishResponse;
import com.example.recipebookbackend.entity.Dish;
import com.example.recipebookbackend.entity.DishIngredient;
import com.example.recipebookbackend.entity.Product;
import com.example.recipebookbackend.enums.AdditionalFlag;
import com.example.recipebookbackend.enums.DishCategory;
import com.example.recipebookbackend.exception.BadRequestException;
import com.example.recipebookbackend.exception.NotFoundException;
import com.example.recipebookbackend.repository.DishRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DishService {

    private final DishRepository dishRepository;
    private final ProductService productService;

    public DishService(DishRepository dishRepository, ProductService productService) {
        this.dishRepository = dishRepository;
        this.productService = productService;
    }

    @Transactional
    public DishResponse create(DishRequest request) {
        validateDishRequest(request);

        MacroParseResult macroResult = parseMacro(request.getName());
        NutritionCalculation calculation = calculateNutrition(request.getIngredients());
        Set<AdditionalFlag> availableFlags = calculateAvailableFlags(request.getIngredients());

        Dish dish = new Dish();
        fillDish(dish, request, macroResult, calculation, availableFlags);

        Dish saved = dishRepository.save(dish);
        return toResponse(saved);
    }

    @Transactional
    public DishResponse update(Long id, DishRequest request) {
        validateDishRequest(request);

        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Блюдо с id=" + id + " не найдено"));

        MacroParseResult macroResult = parseMacro(request.getName());
        NutritionCalculation calculation = calculateNutrition(request.getIngredients());
        Set<AdditionalFlag> availableFlags = calculateAvailableFlags(request.getIngredients());

        fillDish(dish, request, macroResult, calculation, availableFlags);

        Dish saved = dishRepository.save(dish);
        return toResponse(saved);
    }

    public DishResponse getById(Long id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Блюдо с id=" + id + " не найдено"));
        return toResponse(dish);
    }

    public List<DishResponse> getAll(String search,
                                     DishCategory category,
                                     Boolean vegan,
                                     Boolean glutenFree,
                                     Boolean sugarFree) {
        List<Dish> dishes = dishRepository.findAll();

        return dishes.stream()
                .filter(d -> matchesSearch(d, search))
                .filter(d -> category == null || d.getCategory() == category)
                .filter(d -> matchesFlag(d, AdditionalFlag.VEGAN, vegan))
                .filter(d -> matchesFlag(d, AdditionalFlag.GLUTEN_FREE, glutenFree))
                .filter(d -> matchesFlag(d, AdditionalFlag.SUGAR_FREE, sugarFree))
                .sorted(Comparator.comparing(Dish::getName, String.CASE_INSENSITIVE_ORDER))
                .map(this::toResponse)
                .toList();
    }

    public void delete(Long id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Блюдо с id=" + id + " не найдено"));
        dishRepository.delete(dish);
    }

    public DishCalculationResponse calculateDraft(DishRequest request) {
        validateDishRequestForCalculation(request);

        MacroParseResult macroResult = parseMacro(request.getName());
        NutritionCalculation calculation = calculateNutrition(request.getIngredients());
        Set<AdditionalFlag> availableFlags = calculateAvailableFlags(request.getIngredients());

        DishCalculationResponse response = new DishCalculationResponse();
        response.setNormalizedName(macroResult.cleanedName());
        response.setCategoryFromMacro(macroResult.category());
        response.setCalories(round(calculation.calories()));
        response.setProteins(round(calculation.proteins()));
        response.setFats(round(calculation.fats()));
        response.setCarbs(round(calculation.carbs()));
        response.setAvailableFlags(availableFlags);

        return response;
    }

    private void fillDish(Dish dish,
                          DishRequest request,
                          MacroParseResult macroResult,
                          NutritionCalculation calculation,
                          Set<AdditionalFlag> availableFlags) {

        String finalName = macroResult.cleanedName();
        DishCategory finalCategory = request.getCategory() != null
                ? request.getCategory()
                : macroResult.category();

        if (finalCategory == null) {
            throw new BadRequestException("Категория блюда обязательна. Укажите её в поле category или через макрос в названии");
        }

        Set<AdditionalFlag> requestedFlags = request.getFlags() == null ? new HashSet<>() : request.getFlags();
        Set<AdditionalFlag> finalFlags = new HashSet<>(requestedFlags);
        finalFlags.retainAll(availableFlags);

        if (request.getPhotos() != null && request.getPhotos().size() > 5) {
            throw new BadRequestException("У блюда может быть максимум 5 фотографий");
        }

        if (request.getProteins() + request.getFats() + request.getCarbs() > request.getPortionSize()) {
            throw new BadRequestException("Сумма БЖУ блюда на порцию не может превышать размер порции");
        }

        dish.setName(finalName);
        dish.setPhotos(request.getPhotos() == null ? new ArrayList<>() : request.getPhotos());
        dish.setCalories(request.getCalories());
        dish.setProteins(request.getProteins());
        dish.setFats(request.getFats());
        dish.setCarbs(request.getCarbs());
        dish.setPortionSize(request.getPortionSize());
        dish.setCategory(finalCategory);
        dish.setFlags(finalFlags);

        dish.clearIngredients();
        for (DishIngredientRequest ingredientRequest : request.getIngredients()) {
            Product product = productService.getEntityById(ingredientRequest.getProductId());

            DishIngredient ingredient = new DishIngredient();
            ingredient.setProduct(product);
            ingredient.setQuantity(ingredientRequest.getQuantity());

            dish.addIngredient(ingredient);
        }
    }

    private boolean matchesSearch(Dish dish, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }
        return dish.getName() != null &&
                dish.getName().toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT));
    }

    private boolean matchesFlag(Dish dish, AdditionalFlag flag, Boolean required) {
        if (required == null) {
            return true;
        }
        boolean contains = dish.getFlags() != null && dish.getFlags().contains(flag);
        return required == contains;
    }

    private void validateDishRequest(DishRequest request) {
        validateDishRequestForCalculation(request);

        if (request.getCalories() == null ||
                request.getProteins() == null ||
                request.getFats() == null ||
                request.getCarbs() == null) {
            throw new BadRequestException("Для сохранения блюда нужно передать calories, proteins, fats, carbs");
        }
    }

    private void validateDishRequestForCalculation(DishRequest request) {
        if (request.getPhotos() != null && request.getPhotos().size() > 5) {
            throw new BadRequestException("У блюда может быть максимум 5 фотографий");
        }

        if (request.getIngredients() == null || request.getIngredients().isEmpty()) {
            throw new BadRequestException("Состав блюда должен содержать минимум один продукт");
        }

        if (request.getPortionSize() == null || request.getPortionSize() <= 0) {
            throw new BadRequestException("Размер порции должен быть больше 0");
        }

        for (DishIngredientRequest ingredient : request.getIngredients()) {
            if (ingredient.getQuantity() == null || ingredient.getQuantity() <= 0) {
                throw new BadRequestException("Количество каждого продукта должно быть больше 0");
            }
        }
    }

    private NutritionCalculation calculateNutrition(List<DishIngredientRequest> ingredients) {
        double calories = 0.0;
        double proteins = 0.0;
        double fats = 0.0;
        double carbs = 0.0;

        for (DishIngredientRequest item : ingredients) {
            Product product = productService.getEntityById(item.getProductId());
            double multiplier = item.getQuantity() / 100.0;

            calories += product.getCalories() * multiplier;
            proteins += product.getProteins() * multiplier;
            fats += product.getFats() * multiplier;
            carbs += product.getCarbs() * multiplier;
        }

        return new NutritionCalculation(
                round(calories),
                round(proteins),
                round(fats),
                round(carbs)
        );
    }

    private Set<AdditionalFlag> calculateAvailableFlags(List<DishIngredientRequest> ingredients) {
        Set<AdditionalFlag> available = EnumSet.allOf(AdditionalFlag.class);

        for (DishIngredientRequest item : ingredients) {
            Product product = productService.getEntityById(item.getProductId());
            Set<AdditionalFlag> productFlags = product.getFlags() == null ? Set.of() : product.getFlags();
            available.retainAll(productFlags);
        }

        return available;
    }

    public DishResponse toResponse(Dish dish) {
        DishResponse response = new DishResponse();
        response.setId(dish.getId());
        response.setName(dish.getName());
        response.setPhotos(dish.getPhotos());
        response.setCalories(dish.getCalories());
        response.setProteins(dish.getProteins());
        response.setFats(dish.getFats());
        response.setCarbs(dish.getCarbs());
        response.setPortionSize(dish.getPortionSize());
        response.setCategory(dish.getCategory());
        response.setFlags(dish.getFlags() == null ? Set.of() : dish.getFlags());
        response.setIngredients(
                dish.getIngredients().stream().map(this::toIngredientResponse).toList()
        );

        List<DishIngredientRequest> ingredientRequests = dish.getIngredients().stream()
                .map(ingredient -> {
                    DishIngredientRequest request = new DishIngredientRequest();
                    request.setProductId(ingredient.getProduct().getId());
                    request.setQuantity(ingredient.getQuantity());
                    return request;
                }).toList();

        NutritionCalculation calculation = calculateNutrition(ingredientRequests);
        response.setAutoCalculatedCalories(calculation.calories());
        response.setAutoCalculatedProteins(calculation.proteins());
        response.setAutoCalculatedFats(calculation.fats());
        response.setAutoCalculatedCarbs(calculation.carbs());
        response.setAvailableFlags(calculateAvailableFlags(ingredientRequests));
        response.setCreatedAt(dish.getCreatedAt());
        response.setUpdatedAt(dish.getUpdatedAt());

        return response;
    }

    private DishIngredientResponse toIngredientResponse(DishIngredient ingredient) {
        DishIngredientResponse response = new DishIngredientResponse();
        response.setProductId(ingredient.getProduct().getId());
        response.setProductName(ingredient.getProduct().getName());
        response.setQuantity(ingredient.getQuantity());
        return response;
    }

    private MacroParseResult parseMacro(String rawName) {
        if (rawName == null) {
            return new MacroParseResult(null, null);
        }

        List<MacroDefinition> macros = List.of(
                new MacroDefinition("!десерт", DishCategory.DESSERT),
                new MacroDefinition("!первое", DishCategory.FIRST_COURSE),
                new MacroDefinition("!второе", DishCategory.SECOND_COURSE),
                new MacroDefinition("!напиток", DishCategory.DRINK),
                new MacroDefinition("!салат", DishCategory.SALAD),
                new MacroDefinition("!суп", DishCategory.SOUP),
                new MacroDefinition("!перекус", DishCategory.SNACK)
        );

        String lower = rawName.toLowerCase(Locale.ROOT);

        int bestIndex = Integer.MAX_VALUE;
        MacroDefinition found = null;

        for (MacroDefinition macro : macros) {
            int idx = lower.indexOf(macro.token());
            if (idx >= 0 && idx < bestIndex) {
                bestIndex = idx;
                found = macro;
            }
        }

        if (found == null) {
            return new MacroParseResult(rawName.trim(), null);
        }

        String cleaned = removeFirstOccurrence(rawName, found.token()).trim().replaceAll("\\s+", " ");
        return new MacroParseResult(cleaned, found.category());
    }

    private String removeFirstOccurrence(String text, String token) {
        String lowerText = text.toLowerCase(Locale.ROOT);
        String lowerToken = token.toLowerCase(Locale.ROOT);
        int index = lowerText.indexOf(lowerToken);

        if (index < 0) {
            return text;
        }

        return text.substring(0, index) + text.substring(index + token.length());
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private record NutritionCalculation(Double calories, Double proteins, Double fats, Double carbs) {
    }

    private record MacroParseResult(String cleanedName, DishCategory category) {
    }

    private record MacroDefinition(String token, DishCategory category) {
    }
}