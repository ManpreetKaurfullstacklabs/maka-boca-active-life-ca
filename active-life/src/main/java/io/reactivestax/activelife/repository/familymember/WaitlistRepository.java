package io.reactivestax.activelife.repository.familymember;

import io.reactivestax.activelife.Enums.IsWaitListed;
import io.reactivestax.activelife.domain.WaitList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WaitlistRepository extends JpaRepository<WaitList, Long>{
  //  Optional<WaitList>findFirstByOfferedCoursesIdAndStatus(Long offereCourseId, IsWaitListed waitListed);
}
