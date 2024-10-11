package com.team5.pyeonjip.category.utils;

import com.team5.pyeonjip.category.dto.CategoryRequest;
import com.team5.pyeonjip.category.dto.CategoryResponse;
import com.team5.pyeonjip.category.entity.Category;
import com.team5.pyeonjip.category.mapper.CategoryMapper;
import com.team5.pyeonjip.category.repository.CategoryRepository;
import com.team5.pyeonjip.global.exception.ErrorCode;
import com.team5.pyeonjip.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryUtils {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    // id 유효성 검사
    public void getCategory(Long id) {
        categoryRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    // 최상위 카테고리만 조회
    public List<Category> getParentCategories(List<Category> allCategories) {
        return allCategories.stream()
                .filter(category -> category.getParentId() == null)
                .sorted(Comparator.comparingInt(Category::getSort))
                .toList();
    }

    // 부모-자식 카테고리 연결
    public List<CategoryResponse> createChildrenCategories(List<Category> parentCategories, List<Category> allCategories) {

        List<CategoryResponse> responses = new ArrayList<>();

        for (Category parent : parentCategories) {
            List<CategoryResponse> children = allCategories.stream()
                    .filter(child -> parent.getId().equals(child.getParentId()))
                    .sorted(Comparator.comparingInt(Category::getSort))
                    .map(categoryMapper::toResponse)
                    .toList();

            CategoryResponse parentResponses = CategoryResponse.builder()
                    .id(parent.getId())
                    .sort(parent.getSort())
                    .name(parent.getName())
                    .children(children)
                    .build();

            responses.add(parentResponses);
        }

        return responses;
    }

    // 부모카테고리 유효성 검사
    public void validateParent(Long id, CategoryRequest request) {
        Long requestParentId = request.getParentId();

        // 최상위 카테고리로 이동 (NPE 방지)
        if (requestParentId == null) {
            return;
        }

        // 부모 id가 본인 id 인 경우
        if (requestParentId.equals(id)) {
            throw new GlobalException(ErrorCode.INVALID_PARENT_SELF);
        }

        // 부모 id가 존재하지 않는 id 인 경우
        if (!categoryRepository.existsById(requestParentId)) {
            throw new GlobalException(ErrorCode.INVALID_PARENT);
        }
    }

    // sort 변경으로 인한 형제 카테고리 sort 업데이트
    public void updateSiblingSort(CategoryRequest request) {
        List<Category> siblings = categoryRepository.findByParentId(request.getParentId());

        for (Category sibling : siblings) {
            if (sibling.getSort() >= request.getSort()) {
                Category updatedSibling = sibling.toBuilder()
                        .sort(sibling.getSort() + 1)
                        .build();
                categoryRepository.save(updatedSibling);
            }
        }
    }
}