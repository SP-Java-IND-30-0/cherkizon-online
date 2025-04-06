package ru.relex.service;

import ru.relex.model.Ad;

public interface AdService {
    Ad createAd(Ad ad);
    Ad updateAd(int id);
    void deleteAd(int id);
    Ad findAd(int id);
}



