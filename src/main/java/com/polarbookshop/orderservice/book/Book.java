package com.polarbookshop.orderservice.book;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Book {

    private String isbn;

    private String title;

    private String author;

    private Double price;
}
