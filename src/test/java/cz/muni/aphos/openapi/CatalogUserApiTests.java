package cz.muni.aphos.openapi;

import cz.muni.aphos.dao.SpaceObjectDao;
import cz.muni.aphos.dto.ObjectFluxCount;
import cz.muni.aphos.dto.User;
import cz.muni.aphos.openapi.models.Catalog;
import cz.muni.aphos.openapi.models.ComparisonObject;
import cz.muni.aphos.openapi.models.Coordinates;
import cz.muni.aphos.openapi.models.SpaceObjectWithFluxes;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Sql({"/sql/schema.sql", "/sql_test_data/test-data-all-small-sample.sql"})
@AutoConfigureEmbeddedDatabase(provider = ZONKY,
        refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD)
public class CatalogUserApiTests {

    @Autowired
    private CatalogApi catalogApi;

    @Autowired
    private UserApi userApi;

    @Autowired
    private SpaceObjectDao spaceDao;

    @Test
    public void getAvailableCatalogsTest(){
        ResponseEntity<String[]> entity = catalogApi.getCatalogs();
        assertTrue(entity.getStatusCode().is2xxSuccessful());
        String[] catalogs = entity.getBody();
        List<String> catalogList= Arrays.asList(catalogs);
        assertTrue(catalogList.contains(Catalog.defaultValue));
        assertTrue(catalogList.contains(Catalog.allValue));
        assertTrue(catalogList.contains("USNO-B1.0"));
    }

    @Test
    public void getUserByUsernameTest(){
        ResponseEntity<User> entity = userApi.getUserByUsername("Pavel");
        User user = entity.getBody();
        User expectedUser = new User("108133573258632378410");
        expectedUser.setUsername("Pavel");
        expectedUser.setDescription("Description");
        Assertions.assertEquals(expectedUser, user);
    }

    @Test
    public void nonExistentUser_GetUserByUsernameTest(){
        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,() -> userApi.getUserByUsername("NonExistentUser193"));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}
