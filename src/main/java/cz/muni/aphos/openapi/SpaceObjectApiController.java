package cz.muni.aphos.openapi;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.aphos.dao.FluxDao;
import cz.muni.aphos.dao.SpaceObjectDao;
import cz.muni.aphos.dto.FluxUserTime;
import cz.muni.aphos.dto.ObjectFluxCount;
import cz.muni.aphos.dto.User;
import cz.muni.aphos.exceptions.CsvContentException;
import cz.muni.aphos.openapi.models.Catalog;
import cz.muni.aphos.openapi.models.ComparisonObject;
import cz.muni.aphos.openapi.models.Coordinates;
import cz.muni.aphos.openapi.models.SpaceObjectWithFluxes;
import cz.muni.aphos.services.FileHandlingService;
import cz.muni.aphos.services.UserService;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.api.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class SpaceObjectApiController implements SpaceObjectApi {

    private static final Logger log = LoggerFactory.getLogger(SpaceObjectApiController.class);

    @Autowired
    private FileHandlingService fileHandlingService;


    @Autowired
    private UserService userService;

    @Autowired
    private SpaceObjectDao spaceObjectDao;

    @Autowired
    private FluxDao fluxDao;


    // when user uses illegal argument for parameters, for example string "abc" where should be float
    @ExceptionHandler
    public ResponseEntity<ErrorMessage> handleException(IllegalArgumentException e) {
        return new ResponseEntity<>(new ErrorMessage(e.getLocalizedMessage() + " Illegal argument exception"), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<ObjectFluxCount>> findSpaceObjectsByParams(
            @Valid @RequestParam(value = "objectId", required = false) String objectId,
            @Valid @RequestParam(required = false) Catalog catalog,
            @Valid @RequestParam(value = "name", required = false) String name,
            @Nullable @Valid @RequestParam(value = "coordinates", required = false) String coordinates,
            @DecimalMin("0") @Valid @RequestParam(value = "minMag", required = false, defaultValue = "0") Float minMag,
            @DecimalMax("20") @Valid @RequestParam(value = "maxMag", required = false, defaultValue = "15") Float maxMag) {
        try {
            Coordinates coords;
            if (coordinates != null) {
                ObjectMapper mapper = new ObjectMapper();
                coords = mapper.readValue(coordinates, Coordinates.class);
                if(!coords.isValid()){
                    throw new ConstraintViolationException(null);
                }
            } else {
                coords = new Coordinates();
            }

            List<ObjectFluxCount> res = spaceObjectDao.queryObjects(coords.getRightAsc(), coords.getDeclination(),
                    coords.getRadius().toString(),
                    name != null ? name : "", minMag.toString(), maxMag.toString(),
                    catalog != null ? catalog.getValue() : "All catalogues", objectId != null ? objectId : "");
            return new ResponseEntity<>(res, HttpStatus.OK);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (JsonParseException | ConstraintViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coordinates value not correct, use: "
                    + Coordinates.class.getAnnotation(Schema.class).example());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SpaceObject endpoint problem");
        }
    }


    @Override
    public ResponseEntity<SpaceObjectWithFluxes> getSpaceObjectById(
            @Valid @RequestParam(value = "spaceObjectId") String spaceObjectId,
            @Valid @RequestParam(value = "catalog", required = false) Catalog catalog) {
        try {
            SpaceObjectWithFluxes spaceObject = (SpaceObjectWithFluxes) spaceObjectDao.getSpaceObjectByObjectIdCat
                    (spaceObjectId, catalog != null ? catalog.getValue() : Catalog.defaultValue, true);
            if (spaceObject == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SpaceObject not found");
            }
            spaceObject.setFluxes(fluxDao.getFluxesByObj(spaceObject.getId()));
            spaceObject.setNumberOfFluxes(spaceObject.getFluxes().size());
            return new ResponseEntity<>(spaceObject, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("SpaceObject endpoint problem: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SpaceObject internal server error");
        }
    }

    @Override
    public ResponseEntity<ComparisonObject> getComparisonByIdentificators(
            @Valid @RequestParam() String originalId,
            @Valid @RequestParam(required = false) Catalog originalCat,
            @Valid @RequestParam() String referenceId,
            @Valid @RequestParam(required = false) Catalog referenceCat) {
        try {
            ObjectFluxCount original = spaceObjectDao.getSpaceObjectByObjectIdCat
                    (originalId, originalCat != null ? originalCat.getValue() : Catalog.defaultValue, false);
            if (original == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Original object not found");
            }
            ObjectFluxCount reference = spaceObjectDao.getSpaceObjectByObjectIdCat
                    (referenceId, referenceCat != null ? referenceCat.getValue() : Catalog.defaultValue, false);
            if (reference == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reference object not found");
            }
            List<FluxUserTime> data = fluxDao.getFluxesByObjId(original.getId(), reference.getId());
            original.setNumberOfFluxes(Math.toIntExact(spaceObjectDao.getSpaceObjectFluxCount(original.getId())));

            reference.setNumberOfFluxes(Math.toIntExact(spaceObjectDao.getSpaceObjectFluxCount(reference.getId())));

            return new ResponseEntity<>(new ComparisonObject(original, reference, data), HttpStatus.OK);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Comparison object endpoint error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Comparison object endpoint error");
        }
    }

    @Override
    public ResponseEntity<String> uploadCSV(@RequestParam(required = true) MultipartFile file) throws IOException {
        Path path = null;
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File not loaded");
        }
        try {
            //Authentication auth = userService.getAuth();
            User user;
            //if(auth != null){
            //    user =userService.getCurrentUser();
            //}else{
            user = createAnonymousUser();
            //}
            Path dirs = Paths.get("target/temp/parse/");
            Files.createDirectories(dirs);
            int append = 1;
            String filename = "file" + append + ".csv";
            String directory = dirs.toString();
            path = Paths.get(directory, filename);
            while (Files.exists(path)) {
                append++;
                path = Paths.get(directory, "file" + append + ".csv");
            }
            //Files.createTempFile(String.valueOf(dirs),filename);
            file.transferTo(path);
            fileHandlingService.parseAndPersist(path, user);
            return new ResponseEntity<>("File uploaded and data saved.", HttpStatus.OK);

        } catch (CsvContentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is not in correct format");
        } catch (Exception e) {
            log.error("Upload file exception: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Upload file problem, try uploading file in correct format");

        } finally {
            if (path != null) {
                Files.deleteIfExists(path);
            }
        }

    }

    private User createAnonymousUser() {
        User user = new User("7899871233215");
        user.setUsername("Anonymous");
        return user;
    }

}
