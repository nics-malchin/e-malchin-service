package com.nics.e_malchin_service.Entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_signature")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSignature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] signature;

    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();
}
