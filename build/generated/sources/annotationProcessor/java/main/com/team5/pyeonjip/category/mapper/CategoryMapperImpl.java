package com.team5.pyeonjip.category.mapper;

import com.team5.pyeonjip.category.dto.CategoryCreateRequest;
import com.team5.pyeonjip.category.dto.CategoryRequest;
import com.team5.pyeonjip.category.dto.CategoryResponse;
import com.team5.pyeonjip.category.entity.Category;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-26T22:43:10+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.2.jar, environment: Java 21 (Oracle Corporation)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public Category toEntity(CategoryRequest request) {
        if ( request == null ) {
            return null;
        }

        Category.CategoryBuilder category = Category.builder();

        category.id( request.getId() );
        category.name( request.getName() );
        category.sort( request.getSort() );
        category.parentId( request.getParentId() );

        return category.build();
    }

    @Override
    public Category toEntity(CategoryCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        Category.CategoryBuilder category = Category.builder();

        category.sort( request.getSort() );
        category.name( request.getName() );

        return category.build();
    }

    @Override
    public CategoryResponse toResponse(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryResponse.CategoryResponseBuilder categoryResponse = CategoryResponse.builder();

        categoryResponse.id( category.getId() );
        categoryResponse.name( category.getName() );
        categoryResponse.sort( category.getSort() );
        categoryResponse.parentId( category.getParentId() );
        categoryResponse.children( categoryListToCategoryResponseList( category.getChildren() ) );

        return categoryResponse.build();
    }

    protected List<CategoryResponse> categoryListToCategoryResponseList(List<Category> list) {
        if ( list == null ) {
            return null;
        }

        List<CategoryResponse> list1 = new ArrayList<CategoryResponse>( list.size() );
        for ( Category category : list ) {
            list1.add( toResponse( category ) );
        }

        return list1;
    }
}
