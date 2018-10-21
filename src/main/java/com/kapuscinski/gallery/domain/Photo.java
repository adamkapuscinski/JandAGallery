package com.kapuscinski.gallery.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Photo.
 */
@Entity
@Table(name = "photo")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Photo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "tag")
    private String tag;

    
    @Lob
    @Column(name = "photography", nullable = false)
    private byte[] photography;

    @Column(name = "photography_content_type", nullable = false)
    private String photographyContentType;

    @Min(value = 1)
    @Max(value = 100)
    @Column(name = "priority")
    private Integer priority;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public Photo category(String category) {
        this.category = category;
        return this;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTag() {
        return tag;
    }

    public Photo tag(String tag) {
        this.tag = tag;
        return this;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public byte[] getPhotography() {
        return photography;
    }

    public Photo photography(byte[] photography) {
        this.photography = photography;
        return this;
    }

    public void setPhotography(byte[] photography) {
        this.photography = photography;
    }

    public String getPhotographyContentType() {
        return photographyContentType;
    }

    public Photo photographyContentType(String photographyContentType) {
        this.photographyContentType = photographyContentType;
        return this;
    }

    public void setPhotographyContentType(String photographyContentType) {
        this.photographyContentType = photographyContentType;
    }

    public Integer getPriority() {
        return priority;
    }

    public Photo priority(Integer priority) {
        this.priority = priority;
        return this;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Photo photo = (Photo) o;
        if (photo.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), photo.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Photo{" +
            "id=" + getId() +
            ", category='" + getCategory() + "'" +
            ", tag='" + getTag() + "'" +
            ", photography='" + getPhotography() + "'" +
            ", photographyContentType='" + getPhotographyContentType() + "'" +
            ", priority=" + getPriority() +
            "}";
    }
}
