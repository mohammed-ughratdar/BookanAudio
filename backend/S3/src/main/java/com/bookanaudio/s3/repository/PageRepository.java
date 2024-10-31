package com.bookanaudio.pages.repository;

import com.bookanaudio.pages.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PageRepository extends JpaRepository<Page, Long> {

    List<Page> findByBookId(Long bookId);
}
