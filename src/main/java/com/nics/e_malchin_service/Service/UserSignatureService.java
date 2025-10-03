package com.nics.e_malchin_service.Service;
import com.nics.e_malchin_service.DAO.UserSignatureDAO;
import com.nics.e_malchin_service.Entity.UserSignature;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserSignatureService {
    private final UserSignatureDAO repository;

    public UserSignature saveSignature(Long userId, MultipartFile file) throws Exception {
        UserSignature signature = new UserSignature();
        signature.setUserId(userId);
        signature.setSignature(file.getBytes());
        return repository.save(signature);
    }

    public byte[] getSignature(Long id) {
        return repository.findById(Math.toIntExact(id)).map(UserSignature::getSignature).orElse(null);
    }
}
