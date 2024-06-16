package de.unimarburg.samplemanagement.repository;

import de.unimarburg.samplemanagement.model.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {

    @Query("select c from Study c " +
            "where lower(c.studyName) like lower(concat('%', :searchTerm, '%'))")
    List<Study> searchName(@Param("searchTerm") String searchTerm);
    @Query(value = "SELECT * FROM Study c WHERE TO_CHAR(c.study_Date, 'YYYY-MM-DD HH24:MI:SS') LIKE CONCAT('%', :searchTerm, '%')", nativeQuery = true)
    List<Study> searchDate(@Param("searchTerm") String searchTerm);

    @Query(value = "SELECT * FROM Study c WHERE " +
            "(LOWER(c.study_Name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND TO_CHAR(c.study_Date, 'YYYY-MM-DD HH24:MI:SS') LIKE CONCAT('%', :dateText, '%')",
            nativeQuery = true)
    List<Study> findByNameAndBirthDate(@Param("name") String name, @Param("dateText") String dateText);

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
