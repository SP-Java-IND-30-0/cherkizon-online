package ru.relex.service;

import org.springframework.stereotype.Service;
import ru.relex.model.Ad;
import ru.relex.repository.AdServiceRepository;

import java.util.List;

@Service
public class AdServiceImpl implements AdService {
    private final AdService adService;
    private final AdServiceRepository adServiceRepository;

    public AdServiceImpl(AdService  adService, AdServiceRepository adServiceRepository) {
        this.adService = adService;
        this.adServiceRepository = adServiceRepository;

    }

   @Override
    public Ad createAd(Ad ad) {
        return adServiceRepository.save(ad);
    }

    @Override
    public Ad updateAd(int id) {
        return adService.updateAd(id);
    }

    @Override
    public void deleteAd(int ad) {
        adService.deleteAd(ad);
    }

    @Override
    public Ad findAd(int id) {
        return adService.findAd(id);
    }

    public List<Ad> findByAllAds() {
        return adServiceRepository.findByAllAds();
    }
}
