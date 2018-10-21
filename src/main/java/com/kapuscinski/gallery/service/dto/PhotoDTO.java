package com.kapuscinski.gallery.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;

/**
 * A DTO for the Photo entity.
 */
public class PhotoDTO implements Serializable {

    private Long id;

    @NotNull
    private String category;

    private String tag;

    
    @Lob
    private byte[] photography;
    private String photographyContentType;

    @Min(value = 1)
    @Max(value = 100)
    private Integer priority;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public byte[] getPhotography() {
        return photography;
    }

    public void setPhotography(byte[] photography) {
        this.photography = photography;
    }

    public String getPhotographyContentType() {
        return photographyContentType;
    }

    public void setPhotographyContentType(String photographyContentType) {
        this.photographyContentType = photographyContentType;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PhotoDTO photoDTO = (PhotoDTO) o;
        if (photoDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), photoDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PhotoDTO{" +
            "id=" + getId() +
            ", category='" + getCategory() + "'" +
            ", tag='" + getTag() + "'" +
            ", photography='" + getPhotography() + "'" +
            ", priority=" + getPriority() +
            "}";
    }
}
