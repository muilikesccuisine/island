package com.my.survivor.dto.rep;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
public class SurvivorRep {

    private Long id;
    private String userId;

    private String name;
    private Integer gender;
    private Integer age;

    private Integer physicalState;

    private String medicalHistory;

    private Integer islandGrade;

    private Integer contributionPoint;

    // 社交展示
    private LocalDateTime lastActiveAt;
    private String avatarUrl;

}
