package mx.edu.utez.carsishop.models.category.dtos;

import jakarta.validation.constraints.NotNull;

public class CategoryDto {
    @NotNull(groups = {Update.class, ChangeStatus.class})
    private Long id;
    @NotNull(groups = {Register.class, Update.class})
    private String name;

    @NotNull(groups = {Register.class})
    private boolean status;

    public CategoryDto() {
    }

    public CategoryDto(Long id, String name, boolean status) {
        this.id = id;
        this.name = name;
        this.status = status;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public interface Register {
    }

    public interface Update {
    }

    public interface ChangeStatus {
    }
}
