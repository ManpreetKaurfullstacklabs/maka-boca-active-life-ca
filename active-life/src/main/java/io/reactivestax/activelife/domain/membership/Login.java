package io.reactivestax.activelife.domain.membership;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

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
    @JoinColumn(name = "family_member_id", referencedColumnName = "family_member_id")
    private FamilyMembers familyMember;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "password")
    private String familyPin;

    @Column(name="uuid", unique = true)
    private String verificationUUID;


}
