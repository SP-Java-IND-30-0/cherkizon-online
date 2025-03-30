package repository;

import model.Announcement;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository extends JpaRepository <Announcement, Long>  {
    Announcement createAd(Announcement announcement);
    Announcement updateAd(Long id, Announcement updateAnn);
    Announcement findByAllAds(String announcement);
}
