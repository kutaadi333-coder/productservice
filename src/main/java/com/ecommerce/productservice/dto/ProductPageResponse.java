package com.ecommerce.productservice.dto;

import java.util.List;

public class ProductPageResponse {
    private List<ProductResponseDTO> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean isLast;

    // No-argument constructor
    public ProductPageResponse() {}

    // 6-argument constructor
    public ProductPageResponse(List<ProductResponseDTO> content, int pageNumber, int pageSize, long totalElements, int totalPages, boolean isLast) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.isLast = isLast;
    }

    // Getters and Setters
    public List<ProductResponseDTO> getContent() { return content; }
    public void setContent(List<ProductResponseDTO> content) { this.content = content; }

    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public boolean isLast() { return isLast; }
    public void setLast(boolean last) { isLast = last; }
}