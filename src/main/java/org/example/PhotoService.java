package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;

    public String getPhotoUrlById(Long id) {
        Optional<PhotoEntity> photo = photoRepository.findById(id);
        return photo.get().getUrl();
    }

    public void savePhoto(PhotoEntity photoEntity) {
        photoRepository.save(photoEntity);
    }
}
