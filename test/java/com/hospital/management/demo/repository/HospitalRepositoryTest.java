package com.hospital.management.demo.repository;

import com.hospital.management.demo.model.entity.Hospital;
import com.hospital.management.demo.model.enums.OperationalStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class HospitalRepositoryTest {

    @Autowired
    private HospitalRepository hospitalRepository;

    private Hospital testHospital;

    @BeforeEach
    void setUp() {
        testHospital = Hospital.builder()
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .operationalStatus(OperationalStatus.OPERATIONAL)
                .district("Test District")
                .build();
        hospitalRepository.save(testHospital);
    }

    @Test
    void testFindByOperationalStatus() {
        // Cover findByOperationalStatus method
        List<Hospital> hospitals = hospitalRepository.findByOperationalStatus(OperationalStatus.OPERATIONAL);

        assertFalse(hospitals.isEmpty());
        assertEquals(OperationalStatus.OPERATIONAL, hospitals.get(0).getOperationalStatus());
    }

    @Test
    void testFindByOperationalStatusNot() {
        // Cover findByOperationalStatusNot method
        Hospital closedHospital = Hospital.builder()
                .name("Closed Hospital")
                .latitude(40.0)
                .longitude(117.0)
                .operationalStatus(OperationalStatus.CLOSED_OTHER)
                .build();
        hospitalRepository.save(closedHospital);

        List<Hospital> hospitals = hospitalRepository.findByOperationalStatusNot(OperationalStatus.CLOSED_OTHER);

        assertEquals(1, hospitals.size());
        assertEquals(OperationalStatus.OPERATIONAL, hospitals.get(0).getOperationalStatus());
    }

    @Test
    void testFindByDistrict() {
        // Cover findByDistrict method
        List<Hospital> hospitals = hospitalRepository.findByDistrict("Test District");

        assertFalse(hospitals.isEmpty());
        assertEquals("Test District", hospitals.get(0).getDistrict());
    }

    @Test
    void testFindByName_Exists() {
        // Branch: Hospital exists with name
        Optional<Hospital> found = hospitalRepository.findByName("Test Hospital");

        assertTrue(found.isPresent());
        assertEquals("Test Hospital", found.get().getName());
    }

    @Test
    void testFindByName_NotExists() {
        // Branch: Hospital does not exist with name
        Optional<Hospital> found = hospitalRepository.findByName("Nonexistent Hospital");

        assertFalse(found.isPresent());
    }
}
