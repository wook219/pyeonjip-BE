package com.team5.pyeonjip.category.utils;

import com.team5.pyeonjip.category.dto.CategoryRequest;
import com.team5.pyeonjip.category.entity.Category;
import com.team5.pyeonjip.category.repository.CategoryRepository;
import com.team5.pyeonjip.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CategoryUtils {

    private final CategoryRepository categoryRepository;
    private  final ProductRepository productRepository;

    // 최상위 카테고리만 조회
    public List<Category> getParentCategories(List<Category> allCategories) {

        return allCategories.stream()
                .filter(category -> category.getParentId() == null)
                .toList();
    }

    // 부모-자식 카테고리 연결
    public List<Category> createChildrenCategories(List<Category> parentCategories,
                                                           List<Category> allCategories) {

        return parentCategories.stream()
                .map(parent -> {
                    List<Category> children = allCategories.stream()
                            .filter(child -> parent.getId().equals(child.getParentId()))
                            .toList();

                    return parent.toBuilder()
                            .children(children)
                            .build();
                })
                .toList();
    }

    // sort 변경으로 인한 형제 카테고리 sort 업데이트
    public void updateSiblingSort(Category old, CategoryRequest request) {

        // 요청한 순서값이 없으면 재배치 하지 않아도 됨
        if (request.getSort() == null) {
            return;
        }

        List<Category> oldSiblings = categoryRepository.findByParentId(old.getParentId());
        List<Category> newSiblings = categoryRepository.findByParentId(request.getParentId());
        Integer oldSort = old.getSort(), newSort = request.getSort();

        // 형제 카테고리가 변하지 않는다면 해당 뎁스에서만 업데이트, 변한다면 양쪽 뎁스 모두 업데이트
        if (Objects.equals(old.getParentId(), request.getParentId())) {

            // 요청 sort 값이 현재 sort 값보다 클 경우
            if (newSort > oldSort) {

                newSiblings.stream()
                        .filter(category -> category.getSort() > oldSort && category.getSort() <= newSort)
                        .forEach(category -> {
                            categoryRepository.save(category.toBuilder()
                                    .sort(category.getSort() - 1)
                                    .build());
                        });

            // 요청 sort 값이 현재 sort 값보다 작을 경우
            } else if (newSort < oldSort) {

                newSiblings.stream()
                        .filter(category -> category.getSort() >= newSort && category.getSort() < oldSort)
                        .forEach(category -> {
                            categoryRepository.save(category.toBuilder()
                                    .sort(category.getSort() + 1)
                                    .build());
                        });
            }

        } else {

            //기존 형제 카테고리는 사라진 sort 번호를 채워줘야 함
            oldSiblings.stream()
                    .filter(category -> category.getSort() > oldSort)
                    .forEach(category -> {
                        categoryRepository.save(category.toBuilder()
                                .sort(category.getSort()-1)
                                .build());
                    });

            //새 형제 카테고리는 요청 sort 번호를 비워줘야 함
            newSiblings.stream()
                    .filter(category -> category.getSort() >= newSort)
                    .forEach(category -> {
                        categoryRepository.save(category.toBuilder()
                                .sort(category.getSort() + 1)
                                .build());
                    });
        }
    }

    // FIXME: 현재 여러개 삭제는 불가하니 매개변수 타입을 바꿔줘야함
    public void updateSiblingsSort(List<Category> categories) {

        // 삭제할 카테고리의 첫 번째 카테고리 정보 가져오기
        Category old = categories.getFirst();
        Long parentId = old.getParentId();
        Integer sortToDelete = old.getSort();

        // 형제 카테고리 목록 가져오기
        List<Category> oldSiblings = categoryRepository.findByParentId(parentId);

        // 형제 카테고리의 sort 업데이트
        for (Category sibling : oldSiblings) {
            if (sibling.getSort() > sortToDelete) {
                Category updatedSibling = sibling.toBuilder()
                        .sort(sibling.getSort() - 1) // -1로 감소
                        .build();
                categoryRepository.save(updatedSibling);
            }
        }
    }

    // 카테고리 삭제 후, 연관된 프로덕트에 null 적용
    public void deleteCategoriesAndUpdateProducts(List<Category> categories) {

        categories.forEach(category -> {
            productRepository.findByCategoryId(category.getId()).forEach(product -> {
                product.setCategory(null);
            });
        });

        categoryRepository.deleteAll(categories);
    }
}