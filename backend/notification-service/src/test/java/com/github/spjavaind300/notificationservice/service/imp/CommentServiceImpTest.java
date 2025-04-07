package com.github.spjavaind300.notificationservice.service.imp;

import com.github.spjavaind300.notificationservice.client.CommentServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImpTest {

    @Mock
    private CommentServiceClient client;

    private CommentServiceImp commentServiceImp;

    @BeforeEach
    void setUp() {
        commentServiceImp = new CommentServiceImp(client);
    }

    @Test
    void getCommentsAuthorIdsOfAdv() {

        Set<Long> expectedResult = Set.of(1L,2L);

        when(client.getCommentAuthorIds(1)).thenReturn(expectedResult);

        List<Long> result = commentServiceImp.getCommentsAuthorIdsOfAdv(1);

        assertNotNull(result);
        assertEquals(expectedResult.size(), result.size());
    }
}