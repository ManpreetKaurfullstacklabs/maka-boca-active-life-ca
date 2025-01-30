package io.reactivestax.activelife.domain.facility;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "functions")
public class Functions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FUNCTION_ID")
    private Long id;

    @Column(name = "DESCRIPTION", length = 400)
    private String description;

    @Column(name = "CREATED_TS")
    private Date createdTs;

    @Column(name = "LAST_UPDATED_TS")
    private Date lastUpdatedTs;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "LAST_UPDATED_BY")
    private Long lastUpdatedBy;

    @OneToMany(mappedBy = "function")
    private List<FacilityFunctions> facilityFunctions;
}
