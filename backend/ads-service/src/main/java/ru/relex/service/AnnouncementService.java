package service;
import model.Announcement;
import org.springframework.stereotype.Service;
import repository.AnnouncementRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AnnouncementService extends AnnouncementRepository {
    private final HashMap<Long, Announcement> adsHashMap = new HashMap<>();
    private Long nextId = 1L;

    public List<Announcement> getAllAnnouncement() {
        return new ArrayList<>(adsHashMap.values());
    }

    @Override
    public Announcement createAd(Announcement announcement) {
        announcement.setId(nextId++);
        adsHashMap.put(announcement.getId(), announcement);
        return announcement;
    }

    @Override
    public Announcement updateAd(Long id, Announcement updateAnn) {
        if (!adsHashMap.containsKey(id)) {
            throw new NoSuchElementException("Not found");
        }
        updateAnn.setId(id);
        adsHashMap.put(id,updateAnn);
        return updateAnn;
    }

    @Override
    public Announcement findByAllAds(String announcement) {
        return null;
    }
}
