package de.unimarburg.samplemanagement.repository;
import de.unimarburg.samplemanagement.model.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {
    // does name exist in database
    boolean existsByStudyName(String name);
    Study findByStudyName(String name);

    default Long getIdFromName(String studyName) {
        // get id from name
        if (!existsByStudyName(studyName)) {
            throw new IllegalArgumentException("Study with name " + studyName + " does not exist");
        }
        Study study = findByStudyName(studyName);
        return study.getId();
    }

}
