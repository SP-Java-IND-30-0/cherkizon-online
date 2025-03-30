import model.Announcement;

import java.util.List;

@RestController
@RequestMapping("/ads")
public class AnnouncementController {
    private final Announcement service;

    public AnnouncementController(Announcement service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity <List<Announcement>> getAllAds() {
        return ResponseEntity.ok(service.getAllAds());
    }

    @GetMapping("/ads/{id}")
    public ResponseEntity<Announcement> getAd(@PathVariable Long id) {
        return ResponseEntity.ok(adService.getAdById(id));
    }

    @DeleteMapping("/ads/{id}")
    public ResponseEntity<Announcement> deleteAd(@PathVariable Long id) {
        service.deleteAd(id);
        return ResponseEntity.noContent().build();
    }

}

