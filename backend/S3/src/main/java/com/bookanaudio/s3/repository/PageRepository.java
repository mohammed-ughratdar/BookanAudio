package com.bookanaudio.s3.repository;

import com.bookanaudio.s3.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PageRepository extends JpaRepository<Page, Long> {

}
