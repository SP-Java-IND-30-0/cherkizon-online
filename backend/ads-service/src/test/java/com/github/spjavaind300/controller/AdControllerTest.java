package com.github.spjavaind300.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.spjavaind300.SecurityTestUtils;
import com.github.spjavaind300.exception.NotFoundException;
import com.github.spjavaind300.model.dto.AdExtraInfoDto;
import com.github.spjavaind300.model.dto.AdRequestDto;
import com.github.spjavaind300.model.dto.AdResponseDto;
import com.github.spjavaind300.model.dto.ListAdsDto;
import com.github.spjavaind300.model.dto.Role;
import com.github.spjavaind300.security.CustomUserDetails;
import com.github.spjavaind300.security.JwtAuthenticationFilter;
import com.github.spjavaind300.security.JwtConfig;
import com.github.spjavaind300.service.AdService;
import com.github.spjavaind300.service.ImageStorageService;
import com.github.spjavaind300.service.JwtUtils;
import com.github.spjavaind300.service.imp.JwtUtilsImp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = AdController.class)
@Import({SecurityTestUtils.class,
        JwtUtilsImp.class,
        JwtConfig.class,
        JwtAuthenticationFilter.class
})
class AdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdService adService;

    @MockitoBean
    private ImageStorageService imageStorageService;

    private AdResponseDto adDto1, adDto2, adDto3;

    private final String BASE_URI = "/api/ads";

    @BeforeEach
    void setUp() {
        adDto1 = new AdResponseDto(1, "title1", 100, 1L, "image1");
        adDto2 = new AdResponseDto(2, "title2", 120, 1L, "image2");
        adDto3 = new AdResponseDto(3, "title3", 150, 2L, "image3");
        SecurityTestUtils.setupMockUser(1L, Role.USER);
    }

    @AfterEach
    void tearDown() {
        SecurityTestUtils.clearSecurityContext();
    }

    @Test
    @WithMockUser
    void test_getAllAds() throws Exception {
        ListAdsDto adsDto = ListAdsDto.builder().count(3).items(List.of(adDto1, adDto2, adDto3)).build();

        when(adService.getAllAds()).thenReturn(adsDto);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI))
                .andExpect((status().isOk()))
                .andExpect(jsonPath("$.count").value(3))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].title").value("title1"))
                .andExpect(jsonPath("$.items[1].title").value("title2"))
                .andExpect(jsonPath("$.items[2].title").value("title3"))
                .andExpect(jsonPath("$.items.length()").value(3));

    }

    @Test
    void test_getAllAdsForUser() throws Exception {
        ListAdsDto adsDto = ListAdsDto.builder().count(2).items(List.of(adDto1, adDto2)).build();
        when(adService.getAllAdsForUser(1L)).thenReturn(adsDto);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI + "/me")
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(
                                SecurityContextHolder.getContext())))
                .andExpect((status().isOk()))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].title").value("title1"))
                .andExpect(jsonPath("$.items[1].title").value("title2"));
    }

    @Test
    void test_getAdInfo_success() throws Exception {
        AdExtraInfoDto adExtraInfoDto = new AdExtraInfoDto();
        adExtraInfoDto.setId(1);
        adExtraInfoDto.setTitle("title1");

        when(adService.getAdInfo(anyInt())).thenReturn(adExtraInfoDto);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI + "/{id}", adExtraInfoDto.getId())
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(
                                SecurityContextHolder.getContext()))
                        .with(csrf())
                )
                .andExpect((status().isOk()))
                .andExpect(jsonPath("$.pk").value(1))
                .andExpect(jsonPath("$.title").value("title1"));
    }

    @Test
    void test_getAdInfo_whenNotFound_returns404() throws Exception {

        when(adService.getAdInfo(1)).thenThrow(NotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI + "/{id}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(
                                SecurityContextHolder.getContext()))
                        .with(csrf())
                )
                .andExpect((status().isNotFound()));
    }

    @Test
    void test_createAd_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("image", "test.png", "image/png", "test image".getBytes());
        AdRequestDto requestDto = new AdRequestDto("title1", 100, "description");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        when(adService.createAd(1L, requestDto, file)).thenReturn(adDto1);

        MockPart jsonPart = new MockPart("properties", jsonRequest.getBytes());
        jsonPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(MockMvcRequestBuilders.multipart(BASE_URI)
                        .file(file)
                        .part(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Content-Type", "multipart/form-data")
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(
                                SecurityContextHolder.getContext()))
                        .with(csrf())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pk").value(1))
                .andExpect(jsonPath("$.title").value("title1"));

    }

    @Test
    void test_createAd_NotValidRequest_returns400() throws Exception {

        AdRequestDto invalidDto = new AdRequestDto("t", -100, "");
        String jsonRequest = objectMapper.writeValueAsString(invalidDto);

        MockPart jsonPart = new MockPart("properties", jsonRequest.getBytes());
        jsonPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(MockMvcRequestBuilders.multipart(BASE_URI)
                        .file(new MockMultipartFile("image", "test.png", "image/png", "test".getBytes()))
                        .part(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(
                                SecurityContextHolder.getContext()))
                        .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.violations").isArray())
                .andExpect(jsonPath("$.violations.length()").value(3));
    }

    @Test
    void test_updateAd() throws Exception {
        AdRequestDto requestDto = new AdRequestDto("newTitle", 100, "description");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        adDto1.setTitle("newTitle");
        when(adService.updateAd(1, requestDto)).thenReturn(adDto1);

        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_URI + "/{id}", 1)
                        .contentType("application/json")
                        .content(jsonRequest)
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(
                                SecurityContextHolder.getContext()))
                        .with(csrf())
                )
                .andExpect((status().isOk()))
                .andExpect(jsonPath("$.pk").value(1))
                .andExpect(jsonPath("$.title").value("newTitle"));

    }

    @Test
    void updateAdImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile("image", "test.png", "image/png", "test image".getBytes());

        byte[] imageBytes = "test image".getBytes();
        when(adService.updateImage(anyInt(), any(MultipartFile.class))).thenReturn(imageBytes);

        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PATCH, BASE_URI + "/{id}/image", 1)
                        .file(file)
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(
                                SecurityContextHolder.getContext()))
                        .with(csrf())
                )
                .andExpect((status().isOk()))
                .andExpect(MockMvcResultMatchers.content().bytes(imageBytes))
                .andExpect(MockMvcResultMatchers.content().contentType("image/png"));

    }

    @Test
    void test_deleteAd_success() throws Exception {

        CustomUserDetails userDetails = new CustomUserDetails(1L, Role.USER);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        doNothing().when(adService).deleteAd(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URI + "/{id}", 1)
                        .with(authentication(auth))
                        .with(csrf())
                )
                .andExpect((status().isNoContent()));
    }

    @Test
    void test_deleteAd_NotFound_returns404() throws Exception {
        doThrow(NotFoundException.class).when(adService).deleteAd(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URI + "/{id}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(
                                SecurityContextHolder.getContext()))
                        .with(csrf())
                )
                .andExpect((status().isNotFound()));
    }


    @Test
    @WithMockUser
    void getAdImage() throws Exception {
        byte[] imageBytes = "test image".getBytes();
        when(imageStorageService.getFile(anyString())).thenReturn(imageBytes);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI + "/images/{imageKey}", "file.jpg"))
                .andExpect((status().isOk()))
                .andExpect(MockMvcResultMatchers.content().bytes(imageBytes))
                .andExpect(MockMvcResultMatchers.content().contentType("image/jpeg"));

    }
}