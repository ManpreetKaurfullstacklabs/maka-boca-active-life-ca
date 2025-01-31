package io.reactivestax.activelife.domain.membership;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.reactivestax.activelife.Enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "family_group")
public class FamilyGroups {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "family_group_id")
    private Long familyGroupId;

    @Column(name = "family_pin")
    private String familyPin;

    @Column(name = "credits")
    private Long credits;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "createdBy")
    private Long createdBy;

    @Column(name = "last_updated_by")
    private Long lastUpdatedBy;

    @JsonManagedReference
    @OneToMany(mappedBy = "familyGroupId" )
    private List<FamilyMembers> familyMembers;
}
