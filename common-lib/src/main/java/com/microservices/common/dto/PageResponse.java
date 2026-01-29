package com.microservices.common.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Classe générique pour les réponses paginées
 * 
 * @param <T> le type d'éléments dans la page
 * @author Baye Rane
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {
    
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean empty;

    public PageResponse() {}

    public PageResponse(List<T> content, int pageNumber, int pageSize, long totalElements) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
        this.first = pageNumber == 0;
        this.last = pageNumber >= totalPages - 1;
        this.empty = content == null || content.isEmpty();
    }

    // Crée une PageResponse à partir de données de pagination
    public static <T> PageResponse<T> of(List<T> content, int pageNumber, int pageSize, long totalElements) {
        return new PageResponse<>(content, pageNumber, pageSize, totalElements);
    }

    // Crée une PageResponse vide
    public static <T> PageResponse<T> empty() {
        return new PageResponse<>(List.of(), 0, 0, 0);
    }

    // Vérifie s'il y a une page suivante
    public boolean hasNext() {
        return !last;
    }

    // Vérifie s'il y a une page précédente
    public boolean hasPrevious() {
        return !first;
    }

    // Récupère le numéro de la page suivante
    public int getNextPage() {
        return hasNext() ? pageNumber + 1 : -1;
    }

    // Récupère le numéro de la page précédente
    public int getPreviousPage() {
        return hasPrevious() ? pageNumber - 1 : -1;
    }

    // Getters et Setters
    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    @Override
    public String toString() {
        return "PageResponse{" +
                "content=" + content +
                ", pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                ", first=" + first +
                ", last=" + last +
                ", empty=" + empty +
                '}';
    }
}
