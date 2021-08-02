package actions.views;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportView {
    private Integer id;

    private EmployeeView employee;

    private String content;

    private String title;

    private LocalDate reportDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
