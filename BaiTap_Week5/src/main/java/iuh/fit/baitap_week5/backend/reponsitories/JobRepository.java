package iuh.fit.baitap_week5.backend.reponsitories;

import iuh.fit.baitap_week5.backend.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository <Job, Long> {
}