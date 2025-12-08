package com.doanltmmt.Backend.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "progress_report")
public class ProgressReport {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private java.time.LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String lecturerComment;

    private String status; // TODO, IN_PROGRESS, DONE
}

