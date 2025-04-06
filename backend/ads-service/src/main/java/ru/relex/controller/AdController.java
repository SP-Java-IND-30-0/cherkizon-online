package ru.relex.controller;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.relex.model.Ad;
import ru.relex.service.AdServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/ads")
public class AdController {
    private final AdServiceImpl serviceImpl;

    public AdController(AdServiceImpl serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @PostMapping("/ads/{id}")
    public Ad createAd(@RequestBody Ad ad) {
        return serviceImpl.createAd(ad);
    }

    @GetMapping("/ads/{id}")
    public ResponseEntity<Ad> getAd(@PathVariable int id) {
        Ad ad = serviceImpl.findAd(id);
        if (ad == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ad);
    }

    @GetMapping
    public ResponseEntity<List<Ad>> findByAllAds() {
        return ResponseEntity.ok(serviceImpl.findByAllAds());
    }

    @DeleteMapping("/ads/{id}")
    public ResponseEntity<Ad> deleteAd(@PathVariable int id) {
        serviceImpl.deleteAd(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/ads/{id}")
    public RequestEntity<Ad> updateAd(@RequestBody Ad ad, @PathVariable int id) {
        return null;
    }
}

