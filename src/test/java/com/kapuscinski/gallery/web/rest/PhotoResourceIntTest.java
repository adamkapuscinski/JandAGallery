package com.kapuscinski.gallery.web.rest;

import com.kapuscinski.gallery.JandAGalleryApp;

import com.kapuscinski.gallery.domain.Photo;
import com.kapuscinski.gallery.repository.PhotoRepository;
import com.kapuscinski.gallery.service.PhotoService;
import com.kapuscinski.gallery.service.dto.PhotoDTO;
import com.kapuscinski.gallery.service.mapper.PhotoMapper;
import com.kapuscinski.gallery.web.rest.errors.ExceptionTranslator;
import com.kapuscinski.gallery.service.dto.PhotoCriteria;
import com.kapuscinski.gallery.service.PhotoQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.persistence.EntityManager;
import java.util.List;


import static com.kapuscinski.gallery.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PhotoResource REST controller.
 *
 * @see PhotoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JandAGalleryApp.class)
public class PhotoResourceIntTest {

    private static final String DEFAULT_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORY = "BBBBBBBBBB";

    private static final String DEFAULT_TAG = "AAAAAAAAAA";
    private static final String UPDATED_TAG = "BBBBBBBBBB";

    private static final byte[] DEFAULT_PHOTOGRAPHY = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTOGRAPHY = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PHOTOGRAPHY_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTOGRAPHY_CONTENT_TYPE = "image/png";

    private static final Integer DEFAULT_PRIORITY = 1;
    private static final Integer UPDATED_PRIORITY = 2;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private PhotoMapper photoMapper;
    
    @Autowired
    private PhotoService photoService;

    @Autowired
    private PhotoQueryService photoQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPhotoMockMvc;

    private Photo photo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PhotoResource photoResource = new PhotoResource(photoService, photoQueryService);
        this.restPhotoMockMvc = MockMvcBuilders.standaloneSetup(photoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Photo createEntity(EntityManager em) {
        Photo photo = new Photo()
            .category(DEFAULT_CATEGORY)
            .tag(DEFAULT_TAG)
            .photography(DEFAULT_PHOTOGRAPHY)
            .photographyContentType(DEFAULT_PHOTOGRAPHY_CONTENT_TYPE)
            .priority(DEFAULT_PRIORITY);
        return photo;
    }

    @Before
    public void initTest() {
        photo = createEntity(em);
    }

    @Test
    @Transactional
    public void createPhoto() throws Exception {
        int databaseSizeBeforeCreate = photoRepository.findAll().size();

        // Create the Photo
        PhotoDTO photoDTO = photoMapper.toDto(photo);
        restPhotoMockMvc.perform(post("/api/photos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(photoDTO)))
            .andExpect(status().isCreated());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeCreate + 1);
        Photo testPhoto = photoList.get(photoList.size() - 1);
        assertThat(testPhoto.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testPhoto.getTag()).isEqualTo(DEFAULT_TAG);
        assertThat(testPhoto.getPhotography()).isEqualTo(DEFAULT_PHOTOGRAPHY);
        assertThat(testPhoto.getPhotographyContentType()).isEqualTo(DEFAULT_PHOTOGRAPHY_CONTENT_TYPE);
        assertThat(testPhoto.getPriority()).isEqualTo(DEFAULT_PRIORITY);
    }

    @Test
    @Transactional
    public void createPhotoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = photoRepository.findAll().size();

        // Create the Photo with an existing ID
        photo.setId(1L);
        PhotoDTO photoDTO = photoMapper.toDto(photo);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPhotoMockMvc.perform(post("/api/photos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(photoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkCategoryIsRequired() throws Exception {
        int databaseSizeBeforeTest = photoRepository.findAll().size();
        // set the field null
        photo.setCategory(null);

        // Create the Photo, which fails.
        PhotoDTO photoDTO = photoMapper.toDto(photo);

        restPhotoMockMvc.perform(post("/api/photos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(photoDTO)))
            .andExpect(status().isBadRequest());

        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPhotos() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList
        restPhotoMockMvc.perform(get("/api/photos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(photo.getId().intValue())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].tag").value(hasItem(DEFAULT_TAG.toString())))
            .andExpect(jsonPath("$.[*].photographyContentType").value(hasItem(DEFAULT_PHOTOGRAPHY_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photography").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTOGRAPHY))))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY)));
    }
    
    @Test
    @Transactional
    public void getPhoto() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get the photo
        restPhotoMockMvc.perform(get("/api/photos/{id}", photo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(photo.getId().intValue()))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.tag").value(DEFAULT_TAG.toString()))
            .andExpect(jsonPath("$.photographyContentType").value(DEFAULT_PHOTOGRAPHY_CONTENT_TYPE))
            .andExpect(jsonPath("$.photography").value(Base64Utils.encodeToString(DEFAULT_PHOTOGRAPHY)))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY));
    }

    @Test
    @Transactional
    public void getAllPhotosByCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where category equals to DEFAULT_CATEGORY
        defaultPhotoShouldBeFound("category.equals=" + DEFAULT_CATEGORY);

        // Get all the photoList where category equals to UPDATED_CATEGORY
        defaultPhotoShouldNotBeFound("category.equals=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    public void getAllPhotosByCategoryIsInShouldWork() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where category in DEFAULT_CATEGORY or UPDATED_CATEGORY
        defaultPhotoShouldBeFound("category.in=" + DEFAULT_CATEGORY + "," + UPDATED_CATEGORY);

        // Get all the photoList where category equals to UPDATED_CATEGORY
        defaultPhotoShouldNotBeFound("category.in=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    public void getAllPhotosByCategoryIsNullOrNotNull() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where category is not null
        defaultPhotoShouldBeFound("category.specified=true");

        // Get all the photoList where category is null
        defaultPhotoShouldNotBeFound("category.specified=false");
    }

    @Test
    @Transactional
    public void getAllPhotosByTagIsEqualToSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where tag equals to DEFAULT_TAG
        defaultPhotoShouldBeFound("tag.equals=" + DEFAULT_TAG);

        // Get all the photoList where tag equals to UPDATED_TAG
        defaultPhotoShouldNotBeFound("tag.equals=" + UPDATED_TAG);
    }

    @Test
    @Transactional
    public void getAllPhotosByTagIsInShouldWork() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where tag in DEFAULT_TAG or UPDATED_TAG
        defaultPhotoShouldBeFound("tag.in=" + DEFAULT_TAG + "," + UPDATED_TAG);

        // Get all the photoList where tag equals to UPDATED_TAG
        defaultPhotoShouldNotBeFound("tag.in=" + UPDATED_TAG);
    }

    @Test
    @Transactional
    public void getAllPhotosByTagIsNullOrNotNull() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where tag is not null
        defaultPhotoShouldBeFound("tag.specified=true");

        // Get all the photoList where tag is null
        defaultPhotoShouldNotBeFound("tag.specified=false");
    }

    @Test
    @Transactional
    public void getAllPhotosByPriorityIsEqualToSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where priority equals to DEFAULT_PRIORITY
        defaultPhotoShouldBeFound("priority.equals=" + DEFAULT_PRIORITY);

        // Get all the photoList where priority equals to UPDATED_PRIORITY
        defaultPhotoShouldNotBeFound("priority.equals=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    public void getAllPhotosByPriorityIsInShouldWork() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where priority in DEFAULT_PRIORITY or UPDATED_PRIORITY
        defaultPhotoShouldBeFound("priority.in=" + DEFAULT_PRIORITY + "," + UPDATED_PRIORITY);

        // Get all the photoList where priority equals to UPDATED_PRIORITY
        defaultPhotoShouldNotBeFound("priority.in=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    public void getAllPhotosByPriorityIsNullOrNotNull() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where priority is not null
        defaultPhotoShouldBeFound("priority.specified=true");

        // Get all the photoList where priority is null
        defaultPhotoShouldNotBeFound("priority.specified=false");
    }

    @Test
    @Transactional
    public void getAllPhotosByPriorityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where priority greater than or equals to DEFAULT_PRIORITY
        defaultPhotoShouldBeFound("priority.greaterOrEqualThan=" + DEFAULT_PRIORITY);

        // Get all the photoList where priority greater than or equals to (DEFAULT_PRIORITY + 1)
        defaultPhotoShouldNotBeFound("priority.greaterOrEqualThan=" + (DEFAULT_PRIORITY + 1));
    }

    @Test
    @Transactional
    public void getAllPhotosByPriorityIsLessThanSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where priority less than or equals to DEFAULT_PRIORITY
        defaultPhotoShouldNotBeFound("priority.lessThan=" + DEFAULT_PRIORITY);

        // Get all the photoList where priority less than or equals to (DEFAULT_PRIORITY + 1)
        defaultPhotoShouldBeFound("priority.lessThan=" + (DEFAULT_PRIORITY + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultPhotoShouldBeFound(String filter) throws Exception {
        restPhotoMockMvc.perform(get("/api/photos?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(photo.getId().intValue())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].tag").value(hasItem(DEFAULT_TAG.toString())))
            .andExpect(jsonPath("$.[*].photographyContentType").value(hasItem(DEFAULT_PHOTOGRAPHY_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photography").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTOGRAPHY))))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY)));

        // Check, that the count call also returns 1
        restPhotoMockMvc.perform(get("/api/photos/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultPhotoShouldNotBeFound(String filter) throws Exception {
        restPhotoMockMvc.perform(get("/api/photos?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPhotoMockMvc.perform(get("/api/photos/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingPhoto() throws Exception {
        // Get the photo
        restPhotoMockMvc.perform(get("/api/photos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePhoto() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        int databaseSizeBeforeUpdate = photoRepository.findAll().size();

        // Update the photo
        Photo updatedPhoto = photoRepository.findById(photo.getId()).get();
        // Disconnect from session so that the updates on updatedPhoto are not directly saved in db
        em.detach(updatedPhoto);
        updatedPhoto
            .category(UPDATED_CATEGORY)
            .tag(UPDATED_TAG)
            .photography(UPDATED_PHOTOGRAPHY)
            .photographyContentType(UPDATED_PHOTOGRAPHY_CONTENT_TYPE)
            .priority(UPDATED_PRIORITY);
        PhotoDTO photoDTO = photoMapper.toDto(updatedPhoto);

        restPhotoMockMvc.perform(put("/api/photos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(photoDTO)))
            .andExpect(status().isOk());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeUpdate);
        Photo testPhoto = photoList.get(photoList.size() - 1);
        assertThat(testPhoto.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testPhoto.getTag()).isEqualTo(UPDATED_TAG);
        assertThat(testPhoto.getPhotography()).isEqualTo(UPDATED_PHOTOGRAPHY);
        assertThat(testPhoto.getPhotographyContentType()).isEqualTo(UPDATED_PHOTOGRAPHY_CONTENT_TYPE);
        assertThat(testPhoto.getPriority()).isEqualTo(UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    public void updateNonExistingPhoto() throws Exception {
        int databaseSizeBeforeUpdate = photoRepository.findAll().size();

        // Create the Photo
        PhotoDTO photoDTO = photoMapper.toDto(photo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPhotoMockMvc.perform(put("/api/photos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(photoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deletePhoto() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        int databaseSizeBeforeDelete = photoRepository.findAll().size();

        // Get the photo
        restPhotoMockMvc.perform(delete("/api/photos/{id}", photo.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Photo.class);
        Photo photo1 = new Photo();
        photo1.setId(1L);
        Photo photo2 = new Photo();
        photo2.setId(photo1.getId());
        assertThat(photo1).isEqualTo(photo2);
        photo2.setId(2L);
        assertThat(photo1).isNotEqualTo(photo2);
        photo1.setId(null);
        assertThat(photo1).isNotEqualTo(photo2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PhotoDTO.class);
        PhotoDTO photoDTO1 = new PhotoDTO();
        photoDTO1.setId(1L);
        PhotoDTO photoDTO2 = new PhotoDTO();
        assertThat(photoDTO1).isNotEqualTo(photoDTO2);
        photoDTO2.setId(photoDTO1.getId());
        assertThat(photoDTO1).isEqualTo(photoDTO2);
        photoDTO2.setId(2L);
        assertThat(photoDTO1).isNotEqualTo(photoDTO2);
        photoDTO1.setId(null);
        assertThat(photoDTO1).isNotEqualTo(photoDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(photoMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(photoMapper.fromId(null)).isNull();
    }
}
