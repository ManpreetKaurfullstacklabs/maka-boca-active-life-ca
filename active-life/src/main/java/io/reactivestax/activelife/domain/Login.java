package io.reactivestax.activelife.domain;
import io.reactivestax.activelife.domain.membership.FamilyMembers;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "login_requests")
public class Login {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loginId;

    @Column(name = "login_time")
    private LocalDateTime localDateTime;

    @ManyToOne
    @JoinColumn(name = "family_member_id", referencedColumnName = "family_member_id", nullable = false)
    private FamilyMembers familyMember;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "password")
    private String familyPin;

    @Column(name="uuid", unique = true)
    private String verificationUUID;

}
