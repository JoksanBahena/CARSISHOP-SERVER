package mx.edu.utez.carsishop.models.subcategory.dtos;

import jakarta.validation.constraints.NotNull;

public class SubcategoryDto {
    @NotNull(groups = {Update.class, ChangeStatus.class})
    private Long id;

    @NotNull(groups = {Register.class, Update.class})
    private String name;

    public SubcategoryDto() {
    }

    public SubcategoryDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public interface Register {
    }

    public interface Update {
    }

    public interface ChangeStatus {
    }
}
