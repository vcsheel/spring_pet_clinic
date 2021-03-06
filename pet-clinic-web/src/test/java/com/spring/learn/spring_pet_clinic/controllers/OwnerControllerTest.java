package com.spring.learn.spring_pet_clinic.controllers;

import com.spring.learn.spring_pet_clinic.model.Owner;
import com.spring.learn.spring_pet_clinic.services.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {

    private final String CREATE_OR_UPDATE_OWNER_FORM_VIEW = "owners/createOrUpdateOwnerForm";

    @Mock
    OwnerService service;

    @InjectMocks
    OwnerController controller;

    Set<Owner> owners;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        owners = new HashSet<>();
        owners.add(Owner.builder().id(1L).build());
        owners.add(Owner.builder().id(2L).build());

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }


    @Test
    void findOwner() throws Exception {
        mockMvc.perform(get("/owners/find"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("owner"))
                .andExpect(view().name("owners/findOwners"));

        verifyNoInteractions(service);
    }

    @Test
    void displayOwners() throws Exception {

        when(service.findById(anyLong())).thenReturn(Owner.builder().id(1L).build());

        mockMvc.perform(get("/owners/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/ownerDetails"))
                .andExpect(model().attribute("owner", hasProperty("id",is(1L))));
    }

    @Test
    void processFindFormReturnOne() throws Exception {

        List<Owner> oneOwnerList = Arrays.asList(Owner.builder().id(1L).build());

        when(service.findAllByLastNameLike(anyString())).thenReturn(oneOwnerList);

        mockMvc.perform(get("/owners"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/1"));
    }

    @Test
    void processFindFormReturnsMany() throws Exception {

        List<Owner> manyOwners = Arrays.asList(Owner.builder().id(1L).build(), Owner.builder().id(2L).build());

        when(service.findAllByLastNameLike(anyString())).thenReturn(manyOwners);

        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/ownersList"))
                .andExpect(model().attribute("selections", hasSize(2)));
    }

    @Test
    void initCreateOwnerForm() throws Exception {
        mockMvc.perform(get("/owners/new"))
                .andExpect(status().isOk())
                .andExpect(view().name(CREATE_OR_UPDATE_OWNER_FORM_VIEW))
                .andExpect(model().attributeExists("owner"));

        verifyNoInteractions(service);
    }

    @Test
    void processCreateOwnerForm() throws Exception {
        Owner owner = Owner.builder().id(1L).build();
        when(service.save(ArgumentMatchers.any())).thenReturn(owner);

        mockMvc.perform(post("/owners/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/1"));

        verify(service, times(1)).save(ArgumentMatchers.any());
    }

    @Test
    void initUpdateOwnerForm() throws Exception {
        Owner owner = Owner.builder().id(1L).build();
        when(service.findById(anyLong())).thenReturn(owner);

        mockMvc.perform(get("/owners/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name(CREATE_OR_UPDATE_OWNER_FORM_VIEW))
                .andExpect(model().attributeExists("owner"));

        verify(service).findById(anyLong());
    }

    @Test
    void processUpdateOwnerForm() throws Exception {
        Owner owner = Owner.builder().id(1L).build();
        when(service.save(ArgumentMatchers.any())).thenReturn(owner);

        mockMvc.perform(post("/owners/1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/1"))
                .andExpect(model().attributeExists("owner"));

        verify(service).save(ArgumentMatchers.any());
    }
}