package org.example;

import org.example.model.DroolFiles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DroolFilesRepository extends JpaRepository<DroolFiles, Integer> {
}