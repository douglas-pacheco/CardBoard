package edu.dio.CardBoard.dto;

import lombok.Getter;
import lombok.Setter;
// import jakarta.validation.constraints.Min; // No longer needed for count
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid; // For validating elements within the list (if they were objects)
import java.util.ArrayList; // For initialization
import java.util.List;

@Getter
@Setter
public class BoardCreationDTO {

    @NotBlank(message = "Board name cannot be empty")
    @Size(min = 3, max = 50, message = "Board name must be between 3 and 50 characters")
    private String boardName;

    // This list will hold the names of dynamically added pending columns
    // We can add validation for each String in the list if desired
    @Valid // If pendingColumnNames were objects, this would validate them
    @Size(max = 10, message = "Maximum 10 additional pending columns allowed") // Example max size
    private List<@NotBlank(message = "Pending column name cannot be empty")
    @Size(min = 3, max = 50, message = "Pending column name must be between 3 and 50 characters") String> pendingColumnNames;

    @NotBlank(message = "Initial column name cannot be empty")
    @Size(min = 3, max = 50, message = "Initial column name must be between 3 and 50 characters")
    private String initialColumnName;

    @NotBlank(message = "Final column name cannot be empty")
    @Size(min = 3, max = 50, message = "Final column name must be between 3 and 50 characters")
    private String finalColumnName;

    @NotBlank(message = "Cancellation column name cannot be empty")
    @Size(min = 3, max = 50, message = "Cancellation column name must be between 3 and 50 characters")
    private String cancelColumnName;

    public BoardCreationDTO() {
        // Initialize the list to prevent NullPointerExceptions when adding elements via JS
        this.pendingColumnNames = new ArrayList<>();
    }
}
