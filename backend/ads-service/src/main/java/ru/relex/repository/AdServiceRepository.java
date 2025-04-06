package ru.relex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.relex.model.Ad;

import java.util.List;

public interface AdServiceRepository extends JpaRepository<Ad,Long> {
    List<Ad> findByAllAds();
}
