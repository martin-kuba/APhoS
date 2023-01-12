package cz.muni.aphos.mappers;

import cz.muni.aphos.dto.ObjectFluxCount;
import cz.muni.aphos.dto.SpaceObject;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * The SpaceObjectApi mapper. Maps the rows from the space_object table
 *  to the SpaceObject object with all necessary data.
 */
public class ObjectFluxApiMapper implements RowMapper<ObjectFluxCount> {

    @Override
    public ObjectFluxCount mapRow(ResultSet rs, int rowNum) throws SQLException {
        ObjectFluxCount spaceObject = new ObjectFluxCount();
        spaceObject.setId(rs.getLong("obj_id"));
        spaceObject.setName(rs.getString("name"));
        spaceObject.setCatalogId(rs.getString("catalog_id"));
        spaceObject.setCatalog(rs.getString("catalog"));
        spaceObject.setCatalogDec(rs.getString("catalog_dec"));
        spaceObject.setCatalogRec(rs.getString("catalog_rec"));
        spaceObject.setCatalogMag(rs.getFloat("catalog_mag"));
        return spaceObject;
    }
}