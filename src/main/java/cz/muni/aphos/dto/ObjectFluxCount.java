package cz.muni.aphos.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import cz.muni.aphos.helper.ViewField;
import cz.muni.aphos.openapi.models.Catalog;
import cz.muni.aphos.openapi.models.SpaceObjectWithFluxes;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.Internal;
import org.springframework.data.annotation.Reference;
import org.springframework.validation.annotation.Validated;

/**
 * The Data Transfer Object for a result of user searching for a space object.
 * It consists of the object catalog info and number of its fluxes in the database.
 */

@Schema(name="SpaceObject", subTypes = SpaceObjectWithFluxes.class)
@JsonPropertyOrder({"id", "catalog","name", "rightAsc", "declination", "magnitude", "fluxesCount" })
@Validated
public class ObjectFluxCount {

    @JsonIgnore
    private long id;

    @JsonProperty(value = "name", required = true)
    private String name;

    @JsonProperty(value = "catalog", required = true)
    //@Reference(Catalog.class)
    private String catalog;

    @JsonProperty(value = "id", required = true)
    private String catalogId;

    @JsonProperty(value = "rightAsc", required = true)
    private String catalogRec;

    @JsonProperty(value = "declination", required = true)
    private String catalogDec;

    @JsonProperty(value = "magnitude", required = true)
    private Float catalogMag;

    /**
     * number of fluxes the object has in the database
     */
    @JsonProperty(value = "fluxesCount", required = true)
    private int numberOfFluxes;

    public ObjectFluxCount() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public String getCatalogRec() {
        return catalogRec;
    }

    public void setCatalogRec(String catalogRec) {
        this.catalogRec = catalogRec;
    }

    public String getCatalogDec() {
        return catalogDec;
    }

    public void setCatalogDec(String catalogDec) {
        this.catalogDec = catalogDec;
    }

    public Float getCatalogMag() {
        return catalogMag;
    }

    public void setCatalogMag(Float catalogMag) {
        this.catalogMag = catalogMag;
    }

    public int getNumberOfFluxes() {
        return numberOfFluxes;
    }

    public void setNumberOfFluxes(int numberOfFluxes) {
        this.numberOfFluxes = numberOfFluxes;
    }
}
