package io.reactivestax.activelife.domain;
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
    private long loginId;

    @Column(name = "login_time")
    private LocalDateTime localDateTime;

    @OneToOne(fetch = FetchType.LAZY)
    private String familyMemberId;

    @Column(name = "created_by")
    private String createdBy;


}
