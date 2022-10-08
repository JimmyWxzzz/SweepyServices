package com.sweepy.repository;

import com.sweepy.database.UrlTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;


@Repository
public interface UrlRepository extends JpaRepository<UrlTable, Integer> {
    UrlTable findByShortUrl(String shortUrl);
    UrlTable findByLongUrl(String longUrl);

    UrlTable findFirstByOrderByIdDesc();
}
