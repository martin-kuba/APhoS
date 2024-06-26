package cz.muni.aphos.openapi;

import cz.muni.aphos.dto.ObjectFluxCount;
import cz.muni.aphos.openapi.models.Catalog;
import cz.muni.aphos.openapi.models.ComparisonObject;
import cz.muni.aphos.openapi.models.Coordinates;
import cz.muni.aphos.openapi.models.SpaceObjectWithFluxes;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Interface for SpaceObject with annotations for API controllers and swagger-ui.
 * Basic pathing is in @RequestMapping.
 */
@Validated
@RequestMapping("api/space-objects")
public interface SpaceObjectApi {

    /**
     * Find space objects by multiple parameters in query.
     * More info in annotations.
     *
     * @param objectId    object id
     * @param catalog     catalog
     * @param name        name
     * @param coordinates coordinates json string (rightAsc, declination, radius) mapped to Coordinates object
     * @param minMag      minimum magnitude
     * @param maxMag      maximum magnitude
     * @return List of space objects
     */
    @Operation(summary = "Finds space objects by multiple data", description = "No additional data is mandatory, but maximum object count is 100", tags = {"SpaceObject"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ObjectFluxCount.class)))),

            @ApiResponse(responseCode = "400", description = "Invalid values",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))),

            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    @RequestMapping(value = "/search-by-params",
            produces = {"application/json"},
            method = RequestMethod.GET)
    ResponseEntity<List<ObjectFluxCount>> findSpaceObjectsByParams(
            @Parameter(in = ParameterIn.QUERY, description = "Find object based on it's ID in given catalog") @Valid String objectId,
            @Parameter(in = ParameterIn.QUERY, description = "Catalog of space object to return \n\nDefault is " + Catalog.allValue) @Valid Catalog catalog,
            @Parameter(in = ParameterIn.QUERY, description = "Find object by it's name") @Valid String name,
            // coordinates could be in response body, with RequestMapping consumes, but there were problems with python client
            @Parameter(in = ParameterIn.QUERY, description = "Filter by coordinates\n\n" + "Format: " + Coordinates.example) @Nullable @Valid String coordinates,
            @Parameter(in = ParameterIn.QUERY, description = "Find objects based on min magnitude", schema = @Schema(type = "number", format = "float", defaultValue = "0")) @DecimalMin("0") @Valid Float minMag,
            @Parameter(in = ParameterIn.QUERY, description = "Find objects based on max magnitude", schema = @Schema(type = "number", format = "float", defaultValue = "15")) @DecimalMax("20") @Valid Float maxMag);

    /**
     * Find space object by object id and catalog.
     * More info in annotations.
     *
     * @param spaceObjectId space object id
     * @param catalog       catalog
     * @return Space object or other response dependent on existence of the object in database
     */
    @Operation(summary = "Find space object by ID and catalog", description = "Returns a space object with fluxes, maximum fluxes count is 2000", tags = {"SpaceObject", "Flux"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(implementation = SpaceObjectWithFluxes.class))),

            @ApiResponse(responseCode = "400", description = "Invalid catalog or ID supplied",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))),

            @ApiResponse(responseCode = "404", description = "Space object not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))),

            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    @RequestMapping(value = "/search",
            produces = {"application/json"},
            method = RequestMethod.GET)
    ResponseEntity<SpaceObjectWithFluxes> getSpaceObjectById(
            @Parameter(in = ParameterIn.QUERY, description = "ID of space object to return") @Valid String spaceObjectId,
            @Parameter(in = ParameterIn.QUERY, description = "Catalog of space object to return \n\nDefault is " + Catalog.defaultValue) @Valid Catalog catalog);

    /**
     * Get ComparisonObject data by id and catalog of 2 space objects.
     * More info in annotations.
     *
     * @param originalId   original (variable) id of space object
     * @param originalCat  original (variable) catalog
     * @param referenceId  reference (comparison) id of space object
     * @param referenceCat reference (comparison) catalog
     * @return Comparison object with data about stars and photos
     */
    @Operation(summary = "Comparison object of 2 Space objects", description = "Returns a fluxes comparison object", tags = {"SpaceObject", "Flux"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(implementation = ComparisonObject.class))),

            @ApiResponse(responseCode = "400", description = "Invalid catalogs or ID supplied",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))),

            @ApiResponse(responseCode = "404", description = "Space object not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))})

    @RequestMapping(value = "/comparison",
            produces = {"application/json"},
            method = RequestMethod.GET)
    ResponseEntity<ComparisonObject> getComparisonByIdentificators(
            @Parameter(in = ParameterIn.QUERY, description = "ID of space object to return") @Valid String originalId,
            @Parameter(in = ParameterIn.QUERY, description = "Catalog of space object to return") @Valid Catalog originalCat,
            @Parameter(in = ParameterIn.QUERY, description = "ID of space object to return") @Valid String referenceId,
            @Parameter(in = ParameterIn.QUERY, description = "Catalog of space object to return") @Valid Catalog referenceCat);

    /**
     * Endpoint for uploading csv file genarated from sips software.
     * More info in annotations.
     *
     * @param file csv file (from sips) with delimiter ';'
     * @return info about uploading
     * @throws IOException
     */
    @Operation(summary = "Upload file", description = "uploads file", tags = {"SpaceObject", "Flux"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = String.class))),

            @ApiResponse(responseCode = "400", description = "Invalid file",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))),

            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))})
    //@RequestMapping(value = "/upload_file",
    //        method = RequestMethod.POST)
    @RequestMapping(value = "/upload",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = {"application/json"})
    @Hidden
    ResponseEntity<String> uploadCSV(@Parameter(description = "File to upload") MultipartFile file) throws IOException;
}

