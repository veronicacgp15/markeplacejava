package com.vgarcia.marketplace.domain.exception;

public class CategoryCycleException extends RuntimeException {
  public CategoryCycleException(Long categoryId, Long parentId) {
    super("Se ha detectado un ciclo: La categoría " + categoryId + " no puede tener como padre a la categoría " + parentId + " porque es uno de sus descendientes.");
  }

  public CategoryCycleException(Long categoryId, Long parentId, String message) {
    super(message);
  }
}
